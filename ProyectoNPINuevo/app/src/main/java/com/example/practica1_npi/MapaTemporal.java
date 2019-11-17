package com.example.practica1_npi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Actividad principal de la aplicación.
 * Las funcionalidades de esta clase son las siguientes:
 * <ul>
 * <li> Muestra la vista principal (activity_main.xml), que se corresponde con el mapa y la línea
 *      temporal.
 * <li> Se encarga de obtener los datos de la orientación (magnética) del dispositivo y rotar
 *      el mapa para que siempre se encuentre mirando al norte.
 * <li> Se encarga de obtener la localización del usuario y activar/desactivar los recintos del
 *      mapa según en cuál se encuentre actualmente el usuario.
 * <li> Se encarga de manejar los TouchEvents necesarios para modificar la línea temporal (avanzar
 *      y retroceder el año actual).
 * </ul>
 */
public class MapaTemporal extends AppCompatActivity implements SensorEventListener{
    /**
     * Gestor de sensores, que nos permite acceder a los sensores del dispositivo.
     * Se usa para obtener el accelerómetro y magnetómetro del dispositivo.
     */
    private SensorManager mSensorManager;

    /**
     * Accelerómetro.
     */
    private Sensor accelerometer;

    /**
     * Magnetómetro.
     */
    private Sensor magnetometer;

    /**
     * Valores obtenidos del accelerómetro.
     */
    private float[] mGravity = null;

    /**
     * Valores obtenidos del magnetómetro.
     */
    private float[] mGeomagnetic = null;

    /**
     * Ángulo actual entre el norte y el dispositivo (según eje Z), medido en grados sexagesimales.
     */
    private int azimuth = 0;

    /**
     * Mínimo valor (en grados) que tiene que variar el azimuth para rotar el mapa. El uso de este
     * umbral evita que el mapa esté continuamente oscilando ante las pequeñas variaciones en la
     * orientación del dispositivo.
     */
    private int umbralAzimuth = 30;


    /**
     * <i>Runnable</i> que define la animación de la rotación del mapa. La rotación del mapa (ante
     * un cambio en {@link #azimuth} se aplica de forma "suave", rotando el mapa poco a poco.
     */
    private Runnable mapAnimation;

    /**
     * <i>Handler</i> que se encarga de aplicar la animación {@link #mapAnimation}.
     */
    Handler mHandler;

    /**
     * Iteración (<i>frame</i>) de la animación en proceso. Si vale 0, no hay ninguna animación
     * en proceso.
     * @see #mapAnimation
     */
    private int currAnimIt = 0;

    /**
     * Número de iteraciones (<i>frames</i>) totales de la animación en proceso.
     * @see #mapAnimation
     */
    private int totalAnimIt = 0;

    /**
     * Azimuth del mapa en la iteración (<i>frame</i>) actual de la animación. Cuando la animación
     * {@link #mapAnimation} termina, este valor coincide con {@link #azimuth}.
     */
    private int azimuthAnim; // Azimuth actual de la animación (cuando la animación termina, este valor coincide con azimuth)


    /**
     * Posición <i>x</i> anterior de la pulsación sobre la pantalla. Se calcula como la media de
     * las posiciones <i>x</i> de los dos dedos.
     * Se usa junto a {@link #newX} para calcular el desplazamiento de los dedos sobre la pantalla y modificar
     * el año de la línea temporal en función de este desplazamiento.
     * @see LineaTemporal
     */
    private float oldX;

    /**
     * Posición <i>x</i> nueva de la pulsación sobre la pantalla. Se calcula como la media de
     * las posiciones <i>x</i> de los dos dedos.
     * Se usa junto a {@link #oldX} para calcular el desplazamiento de los dedos sobre la pantalla y modificar
     * el año de la línea temporal en función de este desplazamiento.
     * @see LineaTemporal
     */
    private float newX;

    /**
     * Vale <i>True</i> si el gesto de desplazamiento de los dos dedos sobre la pantalla (para
     * modificar el año de la línea temporal) ha empezado y <i>False</i> en otro caso.
     * @see LineaTemporal
     */
    private boolean gestureStarted = false;

    /**
     * Factor de desplazamiento. Este valor es el número de años añadidos/quitados del año actual de
     * la línea temporal por cada píxel (de desplazamiento con los dedos sobre la pantalla). Cuanto
     * mayor sea, mayor será el cambio del año en la línea temporal ante el mismo desplazamiento
     * con los dedos.
     */
    private float yearChangeFactor = 0.15f;


