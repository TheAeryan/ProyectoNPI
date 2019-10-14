package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// Problemas con la precisión (algunas veces no funciona bien el gesto)

public class LineaTemporal extends View {

    // Atributos para controlar el gesto (con dos dedos) de la línea temporal
    private int pointer0Id; // IDs de cada puntero
    private int pointer1Id;
    private PointF pointer0Pos; // Posición antigua del puntero
    private PointF pointer1Pos;
    private PointF pointer0NewPos; // Posición nueva del puntero
    private PointF pointer1NewPos;
    private boolean gestureStarted = false;
    private float umbralDespX = 30; // El desplazamiento en el Eje X debe superar este umbral para que se tenga en cuenta
    private float yearChangeFactor = 0.1f; // Cuántos años se añaden/quitan por píxel desplazado

    // Atributos de la línea temporal (rectángulo)
    private float percWidth = 0.7f; // Tanto por ciento del ancho que ocupa (de su espacio asignado)
    private float percHeight = 0.4f; // Tanto por ciento del alto que ocupa
    private Paint rectPaint;
    private int cornerRadius = 20;

    // Atributos del índice temporal (la raya vertical en el rectángulo)
    private int minYear = 1000; // Año que se corresponde con el "0"
    private int maxYear = 2019; // Año actual
    private int indexYear; // Año seleccionado por el usuario
    private Paint indexPaint;

    // Atributos del texto del año correspondiente a indexYear
    private Paint textPaint;


    public LineaTemporal(Context context) {
        super(context);
        initialize();
    }

    public LineaTemporal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public LineaTemporal(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    // Muestra los puntos en la pantalla (incluidos la linea y el punto medio)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // <Pinto la línea temporal (rectángulo)>

        float x_center = ((float)this.getWidth()) / 2.0f;
        float y_center = ((float)this.getHeight()) / 2.0f;

        y_center += textPaint.descent()*2; // Centro el rectángulo teniendo en cuenta el texto

        float rect_width = this.getWidth()*percWidth;
        float rect_height = this.getHeight()*percHeight;

        float left = x_center - (rect_width / 2.0f); // Pinto el rectángulo centrado
        float right = x_center + (rect_width / 2.0f);
        float top = y_center - (rect_height / 2.0f);
        float bottom = y_center + (rect_height / 2.0f);

        canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, rectPaint);

        // <Pinto el índice temporal (la línea vertical)>

        float perc_index = (indexYear - minYear) / ((float)(maxYear - minYear)); // Posición en porcentaje del índice (0:izquierda, 1:derecha)
        float x_index = left + rect_width*perc_index; // Posición x de la línea

        canvas.drawLine(x_index, top, x_index, bottom, indexPaint);

        // <Pinto el texto del año actual>
        float y_text = top - textPaint.descent()*2; // Lo pinto encima de la línea temporal (rectángulo)
        float x_text = x_center;

        canvas.drawText(Integer.toString(indexYear), x_text, y_text, textPaint);

        invalidate();
    }

    // METODO IMPORTANTE
    // Control de la multipulsacion de la pantalla
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
                // Me espero a pulsar el segundo dedo
                break;
            }
            // Movemos
            case MotionEvent.ACTION_MOVE:   {
                if (gestureStarted){
                    // Calculo las nuevas posiciones de los punteros
                    pointer0NewPos = new PointF(event.getX(event.findPointerIndex(pointer0Id)),
                            event.getY(event.findPointerIndex(pointer0Id)));
                    pointer1NewPos = new PointF(event.getX(event.findPointerIndex(pointer1Id)),
                            event.getY(event.findPointerIndex(pointer1Id)));

                    // Calculo las diferencias en posición
                    float pointer0_diff_x = pointer0NewPos.x - pointer0Pos.x;
                    float pointer1_diff_x = pointer1NewPos.x - pointer1Pos.x;

                    // El desplazamiento en x es igual a la media entre ambos desplazamientos
                    float desp_x;

                    desp_x = (pointer0_diff_x + pointer1_diff_x) / 2.0f;

                    int year_inc;

                    if (Math.abs(desp_x) > umbralDespX) // Solo tengo en cuenta el desplazamiento si es significativo
                        year_inc = (int)(desp_x*yearChangeFactor);
                    else
                        year_inc = 0;

                    indexYear += year_inc; // Cambio el año seleccionado

                    if (indexYear > maxYear)
                        indexYear = maxYear;
                    else if (indexYear < minYear)
                        indexYear = minYear;

                    // Guardo la nueva posición de los punteros
                    pointer0Pos = pointer0NewPos;
                    pointer1Pos = pointer0NewPos;
                }

                break;
            }
            // Levantamos
            case MotionEvent.ACTION_UP:   {
                break;
            }

            // Pulsamos con mas de un dedo
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (event.getPointerCount() == 2 && !gestureStarted) { // Solo se puede pulsar con dos dedos
                    // Guardo la información de ambos punteros
                    pointer0Id = event.getPointerId(0);
                    pointer0Pos = new PointF(event.getX(0), event.getY(0));
                    pointer1Id = event.getPointerId(1);
                    pointer1Pos = new PointF(event.getX(1), event.getY(1));

                    gestureStarted = true;
                }
                else if (gestureStarted) { // Cancelo el gesto si pulsa con un tercer dedo
                    gestureStarted = false;
                }

                break;
            }
            // Levantamos un dedo
            case MotionEvent.ACTION_POINTER_UP:   {
                // Reseteo el gesto en el caso de que ya hubiera empezado (tuviera los dos dedos sobre la pantalla)
                if (gestureStarted) {
                    gestureStarted = false;
                }

                break;
            }

            // La ventana pierde el focus
            case MotionEvent.ACTION_CANCEL:{
                gestureStarted = false; // Cancelo el gesto

                break;
            }

        }


        return true;
    }

    private void initialize(){
        // Rectángulo
        rectPaint = new Paint();
        rectPaint.setColor(Color.BLUE);

        // Índice
        indexYear = 1510; // Año inicial seleccionado por el usuario
        indexPaint = new Paint();
        indexPaint.setColor(Color.WHITE);
        indexPaint.setStrokeWidth(10.0f);
        indexPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Texto
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);;
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(100f); // Tamaño del texto
        textPaint.setTextAlign(Paint.Align.CENTER); // Texto centrado
    }

}