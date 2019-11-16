package com.example.practica1_npi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Vista asociada a los recintos del mapa ({@link Mapa}).
 */
public class RecintoMapa extends FrameLayout {

    /**
     * Estado actual del recinto:
     * <ul>
     * <li> 0 -> no activado (el usuario se encuentra en otro recinto)
     * <li> 1 -> activado (el usuario se encuentra en el recinto)
     * </ul>
     */
    int activation;

    /**
     * Atributo que guarda si algún punto de interés del recinto está activado:
     * <ul>
     * <li> false -> no hay ningún punto de interés activado (el año de la línea temporal
     *      no se corresponde con el intervalo temporal de ninguno de los puntos de interés
     *      del recinto)
     * <li> true -> hay un punto de interés activado (el año de la línea temporal
     *      se corresponde con el intervalo temporal de alguno de los puntos de interés
     *      del recinto)
     * </ul>
     * @see LineaTemporal
     * @see #arrayIntervals
     * @see #arrayPointsInterest
     */
    boolean pointOfInterestActivated = false;

    /**
     * ArrayList donde cada elemento se corresponde con una tupla (minAño, maxAño) del intervalo
     * temporal asociado a cada punto de interés de {@link #arrayPointsInterest}.
     */
    public ArrayList<ArrayList<Integer>> arrayIntervals;

    /**
     * ArrayList que almacena la descripción (String) de cada uno de los puntos de interés.
     */
    public ArrayList<String> arrayPointsInterest;

    /**
     * Texto del punto de interés activado actualmente. Este texto será el que se mostrará en la
     * actividad {@link ActividadTextoPuntoInteres} al pulsar sobre el recinto.
     * @see #arrayPointsInterest
     * @see #pointOfInterestActivated
     */
    String interest_info;

    /**
     * Constructor usado cuando se crea la vista a partir del código.
     * Llama al método {@link #initialize()}.
     * @param context
     */
    public RecintoMapa(Context context) {
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
    public RecintoMapa(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * Constructor usado cuando se "infla" la vista a partir del XML.
     * Llama al método {@link #initialize()}.
     * @param context
     * @param attrs
     */
    public RecintoMapa(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    /**
     * Método llamado cuando se ejecuta el constructor.
     * Inicializa el estado del recinto:
     * <ul>
     * <li> Empieza estando desactivado ({@link #activation} = 0), excepto si es el recinto
     *      de la entrada. Este recinto está activado inicialmente ya que las visitas a la Alhambra
     *      siempre comienzan en la entrada.
     * <li> Inicializa los arrays {@link #arrayPointsInterest} y {@link #arrayIntervals} asociados
     *      a los puntos de interés. El único recinto con un punto de interés es el recinto de los
     *      palacios, que tiene asociado un único punto de interés en el intervalo temporal
     *      [1400, 1600].
     * </ul>
     */
    private void initialize(){
        activation = 0; // Empieza estando desactivado

        arrayIntervals = new ArrayList<>();
        arrayPointsInterest = new ArrayList<>();

        // Si es el recinto de los palacios, le añado un punto de interés
        String view_id = this.getResources().getResourceName(this.getId());

        if (view_id.equals("com.example.practica1_npi:id/recinto_palacios")){
            ArrayList<Integer> interval = new ArrayList<>();
            interval.add(new Integer(1400));
            interval.add(new Integer(1600));

            arrayIntervals.add(interval);
            arrayPointsInterest.add("> Punto de interés del recinto de los Palacios. Años: [1400, 1600]");
        }

        // Si es el recinto de la entrada, lo activo inicialmente
        if (view_id.equals("com.example.practica1_npi:id/recinto_entrada"))
            activation = 1; // El usuario empieza inicialmente en la entrada
    }

    /**
     * Método que pinta el recinto.
     * Este método se llama desde {@link Mapa#onDraw(Canvas)}. Obtiene el año actual de la línea
     * temporal y comprueba si está en el intervalo temporal de algún punto de interés del recinto.
     * Si ese es el caso, almacena la descripción en {@link #interest_info} y establece
     * {@link #pointOfInterestActivated} a True. Acto seguido, pinta el recinto. El color será uno
     * u otro en función de si el recinto está activado o no y, en el caso de que un punto de interés
     * del recinto esté activado, también dibujará un círculo sobre el recinto.
     * @see LineaTemporal
     * @see #activation
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        pointOfInterestActivated = false;

        // Veo cuál es el año actual de la línea temporal
        Activity contexto = (Activity)this.getContext(); // Tengo que llamar al findViewById de la actividad o, si no, solo busca entre las vistas hijas!
        LineaTemporal linea_temporal = (LineaTemporal)contexto.findViewById(R.id.linea_temporal);
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
                paint_circ.setColor(Color.WHITE);
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
                paint_rect_fill.setColor(Color.argb(255, 255, 153, 0));
                paint_rect_fill.setStyle(Paint.Style.FILL);

                Paint paint_rect_stroke = new Paint();
                paint_rect_stroke.setColor(Color.BLACK);
                paint_rect_stroke.setStrokeWidth(2);
                paint_rect_stroke.setStyle(Paint.Style.STROKE);

                canvas.drawRect(left, top, right, bottom, paint_rect_fill);
                canvas.drawRect(left, top, right, bottom, paint_rect_stroke);

                // Pinto un círculo enmedio para marcar el punto de interés
                Paint paint_circ = new Paint();
                paint_circ.setColor(Color.WHITE);
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
                paint_rect_fill.setColor(Color.argb(255, 255, 153, 0));
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

    /**
     * Método llamado cuando se pulsa sobre el recinto.
     * Si no hay ningún punto de interés activado ({@link #pointOfInterestActivated} vale False)
     * no hace nada. Si hay un punto de interés activado en el recinto, cambia a la actividad
     * {@link ActividadTextoPuntoInteres} y le pasa como texto a mostrar la descripción del
     * punto de interés activado ({@link #interest_info}).
     * @param event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        Log.i("INFO", "onTouchEvent activado en RecintoMapa");

        // Define que accion se esta realizando en la pantalla
        // getAction(): clase de acción que se está ejecutando.
        // ACTION_MASK: máscara de bits de partes del código de acción.
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch(action) {
            // Pulsamos
            case MotionEvent.ACTION_DOWN: {
                if (pointOfInterestActivated) { // Si el punto de interés está activado
                    Activity main_activity = (Activity)getContext();

                    Intent intent = new Intent(main_activity, ActividadTextoPuntoInteres.class);

                    intent.putExtra("interest_info", interest_info); // Poner el mensaje entre la zona de memoria compartida por ambas actividades
                    main_activity.startActivity(intent);
                }

                break;
            }

        }

        return true;
    }


    /**
     * Método que se encarga de activar o desactivar el recinto.
     * Funciona como <i>setter</i> de {@link #activation}.
     * @param act Nuevo valor de {@link #activation}.
     */
    public void changeActivation(int act){
        activation = act;
    }
}
