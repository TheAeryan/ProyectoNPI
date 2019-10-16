package com.example.practica1_npi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.location.Location;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ShowLocationActivity extends AppCompatActivity{
    private TextView txtLocation;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private int updateInterval = 2000; // Cada cuánto se actualiza la posición (en milisegundos)

    private int ind_llamada = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_location);

        this.txtLocation = (TextView) findViewById(R.id.txtLocation);

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
                    txtLocation.setText(Double.toString(wayLatitude) + ", " +
                            Double.toString(wayLongitude));
                }
            });
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(updateInterval);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();

                    float accuracy = location.getAccuracy();
                    float speed = location.getSpeed();

                    txtLocation.setText(Integer.toString(ind_llamada) + " - " +
                            Double.toString(wayLatitude) + ", " +
                            Double.toString(wayLongitude) + "\nPrecisión (m): " +
                            Float.toString(accuracy) + "\nVelocidad (m/s): " +
                            Float.toString(speed));

                    ind_llamada++;
                }
            };
        };

        // Periódicamente obtenemos la localización
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    // Dejar de obtener la localización si la aplicación está en segundo plano
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Método que se ejecuta tras pedir los permisos
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
                            txtLocation.setText(Double.toString(wayLatitude) + ", " +
                                    Double.toString(wayLongitude));
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    // Acción del botón
    public void requestLocation(View view){
        // Obtengo la localización y la escribo

        Task location = mFusedLocationClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    // Se ha podido obtener la localización
                    Location currentLocation = (Location) task.getResult();

                    wayLatitude = currentLocation.getLatitude();
                    wayLongitude = currentLocation.getLongitude();
                    txtLocation.setText(Integer.toString(ind_llamada) + " - " +
                            Double.toString(wayLatitude) + ", " +
                            Double.toString(wayLongitude));

                    ind_llamada++;
                }else{
                    // No se ha podido obtener la localización
                    txtLocation.setText("<<ERROR>>");
                }
            }
        });
    }

}
