package com.phucphuong.noisesearch.Utilities.SoundMeterHelper;

import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by phucphuong on 5/20/17.
 */

public class EstimateLevel {



    public EstimateLevel(short[] audioBuffer){

        DoubleFFT_1D doubleFFT_1D = new DoubleFFT_1D(audioBuffer.length);
    }

}
