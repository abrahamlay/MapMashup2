package com.dev.abrahamlay.mapmashup2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dev.abrahamlay.mapmashup2.util.VideoData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.util.List;

/**
 * Created by Abraham on 8/15/2016.
 */
public class UploadsListFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, ToolTipView.OnToolTipViewClickedListener {
    private static final String TAG = UploadsListFragment.class.getName();
    private static Context mContext;
    private Callbacks mCallbacks;
    private GoogleApiClient mGoogleApiClient;
    private GridView mGridView;
    private ImageLoader mImageLoader;
    private ToolTipView myToolTipView;

    public UploadsListFragment() {
    }

    @SuppressLint("ValidFragment")
    public UploadsListFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View listView = inflater.inflate(R.layout.list_fragment, container, false);
        mGridView = (GridView) listView.findViewById(R.id.grid_view);
        TextView emptyView = (TextView) listView.findViewById(android.R.id.empty);

        mGridView.setEmptyView(emptyView);
        return listView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProfileInfo();
    }

    public void setVideos(List<VideoData> videos) {
        if (!isAdded()) {
            return;
        }

        mGridView.setAdapter(new UploadedVideoAdapter(videos));
    }

    public void setProfileInfo() {
        //not sure if mGoogleapiClient.isConnect is appropriate...
        if (!mGoogleApiClient.isConnected() || Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) == null) {
            ((ImageView) getView().findViewById(R.id.avatar))
                    .setImageDrawable(null);
            ((TextView) getView().findViewById(R.id.display_name))
                    .setText(R.string.not_signed_in);
        } else {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson.hasImage()) {
                // Set the URL of the image that should be loaded into this view, and
                // specify the ImageLoader that will be used to make the request.
                ((NetworkImageView) getView().findViewById(R.id.avatar)).setImageUrl(currentPerson.getImage().getUrl(), mImageLoader);
            }
            if (currentPerson.hasDisplayName()) {
                ((TextView) getView().findViewById(R.id.display_name))
                        .setText(currentPerson.getDisplayName());
            }
        }
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) getView().findViewById(R.id.activity_fragment_account_video_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("Your youtube account. ")
                .withTextColor(ContextCompat.getColor(getView().getContext(),R.color.text))
                .withColor(ContextCompat.getColor(getView().getContext(),R.color.bg_screen3))
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, getView().findViewById(R.id.youtube_account));
        myToolTipView.setOnToolTipViewClickedListener(UploadsListFragment.this);



    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGridView.getAdapter() != null) {
            ((UploadedVideoAdapter) mGridView.getAdapter()).notifyDataSetChanged();
        }

        setProfileInfo();
        mCallbacks.onConnected(Plus.AccountApi.getAccountName(mGoogleApiClient));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            Toast.makeText(getActivity(),
                    R.string.connection_to_google_play_failed, Toast.LENGTH_SHORT)
                    .show();

            Log.e(TAG,
                    String.format(
                            "Connection to Play Services Failed, error: %d, reason: %s",
                            connectionResult.getErrorCode(),
                            connectionResult.toString()));
            try {
                connectionResult.startResolutionForResult(getActivity(), 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        mImageLoader = mCallbacks.onGetImageLoader();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        mImageLoader = null;
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {

    }

    public interface Callbacks {
        public ImageLoader onGetImageLoader();

        public void onVideoSelected(VideoData video);

        public void onConnected(String connectedAccountName);
    }

    private class UploadedVideoAdapter extends BaseAdapter {
        private List<VideoData> mVideos;

        private UploadedVideoAdapter(List<VideoData> videos) {
            mVideos = videos;
        }

        @Override
        public int getCount() {
            return mVideos.size();
        }

        @Override
        public Object getItem(int i) {
            return mVideos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mVideos.get(i).getYouTubeId().hashCode();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.list_item, container, false);
            }

            VideoData video = mVideos.get(position);
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(video.getTitle());
            ((NetworkImageView) convertView.findViewById(R.id.thumbnail)).setImageUrl(video.getThumbUri(), mImageLoader);
//            if (mGoogleApiClient.isConnected()) {
//                ((PlusOneButton) convertView.findViewById(R.id.plus_button))
//                        .initialize(video.getWatchUri(), null);
//            }


            convertView.findViewById(R.id.main_target).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mCallbacks.onVideoSelected(mVideos.get(position));
                        }
                    });
            return convertView;
        }
    }
}
