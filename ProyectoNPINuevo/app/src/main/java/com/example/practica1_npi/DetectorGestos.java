package com.example.practica1_npi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.example.practica1_npi.CazaTesoros.CazaTesoros;

import java.util.ArrayList;

/**
 * Reconoce gestos para pasar de una actividad a otra. Si se realiza una Q se pasa a la actividad
 * CazaTesoros; si se realiza una M se pasa a la actividad MapaTemporal.
 */
public class DetectorGestos extends GestureOverlayView implements GestureOverlayView.OnGesturePerformedListener {

    /**
     * Puntuación mínima para detectar un gesto.
     */
    private static final double GESTURE_SCORE = 3.0;

    /**
     * Librería de gestos detectables.
     */
    private static GestureLibrary gestureLibrary;

    /**
     * Constructor básico.
     * @param context El contexto donde se crea el objeto.
     */
    public DetectorGestos(Context context) {
        super(context);
        cargarLibreria();
    }

    /**
     * Constructor con XML
     * @param context El contexto donde se crea el objeto.
     * @param attrs Los atributos del objeto.
     */
    public DetectorGestos(Context context, AttributeSet attrs) {
        super(context, attrs);
        cargarLibreria();
    }

    /**
     * Constructor con XML y estilo.
     * @param context El contexto donde se crea el objeto.
     * @param attrs Los atributos del objeto.
     * @param defStyleAttr El estilo del objeto.
     */
    public DetectorGestos(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cargarLibreria();
    }

    /**
     * Carga la librería de gestos.
     * Solo se carga la primera vez; después se pone a la espera de recibir gestos.
     */
    private void cargarLibreria() {
        Activity act = (Activity) getContext();
        // Cargamos los gestos de la liberia si no lo están
        if (gestureLibrary == null)
            gestureLibrary = GestureLibraries.fromRawResource(act, R.raw.gesture);
        // Si no carga, da error y acaba
        if (!gestureLibrary.load()) {
            Log.e("GesturePerformListener", "La libreria de gestos no se cargó.");
            act.finish();
        } else
            // Nos ponemos a escuchar
            addOnGesturePerformedListener(this);
    }

    /**
     * Procesa un gesto realizado. 
     * Miramos si ha hecho un gesto formando una M o una Q con cierto umbral de puntuación, y en 
     * caso afirmativo redirigimos a la actividad deseada.
     * @param gestureOverlayView La vista donde se ha hecho el gesto
     * @param gesture El gesto realizado
     * @see CazaTesoros
     * @see MapaTemporal
     */
    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
        // Reconocemos el gesto y la lista de match
        ArrayList<Prediction> predictionList = gestureLibrary.recognize(gesture);

        // Si hay alguna al menos
        if (predictionList.size() > 0) {
            Activity activity = (Activity) getContext();
            Class actividad_objetivo = null;
            StringBuilder messageBuffer = new StringBuilder();

            // Obtenemos el mejor match
            Prediction firstPrediction = predictionList.get(0);

            // Si el gesto coincide con alguno por encima de un umbral
            if (firstPrediction.score > GESTURE_SCORE) {
                String action = firstPrediction.name;

                // Seleccionamos la actividad según el gesto
                switch (action) {
                    // CazaTesoros
                    case "q":
                        actividad_objetivo = CazaTesoros.class;
                        messageBuffer.append("Activating TreasureHunt!");
                        break;
                    // Linea temporal
                    case "m":
                        actividad_objetivo = MapaTemporal.class;
                        messageBuffer.append("Activating TimeLine!");
                        break;
                }

                // Si hacemos un gesto para ir a la actividad distinta a la actual
                if (activity.getClass() != actividad_objetivo) {
                    Intent intent = new Intent(activity, actividad_objetivo);
                    activity.startActivity(intent);
                }
            } else
                messageBuffer.append("You need to draw Q or M.");
            // Toast con el resultado
            Toast.makeText(activity, messageBuffer.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}