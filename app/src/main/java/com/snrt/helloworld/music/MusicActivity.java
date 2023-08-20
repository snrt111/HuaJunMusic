package com.snrt.helloworld.music;

import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.snrt.helloworld.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private List<MusicVO> musics;
    private int position;
    private ImageButton prev;
    private ImageButton next;
    private ImageButton playOrPause;
    private ImageView imageView;
    private ObjectAnimator objectAnimator;
    private Visualizer visualizer;
    private SpectrumView fireCircleView;
    private SeekBar durationBar;
    private TextView allTime;
    private TextView nowTime;
    private TextView songName;
    private Timer timer = null;

    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private LyricView lyricView;


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
        playOrPause = findViewById(R.id.btn_play);
        prev = findViewById(R.id.btn_prev);
        next = findViewById(R.id.btn_next);
        mediaPlayer = new MediaPlayer();
        durationBar = findViewById(R.id.duration_seekBar);
        songName = findViewById(R.id.song_name);
        allTime = findViewById(R.id.all_time);
        nowTime = findViewById(R.id.now_time);
        fireCircleView = findViewById(R.id.spectrumView);
        lyricView = findViewById(R.id.lyric_view);
        imageView = findViewById(R.id.music_pic);
        objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0.0f, 360.0f);
        objectAnimator.setDuration(5000);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        setPrevAndNext();
    }

    private void initData() {
        Intent intent = getIntent();
        musics = (List<MusicVO>) intent.getSerializableExtra("data");
        position = intent.getIntExtra("position", 0);
    }


    private void startPlay() {
        try {
            MusicVO currentMusic = musics.get(position);
            String url = currentMusic.getUrl();
            songName.setText(currentMusic.getName());
            lyricView.setLyrics(LyricParser.parseLyrics(currentMusic.getLyric()));
            if(url != null && url !="") {

                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
            } else {

                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.valueOf(currentMusic.getId()));
                mediaPlayer.reset();
                mediaPlayer.setDataSource(MusicActivity.this, uri);
                mediaPlayer.prepare();
            }

            this.mediaPlayer.start();
            this.playOrPause.setBackgroundResource(R.drawable.ic_media_pause);
            this.objectAnimator.start();

            durationBar.setMax(mediaPlayer.getDuration());
            allTime.setText(showTime(mediaPlayer.getDuration()));
            nowTime.setText(showTime(mediaPlayer.getCurrentPosition()));
            //监听播放时回调函数
            if(timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(!isSeekBarChanging){
                            if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                                int currentPosition = mediaPlayer.getCurrentPosition();
                                durationBar.setProgress(currentPosition);
                                lyricView.setCurrentProgress(currentPosition);
                            }
                        }
                    }
                },0,200);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String showTime(int time){
        DateFormat dateFormat = new SimpleDateFormat("mm:ss");
        String format = dateFormat.format(new Date(time));
        return format;
    }


    private void initEvent() {
        playOrPause.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextMusic();
            }
        });
        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!isSeekBarChanging) {
                    nowTime.setText(showTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                nowTime.setText(showTime(mediaPlayer.getCurrentPosition()));
                isSeekBarChanging = false;
            }
        });

        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                        // Waveform data capture callback
                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                        // FFT data capture callback
                        fireCircleView.updateFFTData(fft); // Update the SpectrumView with FFT data
                    }
                },
                Visualizer.getMaxCaptureRate() / 2, false, true
        );

        visualizer.setEnabled(true);
    }

    private void musicPlay() {
        this.mediaPlayer.start();
        this.playOrPause.setBackgroundResource(R.drawable.ic_media_pause);
        this.objectAnimator.resume();
    }

    private void musicPause() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.playOrPause.setBackgroundResource(R.drawable.ic_media_play);
            this.objectAnimator.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mediaPlayer.release();
        this.mediaPlayer = null;
        if (this.visualizer != null) {
            this.visualizer.release();
        }
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
