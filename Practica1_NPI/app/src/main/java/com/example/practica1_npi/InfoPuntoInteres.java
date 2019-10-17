package com.example.practica1_npi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class InfoPuntoInteres extends RelativeLayout {
    private TextView textInfo;
    private ImageView icon;

    private int imageWidth = 150;
    private int imageHeight = 150;

    public InfoPuntoInteres(Context context) {
        super(context);
        initialize();
    }

    public InfoPuntoInteres(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public InfoPuntoInteres(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize(){
        // Creo un textView
        textInfo = new TextView(this.getContext());

        ArrayList<String> info_text = new ArrayList();

        info_text.add("Elemento 1");
        info_text.add("Elemento 2");
        info_text.add("Elemento 3");

        setInfoText(info_text);

        // Creo el icono
        icon = new ImageView(this.getContext());

        Bitmap img= BitmapFactory.decodeResource(getResources(),R.drawable.info_icon);//image is your image
        img= Bitmap.createScaledBitmap(img, imageWidth,imageHeight,true);
        icon.setImageBitmap(img);

        // Añado el icono
        setIconView();
    }

    private void setInfoText(ArrayList<String> listInfo){
        String whole_text = new String();

        for (String info_elem : listInfo){
            whole_text += "\u2022 " + info_elem + "\n";
        }

        textInfo.setText(whole_text);
    }

    private void setIconView(){
        this.removeAllViews();
        this.setGravity(Gravity.RIGHT);
        this.addView(icon);
    }

    private void setTextInfoView(){
        this.removeAllViews();
        this.setGravity(Gravity.LEFT);
        this.addView(textInfo);
    }
}
