package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
        state = 0; // Empieza estando desactivado
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        switch(state){
            case 0:{ // desactivado
                // Pinto un triángulo para marcar el recinto
                float x_center = getX() + getWidth()  / 2;
                float y_center = getY() + getHeight() / 2;

                float left = x_center - (getWidth() / 2.0f); // Pinto el rectángulo centrado
                float right = x_center + (getWidth() / 2.0f);
                float top = y_center - (getHeight() / 2.0f);
                float bottom = y_center + (getHeight() / 2.0f);

                Paint paint_rect_fill = new Paint();
                paint_rect_fill.setColor(Color.DKGRAY);
                paint_rect_fill.setStyle(Paint.Style.FILL);

                Paint paint_rect_stroke = new Paint();
                paint_rect_stroke.setColor(Color.BLACK);
                paint_rect_stroke.setStrokeWidth(2);
                paint_rect_stroke.setStyle(Paint.Style.STROKE);

                canvas.drawRect(left, top, right, bottom, paint_rect_fill);
                canvas.drawRect(left, top, right, bottom, paint_rect_stroke);

                break;
            }
            case 1:{ // activado
                // Pinto un triángulo para marcar el recinto
                float x_center = getX() + getWidth()  / 2;
                float y_center = getY() + getHeight() / 2;

                float left = x_center - (getWidth() / 2.0f); // Pinto el rectángulo centrado
                float right = x_center + (getWidth() / 2.0f);
                float top = y_center - (getHeight() / 2.0f);
                float bottom = y_center + (getHeight() / 2.0f);

                Paint paint_rect_fill = new Paint();
                paint_rect_fill.setColor(Color.YELLOW);
                paint_rect_fill.setStyle(Paint.Style.FILL);

                Paint paint_rect_stroke = new Paint();
                paint_rect_stroke.setColor(Color.BLACK);
                paint_rect_stroke.setStrokeWidth(2);
                paint_rect_stroke.setStyle(Paint.Style.STROKE);

                canvas.drawRect(left, top, right, bottom, paint_rect_fill);
                canvas.drawRect(left, top, right, bottom, paint_rect_stroke);

                break;
            }
            case 2:{ // desactivado+punto int
                // Pinto un triángulo para marcar el recinto
                float x_center = getX() + getWidth()  / 2;
                float y_center = getY() + getHeight() / 2;

                float left = x_center - (getWidth() / 2.0f); // Pinto el rectángulo centrado
                float right = x_center + (getWidth() / 2.0f);
                float top = y_center - (getHeight() / 2.0f);
                float bottom = y_center + (getHeight() / 2.0f);

                Paint paint_rect_fill = new Paint();
                paint_rect_fill.setColor(Color.DKGRAY);
                paint_rect_fill.setStyle(Paint.Style.FILL);

                Paint paint_rect_stroke = new Paint();
                paint_rect_stroke.setColor(Color.BLACK);
                paint_rect_stroke.setStrokeWidth(2);
                paint_rect_stroke.setStyle(Paint.Style.STROKE);

                canvas.drawRect(left, top, right, bottom, paint_rect_fill);
                canvas.drawRect(left, top, right, bottom, paint_rect_stroke);

                // Pinto un círculo enmedio para marcar el punto de interés
                Paint paint_circ = new Paint();
                paint_circ.setColor(Color.BLUE);
                paint_circ.setStyle(Paint.Style.FILL);

                canvas.drawCircle(x_center, y_center, 30, paint_circ);

                break;
            }
            case 3:{ // activado+punto int
                // Pinto un triángulo para marcar el recinto
                float x_center = getX() + getWidth()  / 2;
                float y_center = getY() + getHeight() / 2;

                float left = x_center - (getWidth() / 2.0f); // Pinto el rectángulo centrado
                float right = x_center + (getWidth() / 2.0f);
                float top = y_center - (getHeight() / 2.0f);
                float bottom = y_center + (getHeight() / 2.0f);

                Paint paint_rect_fill = new Paint();
                paint_rect_fill.setColor(Color.YELLOW);
                paint_rect_fill.setStyle(Paint.Style.FILL);

                Paint paint_rect_stroke = new Paint();
                paint_rect_stroke.setColor(Color.BLACK);
                paint_rect_stroke.setStrokeWidth(2);
                paint_rect_stroke.setStyle(Paint.Style.STROKE);

                canvas.drawRect(left, top, right, bottom, paint_rect_fill);
                canvas.drawRect(left, top, right, bottom, paint_rect_stroke);

                // Pinto un círculo enmedio para marcar el punto de interés
                Paint paint_circ = new Paint();
                paint_circ.setColor(Color.BLUE);
                paint_circ.setStyle(Paint.Style.FILL);

                canvas.drawCircle(x_center, y_center, 30, paint_circ);

                break;
            }
        }
    }

    public void changeState(int newState){
        if (newState != state && newState >=0 && newState <= 3)
            state = newState;
    }
}
