package com.dev.abrahamlay.mapmashup2;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.dev.abrahamlay.mapmashup2.util.ChannelData;
import com.dev.abrahamlay.mapmashup2.util.NetworkSingleton;
import com.dev.abrahamlay.mapmashup2.util.Utils;
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
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dev.abrahamlay.mapmashup2.AccountVideoListActivity.ACCOUNT_KEY;

public class ChannelListActivity extends AppCompatActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    private static final int REQUEST_GMS_ERROR_DIALOG = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final int REQUEST_AUTHORIZATION = 3;
    private static final int RESULT_PICK_IMAGE_CROP = 4;
    private static final int RESULT_VIDEO_CAP = 5;
    private static final int REQUEST_DIRECT_TAG = 6;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
    private GoogleAccountCredential credential;
    private String mChosenAccountName;
    private EditText searchChannelInput;
    private Button searchChannelButton;
    private ListView channelFound;
    private ImageLoader mImageLoader;
    private ProgressDialog progress;
    private YouTube.Search.List query;
    private static final long NUMBER_OF_CHANNEL_RETURNED=25;
    private ToolTipView myToolTipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

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
        searchChannelInput=(EditText) findViewById(R.id.search_channel_input);
        searchChannelButton=(Button) findViewById(R.id.search_channel_button);
        channelFound = (ListView)findViewById(R.id.channel_found_list);
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_search_channel_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("Search channel with keyword")
                .withTextColor(ContextCompat.getColor(getApplicationContext(),R.color.text))
                .withColor(ContextCompat.getColor(getApplicationContext(),R.color.bg_screen4))
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.search_channel_input));
        myToolTipView.setOnToolTipViewClickedListener(ChannelListActivity.this);

        searchChannelButton.setOnClickListener(this);

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


    private void ensureLoader() {
        if (mImageLoader == null) {
            // Get the ImageLoader through your singleton class.
            mImageLoader = NetworkSingleton.getInstance(this).getImageLoader();
        }
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
//            case REQUEST_DIRECT_TAG:
//                if (resultCode == Activity.RESULT_OK && data != null
//                        && data.getExtras() != null) {
//                    String youtubeId = data.getStringExtra(YOUTUBE_ID);
//                    String nama = data.getExtras().getString(TagLocationActivity.nodeNama);
//                    long jenis = data.getExtras().getLong(TagLocationActivity.nodeKodeJenis);
//                    double longitude = data.getExtras().getDouble(TagLocationActivity.nodeLongitude);
//                    double latitude =data.getExtras().getDouble(TagLocationActivity.nodeLatitude);
//                    directTag(youtubeId,nama,jenis,longitude,latitude,mChosenAccountName);
//
//                }
//                break;
        }
    }


    private void loadData(String keywords) {
        if (mChosenAccountName == null) {
            return ;
        }

        SearchChannelOnYoutube(keywords);
    }

    private void SearchChannelOnYoutube(final String keywords) {
        if (mChosenAccountName == null) {
            Toast.makeText(getApplicationContext(),"No Account Yet.. ",Toast.LENGTH_LONG);
//            chooseAccount();
            return;
        }
        setProgressBarIndeterminateVisibility(true);
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading_channel));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        new AsyncTask<Void, Void, List<ChannelData>>() {

            @Override
            protected List<ChannelData> doInBackground(Void... voids) {

                YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                        credential).setApplicationName(Constants.APP_NAME)
                        .build();
                try{
                    query=youtube.search().list("id,snippet");
                    query.setPart("id,snippet");
                    query.setType("channel");
                    query.setQ(keywords);
                    query.setMaxResults(NUMBER_OF_CHANNEL_RETURNED);
                    SearchListResponse searchChannelResponse= query.execute();
                    List<SearchResult> result=searchChannelResponse.getItems();
                    List<ChannelData> ChannelDataList= new ArrayList<ChannelData>();
                    List<String> ChannelIds = new ArrayList<String>();

                    for(SearchResult channellist:result){
                        ChannelIds.add(channellist.getId().getChannelId());
                    }

                    ChannelListResponse clr = youtube.channels()
                            .list("id,snippet")
                            .setId(TextUtils.join(",", ChannelIds)).execute();
                    for(Channel channel:clr.getItems()){
                        ChannelData channelData=new ChannelData();
                        channelData.setChannel(channel);
                        ChannelDataList.add(channelData);
                        Log.d("Search ","Video :"+channel.getId() +"ThumbUrl : "+channel.getSnippet().getThumbnails().getDefault());
                    }

                    return ChannelDataList;
                } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                    showGooglePlayServicesAvailabilityErrorDialog(availabilityException
                            .getConnectionStatusCode());
                } catch (UserRecoverableAuthIOException userRecoverableException) {
                    startActivityForResult(
                            userRecoverableException.getIntent(),
                            REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    Utils.logAndShow(ChannelListActivity.this, Constants.APP_NAME, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<ChannelData> Channels) {
                setProgressBarIndeterminateVisibility(false);
                if (progress.isShowing()) {
                    progress.setIndeterminate(false);
                    progress.dismiss();
                    if (Channels == null) {
                        Toast.makeText(ChannelListActivity.this,"Channel could not found",Toast.LENGTH_SHORT);
                        return;
                    }
                }

                updateChannelFound(Channels);
//                mUploadsListFragment.setVideos(videos);
            }
        }.execute((Void) null);
    }

    private void updateChannelFound(final List<ChannelData> searchResults){
        final ArrayAdapter<ChannelData> adapter = new ArrayAdapter<ChannelData>(getApplicationContext(), R.layout.list_search_channel, searchResults){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_search_channel, parent, false);
                }
                NetworkImageView thumbnail = (NetworkImageView)convertView.findViewById(R.id.channel_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.channel_title);
                TextView description = (TextView)convertView.findViewById(R.id.channel_description);

                ChannelData searchResult = searchResults.get(position);

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
                Toast.makeText(ChannelListActivity.this,"Channel : "+ searchResults.get(i).getTitle(),Toast.LENGTH_SHORT);
                Intent intent = new Intent(ChannelListActivity.this, ChannelVideoListActivity.class);
                intent.putExtra(AccountVideoListActivity.CHANNEL_ID, searchResults.get(i).getYouTubeChannelId());
                startActivityForResult(intent, REQUEST_DIRECT_TAG);
            }
        };
        channelFound.setOnItemClickListener(clickListener);
        channelFound.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        String keyword=searchChannelInput.getText().toString();
        loadData(keyword);
        Toast.makeText(this,"Here is keyword :" +keyword,Toast.LENGTH_LONG);
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,ChannelListActivity.this,
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
