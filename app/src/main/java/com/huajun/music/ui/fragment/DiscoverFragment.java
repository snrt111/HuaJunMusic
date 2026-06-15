package com.huajun.music.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.huajun.music.R;
import com.huajun.music.api.MusicApi;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Playlist;
import com.huajun.music.model.Song;
import com.huajun.music.ui.activity.PlaylistDetailActivity;
import com.huajun.music.ui.adapter.PlaylistAdapter;
import com.huajun.music.ui.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private RecyclerView rvHotSongs;
    private RecyclerView rvPlaylists;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout searchLayout;

    private List<Song> hotSongs = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();
    private SongListAdapter songAdapter;
    private PlaylistAdapter playlistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initViews(view);
        loadData();
        return view;
    }

    private void initViews(View view) {
        refreshLayout = view.findViewById(R.id.refresh_layout);
        searchLayout = view.findViewById(R.id.tv_search);
        rvHotSongs = view.findViewById(R.id.rv_hot_songs);
        rvPlaylists = view.findViewById(R.id.rv_playlists);

        rvHotSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongListAdapter(getContext(), hotSongs);
        songAdapter.setOnItemClickListener(position -> {
            PlayManager.getInstance().playSongs(hotSongs, position);
        });
        rvHotSongs.setAdapter(songAdapter);

        rvPlaylists.setLayoutManager(new GridLayoutManager(getContext(), 3));
        playlistAdapter = new PlaylistAdapter(getContext(), playlists);
        playlistAdapter.setOnItemClickListener(position -> {
            Playlist pl = playlists.get(position);
            Intent intent = new Intent(getContext(), PlaylistDetailActivity.class);
            intent.putExtra("playlist_id", pl.getId());
            intent.putExtra("playlist_name", pl.getName());
            intent.putExtra("playlist_cover", pl.getCoverImgUrl());
            startActivity(intent);
        });
        rvPlaylists.setAdapter(playlistAdapter);

        searchLayout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), com.huajun.music.ui.activity.SearchActivity.class));
        });

        refreshLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        refreshLayout.setRefreshing(true);

        MusicApi.getInstance().getRecommendPlaylists(12, new MusicApi.Callback<List<Playlist>>() {
            @Override
            public void onSuccess(List<Playlist> data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    playlists.clear();
                    if (data != null) playlists.addAll(data);
                    playlistAdapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onError(String msg) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "加载失败: " + msg, Toast.LENGTH_SHORT).show());
                }
            }
        });

        MusicApi.getInstance().getTopSongs(30, new MusicApi.Callback<List<Song>>() {
            @Override
            public void onSuccess(List<Song> data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    hotSongs.clear();
                    if (data != null) {
                        for (Song s : data) {
                            if (s.getUrl() != null && !s.getUrl().isEmpty()) hotSongs.add(s);
                        }
                    }
                    songAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                });
            }
            @Override
            public void onError(String msg) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "加载失败: " + msg, Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }
}
