package com.example.practica1_npi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationProviderClient; // Usa el GPS y/o WIFI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setContentView(R.layout.mostrar_localizacion);

        connectLocationClient();
        getLocation();
    }

    protected void connectLocationClient(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    protected void getLocation(){

        try{
            //if (mLocationPermissionsGranted){

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            // Se ha podido obtener la localización

                            // <Localización>
                            Location currentLocation = (Location) task.getResult();

                            // Latitud y longitud
                            double latitud = currentLocation.getLatitude();
                            double longitud = currentLocation.getLongitude();

                            // Lo muestro en la vista
                            TextView texto = (TextView)findViewById(R.id.texto_localizacion);
                            texto.setText(Double.toString(latitud) + " " + Double.toString(longitud));
                        }else{
                            // No se ha podido obtener la localización
                            TextView texto = (TextView)findViewById(R.id.texto_localizacion);
                            texto.setText("<ERROR>");
                        }
                    }
                });

            //}

        }catch(SecurityException e){ // No tiene permisos
            TextView texto = (TextView)findViewById(R.id.texto_localizacion);
            texto.setText("<PERMISOS REQUERIDOS>");
        }
    }
}
