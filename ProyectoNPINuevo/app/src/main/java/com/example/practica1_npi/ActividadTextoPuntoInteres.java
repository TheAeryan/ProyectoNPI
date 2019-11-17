package com.example.practica1_npi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que se encarga de mostrar la información de los puntos de interés del mapa.
 * Al pulsar en un recinto del mapa que tenga un punto de interés para el año en el que se encuentra
 * la línea temporal (el punto de interés esté activado), se cambia a esta actividad, que se
 * encarga de mostrar la información asociada al punto de interés. En este prototipo, solo existe
 * un punto de interés, y la información mostrada se corresponde con el nombre del recinto y el intervalo
 * temporal ( [año_min, año_max] ) asociado al punto de interés.
 * @see RecintoMapa
 */
public class ActividadTextoPuntoInteres extends AppCompatActivity {

    /**
     * Método llamado cuando se cambia a esta actividad.
     * Le asocia el layout "activity_texto_punto_interes", obtiene el texto a mostrar a partir
     * del Intent y lo muestra usando un textView.
     * @param savedInstanceState Conjunto de datos del estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_texto_punto_interes);

        // Obtengo el texto del punto de interés a mostrar
        Intent intent = getIntent();
        String punto_interes_info = intent.getStringExtra("interest_info");

        // Muestro el texto en el textview
        TextView txt_info = findViewById(R.id.txtInfoPuntoInteres);
        txt_info.setTextSize(20f);
        txt_info.setText(punto_interes_info);
    }

}
