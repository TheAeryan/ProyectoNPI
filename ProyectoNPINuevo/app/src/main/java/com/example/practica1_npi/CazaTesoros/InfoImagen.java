package com.example.practica1_npi.CazaTesoros;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.practica1_npi.R;

/**
 * Dialog modificado.
 * Se muestra la información de una imagen desbloqueada que haya sido seleccionada, con un
 * recuadro por encima de la actividad CazaTesoros.
 */
public class InfoImagen extends Dialog implements android.view.View.OnClickListener {

    /**
     * ID de la imagen de la que se pide información.
     */
    private int idImagen;

    /**
     * Constructor básico.
     * @param activity La actividad donde se ha creado el dialogo.
     * @param idImagen La imagen de la que se muestra la información.
     */
    public InfoImagen(Activity activity, int idImagen) {
        super(activity);
        this.idImagen = idImagen;
    }

    /**
     * Cuando se crea el diálogo.
     * Se crea un recuadro con el texto de la imagen seleccionada y su imagen. Cuando se quiera
     * cerrar se pulsa el botón para quitar este dialogo.
     * @param savedInstanceState Conjunto de datos del estado de la instancia.
     * @see Imagen#onLongClick(View)
     * @see InfoImagen#establecerInformacion()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pide una ventana sin título
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Se establece el layout
        setContentView(R.layout.info_imagen);
        // Ponemos la información
        establecerInformacion();
        // Se pone a la escucha al botón
        Button ok = findViewById(R.id.boton_ok);
        ok.setOnClickListener(this);
    }

    /**
     * Pone el texto y la foto de la imagen seleccionada.
     * @see InfoImagen#onCreate(Bundle)
     */
    public void establecerInformacion() {
        String texto;
        int imagen;
        // Se toma el texto/imagen según la ID
        switch (idImagen) {
            case R.id.imagen1:
                texto = "Texto de prueba 1\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.i1;
                break;
            case R.id.imagen2:
                texto = "Texto de prueba 2\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.i2;
                break;
            case R.id.imagen3:
                texto = "Texto de prueba 3\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.i3;
                break;
            case R.id.imagen4:
                texto = "Texto de prueba 4\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.i4;
                break;
            case R.id.imagen5:
                texto = "Texto de prueba 5\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.i5;
                break;
            case R.id.imagen6:
                texto = "Texto de prueba 6\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.i6;
                break;
            case R.id.qr1:
                texto = "Texto de prueba 7\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.qr1;
                break;
            case R.id.qr2:
                texto = "Texto de prueba 8\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.qr2;
                break;
            case R.id.qr3:
                texto = "Texto de prueba 9\nTexto Texto\nTexto Texto\nTexto Texto \nTexto\nTexto\nTexto";
                imagen = R.drawable.qr3;
                break;
            default:
                Log.e("InfoImagen", "establecerTexto - No debería llegar aquí.");
                texto = "";
                imagen = -1;
        }
        // Se busca la zona de texto y se pone la información
        TextView text = findViewById(R.id.textoInfo);
        text.setText(texto);
        // Se busca la zona de imagen y se pone la imagen correspondiente
        ImageView img = findViewById(R.id.imagenInfo);
        img.setImageResource(imagen);
    }

    /**
     * Al hacer click en el botón se cierra el diálogo.
     * @param v EL botón donde se hace click.
     */
    @Override
    public void onClick(View v) {
        dismiss();
    }
}
