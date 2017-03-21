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

import com.phucphuong.noisesearch.Fragments.MapFragment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
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
    private int BUFFSIZE = sampleRateInHz * 4; //320 - default
    //private int BUFFSIZE = sampleRateInHz * 2;

    //for SPL calculation
    double splValue = 0.0;
    int calibrationValue;


    //for location
    public GPSTracker gpsTracker;
    Context context;
    Location location;
    double latitude, longitude;

    //for log
    private String FILENAME = "";
    private String device_id = "";
    private String timeStamp;
    private DateFormat timeStampFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss.SSS");

    //for testing
    String errorTag = "has an error: ";

    public SoundMeter(Handler h, Context context) {
        this.handler = h;
        this.context = context;
        gpsTracker = new GPSTracker(context);
        logThread.start();
    }


    public class LogRunnable implements Runnable{

        @Override
        public void run() {
            initializeLog();
            while (logThreadShouldRun){
                writeLog();
            }
        }
    }

    public Thread logThread = new Thread(new LogRunnable());

    public class MyRunable implements Runnable{

        @Override
        public void run() {

            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz,
                        CHANNEL, ENCODING, BUFFSIZE);
                Log.e("State", Integer.toString(recordInstance.getState()));
                recordInstance.startRecording();

                short[] temBuffer = new short[BUFFSIZE];

                while (isRunning) {
                    long startTime = System.currentTimeMillis();
                    splValue = measureDecibel(temBuffer, BUFFSIZE, recordInstance);
                    long endTime = System.currentTimeMillis();
                    getTimestamp(startTime, endTime);
                    //get the location
                    location = gpsTracker.lastLocation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    sendMessage();

                }
                recordInstance.stop();
                recordInstance.release();
                gpsTracker.stopUsingGPS();

            } catch (Exception e) {
                Log.e("MY TAG: ", "FAILUREEEEEEEEEEEE");
                e.printStackTrace();
            }
        }
    }

    public Thread thread = new Thread(new MyRunable());

    private void sendMessage(){
        data = Message.obtain();
        b = new Bundle();
        b.putDouble("spl", splValue);
        b.putBoolean("isRunning", isRunning);
        b.putDoubleArray("location", new double[]{latitude, longitude});
        data.setData(b);
        handler.sendMessage(data);
    }

    private double measureDecibel(short[] temBuffer, int BUFFSIZE, AudioRecord recordInstance){

        double rsmValue = 0;
        double spl;

        Arrays.fill(temBuffer, (short) 0);

        recordInstance.read(temBuffer, 0, BUFFSIZE);

        for (int i = 0; i < BUFFSIZE; i++) {
            rsmValue += temBuffer[i] * temBuffer[i];
        }

        rsmValue = Math.sqrt(rsmValue);
        spl = 10 * Math.log10(rsmValue/BUFFSIZE) + 94;
        spl = Math.round(spl);
        return  spl;
    }

    public void terminate() {
        isRunning = false;
    }


    public void initializeLog(){

        device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        setFileName(); // log's name
        String header = "Device ID" + "," + "Timestamp" + "," + "Pressure" + "," + "Latitude" + "," + "Longitude" + "\n"; //set header for columns
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            out.write(header.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setFileName(){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = df.format(Calendar.getInstance().getTime());
        FILENAME = date + ".csv";
    }

    public synchronized void writeLog(){

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String data;
        data = device_id + "," + timeStamp + "," + Double.toString(splValue) + "," + Double.toString(longitude) + "," + Double.toString(latitude) +"\n";
        try{
            FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            out.write(data.getBytes());
            out.close();

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
