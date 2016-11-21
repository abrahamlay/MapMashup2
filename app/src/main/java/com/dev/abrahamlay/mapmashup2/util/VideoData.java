package com.dev.abrahamlay.mapmashup2.util;

import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Abraham on 7/26/2016.
 */
public class VideoData {
    private Video mVideo;

    public Video getVideo() {
        return mVideo;
    }

    public void setVideo(Video video) {
        mVideo = video;
    }

    public String getYouTubeId() {
        return mVideo.getId();
    }

    public String getDescription() {
        return mVideo.getSnippet().getDescription();
    }
    public String getTitle() {
        return mVideo.getSnippet().getTitle();
    }

    public VideoSnippet addTags(Collection<? extends String> tags) {
        VideoSnippet mSnippet = mVideo.getSnippet();
        List<String> mTags = mSnippet.getTags();
        if (mTags == null) {
            mTags = new ArrayList<String>(2);
        }
        mTags.addAll(tags);
        return mSnippet;
    }

    public String getThumbUri() {
        return mVideo.getSnippet().getThumbnails().getDefault().getUrl();
    }

    public String getWatchUri() {
        return "http://www.youtube.com/watch?v=" + getYouTubeId();
    }
}