    /**
     * Latitud de la localización actual del usuario.
     * @see #wayLongitude
     * @see #mFusedLocationClient
     */
    private double wayLatitude = 0.0;

    /**
     * Longitud de la localización actual del usuario.
     * @see #wayLatitude
     * @see #mFusedLocationClient
     */
    private double wayLongitude = 0.0;

    /**
     * Cliente usado para obtener la localización del usuario.
     * Hace uso de la <i>Fused Location Provider API</i> de Google. Este servicio de Google
     * obtiene la localización a partir de los datos del GPS del dispositivo y de las redes WIFI
     * cercanas.
     * El cliente se inicializa llamando al método LocationServices.getFusedLocationProviderClient(this),
     * siendo <i>this</i> la actividad desde donde obtener la localización.
     * Después, se llama al método mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
     * location) para obtener una primera localización y guardar la latitud y longitud. <i>this</i> es
     * la actividad desde donde obtener la localización y <i>location</i> es una instancia de
     * OnSuccessListener<Location>.
     * Por último, se llama a mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
     * Looper.getMainLooper()) para que se obtenga la localización automáticamente de forma periódica.
     * <i>locationRequest</i> es {@link #locationRequest}, <i>locationCallback</i> es
     * {@link #locationCallback} y <i>Looper.getMainLooper()</i> es el <i>Looper</i> principal
     * de la aplicación.
     * En el caso de que se quiera dejar de obtener la localización de forma periódica,
     * se llama a mFusedLocationClient.removeLocationUpdates(locationCallback). <i>locationCallback</i>
     * es {@link #locationCallback}.
     * @see #wayLatitude
     * @see #wayLongitude
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Define las propiedades de las peticiones de localización.
     * Se inicializa llamando a LocationRequest.create().
     * Llamando a locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) se indica que se quiere
     * obtener la localización con la mayor precisión posible. Se pueden pasar otros valores
     * como parámetros, indicando que se quiere ahorrar la mayor energía posible o que se quiere
     * un equilibrio entre gasto y precisión, por ejemplo.
     * Llamando a locationRequest.setInterval(updateInterval), se indica cada cuántos milisegundos
     * ({@link #updateInterval}) se quiere obtener una nueva localización.
     * @see #mFusedLocationClient
     */
    private LocationRequest locationRequest;

    /**
     * Define el código que se ejecuta cada vez que se obtiene una nueva localización.
     * Para ello, hay que crear una clase que herede de esta y definir el código a ejecutar
     * en el método onLocationResult(LocationResult locationResult) de esta nueva clase.
     * @see #mFusedLocationClient
     */
    private LocationCallback locationCallback;

    /**
     * Cada cuántos milisegundos se obtiene una nueva localización.
     * @see #locationRequest
     */
    private int updateInterval = 2000;

    /**
     * Posición del centro del recinto de la entrada. En este prototipo, se ha tomado la posición
     * del aula de prácticas de NPI.
     */
    private double latEntrada = 37.1973584, longEntrada = -3.6246068; // Posición del aula de prácticas de NPI

    /**
     * Posición del centro del recinto de los palacios. En este prototipo, se ha tomado la posición
     * del ala izquierda del pasillo de la facultad.
     */
    private double latPalacios = 37.1972543, longPalacios = -3.6240355;

    /**
     * Posición del centro del recinto del generalife. En este prototipo, se ha tomado la posición
     * de la zona exterior del pasillo de la facultad.
     */
    private double latGeneralife = 37.1972703, longGeneralife = -3.6246068;

    /**
     * Número de veces consecutivas que se debe detectar que el usuario está en un recinto diferente
     * antes de cambiar de recinto activado. Esto sirve para que, en el caso de que el usuario esté
     * en el límite entre dos recintos, la aplicación no esté continuamente cambiando de recinto activado.
     */
    private int veces_para_cambiar_loc = 5;

    /**
     * Número de veces consecutivas que se ha medido la localización del usuario en un recinto diferente.
     * @see #veces_para_cambiar_loc
     */
    private int cambios_loc_consecutivos = 0;

    /**
     * Recinto activado actualmente: 0 -> entrada, 1 -> palacios, 2 -> generalife
     */
    private int recinto_activado = 0;


