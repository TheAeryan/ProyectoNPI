package com.example.practica1_npi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Vista asociada a la línea temporal de la actividad {@link MapaTemporal}.
 * Guarda el año actual seleccionado por el usuario y muestra dentro de la vista principal de la
 * aplicación (activity_main.xml) la línea temporal con el año seleccionado.
 */
public class LineaTemporal extends View {

    /**
     * Tanto por ciento del ancho que ocupa el rectángulo de la línea temporal.
     */
    private float percWidth = 0.7f; // Tanto por ciento del ancho que ocupa

    /**
     * Tanto por ciento del alto que ocupa el rectángulo de la línea temporal.
     */
    private float percHeight = 0.4f; // Tanto por ciento del alto que ocupa

    /**
     * Paint usado para pintar el rectángulo de la línea temporal.
     */
    private Paint rectPaint;

    /**
     * Radio de las esquinas del rectángulo de la línea temporal, para que
     * tenga las esquinas redondeadas.
     */
    private int cornerRadius = 20;

    /**
     * Año mínimo de la línea temporal. El usuario no puede elegir ningún año
     * menor que este.
     */
    private int minYear = 1000;

    /**
     * Año máximo de la línea temporal. El usuario no puede elegir ningún año
     * mayor que este. Se corresponde con el año actual (el 2019 en este caso).
     */
    private int maxYear = 2019;

    /**
     * Año actual de la línea temporal (seleccionado por el usuario). Debe encontrarse
     * en el intervalo [{@link #minYear}, {@link #maxYear}].
     */
    private int indexYear; // Año seleccionado por el usuario

    /**
     * Paint usado para pintar el índice de la línea temporal. Este índice se corresponde
     * con una línea vertical que se pinta sobre el rectángulo y muestra gráficamente el
     * año actual de la línea temporal ({@link #indexYear}).
     */
    private Paint indexPaint;

    /**
     * Paint usado para pintar el texto del año actual de la línea temporal ({@link #indexYear}).
     * Este texto se pinta encima del rectángulo y muestra de forma exacta el valor del año actual
     * ({@link #indexYear}).
     */
    private Paint textPaint;

    /**
     * Constructor usado cuando se crea la vista a partir del código.
     * Llama al método {@link #initialize()}.
     * @param context
     */
    public LineaTemporal(Context context) {
        super(context);
        initialize();
    }

    /**
     * Constructor usado cuando se "infla" la vista a partir del XML.
     * Llama al método {@link #initialize()}.
     * @param context
     * @param attrs
     * @param defStyle
     */
    public LineaTemporal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * Constructor usado cuando se "infla" la vista a partir del XML.
     * Llama al método {@link #initialize()}.
     * @param context
     * @param attrs
     */
    public LineaTemporal(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    /**
     * Método que pinta la vista.
     * Este método pinta todos los elementos de la línea temporal: el rectángulo, el índice
     * asociado al año actual y el texto que muestra el año actual. Al terminar, llama a
     * invalidate() para que se redibuje la vista lo más pronto posible.
     * @see View#invalidate()
     * @param canvas
     */
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

    /**
     * Método llamado cuando se ejecuta el constructor.
     * Se encarga de crear los atributos {@link #rectPaint}, {@link #indexPaint} y {@link #textPaint}.
     * También inicializa la línea temporal al año inicial ({@link #indexYear}),
     * que se corresponde en este caso al 1510.
     */
    private void initialize(){
        // Rectángulo
        rectPaint = new Paint();
        rectPaint.setColor(Color.argb(255, 0, 153, 0));

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

    /**
     * Método encargado de modificar el año actual.
     * El año pasado como parámetro se suma al año actual ({@link #indexYear}). Este método
     * se asegura de que {@link #indexYear} se encuentre dentro del rango
     * [{@link #minYear}, {@link #maxYear}]. Además, repinta el mapa por si hubiera aparecido/desaparecido
     * algún punto de interés e invalida también la propia vista, para que se vuelva a pintar.
     * @see #invalidate()
     * @see Mapa
     * @param newYear Año (positivo o negativo) que <b>sumar</b> al año actual {@link #indexYear}
     */
    public void addIndexYear(int newYear){
        indexYear += newYear;

        if (indexYear > maxYear)
            indexYear = maxYear;
        else if (indexYear < minYear)
            indexYear = minYear;

        // Repinto el mapa por si han cambiado los puntos de interés
        Activity contexto = (Activity)this.getContext(); // Tengo que llamar al findViewById de la actividad o, si no, solo busca entre las vistas hijas!
        Mapa mapa = (Mapa)contexto.findViewById(R.id.mapa);

        mapa.invalidate();

        // Hago que se vuelva a pintar
        invalidate();
    }

    /**
     * <i>Getter</i> de {@link #indexYear}.
     * @return El valor del año actual de la línea temporal ({@link #indexYear}).
     */
    public int getIndexYear(){
        return indexYear;
    }
}