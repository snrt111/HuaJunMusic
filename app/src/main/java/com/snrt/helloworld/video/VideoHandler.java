package com.snrt.helloworld.video;

import android.content.Context;
import android.content.Intent;
import com.snrt.helloworld.Handler;

public class VideoHandler implements Handler {

    private static final String TAG = "MusicHandler";
    private Context context;


    public VideoHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handler() {
        Intent intent = new Intent();
        intent.setClass(this.context, VideoListActivity.class);
        context.startActivity(intent);
    }
}
