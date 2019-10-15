package com.example.practica1_npi;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CustomRotationAnimation extends Animation {

    private static final float SPEED = 0.5f;

    private int azimuth_ini;
    private int azimuth_end;
    private int azimuth_inc;

    private float duration_inc;

    public CustomRotationAnimation(int azi_ini, int azi_end, int azi_inc, int dur_inc) {
        azimuth_ini = azi_ini;
        azimuth_end = azi_end;
        azimuth_inc = azi_inc;
        duration_inc = dur_inc;

        setDuration(((azi_end - azi_ini) / azi_inc)*dur_inc);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        /*float offset = (mEnd - mStart) * interpolatedTime + mStart;
        mOffset = (int) offset;
        postInvalidate();*/
    }
}

