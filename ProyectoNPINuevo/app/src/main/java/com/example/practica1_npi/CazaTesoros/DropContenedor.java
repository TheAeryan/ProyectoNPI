package com.example.practica1_npi.CazaTesoros;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.practica1_npi.R;

/**
 * ConstraintLayout modificado.
 * Representa un contenedor donde se puede soltar una imagen con una acción de arrastrar y soltar.
 * Si se suelta en el de "Clue" da una pista y en "Scan" activa el escaneo.
 */
public class DropContenedor extends ConstraintLayout implements View.OnDragListener {

    /**
     * Constructor básico.
     * @param context El contexto donde se crea el objeto.
     */
    public DropContenedor(Context context) {
        super(context);
        // A la escucha del arrastrar y soltar
        setOnDragListener(this);
    }

    /**
     * Constructor con XML
     * @param context El contexto donde se crea el objeto.
     * @param attrs Los atributos del objeto.
     */
    public DropContenedor(Context context, AttributeSet attrs) {
        super(context, attrs);
        // A la escucha del arrastrar y soltar
        setOnDragListener(this);
    }

    /**
     * Constructor con XML y estilo.
     * @param context El contexto donde se crea el objeto.
     * @param attrs Los atributos del objeto.
     * @param defStyleAttr El estilo del objeto.
     */
    public DropContenedor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // A la escucha del arrastrar y soltar
        setOnDragListener(this);
    }

    /**
     * Cuando se produce una acción de arrastrar y soltar.
     * Cuando una imagen toca el contenedor, cambia de color.
     * Cuando la imagen se suelta dentro del contenedor, si es "Scan" llamará a la
     * cámara/código QR; y si es "Clue", dará una pista.
     * @param view La view del objeto que escucha.
     * @param event El evento de arrastre y soltar.
     * @see DropContenedor#hacerFoto(CazaTesoros, int)
     * @see DropContenedor#darPista(CazaTesoros, int)
     * @see Imagen#onLongClick(View)
     * @return Si se ha consumido el evento de arrastre.
     */
    @Override
    public boolean onDrag(View view, DragEvent event) {
        // Tipo de acción que se está produciendo
        int action = event.getAction();
        switch (action) {
            // Arrastre comenzado
            case DragEvent.ACTION_DRAG_STARTED:
                // Si coincide la información se puede recibir información
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
            // Cuando la imagen entra en el contenedor
            case DragEvent.ACTION_DRAG_ENTERED:
                // Fondo de amarillo
                getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                // Invalidamos para redibujar
                invalidate();
                return true;
            // Ignoramos esto
            case DragEvent.ACTION_DRAG_LOCATION:
                return true;
            // Arrastre terminado
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                // Reseteamos el color
                getBackground().clearColorFilter();
                // Invalidamos para redibujar
                invalidate();
                return true;
            // Imagen soltada en el contenedor
            case DragEvent.ACTION_DROP:
                // Información del arrastre
                ClipData.Item item = event.getClipData().getItemAt(0);
                // ID de la imagen soltada
                int idActual = Integer.parseInt(item.getText().toString());
                // Actualizamos la idActual de CazaTesoros
                CazaTesoros activity = (CazaTesoros) getContext();
                activity.setIDActual(idActual);
                // Reseteamos el color
                getBackground().clearColorFilter();
                // Invalidamos para redibujar
                invalidate();
                // Si el contenedor es "Scan", hacemos foto
                if (this == activity.findViewById(R.id.scan))
                    hacerFoto(activity, idActual);
                // Si es "Clue", damos pista
                else if (this == activity.findViewById(R.id.clue))
                    darPista(activity, idActual);
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Lanza actividad a la cámara o al detector QR.
     * Mira la imagen seleccionada con idActual y si es QR lanza una actividad al detector QR;
     * si no entonces una actividad a la cámara.
     * @param activity La actividad CazaTesoros.
     * @param idActual La id de la imagen seleccionada.
     * @see DropContenedor#onDrag(View, DragEvent)
     * @see CazaTesoros#BARCODE_RESULT
     * @see CazaTesoros#CAMERA_RESULT
     * @see Imagen#getEsQR()
     * @see DetectarQRActivity
     */
    private void hacerFoto(CazaTesoros activity, int idActual) {
        // Imagen actual
        Imagen img = activity.findViewById(idActual);
        Intent it;
        // Codigo de intent
        int codigo;
        // Si es QR, el detector QR
        if (img.getEsQR()) {
            // Intención y código detector QR
            it = new Intent(activity, DetectarQRActivity.class);
            codigo = CazaTesoros.BARCODE_RESULT;
        // Si no, la cámara
        } else {
            // Intención y código cámara
            it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            codigo = CazaTesoros.CAMERA_RESULT;
        }
        // Empezamos actividad con resultado
        activity.startActivityForResult(it, codigo);
    }

    /**
     * Da una pista de la imagen seleccionada.
     * Lanza un toast con una pista para encontrar el código QR seleccionado.
     * @param activity La actividad CazaTesoros.
     * @param idActual La id de la imagen seleccionada.
     * @see DropContenedor#onDrag(View, DragEvent)
     */
    private void darPista(CazaTesoros activity, int idActual) {
        String pista = "";
        // Pista según la imagen
        switch (idActual) {
            case R.id.imagen1:
                pista = "Busca por algun sitio del principio";
                break;
            case R.id.imagen2:
                pista = "Busca por algun sitio del final";
                break;
            case R.id.imagen3:
                pista = "Busca por algun sitio entre medias";
                break;
            case R.id.imagen4:
                pista = "Busca por algun sitio de por dentro";
                break;
            case R.id.imagen5:
                pista = "Busca por algun sitio de por arriba";
                break;
            case R.id.imagen6:
                pista = "Busca por algun sitio de la pared";
                break;
            case R.id.qr1:
                pista = "GRAAAAAAAAAAAAAAAUR!";
                break;
            case R.id.qr2:
                pista = "Tolon tolon tolon tolon!";
                break;
            case R.id.qr3:
                pista = "Muy circular esto.";
                break;
        }
        // Toast con la pista
        Toast.makeText(activity, pista, Toast.LENGTH_LONG).show();
    }
}
