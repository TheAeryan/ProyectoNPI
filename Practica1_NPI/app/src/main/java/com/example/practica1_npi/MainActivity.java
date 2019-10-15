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
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
    private int azimuth = 0; // Ángulo entre el norte y el dispositivo (según eje Z) (en grados)
    private int umbralAzimuth = 25; // Valor (en grados) que tiene que variar el azimuth para que se tenga en cuenta

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
        setMapRotation(0, 0);

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

    // MAL: hay un bug que hace que parpadee
    private void setMapRotation(int old_azimuth, int new_azimuth){
        ConstraintLayout map = (ConstraintLayout)findViewById(R.id.mapa);

        // Aplica la rotación lentamente mediante una animación
        int rotation, rot1, rot2;

        // Veo si es más corta la rotación hacia la derecha o hacia la izquierda
        rot1 = new_azimuth - old_azimuth;
        rot2 = 360 - Math.abs(rot1);

        if (rot1 >= 0) // El sentido de rotación de rot2 es el opuesto de rot1
            rot2 *= -1;

        if (Math.min(Math.abs(rot1), Math.abs(rot2)) == Math.abs(rot1))
            rotation = rot1;
        else
            rotation = rot2;

        RotateAnimation rotate = new RotateAnimation(0, rotation,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(1000);
        map.setAnimation(rotate);

        // Cuando termina la animación establezco la rotación del mapa a la orientación final de la animación
        rotate.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                map.setRotation(new_azimuth);
            }
        });


    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Método que se llama automáticamente cuando hay nuevos datos de sensores
    public void onSensorChanged(SensorEvent event){

        // Solo tengo en cuenta las mediciones con cierta precisión
        if (event.accuracy >= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) {

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
                    float azimuth_rad = orientation[0]; // Azimuth -> ángulo en radianes de rotación según el eje Z. El rango de valores es [-PI, PI]
                    int azimuth_grad = (int) Math.toDegrees(-azimuth_rad); // La rotación es en sentido opuesto

                    if (distAzimuths(azimuth_grad, azimuth) >= umbralAzimuth) {
                        int azimuth_prev = azimuth;

                        // Lo transformo al rango [0, 360)
                        if (azimuth_grad >= 0)
                            azimuth = (int) azimuth_grad;
                        else
                            azimuth = (int) (azimuth_grad + 360);

                        // Roto el mapa según el azimuth
                        setMapRotation(azimuth_prev, azimuth);

                        // Quitar
                        txtOrientation.setText("Azimuth: " + Integer.toString(azimuth));
                    }
                }
            }
        }
    }

    // Los azimuths tienen que ser en grados
    private int distAzimuths(int a, int b){
        int az_a, az_b;

        if (a >= 0)
            az_a = a;
        else // Si es negativo, lo paso del rango [-180, 0) a [180, 360)
            az_a = a + 360;

        if (b >= 0)
            az_b = b;
        else
            az_b = b + 360;

        return Math.abs(az_a - az_b);
    }
}
