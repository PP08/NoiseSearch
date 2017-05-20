package com.phucphuong.noisesearch.Utilities;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import android.widget.TextView;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
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
//        TODO: calibrate the value
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
                short[] audioBuffer = new short[BUFFSIZE / 2];
                while (isRunning) {
                    Log.e("test", "test");
                    splValue = measureDecibel(audioBuffer, audioBuffer.length, recordInstance);
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

    private double measureDecibel(short[] audioBuffer, int audioBufferSize, AudioRecord recordInstance) {

        recordInstance.read(audioBuffer, 0, audioBufferSize);

        double[] audioBufferInDouble = new double[audioBufferSize];


        for (int i = 0; i < audioBufferSize; i++) {
            audioBufferInDouble[i] = audioBuffer[i];
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(audioBufferSize);

        fft.realForward(audioBufferInDouble);


        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < audioBufferSize; i++) {
            audioBufferInDouble[i] = Math.abs(audioBufferInDouble[i]);
            if (audioBufferInDouble[i] == 0){
                audioBufferInDouble[i] = 1e-17;
            }

            if (audioBufferInDouble[i] < sampleRateInHz / 2) {
                indices.add(i);
            }
        }

        double[] f = new double[indices.size()];

        for (int i = 0; i < indices.size(); i++) {
            f[i] = audioBufferInDouble[indices.get(i)];
//            X[i] = audioBufferInDouble[indices.get(i)];
        }

        double[] A;

        //filter A-weight

        A = filterA(f);

        double sumSquare = 0;

        for (int i = 0; i < A.length/2 + 1; i++){
            sumSquare += A[i] * A[i];
        }

        sumSquare = 2 * sumSquare / A.length;

        double dBA = 10 * Math.log10(sumSquare) + (double) 94;

        dBA += (double) calibrationValue;

        dBA = Math.round(dBA * 100.0) / 100.0;

        return dBA;
    }


    private double[] filterA(double[] f) {

        double c1 = 3.5041384e16;
        double c2 = Math.pow(20.598997, 2);
        double c3 = Math.pow(107.65265, 2);
        double c4 = Math.pow(737.86223, 2);
        double c5 = Math.pow(12194.217, 2);

        double[] num = new double[f.length];
        double[] den = new double[f.length];
        double[] A = new double[f.length];

        for (int i = 0; i < f.length; i++) {

            f[i] = Math.pow(f[i], 2);

            num[i] = c1 * Math.pow(f[i], 4);

            den[i] = Math.pow(c2 + f[i], 2) * (c3 + f[i]) * (c4 + f[i]) * Math.pow(c5 + f[i], 2);

            A[i] = num[i] / den[i];

        }

        return A;

    }
}
