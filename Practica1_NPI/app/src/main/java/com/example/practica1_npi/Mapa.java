package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class Mapa extends ConstraintLayout {

    public Mapa(Context context) {
        super(context);
    }

    public Mapa(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Mapa(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Obtengo las vistas
        View view_entrada = findViewById(R.id.recinto_entrada);
        View view_alcazaba = findViewById(R.id.recinto_alcazaba);
        View view_generalife = findViewById(R.id.recinto_generalife);
        View view_palacios = findViewById(R.id.recinto_palacios);

        // Pinto todos los recintos
        view_entrada.draw(canvas);
        view_alcazaba.draw(canvas);
        view_generalife.draw(canvas);
        view_palacios.draw(canvas);

    }
}
