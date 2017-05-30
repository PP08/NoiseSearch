package com.phucphuong.noisesearch.Utilities;

import android.content.Context;
import android.location.Location;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.phucphuong.noisesearch.Fragments.MapFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Arrays;
/**
 * Created by phucphuong on 3/16/17.
 */

public class SoundMeter {

    //for thread
    Handler handler;
    boolean isRunning = true;
    private Message data;
    private Bundle b;
    boolean logThreadShouldRun = true;


    //for audio recorder
    AudioRecord recordInstance;
    int sampleRateInHz = 44100;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private int BUFFSIZE; //320 - default
    //private int BUFFSIZE = sampleRateInHz * 2;

    //for SPL calculation
    double splValue = 0.0;
    float calibrationValue;
    boolean speedMode;


    //for location
    public GPSTracker gpsTracker;
    Context context;
    Location location;
    double latitude, longitude;


    //restrict the radius for single point
    double init_latitude, init_longitude;
    int mode;
    boolean out_the_circle = false;

    //for log
    public String FILENAME = "";
    private String device_id = "";
    private String timeStamp;
    private DateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "yyyy:MM:dd:HH:mm:ss.SSS"

    private String prefix;
    //for testing
    String errorTag = "has an error: ";

    public SoundMeter(Handler h, Context context, float calValue, boolean speedMode, String prefix, double init_latitude, double init_longitude, int mode) {
        this.handler = h;
        this.context = context;
        this.calibrationValue = calValue;
        this.speedMode = speedMode;
        this.prefix = prefix;
        this.init_latitude = init_latitude;
        this.init_longitude = init_longitude;
        this.mode = mode;
        gpsTracker = new GPSTracker(context);
        logThread.start();
    }


    public class LogRunnable implements Runnable{

        @Override
        public void run() {
            try {
                initializeLog();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (logThreadShouldRun){
                writeLog();
            }
        }
    }

    public Thread logThread = new Thread(new LogRunnable());

    public class MyRunable implements Runnable{

        @Override
        public void run() {

            if (speedMode){
                BUFFSIZE = sampleRateInHz * 2;
            }else {
                BUFFSIZE = sampleRateInHz * 4;
            }

            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz,
                        CHANNEL, ENCODING, BUFFSIZE);
                Log.e("State", Integer.toString(recordInstance.getState()));
                recordInstance.startRecording();

                short[] audioBuffer = new short[BUFFSIZE / 2];

                while (isRunning) {
                    long startTime = System.currentTimeMillis();
                    splValue = measureDecibel(audioBuffer, audioBuffer.length, recordInstance);
                    long endTime = System.currentTimeMillis();

                    //get the location
                    location = gpsTracker.lastLocation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (mode == 1){
                        double distance = getTheDistance(init_latitude, init_longitude, latitude, longitude);
                        if (distance > 10){
                            terminate();
                            out_the_circle = true;
                        }else {
                            getTimestamp(startTime, endTime);
                        }
                    }else {
                        getTimestamp(startTime, endTime);
                    }


//                    //get the location
//                    location = gpsTracker.lastLocation;
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
                    sendMessage();

                }
                recordInstance.stop();
                recordInstance.release();
                gpsTracker.stopUsingGPS();

            } catch (Exception e) {
                Log.e("test", "FAILUREEEEEEEEEEEE");
                e.printStackTrace();
            }
        }
    }

    private double getTheDistance(double init_latitude, double init_longitude, double latitude, double longitude) {
        double R = 6372.8;
        double delta_latitude = Math.toRadians(latitude - init_latitude);
        double delta_longitude = Math.toRadians(longitude - init_longitude);

        double rad_latitude1 = Math.toRadians(init_latitude);
        double rad_latitude2 = Math.toRadians(latitude);

        double a = Math.pow(Math.sin(delta_latitude / 2), 2) + Math.cos(rad_latitude1) * Math.cos(rad_latitude2) *
                Math.pow(Math.sin(delta_longitude / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return R * c * 1000;

    }


    public Thread thread = new Thread(new MyRunable());

    private void sendMessage(){
        data = Message.obtain();
        b = new Bundle();
        b.putDouble("spl", splValue);
        b.putBoolean("isRunning", isRunning);
        b.putBoolean("outTheCircle", out_the_circle);
        b.putDoubleArray("location", new double[]{latitude, longitude});
        data.setData(b);
        handler.sendMessage(data);
    }

    private double measureDecibel(short[] audioBuffer, int audioBufferSize, AudioRecord recordInstance){

        double rsmValue = 0;
        double spl;

//        Arrays.fill(audioBuffer, (short) 0);

        recordInstance.read(audioBuffer, 0, audioBufferSize);

        for (int i = 0; i < audioBufferSize; i++) {
            rsmValue += audioBuffer[i] * audioBuffer[i];
        }

        rsmValue = Math.sqrt(rsmValue);
        spl = 10 * Math.log10(rsmValue/audioBufferSize) + 94;

        spl += (double)calibrationValue;
        spl = Math.round(spl * 100.0) / 100.0;
        return  spl;
    }

    public void terminate() {
        isRunning = false;
    }


    public void initializeLog() throws IOException {

        device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        setFileName(); // log's name
        String header = "Device ID" + "," + "Timestamp" + "," + "Pressure" + "," + "Latitude" + "," + "Longitude" + "\n"; //set header for columns

        File fileDir = new File(context.getFilesDir() + "/Unsent Files");
        fileDir.mkdirs();

        File file = new File(fileDir, FILENAME);
        FileOutputStream stream = new FileOutputStream(file);

        stream.write(header.getBytes());
        stream.close();
    }

    public void setFileName(){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = df.format(Calendar.getInstance().getTime());
        FILENAME = prefix + "-" + date + ".csv";
    }

    public synchronized void writeLog(){

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String data;
        data = device_id + "," + timeStamp + "," + Double.toString(splValue) + "," + Double.toString(latitude) + "," + Double.toString(longitude) +"\n";
        try{
            File fileDir = new File(context.getFilesDir() + "/Unsent Files");
            File file = new File(fileDir, FILENAME);
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
            stream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void getTimestamp(long t1, long t2){

        long averageTime = (t1 + t2) / 2;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(averageTime);
        timeStamp = timeStampFormat.format(calendar.getTime());
        notify();
    }

}
