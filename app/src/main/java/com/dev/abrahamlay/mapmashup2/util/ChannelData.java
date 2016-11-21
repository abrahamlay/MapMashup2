package com.dev.abrahamlay.mapmashup2.util;

import com.google.api.services.youtube.model.Channel;

/**
 * Created by Abraham on 11/9/2016.
 */

public class ChannelData {
    private Channel mChannel;

    public Channel getChannel() {
        return mChannel;
    }

    public void setChannel(Channel Channel) {
        mChannel = Channel;
    }

    public String getYouTubeChannelId() {
        return mChannel.getId();
    }

    public String getDescription() {
        return mChannel.getSnippet().getDescription();
    }
    public String getTitle() {
        return mChannel.getSnippet().getTitle();
    }

     public String getThumbUri() {
        return mChannel.getSnippet().getThumbnails().getDefault().getUrl();
    }

}
