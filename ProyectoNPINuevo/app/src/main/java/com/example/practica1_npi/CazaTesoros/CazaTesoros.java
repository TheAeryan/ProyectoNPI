package com.example.practica1_npi.CazaTesoros;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.practica1_npi.R;
import com.google.android.gms.vision.Detector;

/**
 * Actividad principal de CazaTesoros.
 * Consiste en ir encontrando diversos patrones geométricos en la Alhambra que se pueden detectar
 * con la cámara y también códigos QR escondidos que hay que escanear. Después de encontrarlos se
 * puede ver la información al respecto y además hay pistas por si no los encuentras.
 */
public class CazaTesoros extends AppCompatActivity {

    /**
     * Código de respuesta: detector QR.
     * Es el código de respuesta para una llamada al detector QR.
     */
    public static final int BARCODE_RESULT = 1400;
    /**
     * Código de respuesta: cámara.
     * Es el código de respuesta para una llamada la cámara.
     */
    public static final int CAMERA_RESULT = 1200;

    /**
     * Código de permiso para usar la cámara.
     */
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    /**
     * ID de la última imagen seleccionada.
     * Se guarda la ID de la imagen que ha sido arrastrad para un evento para saber
     * cual se tiene que actualizar.
     */
    private int idActual;

    /**
     * Establece la última id de la imagen.
     * @param idActual La nueva id a cambiar.
     */
    public void setIDActual(int idActual) {
        this.idActual = idActual;
    }

    /**
     * Crea la vista de la actividad.
     * Pone el layout, pide los permisos para la cámara si no los tuviera.
     * @param savedInstanceState Conjunto de datos del estado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se pone el layout
        setContentView(R.layout.activity_caza_tesoros);
        // Chequea si se tienen permisos para usar la cámara
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            // Si no se tienen permisos se piden
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
    }

    /**
     * Procesa la finalización del resultado de una actividad.
     * Según el código de finalización (si ha sido correcto) y el código de resultado
     * (tipo de petición) realiza varias tareas: Si ha sido de la cámara, se actualiza la imagen
     * con la foto obtenida; si es el lector QR entonces se desvela la imagen secreta correspondiente.
     * @param requestCode El tipo de petición de actividad
     * @param resultCode El resultado de la actividad
     * @param data Los datos resultantes de la actividad
     * @see DropContenedor#hacerFoto(CazaTesoros, int)
     * @see Imagen#desbloquearImagen(Bitmap)
     * @see Imagen#desbloquearImagen(int)
     * @see DetectarQRActivity#QR_ID
     * @see DetectarQRActivity.DetectorProcessorBarcode#receiveDetections(Detector.Detections)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Si el resultado es correcto
        if (RESULT_OK == resultCode) {
            // Si el código es de la cámara
            if (CAMERA_RESULT == requestCode){
                // Datos resultado
                Bundle extras = data.getExtras();
                // Tomamos la imagen actual
                Imagen img = findViewById(idActual);
                // Ponemos la nueva foto desde la cámara
                img.desbloquearImagen((Bitmap) extras.get("data"));
            } else if (BARCODE_RESULT == requestCode) {
                // ID del QR
                int id = data.getIntExtra(DetectarQRActivity.QR_ID, -1);
                if (id != -1) {
                    Imagen img = findViewById(id);
                    int foto;
                    // Seleccionamos la imagen del QR correspondiente
                    switch (id) {
                        case R.id.qr1:
                            foto = R.drawable.qr1;
                            break;
                        case R.id.qr2:
                            foto = R.drawable.qr2;
                            break;
                        case R.id.qr3:
                            foto = R.drawable.qr3;
                            break;
                        default:
                            foto = -1;
                            Log.e("CazaTesoros:","OnActivityResult - ID no válida");
                    }
                    // Desbloqueamos la imágen secreta
                    img.desbloquearImagen(foto);
                }
            }
            // Toast felicitando
            Toast.makeText(this, "Congratulations! Keep on like this.", Toast.LENGTH_LONG).show();
        }
    }
}

