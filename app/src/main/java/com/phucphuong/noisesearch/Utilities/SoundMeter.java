package com.phucphuong.noisesearch.Utilities;

import android.content.Context;
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

import java.io.FileOutputStream;
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
    public boolean kill = false;

    //for audio recorder
    AudioRecord recordInstance;
    int sampleRateInHz = 44100;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private int BUFFSIZE = sampleRateInHz * 4; //320 - default

    //for SPL calculation
    double splValue = 0.0;
    int calibrationValue;


    //for testing
    String errorTag = "has an error: ";


    public SoundMeter(Handler h) {
        this.handler = h;
    }

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
                    splValue = measureDecibel(temBuffer, BUFFSIZE, recordInstance);
                    //Log.e(errorTag, Double.toString(splValue));
                    sendMessage();

                }

                recordInstance.stop();
                recordInstance.release();

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

}
