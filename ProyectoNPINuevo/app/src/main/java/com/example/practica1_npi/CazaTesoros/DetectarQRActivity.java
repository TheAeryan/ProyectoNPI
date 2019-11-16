package com.example.practica1_npi.CazaTesoros;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.practica1_npi.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Actividad para poder detectar códigos QR.
 * Busca un código QR y lo devuelve a la actividad CazaTesoros con la id del código QR encontrado.
 */
public class DetectarQRActivity extends AppCompatActivity {

    /**
     * Código de respuesta con la ID de imagen obtenida.
     */
    public static final String QR_ID = "QR_ID";

    /**
     * Mensaje detectado.
     * Por defecto vacío.
     */
    private String cadena = "";

    /**
     * Último mensaje detectado.
     * Por defecto vacío; se usa para evitar repeticiones.
     */
    private String cadenaAnterior = "";

    /**
     * Crea la vista de la actividad.
     * Muestra la cámara preparada para detectar QR y crea el detector.
     * @param savedInstanceState Conjunto de datos del estado de la instancia.
     * @see DropContenedor#hacerFoto(CazaTesoros, int)
     * @see DetectarQRActivity#iniciarDetectorQR()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establecemos el layout
        setContentView(R.layout.activity_detectar_qr);
        // Toast que nos indica que está buscando QR
        Toast.makeText(this, "Searching QR!", Toast.LENGTH_LONG).show();
        // Iniciamos la detección
        iniciarDetectorQR();
    }

    /**
     * Inicia el detector QR.
     * Crea un detector de QR que actúa por debajo de la cámara y la muestra en la superficie
     * de la actividad. Cuando el detector detecta algo lo procesa.
     * @see DetectarQRActivity#onCreate(Bundle)
     * @see SurfaceHolderBarcode
     * @see DetectorProcessorBarcode
     */
    public void iniciarDetectorQR() {
        // Obtenemos el tamaño de la ventana
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;

        // Creamos un detector para códigos QR
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        // Establecemos la cámara con el detector y el tamaño de la ventana
        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(width, height)
                .setAutoFocusEnabled(true)
                .build();

        // Mostramos la cámara en la superficie
        SurfaceView surfaceView = findViewById(R.id.camera);
        surfaceView.getHolder().addCallback(new SurfaceHolderBarcode(cameraSource, surfaceView));

        // Establecemos el procesador de la detección
        barcodeDetector.setProcessor(new DetectorProcessorBarcode());
    }

    /**
     * Clase que establece la cámara en la superficie.
     * @see DetectarQRActivity#iniciarDetectorQR()
     */
    class SurfaceHolderBarcode implements SurfaceHolder.Callback {

        /**
         * La cámara con un detector por debajo.
         */
        private CameraSource cameraSource;

        /**
         * Una view de superficie para mostrar la cámara.
         */
        private SurfaceView surfaceView;

        /**
         * Constructor básico.
         * @param cameraSource La cámara con el detector.
         * @param surfaceView La superficie para mostrar la cámara.
         */
        public SurfaceHolderBarcode(CameraSource cameraSource, SurfaceView surfaceView) {
            this.cameraSource = cameraSource;
            this.surfaceView = surfaceView;
        }

        /**
         * Cuando se crea la superficie se inicia la cámara.
         * @param holder El controlador que exhibe la superficie.
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                cameraSource.start(surfaceView.getHolder());
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }

        /**
         * Si se cambia la superficie no hace nada.
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        /**
         * Cuando se destruye la superficie, se para la cámara.
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cameraSource.stop();
        }
    }

    /**
     * Clase que procesa la detección de un código QR.
     * @see DetectarQRActivity#iniciarDetectorQR()
     */
    class DetectorProcessorBarcode implements Detector.Processor<Barcode> {

        /**
         * Al terminar no hace nada.
         */
        @Override
        public void release() {}

        /**
         * Procesamos el resultado de una detección.
         * Comprobamos que se ha reconocido algo, y que el texto obtenido coincide con algun texto
         * válido. Finalmente se comprueba si es una ID válida de una imagen no desbloqueada.
         * @param detectado La lista de cosas que se han detectado.
         * @see Imagen#getDesbloqueada()
         * @see CazaTesoros#onActivityResult(int, int, Intent)
         */
        @Override
        public void receiveDetections(Detector.Detections<Barcode> detectado) {
            // Los códigos que ha detectado
            final SparseArray<Barcode> codigos = detectado.getDetectedItems();
            // Si hay algo lo procesamos
            if (codigos.size() > 0) {
                // Actualizamos el mensaje que se lee
                cadena = codigos.valueAt(0).displayValue;
                // Verificamos que el mensaje leido anteriormente no sea igual al actual
                // para evitar muchas llamadas con el mismo mensaje
                if (!cadena.equals(cadenaAnterior)) {
                    // Actualizamos
                    cadenaAnterior = cadena;
                    int id;
                    switch (cadena) {
                        case "QR1":
                            id = R.id.qr1;
                            break;
                        case "QR2":
                            id = R.id.qr2;
                            break;
                        case "QR3":
                            id = R.id.qr3;
                            break;
                        default:
                            id = -1;
                    }
                    if (id != -1) {
                        // Imagen de la ID
                        Imagen img = findViewById(id);
                        // Si no está desbloqueada, terminamos.
                        if (!img.getDesbloqueada()) {
                            // Intención de vuelta
                            Intent returnIntent = new Intent();
                            // Mandamos el código QR leido
                            returnIntent.putExtra(QR_ID, id);
                            // Código OK
                            setResult(Activity.RESULT_OK, returnIntent);
                            // Finalizamos
                            finish();
                        }
                    }
                }
            }

        }

    }
}