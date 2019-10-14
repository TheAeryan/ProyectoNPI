package com.example.practica1_npi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.content.Intent;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] mGravity = null;   // Atributos que guardan los datos recibidos por los sensores
    private float[] mGeomagnetic = null;
    private float azimuth = 0; // Ángulo entre el norte y el dispositivo (según eje Z) (en grados)
    private float umbralAzimuth = 25; // Valor (en grados) que tiene que variar el azimuth para que se tenga en cuenta

    // Quitar
    private TextView txtOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cambio a la actividad para probar la localización
        Intent intent = new Intent(this, ShowLocationActivity2.class);
        //startActivity(intent);

        setContentView(R.layout.activity_main);

        getSensors();

        //setContentView(R.layout.mostrar_localizacion);

        // La orientación Inicial es mirando al norte
        setMapRotation(0);

        txtOrientation = findViewById(R.id.txtOrientation);
    }

    private void getSensors(){
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    // Se añade el Listener cuando la aplicación vuelve a estar activa
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI); // Sensor_Delay_UI -> cada cuanto se obtienen datos de los sensores
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    // Cuando la aplicación pasa a segundo plano, se deja de actualizar la información de los sensores
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void setMapRotation(float grades){
        ConstraintLayout map = (ConstraintLayout)findViewById(R.id.mapa);

        // Aplica la rotación lentamente

        // Ver cómo hacerlo lentamente!!!
        if (grades > map.getRotation()) {
            float rot_inc = 1; // En grados

            for (float curr_rot = map.getRotation(); curr_rot <= grades; curr_rot += rot_inc)
                map.setRotation(curr_rot);
        }
        else{
            float rot_inc = -1; // En grados

            for (float curr_rot = map.getRotation(); curr_rot >= grades; curr_rot += rot_inc)
                map.setRotation(curr_rot);
        }

        map.setRotation(grades);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Método que se llama automáticamente cuando hay nuevos datos de sensores
    public void onSensorChanged(SensorEvent event){

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        // Calcular orientación una vez tenemos
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            // Obtenemos la matriz de rotación del dispositivo en coordenadas mundiales a partir
            // de la información del magnetómetro y acelerómetro
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth_rad = orientation[0]; // Azimuth -> ángulo en radianes de rotación según el eje Z
                float azimuth_grad = (float)Math.toDegrees(azimuth_rad);

                if (Math.abs(azimuth_grad - azimuth) >= umbralAzimuth) {
                    azimuth = azimuth_grad;

                    // Roto el mapa según el azimuth
                    setMapRotation(azimuth);

                    // Quitar
                    txtOrientation.setText("Azimuth: " + Float.toString(azimuth));
                }
            }
        }

    }
}
