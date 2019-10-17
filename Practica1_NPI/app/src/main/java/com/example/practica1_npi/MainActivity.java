package com.example.practica1_npi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import android.content.Intent;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    // <Atributos para la rotación del mapa>

    private float[] mGravity = null;   // Atributos que guardan los datos recibidos por los sensores
    private float[] mGeomagnetic = null;
    private int azimuth = 0; // Ángulo entre el norte y el dispositivo (según eje Z) (en grados)
    private int umbralAzimuth = 30; // Valor (en grados) que tiene que variar el azimuth para que se tenga en cuenta

    // <Atributos para la animación de la rotación>

    private Runnable mapAnimation; // Se encarga de ejecutar la animación de rotación del mapa
    Handler mHandler; // Handler que se encarga de controlar la animación
    private int currAnimIt = 0; // Iteración de la animación en proceso (si no vale 0, hay una animación en proceso)
    private int totalAnimIt = 0; // Número de iteraciones (frames) de la animación en proceso
    private int azimuthAnim; // Azimuth actual de la animación (cuando la animación termina, este valor coincide con azimuth)

    // <Atributos para el manejo de la línea temporal>

    private float oldX;
    private float newX;
    private boolean gestureStarted = false;
    private float yearChangeFactor = 0.15f; // Cuántos años se añaden/quitan por píxel desplazado

    // Quitar
    private TextView txtOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        // Cambio a la actividad para probar la localización
        //Intent intent = new Intent(this, ShowLocationActivity.class);
        //startActivity(intent);

        setContentView(R.layout.activity_main);

        getSensors();

        //setContentView(R.layout.mostrar_localizacion);

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

        // Si ya había una animación en proceso, la cancelo y retomo por donde iba
        if (currAnimIt != 0){
            mHandler.removeCallbacks(mapAnimation);
            old_azimuth = azimuthAnim; // Empiezo por el azimuth de la animación cancelada
        }

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

        // Aplico la rotación lentamente
        int deg_inc;

        if (rotation >= 0) // Incrementos de 1 o -1 grados
            deg_inc = 1;
        else
            deg_inc = -1;

        int dur_inc = 4; // Duración en milisegundos de cada incremento

        currAnimIt = 1; // Empezamos por la primera iteración de la animación
        totalAnimIt = Math.abs(rotation / deg_inc);

        final int old_azimuth2 = old_azimuth; // Hago la variable final para poder usarla en la inner class

        mapAnimation = new Runnable() {
            @Override
            public void run() {
                // Aplico la rotación de un incremento
                if (currAnimIt <= totalAnimIt){
                    azimuthAnim = old_azimuth2 + deg_inc*currAnimIt;
                    map.setRotation(azimuthAnim);
                    currAnimIt += 1;

                    mHandler.postDelayed(mapAnimation, dur_inc);
                }
                else{ // Termino la animación
                    currAnimIt = 0;
                    totalAnimIt = 0;
                    mHandler.removeCallbacks(mapAnimation);
                }
            }
        };

        mHandler.postDelayed(mapAnimation, dur_inc);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        LineaTemporal linea_temporal = (LineaTemporal)findViewById(R.id.linea_temporal);

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
                    // Calculo la nueva posición x
                    newX = (event.getX(0) + event.getX(1)) / 2.0f;

                    // Calculo el desplazamiento
                    float desp_x = newX - oldX;

                    int year_inc = (int)(desp_x*yearChangeFactor);

                    // Cambio el año seleccionado
                    linea_temporal.addIndexYear(year_inc);

                    // Guardo la nueva posición x
                    oldX = newX;
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
                    // Guardo la posición de los dedos como la media de los dos punteros
                    oldX = (event.getX(0) + event.getX(1)) / 2.0f;

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
}
