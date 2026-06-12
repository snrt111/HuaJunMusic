package com.huajun.music.ui.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.huajun.music.R;
import com.huajun.music.api.MusicApi;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.PlayMode;
import com.huajun.music.model.Song;
import com.huajun.music.ui.view.LyricView;
import com.huajun.music.util.LyricParser;

import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {

    private ImageView ivBack, ivCover, ivPlayMode, ivPrev, ivPlay, ivNext, ivFavorite;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private LyricView lyricView;
    private ObjectAnimator rotateAnimator;
    private boolean hasLyric = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        initAnimators();
        initObservers();
        updateUI(PlayManager.getInstance().getCurrentSong().getValue());
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        ivPlayMode = findViewById(R.id.iv_play_mode);
        ivPrev = findViewById(R.id.iv_prev);
        ivPlay = findViewById(R.id.iv_play);
        ivNext = findViewById(R.id.iv_next);
        ivFavorite = findViewById(R.id.iv_favorite);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        seekBar = findViewById(R.id.seek_bar);
        lyricView = findViewById(R.id.lyric_view);

        ivBack.setOnClickListener(v -> finish());
        ivPlayMode.setOnClickListener(v -> {
            PlayManager.getInstance().togglePlayMode();
            updatePlayModeUI();
        });
        ivPrev.setOnClickListener(v -> PlayManager.getInstance().previous());
        ivPlay.setOnClickListener(v -> PlayManager.getInstance().playPause());
        ivNext.setOnClickListener(v -> PlayManager.getInstance().next());
        ivFavorite.setOnClickListener(v -> {
            ivFavorite.setSelected(!ivFavorite.isSelected());
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) tvCurrentTime.setText(formatTime(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayManager.getInstance().seekTo(seekBar.getProgress());
            }
        });

        updatePlayModeUI();
    }

    private void initAnimators() {
        rotateAnimator = ObjectAnimator.ofFloat(ivCover, "rotation", 0f, 360f);
        rotateAnimator.setDuration(20000);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(new LinearInterpolator());
    }

    private void initObservers() {
        PlayManager.getInstance().getCurrentSong().observe(this, this::updateUI);
        PlayManager.getInstance().getIsPlaying().observe(this, playing -> {
            if (playing != null && playing) {
                ivPlay.setImageResource(R.drawable.ic_pause_large);
                if (rotateAnimator != null && !rotateAnimator.isRunning()) rotateAnimator.start();
            } else {
                ivPlay.setImageResource(R.drawable.ic_play_large);
                if (rotateAnimator != null) rotateAnimator.pause();
            }
        });
        PlayManager.getInstance().getCurrentPosition().observe(this, pos -> {
            if (pos != null) {
                seekBar.setProgress(pos.intValue());
                tvCurrentTime.setText(formatTime(pos));
                if (hasLyric) lyricView.setCurrentTime(pos);
            }
        });
        PlayManager.getInstance().getDuration().observe(this, dur -> {
            if (dur != null && dur > 0) {
                seekBar.setMax(dur.intValue());
                tvTotalTime.setText(formatTime(dur));
            }
        });
        PlayManager.getInstance().getPlayMode().observe(this, mode -> updatePlayModeUI());
    }

    private void updateUI(Song song) {
        if (song == null) return;
        tvTitle.setText(song.getName());
        tvArtist.setText(song.getArtistName());
        if (song.getPicUrl() != null && !song.getPicUrl().isEmpty()) {
            Glide.with(this).load(song.getPicUrl()).circleCrop().into(ivCover);
        } else {
            ivCover.setImageResource(R.drawable.ic_music_note);
        }
        loadLyric(song);
        Long dur = PlayManager.getInstance().getDuration().getValue();
        if (dur != null && dur > 0) {
            seekBar.setMax(dur.intValue());
            tvTotalTime.setText(formatTime(dur));
        } else if (song.getDuration() != null && song.getDuration() > 0) {
            seekBar.setMax(song.getDuration().intValue());
            tvTotalTime.setText(song.getFormattedDuration());
        }
    }

    private void loadLyric(Song song) {
        if (song == null || song.getId() == null) return;
        hasLyric = false;
        MusicApi.getInstance().getSongLyric(song.getId(), new MusicApi.Callback<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> {
                    lyricView.setLyrics(LyricParser.parse(data));
                    hasLyric = true;
                });
            }
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> lyricView.setLyrics(LyricParser.parse(null)));
            }
        });
    }

    private void updatePlayModeUI() {
        PlayMode mode = PlayManager.getInstance().getCurrentPlayMode();
        switch (mode) {
            case RANDOM:
                ivPlayMode.setImageResource(R.drawable.ic_shuffle);
                break;
            case SINGLE:
                ivPlayMode.setImageResource(R.drawable.ic_repeat_one);
                break;
            default:
                ivPlayMode.setImageResource(R.drawable.ic_repeat);
        }
    }

    private String formatTime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rotateAnimator != null) rotateAnimator.cancel();
    }
}
