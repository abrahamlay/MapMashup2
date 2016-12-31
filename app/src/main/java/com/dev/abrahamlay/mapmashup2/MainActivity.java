package com.dev.abrahamlay.mapmashup2;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dev.abrahamlay.mapmashup2.util.GPSTracker;
import com.dev.abrahamlay.mapmashup2.util.NetworkSingleton;
import com.dev.abrahamlay.mapmashup2.util.ServiceHandler;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        YouTubePlayer.PlayerStateChangeListener,
        YouTubePlayer.OnFullscreenListener {
    //    ProgressDialog pDialog;
//    String url = "http://abrahamlay.esy.es/mapmashupservice/getPlaceList.php";
    private GoogleMap mMap;
    private GPSTracker gps;
    private double latitude;
    private double longitude;
    private Marker marker;
    ServiceHandler service = new ServiceHandler();
    String TAG = "MainActivity";

    public ImageLoader mImageLoader;

    private static final String YOUTUBE_FRAGMENT_TAG = "youtube";
    private boolean mIsFullScreen=false;
    private Toolbar mActionBarToolbar;
    private FloatingActionButton fab;
    private Button webViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        autocomplete();

        googlemaps();
        fabButton();

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

//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
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

    private void fabButton() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectUploadActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        webViewButton=(Button) findViewById(R.id.buttonWebView);
        webViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
////            case R.id.sign_in_button:
////                chooseAccount();
//                break;
////            case R.id.sign_out_button:
////                chooseAccount();
//                break;
//        }
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

//        mMap.getUiSettings().setMyLocationButtonEnabled(true);

//        new GoogleMap.OnMyLocationButtonClickListener() {
//
//            @Override
//            public boolean onMyLocationButtonClick() {
//
//                return false;
//            }
//        };

        service.GetJsonMarkerData(Constants.url_marker,TAG,MainActivity.this,mMap);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());


//        String data="{\"tempatwisata\":[{\"kodeTempatWisata\":\"1\",\"namaTempatWisata\":\"Gunung Bromo\",\"rating\":\"3\",\"Longitude\":\"-7.942092\",\"Latitude\":\"112.812924\",\"linkVideo\":\"n5N4eKT0UIw\",\"namaJenis\":\"Gunung\"}]}";

        gps = new GPSTracker(MainActivity.this);
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



        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
       String youtubeID = service.getYouTubeID(marker.getId());
        ViewVideo(youtubeID);
        Toast.makeText(this, "Video Play : "+marker.getTitle()+" "+youtubeID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    @Override
    public void onFullscreen(boolean b) {
        mIsFullScreen = b;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        showErrorToast(errorReason.toString());
    }


    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
//        private final LinearLayout mLinearLayout;
        private  View view;

        private NetworkImageView image;

        public CustomInfoWindowAdapter() {
//            Get the ImageLoader through your singleton class.
            mImageLoader = NetworkSingleton.getInstance(getApplicationContext()).getImageLoader();
            view = getLayoutInflater().inflate(R.layout.custom_info_marker,null );
        }


        @Override
        public View getInfoContents(Marker marker) {

            return contentCustomMarker(marker);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            if (MainActivity.this.marker != null
                    && MainActivity.this.marker.isInfoWindowShown()) {
                MainActivity.this.marker.hideInfoWindow();
                MainActivity.this.marker.showInfoWindow();
            }
            return null;
        }

        private View contentCustomMarker(Marker marker) {
            MainActivity.this.marker = marker;
            String youtubeID = service.getYouTubeID(marker.getId());
            String rating = service.getRating(marker.getId());

//
            image=(NetworkImageView) view.findViewById(R.id.thumbnail);
            final TextView titleUi = (TextView) view.findViewById(R.id.titleMarker);
            final TextView snippetUi = (TextView) view.findViewById(R.id.snippetMarker);
            RatingBar simpleRatingBar = (RatingBar) view.findViewById(R.id.myRatingBar); // initiate a rating bar
//            simpleRatingBar.setBackgroundColor(Color.); // set background color for a rating bar
            String thumburl=service.getThumbUrl(marker.getId());
//            Log.d(TAG,thumburl);
//            String thumburl=null;


                    if(thumburl!=null){
                        image.setImageUrl(thumburl, mImageLoader);
                    }else {
                        image.setImageDrawable(null);
                    }
                    if (marker.getTitle() != null) {
                        titleUi.setText(marker.getTitle());
                    } else {
                        titleUi.setText("Unknown Title");
                    }
//                    if (marker.getSnippet() != null) {
//                        snippetUi.setText("Rating :" + marker.getSnippet() + " " + youtubeID);
//                    } else {
//                        snippetUi.setText("Unknown Snippet");
//                    }

                    if (rating != null) {
                        simpleRatingBar.setRating(Float.valueOf(rating));
                    } else {
                        simpleRatingBar.setRating(0);
                    }
            return view;
        }

    }

    public void ViewVideo(final String youtubeId) {
        YouTubePlayerFragment playerFragment = YouTubePlayerFragment
                .newInstance();


        getFragmentManager()
                .beginTransaction()
                .replace(R.id.video_box, playerFragment,
                        YOUTUBE_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        playerFragment.initialize(Auth.KEY,
                new YouTubePlayer.OnInitializedListener() {
                    public YouTubePlayer mYouTubePlayer;

                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                        youTubePlayer.cueVideo(youtubeId);
                        mYouTubePlayer = youTubePlayer;
                        youTubePlayer
                                .setPlayerStateChangeListener(MainActivity.this);
                        youTubePlayer
                                .setOnFullscreenListener(MainActivity.this);

                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult result) {
                        showErrorToast(result.toString());
                    }
                });
    }
    private void showErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }


}


