package com.example.practica1_npi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActividadTextoPuntoInteres extends AppCompatActivity {

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
