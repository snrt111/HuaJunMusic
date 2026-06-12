package com.huajun.music.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huajun.music.R;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Song;
import com.huajun.music.ui.fragment.DiscoverFragment;
import com.huajun.music.ui.fragment.MineFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private DiscoverFragment discoverFragment;
    private MineFragment mineFragment;

    private View miniPlayer;
    private ImageView ivMiniCover;
    private TextView tvMiniTitle;
    private TextView tvMiniArtist;
    private ImageView ivMiniPlay;
    private ImageView ivMiniNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();
        initMiniPlayer();
        initBottomNav();

        // 预初始化音乐服务（通过 PlayManager 延迟启动，避免 targetSdk>=26 时 startService 后台限制）
        PlayManager.getInstance().init(getApplicationContext());
    }

    private void initFragments() {
        discoverFragment = new DiscoverFragment();
        mineFragment = new MineFragment();
        switchFragment(discoverFragment);
    }

    private void initBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_discover) {
                switchFragment(discoverFragment);
                return true;
            } else if (id == R.id.nav_mine) {
                switchFragment(mineFragment);
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment fragment) {
        if (currentFragment == fragment) return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) ft.hide(currentFragment);
        if (!fragment.isAdded()) ft.add(R.id.fragment_container, fragment);
        ft.show(fragment).commit();
        currentFragment = fragment;
    }

    private void initMiniPlayer() {
        miniPlayer = findViewById(R.id.mini_player);
        ivMiniCover = findViewById(R.id.iv_mini_cover);
        tvMiniTitle = findViewById(R.id.tv_mini_title);
        tvMiniArtist = findViewById(R.id.tv_mini_artist);
        ivMiniPlay = findViewById(R.id.iv_mini_play);
        ivMiniNext = findViewById(R.id.iv_mini_next);

        PlayManager.getInstance().getCurrentSong().observe(this, song -> {
            if (song != null) {
                miniPlayer.setVisibility(View.VISIBLE);
                tvMiniTitle.setText(song.getName());
                tvMiniArtist.setText(song.getArtistName());
                if (song.getPicUrl() != null && !song.getPicUrl().isEmpty()) {
                    Glide.with(this).load(song.getPicUrl()).circleCrop().into(ivMiniCover);
                } else {
                    ivMiniCover.setImageResource(R.drawable.ic_music_note);
                }
            } else {
                miniPlayer.setVisibility(View.GONE);
            }
        });

        PlayManager.getInstance().getIsPlaying().observe(this, playing -> {
            if (playing != null && playing) {
                ivMiniPlay.setImageResource(R.drawable.ic_pause);
            } else {
                ivMiniPlay.setImageResource(R.drawable.ic_play);
            }
        });

        ivMiniPlay.setOnClickListener(v -> PlayManager.getInstance().playPause());
        ivMiniNext.setOnClickListener(v -> PlayManager.getInstance().next());
        miniPlayer.setOnClickListener(v -> {
            Song s = PlayManager.getInstance().getCurrentSong().getValue();
            if (s != null) {
                startActivity(new Intent(this, PlayerActivity.class));
            }
        });
    }
}
