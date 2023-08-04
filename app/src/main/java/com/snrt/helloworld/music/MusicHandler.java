package com.snrt.helloworld.music;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import com.snrt.helloworld.Handler;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class MusicHandler implements Handler {

    private static final String TAG = "MusicHandler";
    private Context context;


    public MusicHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handler() {
        Intent intent = new Intent();
        intent.setClass(this.context, MusicListActivity.class);
        context.startActivity(intent);
    }
}
