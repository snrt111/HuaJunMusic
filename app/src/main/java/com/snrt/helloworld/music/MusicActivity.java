package com.snrt.helloworld.music;

import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.snrt.helloworld.R;

import java.io.IOException;
import java.util.List;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private List<MusicVO> musics;
    private int position;
    private Button prev;
    private Button next;
    private Button playOrPause;
    private ImageView imageView;
    private ObjectAnimator objectAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initData();
        initView();
        initEvent();
        startPlay();
    }

    private void initView() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);

        //
        imageView = findViewById(R.id.music_pic);
        objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0.0f, 360.0f);
        objectAnimator.setDuration(5000);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
    }

    private void initData() {
        Intent intent = getIntent();
        musics = (List<MusicVO>) intent.getSerializableExtra("data");
        position = intent.getIntExtra("position", 0);
    }


    private void startPlay() {
        try {
            Uri uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musics.get(position).getId());
            mediaPlayer.reset();
            mediaPlayer.setDataSource(MusicActivity.this, uri);
            mediaPlayer.prepare();
            this.mediaPlayer.start();
            this.playOrPause.setText(R.string.pause);
            this.objectAnimator.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void initEvent() {
        playOrPause = findViewById(R.id.btn_play);
        playOrPause.setOnClickListener(this);
        prev = findViewById(R.id.btn_prev);
        prev.setOnClickListener(this);
        next = findViewById(R.id.btn_next);
        next.setOnClickListener(this);
        setPrevAndNext();
    }

    private void musicPlay() {
        this.mediaPlayer.start();
        this.playOrPause.setText(R.string.pause);
        this.objectAnimator.resume();
    }

    private void musicPause() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.playOrPause.setText(R.string.play);
            this.objectAnimator.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mediaPlayer.release();
        this.mediaPlayer = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play -> {
                if(this.mediaPlayer.isPlaying()) {
                    musicPause();
                } else {
                    musicPlay();
                }
            }
            case R.id.btn_prev -> {
                prevMusic();
            }
            case R.id.btn_next -> {
                nextMusic();
            }
        }
    }

    private void prevMusic() {
        position -= 1;
        setPrevAndNext();
        startPlay();
    }

    private void nextMusic() {
        position += 1;
        setPrevAndNext();
        startPlay();
    }

    private void setPrevAndNext() {
        if (position == 0) {
            prev.setVisibility(View.INVISIBLE);
        }
        if (position > 0) {
            prev.setVisibility(View.VISIBLE);
        }
        if (position < musics.size() - 1) {
            next.setVisibility(View.VISIBLE);
        }
        if (position == musics.size() - 1) {
            next.setVisibility(View.INVISIBLE);
        }
    }
}
