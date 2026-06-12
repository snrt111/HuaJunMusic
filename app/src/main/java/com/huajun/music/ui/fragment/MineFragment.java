package com.huajun.music.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huajun.music.R;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Song;
import com.huajun.music.ui.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView tvHistoryCount;
    private TextView tvPlayAll;

    private List<Song> historyList = new ArrayList<>();
    private SongListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void initViews(View view) {
        rvHistory = view.findViewById(R.id.rv_history);
        tvHistoryCount = view.findViewById(R.id.tv_history_count);
        tvPlayAll = view.findViewById(R.id.tv_play_all);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongListAdapter(getContext(), historyList);
        adapter.setOnItemClickListener(position -> {
            PlayManager.getInstance().playSongs(historyList, position);
        });
        rvHistory.setAdapter(adapter);

        tvPlayAll.setOnClickListener(v -> {
            if (!historyList.isEmpty()) {
                PlayManager.getInstance().playSongs(historyList, 0);
            }
        });
    }

    private void refreshData() {
        historyList.clear();
        List<Song> history = PlayManager.getInstance().getHistory();
        if (history != null) historyList.addAll(history);
        adapter.notifyDataSetChanged();
        tvHistoryCount.setText("(" + historyList.size() + "首)");
    }
}
