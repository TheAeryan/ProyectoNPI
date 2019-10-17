package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class RecintoMapa extends FrameLayout {
    private int state; // 0:desactivado, 1:activado, 2:desactivado+punto int., 3:activado+punto int.

    public RecintoMapa(Context context) {
        super(context);
        initialize();
    }

    public RecintoMapa(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public RecintoMapa(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize(){
        state = 0;
    }

    @Override
    protected void onDraw(Canvas canvas){
        switch(state){
            case 0:{ // desactivado
                this.setBackgroundColor(Color.LTGRAY);
                break;
            }
            case 1:{ // activado
                this.setBackgroundColor(Color.YELLOW);
                break;
            }
        }
    }

    public void changeState(int newState){
        if (newState != state && newState >=0 && newState <= 3)
            state = newState;
    }
}
