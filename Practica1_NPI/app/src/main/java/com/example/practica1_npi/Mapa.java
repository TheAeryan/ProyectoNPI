package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Mapa extends ConstraintLayout {

    public Mapa(Context context) {
        super(context);
        initialize();
    }

    public Mapa(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public Mapa(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize(){

    }

    @Override
    protected void onDraw(Canvas canvas){
        // Pruebo a cambiar el fondo de un recinto
        View view = findViewById(R.id.recinto_entrada);
        view.setBackgroundColor(Color.BLUE);
    }
}
