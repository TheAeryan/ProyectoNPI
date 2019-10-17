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

    public void addIndexYear(int newYear){
        indexYear += newYear;

        if (indexYear > maxYear)
            indexYear = maxYear;
        else if (indexYear < minYear)
            indexYear = minYear;

        // Hago que se vuelva a pintar
        invalidate();
    }
}