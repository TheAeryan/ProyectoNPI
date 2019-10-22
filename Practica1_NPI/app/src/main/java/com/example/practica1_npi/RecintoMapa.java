package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class RecintoMapa extends FrameLayout {
    int activation; // 0 -> apagado (el usuario no está en este recinto), 1 -> encendido (el usuario está en este recinto)
    boolean pointOfInterestActivated = false; // Vale true si el año es el correcto para que se active un punto de interés

    public ArrayList<ArrayList<Integer>> arrayIntervals; // Cada elemento es el intervalo de años de cada punto de interés
    public ArrayList<String> arrayPointsInterest; // Cada elemento se corresponde con la descripción de un punto de interés

    String interest_info; // Texto a mostrar cuando se pulsa sobre el recinto (el correspondiente al punto de interés dado por el año de la línea temporal)

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
        activation = 0; // Empieza estando desactivado

        arrayIntervals = new ArrayList<>();
        arrayPointsInterest = new ArrayList<>();

        // Si es el recinto de los palacios, le añado un punto de interés
        if (this.getId() == R.id.recinto_palacios){ // No funciona
            ArrayList<Integer> interval = new ArrayList<>();
            interval.add(new Integer(1400));
            interval.add(new Integer(1600));

            arrayIntervals.add(interval);
            arrayPointsInterest.add("> Punto de interés del recinto de los Palacios. Años: [1400, 1600]");
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        pointOfInterestActivated = false;

        // Veo cuál es el año actual de la línea temporal
        LineaTemporal linea_temporal = (LineaTemporal)findViewById(R.id.linea_temporal);
        int currYear;

        if (linea_temporal != null) // Me aseguro de que ya exista la línea temporal
            currYear = linea_temporal.getIndexYear();
        else
            currYear = -1;

        // Miro si para ese año hay algún punto de interés
        for (int i = 0; i < arrayIntervals.size(); i++){
            int lowerYear = arrayIntervals.get(i).get(0);
            int upperYear = arrayIntervals.get(i).get(1);

            if (currYear > lowerYear && currYear <= upperYear){
                pointOfInterestActivated = true; // Muestro el punto de interés
                interest_info = arrayPointsInterest.get(i); // Guardo la información para mostrarla en cuanto se toque el recinto
            }

        }

        if (activation == 0){ // Apagado
            if (pointOfInterestActivated){
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
            }
            else{
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
            }
        }
        else{ // Encendido
            if (pointOfInterestActivated){
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
            }
            else{
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
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        // Define que accion se esta realizando en la pantalla
        // getAction(): clase de acción que se está ejecutando.
        // ACTION_MASK: máscara de bits de partes del código de acción.
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch(action) {
            // Pulsamos
            case MotionEvent.ACTION_DOWN: {
                if (pointOfInterestActivated) { // Si el punto de interés está activado
                    // <TODO>
                    // Cambio a la actividad para mostar la información del punto de interés si el año es el correcto
                }

                break;
            }

        }

        return true;
    }

    // Función para encender o apagar el recinto, en función de dónde se encuentre el usuario
    public void changeActivation(int act){
        activation = act;
    }
}
