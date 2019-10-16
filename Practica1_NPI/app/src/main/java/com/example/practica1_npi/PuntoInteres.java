package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class PuntoInteres extends View {
    private Drawable landmarkImage;

    public PuntoInteres(Context context) {
        super(context);
        initialize();
    }

    public PuntoInteres(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public PuntoInteres(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize(){
        landmarkImage = getResources().getDrawable(R.drawable.landmark_icon, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}
