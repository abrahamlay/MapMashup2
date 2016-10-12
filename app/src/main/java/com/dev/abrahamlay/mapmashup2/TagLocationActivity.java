package com.dev.abrahamlay.mapmashup2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dev.abrahamlay.mapmashup2.util.GPSTracker;
import com.dev.abrahamlay.mapmashup2.util.ServiceHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class TagLocationActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private GPSTracker gps;
    private double latitude;
    private double longitude;
    private String TAG="TagLocationActivity";
    private Intent intent;
    public static final String nodeNama="namaTempatWisata";
    public static final String nodeJenis="jenis";
    public static final String nodeKodeJenis="kodejenis";
    public static final String nodeLongitude="longitude";
    public static final String nodeLatitude="latitude";
    private EditText namaTempatWisata;
    private Spinner jenis;
    private ServiceHandler service= new ServiceHandler();
    private String url="http://abrahamlay.esy.es/mapmashupservice/getJenis.php";
    private ArrayList<String> jenisList;
    private String namaJenis;
    private long kodeJenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_location);
        googlemaps();
        setupform();
        intent = getIntent();
//        String youtubeId = intent.getStringExtra(VideoListActivity.YOUTUBE_ID);
    }

    private void googlemaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTagLocation);
        mapFragment.getMapAsync(this);

    }

    private void setupform(){

        namaTempatWisata=(EditText) findViewById(R.id.namaTempatWisata);
        jenis=(Spinner) findViewById(R.id.jenis);
        jenis.setOnItemSelectedListener(this);
        service.GetJsonSpinnerJenis(url,TAG,TagLocationActivity.this,jenis);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        mMap.setMyLocationEnabled(true);

        gps = new GPSTracker(TagLocationActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            LatLng region = new LatLng(latitude, longitude);
            Log.d(TAG,"Marker GPS pos :"+latitude+","+longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(region));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


    }

    public void submitTagLocation(View view){
        if(namaTempatWisata.getText()!=null && namaJenis!="" && kodeJenis!=0){
        intent.putExtra(nodeNama,namaTempatWisata.getText().toString());
        intent.putExtra(nodeKodeJenis,kodeJenis);
            intent.putExtra(nodeJenis,namaJenis);
        intent.putExtra(nodeLongitude,longitude);
        intent.putExtra(nodeLatitude,latitude);
        this.setResult(RESULT_OK,intent);
        finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        namaJenis = adapterView.getItemAtPosition(i).toString();
        kodeJenis =adapterView.getItemIdAtPosition(i);
        kodeJenis=kodeJenis+1;
        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: "+kodeJenis+" "+ namaJenis, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
