package com.uagrm.informatica.johana.radar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.Manifest;


import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private static final String TAG = "mapa";
    private GoogleMap mMap;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Location mLocation;
    private DatabaseReference myRef;
    private ArrayList<UserInfo> userInfos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        checkUsuarioIdentificado();

        myRef= FirebaseDatabase.getInstance().getReference("ubicaciones");


        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            ubicacion();
        }



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        inicializarEventListenerDataBase();

    }

    private void inicializarEventListenerDataBase() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                mMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    drawMarker(snapshot.getValue(UserInfo.class));
                    i++;
                }
                printLog("Firebase dataChange cant userInfo recibidas: "+i);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void drawMarker(UserInfo userInfo) {
        LatLng latLng = new LatLng(userInfo.latitud, userInfo.longitud);

        mMap.addMarker(new MarkerOptions().position(latLng).title(userInfo.name));

        if (userInfo.uid.equals(user.getUid()))
        {
            moverCamaraToLocation(userInfo.latitud,userInfo.longitud);
        }
    }


    private void ubicacion() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 20, (LocationListener) Local);


    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ubicacion();
                return;
            }
        }
    }

    public class Localizacion implements LocationListener {
        MapsActivity mainActivity;
        public void setMainActivity(MapsActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                mLocation=loc;
                printLog("onLocationChanged");
                UserInfo info=new UserInfo(user.getUid(),user.getDisplayName(),
                        user.getPhotoUrl()+"",loc.getLatitude(),loc.getLongitude(),"");
                myRef.child(user.getUid()).setValue(info);
                moverCamaraToLocation(loc.getLatitude(),loc.getLongitude());
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast mensajeGPS = Toast.makeText(getApplicationContext(), "GPS Desactivado", Toast.LENGTH_SHORT);
            mensajeGPS.setGravity(Gravity.CENTER, 0, 0);
            mensajeGPS.show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast mensajeGPS = Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_SHORT);
            mensajeGPS.setGravity(Gravity.CENTER, 0, 0);
            mensajeGPS.show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    private void addMarcador(double lat, double log,String title,String color)
    {


    }

    private void moverCamaraToLocation(double lat, double log) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,log)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }


    private void checkUsuarioIdentificado() {
        if (user == null) {
           gotoLoginActivity();
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        String uid = user.getUid();



        printLog("\t"+name +" "+ email+" "+photoUrl+" "+uid);
    }

    private void printLog(String s) {
        Log.i(TAG,s);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    public void cerrarSesion(View view) {

        auth.signOut();
        gotoLoginActivity();
    }

    private void gotoLoginActivity() {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

}
