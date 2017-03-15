package com.phucphuong.noisesearch.Fragments;

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
    int FREQUENCY = 8000;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private int BUFFSIZE = 16000; //320 - default

    //for SPL calculation
    double splValue = 0.0;
    int calibrationValue;


    public SoundMeter(Handler h, int calValue) {
        this.handler = h;
        this.calibrationValue = calValue;
    }

    public class MyRunable implements Runnable{

        @Override
        public void run() {

            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, CHANNEL, ENCODING, BUFFSIZE);
                Log.e("State", Integer.toString(recordInstance.getState()));
                recordInstance.startRecording();

                short[] temBuffer = new short[BUFFSIZE];

                while (isRunning) {
                    double rsmValue = 0.0;

                    for (int i = 0; i < BUFFSIZE; i++) {
                        temBuffer[i] = 0;
                    }

                    recordInstance.read(temBuffer, 0, BUFFSIZE);

                    for (int i = 0; i < BUFFSIZE; i++) {
                        rsmValue += temBuffer[i] * temBuffer[i];
                    }

                    rsmValue = Math.sqrt(rsmValue);

                    splValue = 10 * Math.log10(rsmValue/BUFFSIZE);

                    splValue += calibrationValue;
                    splValue = Math.round(splValue);
                    sendMessage(isRunning);

                }

                recordInstance.stop();
                recordInstance.release();
                kill = true;
                splValue = 0;
                sendMessage(isRunning);

            } catch (Exception e) {
                Log.e("MY TAG: ", "FAILUREEEEEEEEEEEE");
                e.printStackTrace();
            }
        }
    }



    private void sendMessage(boolean isRunning){
        data = Message.obtain();
        b = new Bundle();
        b.putDouble("spl", splValue);
        data.setData(b);
        handler.sendMessage(data);
    }

}
