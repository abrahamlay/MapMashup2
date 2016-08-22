package com.dev.abrahamlay.mapmashup2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.abrahamlay.mapmashup2.util.GPSTracker;
import com.dev.abrahamlay.mapmashup2.util.JSONParser;
import com.dev.abrahamlay.mapmashup2.util.MarkerData;
import com.dev.abrahamlay.mapmashup2.util.ServiceHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener ,View.OnClickListener{


    private GoogleMap mMap;
    private GPSTracker gps;
    private double latitude;
    private double longitude;

    private TextView displayName;
    private ImageView photo;
    private SignInButton login;
    private Button logout;

    String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autocomplete();

        googlemaps();
        fabButton();
        navigationdrawer();


//        GoogleSignIn(savedInstanceState);

    }

    private void googlemaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void autocomplete() {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build();

        final SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(autocompleteFragment.getTag(), "Place: " + place.getName());

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(autocompleteFragment.getTag(), "An error occurred: " + status);
            }
        });
    }

    private void setup(NavigationView navigationView) {
        View header = navigationView.getHeaderView(0);

        displayName = (TextView) header.findViewById(R.id.status);
//        email = (TextView) header.findViewById(R.id.email);
        photo = (ImageView) header.findViewById(R.id.ProfilPicture);

        // Button listeners
        login = (SignInButton) header.findViewById(R.id.sign_in_button);
        logout = (Button) header.findViewById(R.id.sign_out_button);
    }



    private void navigationdrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setup(navigationView);
    }

    private void fabButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (Accountname!=null) {
                    Intent intent = new Intent(getApplicationContext(), VideoListActivity.class);
//                    intent.putExtra("Account", Accountname);
////                intent.putExtra("displayName",name);
                    startActivity(intent);
//                }else{
//                    signIn();
//                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    private void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage(getString(R.string.loading));
//            mProgressDialog.setIndeterminate(true);
//        }
//
//        mProgressDialog.show();
//    }
//
//    private void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.hide();
//        }
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
//                chooseAccount();
                break;
            case R.id.sign_out_button:
//                chooseAccount();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.video_list_item, null);
        // create class object
        gps = new GPSTracker(MainActivity.this);

        ServiceHandler service= new ServiceHandler();

        service.GetData("http://abrahamlay.esy.es/mapmashupservice/getPlaceList.php");
//        String data = null;
//        try {
//            data = service.GetData("http://abrahamlay.esy.es/mapmashupservice/getPlaceList.php");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        String data="{\"tempatwisata\":[{\"kodeTempatWisata\":\"1\",\"namaTempatWisata\":\"Gunung Bromo\",\"rating\":\"3\",\"Longitude\":\"-7.942092\",\"Latitude\":\"112.812924\",\"linkVideo\":\"n5N4eKT0UIw\",\"namaJenis\":\"Gunung\"}]}";
        JSONParser jsonparsing= new JSONParser();
        Log.d(TAG,"JSON: "+data);

        List<MarkerData> marker=jsonparsing.getMarker(data);
        for(int i=0;i<marker.size();i++){
            LatLng region = new LatLng(marker.get(i).getLongitude(), marker.get(i).getLatitude());
            Log.d(TAG,"Marker pos :"+marker.get(i).getLongitude()+","+marker.get(i).getLatitude());
            mMap.addMarker(new MarkerOptions()
                    .position(region)
                    .title(marker.get(i).getNamaTempatWisata()+" " + marker.get(i).getLatitude() + "," + marker.get(i).getLongitude())
            );

        }
        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            // Add a marker in Sydney, Australia, and move the camera.
            LatLng region = new LatLng(latitude, longitude);
            Log.d(TAG,"Marker GPS pos :"+latitude+","+longitude);

            mMap.addMarker(new MarkerOptions()
                    .position(region)
                    .title("Kota X /n" + latitude + "," + longitude)
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(region));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
}
