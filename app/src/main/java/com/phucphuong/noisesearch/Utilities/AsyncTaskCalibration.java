package com.phucphuong.noisesearch.Utilities;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by phucphuong on 3/21/17.
 */

public class AsyncTaskCalibration extends AsyncTask<Double, Double, Void> {

    AudioRecord recordInstance;
    private int sampleRateInHz;
    private static int ENCODING;
    private static int CHANNEL;
    private int BUFFSIZE;
    boolean isRunning, speedMode;
    Thread thread;
    private double splValue = 0.0;
    float calibrationValue;

    private TextView textView;

    public AsyncTaskCalibration(TextView tv, float calibrationValue, boolean speedMode) {
        this.textView = tv;
        this.calibrationValue = calibrationValue;
        this.speedMode = speedMode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sampleRateInHz = 44100;
        ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        CHANNEL = AudioFormat.CHANNEL_IN_MONO;
        if (speedMode) {
            BUFFSIZE = sampleRateInHz * 2;
        } else {
            BUFFSIZE = sampleRateInHz * 4;
        }
        isRunning = true;
        if (recordInstance != null) {
            recordInstance.release();
        }

    }

    @Override
    protected Void doInBackground(Double... params) {

        thread = new Thread(new MyRunable());
        thread.start();
        return null;
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        textView.setText(Double.toString(splValue));
//        TODO: calirate the value
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public class MyRunable implements Runnable {

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
                    Log.e("test", "test");
                    splValue = measureDecibel(temBuffer, BUFFSIZE, recordInstance);
                    splValue += (double) calibrationValue;
                    publishProgress(splValue);
                }
                if (recordInstance.getState() == 1) {
                    try {
                        recordInstance.stop();
                        recordInstance.release();
                    } catch (Exception ignored) {

                    }
                }
            } catch (Exception e) {
                Log.e("MY TAG: ", "FAILUREEEEEEEEEEEE");
                e.printStackTrace();
            }
        }
    }

    private double measureDecibel(short[] temBuffer, int BUFFSIZE, AudioRecord recordInstance) {

        double rsmValue = 0;
        double spl;
        Arrays.fill(temBuffer, (short) 0);
        recordInstance.read(temBuffer, 0, BUFFSIZE);
        for (int i = 0; i < BUFFSIZE; i++) {
            rsmValue += temBuffer[i] * temBuffer[i];
        }
        rsmValue = Math.sqrt(rsmValue);
        spl = 10 * Math.log10(rsmValue / BUFFSIZE) + 94;
        spl = Math.round(spl);
        return spl;
    }
}
