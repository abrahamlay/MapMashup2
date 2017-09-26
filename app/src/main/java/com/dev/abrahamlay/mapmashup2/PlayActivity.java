package com.dev.abrahamlay.mapmashup2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.abrahamlay.mapmashup2.util.VideoData;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

public class PlayActivity extends AppCompatActivity implements YouTubePlayer.PlayerStateChangeListener,
        YouTubePlayer.OnFullscreenListener, ToolTipView.OnToolTipViewClickedListener {

    private static final String YOUTUBE_FRAGMENT_TAG = "youtube";
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
    GoogleAccountCredential credential;
    private YouTubePlayer mYouTubePlayer;
    private boolean mIsFullScreen = false;
    private Intent intent;

    private String TAG="PlayActivity";
    private static final int REQUEST_TAG_LOCATION=10;
    private TextView reviewNama;
    private TextView reviewJenis;
    private LinearLayout location;
    private LinearLayout position;
    private LinearLayout reviewTagLocation;
    private TextView reviewLongitude;
    private TextView reviewLatitude;
    private TextView reviewTrigger;
    private String nama;
    private String jenis;
    private double latitude;
    private double longitude;
    private long kodeJenis;
    private float tagRating;
    private ToolTipView myToolTipView;

    public PlayActivity() {
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void directLite(View view) {
        intent.putExtra(TagLocationActivity.nodeNama,nama);
        intent.putExtra(TagLocationActivity.nodeKodeJenis,kodeJenis);
        intent.putExtra(TagLocationActivity.nodeJenis,jenis);
        intent.putExtra(TagLocationActivity.nodeLongitude,longitude);
        intent.putExtra(TagLocationActivity.nodeLatitude,latitude);
        intent.putExtra(TagLocationActivity.nodeRating,tagRating);
        this.setResult(RESULT_OK, intent);
        finish();
    }

    public void panToVideo(final String youtubeId) {
        popPlayerFromBackStack();
        YouTubePlayerFragment playerFragment = YouTubePlayerFragment
                .newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, playerFragment,
                        YOUTUBE_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();
        playerFragment.initialize(Auth.KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(youtubeId);
                        mYouTubePlayer = youTubePlayer;
                        youTubePlayer
                                .setPlayerStateChangeListener(PlayActivity.this);
                        youTubePlayer
                                .setOnFullscreenListener(PlayActivity.this);
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult result) {
                        showErrorToast(result.toString());
                    }
                });
    }

    public boolean popPlayerFromBackStack() {
        if (mIsFullScreen) {
            mYouTubePlayer.setFullscreen(false);
            return false;
        }
        if (getFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG) != null) {
            getFragmentManager().popBackStack();
            return false;
        }
        return true;
    }

    @Override
    public void onAdStarted() {
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        showErrorToast(errorReason.toString());
    }

    private void showErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onLoaded(String arg0) {
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onVideoEnded() {
        // popPlayerFromBackStack();
    }

    @Override
    public void onVideoStarted() {
    }

    @Override
    public void onFullscreen(boolean fullScreen) {
        mIsFullScreen = fullScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_play);
        setupReview();
        intent = getIntent();
        Button submitButton = (Button) findViewById(R.id.submit_button);
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            submitButton.setVisibility(View.GONE);
            setTitle(R.string.playing_uploaded_video);
        }
        String youtubeId = intent.getStringExtra(AccountVideoListActivity.YOUTUBE_ID);
        panToVideo(youtubeId);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_TAG_LOCATION:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    location.setVisibility(View.VISIBLE);
                    position.setVisibility(View.VISIBLE);

                    reviewTrigger.setText("My Location :");
                    nama=data.getExtras().getString(TagLocationActivity.nodeNama);
                    jenis=data.getExtras().getString(TagLocationActivity.nodeJenis);
                    kodeJenis=data.getExtras().getLong(TagLocationActivity.nodeKodeJenis);
                    longitude=data.getExtras().getDouble(TagLocationActivity.nodeLongitude);
                    latitude =data.getExtras().getDouble(TagLocationActivity.nodeLatitude);
                    tagRating =data.getExtras().getFloat(TagLocationActivity.nodeRating);

                    reviewNama.setText(nama);
                    reviewJenis.setText(jenis);
                    reviewLongitude.setText(String.valueOf(longitude));
                    reviewLatitude.setText(String.valueOf(latitude));
                }


        }
    }

    private void setupReview(){

        reviewTagLocation= (LinearLayout) findViewById(R.id.reviewTagLocation);
        location= (LinearLayout) findViewById(R.id.location1);
        position= (LinearLayout) findViewById(R.id.position);
        location.setVisibility(View.GONE);
        position.setVisibility(View.GONE);

        reviewTrigger=(TextView) findViewById(R.id.tagLocationTrigger);
        reviewNama=(TextView) findViewById(R.id.reviewNamaTWisata);
        reviewJenis=(TextView) findViewById(R.id.reviewJenis);
        reviewLongitude=(TextView) findViewById(R.id.reviewLongitude);
        reviewLatitude=(TextView) findViewById(R.id.reviewLatitude);
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_play_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("Tag a tour place location")
                .withTextColor(ContextCompat.getColor(getApplicationContext(),R.color.text))
                .withColor(ContextCompat.getColor(getApplicationContext(),R.color.bg_screen3))
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.reviewTagLocation));
        myToolTipView.setOnToolTipViewClickedListener(PlayActivity.this);
    }


    public void tagLocation(View view) {
        Intent i=new Intent(this,TagLocationActivity.class);
        startActivityForResult(i , REQUEST_TAG_LOCATION);
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {

    }

    public interface Callbacks {

        public void onVideoSelected(VideoData video);

        public void onResume();

    }
}
