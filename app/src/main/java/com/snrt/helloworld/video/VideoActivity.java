package com.snrt.helloworld.video;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.snrt.helloworld.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    private VideoView videoView;

    private MediaController mController;
    private Long id;
    private Button back;
    private List<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initData();
        initView();
        initEvent();
        play();

    }

    private void initEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void initData() {
        Intent intent = getIntent();
        id = intent.getLongExtra("id",0);
        Bundle bundle=intent.getExtras();
//        videoList = (List<Video>) bundle.getSerializable("list");
        Log.e(TAG, "initData: "+ id);
    }

    private void initView() {
        videoView = findViewById(R.id.vv);
        back = findViewById(R.id.vv_back);
        mController = new MediaController(this);
        mController.setMediaPlayer(videoView);
        videoView.setMediaController(mController);
    }

    public void play(){
        Uri contentUri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        videoView.setVideoURI(contentUri);
        videoView.requestFocus();
        videoView.start();
    }


}
