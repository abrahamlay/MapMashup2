package com.dev.abrahamlay.mapmashup2;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dev.abrahamlay.mapmashup2.util.NetworkSingleton;
import com.dev.abrahamlay.mapmashup2.util.ServiceHandler;
import com.dev.abrahamlay.mapmashup2.util.Utils;
import com.dev.abrahamlay.mapmashup2.util.VideoData;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dev.abrahamlay.mapmashup2.AccountVideoListActivity.ACCOUNT_KEY;
import static com.dev.abrahamlay.mapmashup2.AccountVideoListActivity.YOUTUBE_ID;

public class SearchVideoListActivity extends AppCompatActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {

    private static final int REQ_CODE=123;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    private static final int REQUEST_GMS_ERROR_DIALOG = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final int REQUEST_AUTHORIZATION = 3;
    private static final int RESULT_PICK_IMAGE_CROP = 4;
    private static final int RESULT_VIDEO_CAP = 5;
    private static final int REQUEST_DIRECT_TAG = 6;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
    private EditText searchInput;
    private Button searchButton;
    private ListView videosFound;
    private GoogleAccountCredential credential;
    private String mChosenAccountName;
    private ProgressDialog progress;
    private YouTube.Search.List query;
    private static final long NUMBER_OF_VIDEOS_RETURNED=10;
    private ImageLoader mImageLoader;
    private String TAG="SearchVideoList";
    private ToolTipView myToolTipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video_list);
        checkPermissionContacts();
        ensureLoader();
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(Auth.SCOPES));

        credential.setBackOff(new ExponentialBackOff());

        if (savedInstanceState != null) {
            mChosenAccountName = savedInstanceState.getString(ACCOUNT_KEY);
            Log.d("AccountName","AccountName : "+mChosenAccountName);
        } else {
            loadAccount();
        }

        credential.setSelectedAccountName(mChosenAccountName);
        searchInput=(EditText) findViewById(R.id.search_input);
        searchButton=(Button) findViewById(R.id.search_button);
        videosFound = (ListView)findViewById(R.id.video_found_list);

        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_search_video_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("Search video with keyword")
                .withTextColor(ContextCompat.getColor(getApplicationContext(),R.color.text))
                .withColor(ContextCompat.getColor(getApplicationContext(),R.color.bg_screen4))
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.search_input));
        myToolTipView.setOnToolTipViewClickedListener(SearchVideoListActivity.this);

        searchButton.setOnClickListener(this);

    }

    private void checkPermissionContacts(){
        if (ContextCompat.checkSelfPermission(SearchVideoListActivity.this, android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchVideoListActivity.this,new String[]{ android.Manifest.permission.GET_ACCOUNTS},REQ_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == REQ_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can submit your youtube video by search video",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ensureLoader() {
        if (mImageLoader == null) {
            // Get the ImageLoader through your singleton class.
            mImageLoader = NetworkSingleton.getInstance(this).getImageLoader();
        }
    }

    private void loadAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        mChosenAccountName = sp.getString(ACCOUNT_KEY, null);
        Log.d("AccountName","Load AccountName : "+mChosenAccountName);
        if(mChosenAccountName==null){
            chooseAccount();
        }
        invalidateOptionsMenu();
    }

    private void saveAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        sp.edit().putString(ACCOUNT_KEY, mChosenAccountName).commit();


    }

    private void loadData(String keywords) {
        if (mChosenAccountName == null) {
            return ;
        }

        SearchVideoOnYoutube(keywords);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GMS_ERROR_DIALOG:
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != Activity.RESULT_OK) {
                    chooseAccount();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(
                            AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mChosenAccountName = accountName;
                        credential.setSelectedAccountName(accountName);
                        saveAccount();
                    }
                }
                break;
            case REQUEST_DIRECT_TAG:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String youtubeId = data.getStringExtra(YOUTUBE_ID);
                    String nama = data.getExtras().getString(TagLocationActivity.nodeNama);
                    long jenis = data.getExtras().getLong(TagLocationActivity.nodeKodeJenis);
                    double longitude = data.getExtras().getDouble(TagLocationActivity.nodeLongitude);
                    double latitude =data.getExtras().getDouble(TagLocationActivity.nodeLatitude);
                    float rating =data.getExtras().getFloat(TagLocationActivity.nodeRating);
                    directTag(youtubeId,nama,jenis,longitude,latitude,mChosenAccountName,rating);

                }
                break;
        }
    }

    private void directTag(String Youtubeid, String nama, long jenis, double longitude, double latitude, String mAccount,float rating) {
        ServiceHandler sc= new ServiceHandler();

        sc.PostTempatWisataData(Constants.url_newtag,TAG,SearchVideoListActivity.this,nama,jenis,longitude,latitude,mAccount,Youtubeid, rating);

        Log.d(TAG,"Lokasi :"+nama+" "+jenis+" "+longitude+" "+latitude+" "+mAccount+" "+Youtubeid);

        Intent intent= new Intent(SearchVideoListActivity.this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCOUNT_KEY, mChosenAccountName);
    }

    private void SearchVideoOnYoutube(final String keywords) {
        if (mChosenAccountName == null) {
            Toast.makeText(getApplicationContext(),"No Account Yet.. ",Toast.LENGTH_LONG).show();
//            chooseAccount();
            return;
        }
        setProgressBarIndeterminateVisibility(true);
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading_video));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        new AsyncTask<Void, Void, List<VideoData>>() {
            @Override
            protected List<VideoData> doInBackground(Void... voids) {


                YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                        credential).setApplicationName(Constants.APP_NAME)
                        .build();
                        try{
                            query=youtube.search().list("id,snippet");
                            query.setPart("id,snippet");
                            query.setType("video");
                            query.setQ(keywords);
//                            query.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                            query.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                            SearchListResponse searchVideoResponse= query.execute();
                                List<SearchResult> result=searchVideoResponse.getItems();
                                List<VideoData> videoDataList= new ArrayList<VideoData>();
                                List<String> videoIds = new ArrayList<String>();

                                for(SearchResult videolist:result){
                                   videoIds.add(videolist.getId().getVideoId());
                                   }

                            VideoListResponse vlr = youtube.videos()
                                    .list("id,snippet,status")
                                    .setId(TextUtils.join(",", videoIds)).execute();

                                for(Video video:vlr.getItems()){
                                    VideoData videoData=new VideoData();
                                    videoData.setVideo(video);
                                    videoDataList.add(videoData);
                                    Log.d("Search ","Video :"+video.getId() +"ThumbUrl : "+video.getSnippet().getThumbnails().getDefault());
                                }
                            return videoDataList;
                        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                            showGooglePlayServicesAvailabilityErrorDialog(availabilityException
                                    .getConnectionStatusCode());
                        } catch (UserRecoverableAuthIOException userRecoverableException) {
                            startActivityForResult(
                                    userRecoverableException.getIntent(),
                                    REQUEST_AUTHORIZATION);
                        } catch (IOException e) {
                            Utils.logAndShow(SearchVideoListActivity.this, Constants.APP_NAME, e);
                        }
                            return null;
            }

            @Override
            protected void onPostExecute(List<VideoData> videos) {
                setProgressBarIndeterminateVisibility(false);
                if (progress.isShowing()) {
                    progress.setIndeterminate(false);
                    progress.dismiss();
                    if (videos == null) {
                        return;
                    }
                }

                updateVideosFound(videos);
//                mUploadsListFragment.setVideos(videos);
            }
        }.execute((Void) null);
    }

    private void updateVideosFound(final List<VideoData> searchResults){
        final ArrayAdapter<VideoData> adapter = new ArrayAdapter<VideoData>(getApplicationContext(), R.layout.list_search_video, searchResults){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_search_video, parent, false);
                }
                NetworkImageView thumbnail = (NetworkImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                TextView description = (TextView)convertView.findViewById(R.id.video_description);

                VideoData searchResult = searchResults.get(position);

//                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                thumbnail.setImageUrl(searchResult.getThumbUri(),mImageLoader);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };
        AdapterView.OnItemClickListener clickListener=new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SearchVideoListActivity.this,"Video : "+ searchResults.get(i).getTitle(),Toast.LENGTH_SHORT);
                Intent intent = new Intent(SearchVideoListActivity.this, PlayActivity.class);
                intent.putExtra(YOUTUBE_ID, searchResults.get(i).getYouTubeId());
                startActivityForResult(intent, REQUEST_DIRECT_TAG);
            }
        };
       videosFound.setOnItemClickListener(clickListener);
       videosFound.setAdapter(adapter);
    }



    @Override
    public void onClick(View view) {
        String keyword=searchInput.getText().toString();
        loadData(keyword);
        Toast.makeText(this,"Here is keyword :" +keyword,Toast.LENGTH_LONG);
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,SearchVideoListActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }
    /**
     * Check that Google Play services APK is installed and up to date.
     */
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {

    }
}