    /**
     * Método llamado cuando se crea/cambia a esta actividad.
     * Realiza las siguientes tareas:
     * <ul>
     * <li> Le asocia el layout "activity_main.xml"
     * <li> Obtiene los sensores (llama a {@link #getSensors()})
     * <li> Inicializa la funcionalidad para obtener la localización (mediante el uso
     *      de {@link #mFusedLocationClient}). Llama a {@link #startLocationUpdates()}
     *      para que se empiece a obtener la localización de forma periódica.
     * </ul>
     * @param savedInstanceState Conjunto de datos del estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        setContentView(R.layout.activity_main);

        // Obtenemos los sensores
        getSensors();

        // Iniciamos la funcionalidad para obtener la localización

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Pedir permisos si es necesario
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION}, 1);
        }
        else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                }
            });
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Obtengo la localización con la mayor precisión posible
        locationRequest.setInterval(updateInterval);

        // Código que se ejecuta cada vez que se obtiene una nueva localización
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();

                    // Activo el recinto más cercano al punto actual
                    float[] dist_a_entrada = new float[3];
                    float[] dist_a_palacios = new float[3];
                    float[] dist_a_generalife = new float[3];

                    // Calculo la distancia de la posición actual al centro de cada recinto
                    // Las distancias se guardan en la posición 0 del array pasado como último parámetro al método
                    Location.distanceBetween(wayLatitude, wayLongitude, latEntrada, longEntrada, dist_a_entrada);
                    Location.distanceBetween(wayLatitude, wayLongitude, latPalacios, longPalacios, dist_a_palacios);
                    Location.distanceBetween(wayLatitude, wayLongitude, latGeneralife, longGeneralife, dist_a_generalife);

                    // Obtengo los recintos
                    RecintoMapa entrada = (RecintoMapa)findViewById(R.id.recinto_entrada);
                    RecintoMapa palacios = (RecintoMapa)findViewById(R.id.recinto_palacios);
                    RecintoMapa generalife = (RecintoMapa)findViewById(R.id.recinto_generalife);

                    // Nuevo recinto "activado" -> donde el LocationClient cree que se encuentra el usuario
                    int nuevo_recinto_activado; // 0: entrada, 1: palacios, 2: generalife

                    if (dist_a_entrada[0] < dist_a_palacios[0] && dist_a_entrada[0] < dist_a_generalife[0]){
                        nuevo_recinto_activado = 0;
                    }
                    else if (dist_a_palacios[0] < dist_a_generalife[0]){
                        nuevo_recinto_activado = 1;
                    }
                    else{
                        nuevo_recinto_activado = 2;
                    }

                    if (nuevo_recinto_activado != recinto_activado){
                        // Posible cambio de recinto
                        // Aumento en 1 el contador de cambios consecutivos
                        cambios_loc_consecutivos++;

                        // Se ha medido la localización varias veces consecutivas en un recinto diferente
                        // Cambio de recinto
                        if (cambios_loc_consecutivos == veces_para_cambiar_loc){
                            recinto_activado = nuevo_recinto_activado;

                            // Reseteo el contador de cambios consecutivos
                            cambios_loc_consecutivos = 0;

                            // Desactivo todos los recintos
                            entrada.changeActivation(0);
                            palacios.changeActivation(0);
                            generalife.changeActivation(0);

                            // Activo el nuevo recinto
                            switch(recinto_activado){
                                case 0:
                                    entrada.changeActivation(1);
                                    break;
                                case 1:
                                    palacios.changeActivation(1);
                                    break;
                                case 2:
                                    generalife.changeActivation(1);
                                    break;
                            }

                            // Invalido el mapa para que se vuelva a pintar
                            View mapa = findViewById(R.id.mapa);
                            mapa.invalidate();
                        }
                    }
                    else{ // El recinto no ha cambiado
                        // Reseteo el contador de cambios_consecutivos
                        cambios_loc_consecutivos = 0;
                    }

                }
            };
        };

        // Periódicamente obtenemos la localización
        startLocationUpdates();
    }

    /**
     * Obtiene los sensores del dispositivo para el magnetómetro ({@link #magnetometer}) y
     * accelerómetro ({@link #accelerometer}).
     * @see #mSensorManager
     */
    private void getSensors(){
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    /**
     * Método que se ejecuta cuando se reanuda la aplicación (vuelve a pasar a primer plano
     * después de haber estado en segundo plano).
     * Reanuda la obtención de datos de los sensores y de la localización.
     * @see #onPause()
     * @see #startLocationUpdates()
     */
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI); // Sensor_Delay_UI -> cada cuanto se obtienen datos de los sensores
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        startLocationUpdates();
    }


    /**
     * Método ejecutado cuando la aplicación se pausa (pasa a segundo plano).
     * Deja de obtener datos de los sensores y localización hasta que la aplicación vuelva
     * a primer plano.
     * @see #onResume()
     * @see #stopLocationUpdates()
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        stopLocationUpdates();
    }

    /**
     * Rota el mapa ante un cambio en el {@link #azimuth} del dispositivo.
     * La rotación se lleva a cabo de forma progresiva, mediante el uso de {@link #mapAnimation}.
     * @param old_azimuth Azimuth antiguo del dispositivo (antes de aplicar la nueva rotación al mapa).
     * @param new_azimuth Nuevo azimuth del dispositivo, con el que se debe corresponder el mapa tras aplicar la rotación.
     * @see Mapa
     * @see #mapAnimation
     * @see #currAnimIt
     * @see #totalAnimIt
     */
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

    /**
     * Método llamado cuando cambia la precisión de las medidas de un sensor.
     * En este prototipo, no hace nada.
     * @param sensor Sensor cuyas medidas han cambiado de precisión.
     * @param accuracy Nueva precisión del sensor.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    /**
     * Método llamado cada vez que se reciben nuevos datos de sensores (en nuestro caso,
     * del {@link #magnetometer} o {@link #accelerometer}).
     * En función del tipo de sensor, se guardan los valores de la medida en {@link #mGravity}
     * o {@link #mGeomagnetic}.
     * Acto seguido, si se tienen los datos de ambos sensores, se calcula la orientación
     * del dispositivo ({@link #azimuth}), mediante los métodos
     * {@link SensorManager#getRotationMatrix(float[], float[], float[], float[])} y
     * {@link SensorManager#getOrientation(float[], float[])}.
     * Por último, si el cambio de orientación supera al umbral {@link #umbralAzimuth},
     * se rota el mapa mediante {@link #setMapRotation(int, int)}.
     * @param event Objeto que encapsula los datos de la medida producida.
     */
    public void onSensorChanged(SensorEvent event){

        // Solo tengo en cuenta las mediciones con cierta precisión
        if (event.accuracy >= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;

            // Calcular orientación una vez tenemos los datos de ambos sensores
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
                    }
                }
            }
        }
    }


    /**
     * Calcula la distancia, en grados sexagesimales, entre dos azimuths distintos.
     * @param a Azimuth 1.
     * @param b Azimuth 2.
     * @return Distancia entre <i>a</i> y <i>b</i>.
     * @see #azimuth
     */
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

    /**
     * Método llamado cuando se produce un <i>TouchEvent</i> (se toca la pantalla).
     * Se encarga de modificar el año de la línea temporal. Para ello, el usuario
     * debe arrastrar <b>dos dedos</b> en la pantalla de izquierda a derecha, si quiere
     * avanzar en el año actual, o de derecha a izquierda, si quiere retroceder de año.
     * El gesto no tiene por qué realizarse sobre la línea temporal, sino que puede ser
     * en cualquier lugar de la pantalla.
     * @param event
     * @see LineaTemporal
     * @see #gestureStarted
     * @see #oldX
     * @see #newX
     */
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

    /**
     * Método llamado para empezar a obtener de forma periódica la localización del usuario.
     * @see #mFusedLocationClient
     * @see #stopLocationUpdates()
     */
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    /**
     * Método llamado para dejar de obtener de forma periódica la localización del usuario.
     * Este método se llama en {@link #onPause()} para así ahorrar energía mientras la aplicación
     * está en segundo plano.
     * @see #mFusedLocationClient
     * @see #stopLocationUpdates()
     */
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }


    /**
     * Método ejecutado tras pedirle permisos de aplicación al usuario.
     * En nuestro caso, se ejecuta tras pedirle los permisos para obtener la localización (
     * ACCESS_FINE_LOCATION y ACCESS_COARSE_LOCATION).
     * Si el usuario ha concedido los permisos a la aplicación, se obtiene la posición inicial del
     * dispositivo y se guarda en {@link #wayLongitude}, {@link #wayLatitude}. En caso contrario,
     * se muestra un mensaje mediante un <i>Toast</i>.
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @see #mFusedLocationClient
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
