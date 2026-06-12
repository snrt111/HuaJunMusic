package com.huajun.music.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huajun.music.R;
import com.huajun.music.api.MusicApi;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Playlist;
import com.huajun.music.model.Song;
import com.huajun.music.ui.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    private ImageView ivBack, ivCover, ivPlayAll;
    private TextView tvName, tvCount;
    private RecyclerView recyclerView;
    private View loading;

    private final List<Song> songs = new ArrayList<>();
    private SongListAdapter adapter;
    private String playlistId, playlistName, playlistCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        playlistId = getIntent().getStringExtra("playlist_id");
        playlistName = getIntent().getStringExtra("playlist_name");
        playlistCover = getIntent().getStringExtra("playlist_cover");

        initViews();
        loadData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        ivPlayAll = findViewById(R.id.iv_play_all);
        tvName = findViewById(R.id.tv_name);
        tvCount = findViewById(R.id.tv_count);
        recyclerView = findViewById(R.id.recycler_view);
        loading = findViewById(R.id.loading);

        tvName.setText(playlistName);
        if (playlistCover != null && !playlistCover.isEmpty()) {
            Glide.with(this).load(playlistCover).into(ivCover);
        }

        ivBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongListAdapter(this, songs);
        adapter.setOnItemClickListener(position -> {
            PlayManager.getInstance().playSongs(songs, position);
        });
        recyclerView.setAdapter(adapter);

        ivPlayAll.setOnClickListener(v -> {
            if (!songs.isEmpty()) {
                PlayManager.getInstance().playSongs(songs, 0);
                Toast.makeText(this, "开始播放全部", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        loading.setVisibility(View.VISIBLE);
        MusicApi.getInstance().getPlaylistDetail(playlistId, new MusicApi.Callback<Playlist>() {
            @Override
            public void onSuccess(Playlist data) {
                runOnUiThread(() -> {
                    loading.setVisibility(View.GONE);
                    if (data != null && data.getTracks() != null) {
                        for (Song s : data.getTracks()) {
                            if (s.getUrl() != null && !s.getUrl().isEmpty()) songs.add(s);
                        }
                        tvCount.setText("共 " + songs.size() + " 首");
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(PlaylistDetailActivity.this, "加载失败: " + msg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
