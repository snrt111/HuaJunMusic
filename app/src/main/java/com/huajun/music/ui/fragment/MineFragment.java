package com.huajun.music.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huajun.music.R;
import com.huajun.music.config.ApiConfig;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Song;
import com.huajun.music.ui.activity.ApiSettingsActivity;
import com.huajun.music.ui.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView tvHistoryCount;
    private TextView tvPlayAll;
    private LinearLayout llApiSettings;
    private TextView tvCurrentApiStatus;

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
        updateApiStatus();
    }

    private void initViews(View view) {
        rvHistory = view.findViewById(R.id.rv_history);
        tvHistoryCount = view.findViewById(R.id.tv_history_count);
        tvPlayAll = view.findViewById(R.id.tv_play_all);
        llApiSettings = view.findViewById(R.id.ll_api_settings);
        tvCurrentApiStatus = view.findViewById(R.id.tv_current_api_status);

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

        // API设置点击事件
        llApiSettings.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ApiSettingsActivity.class));
        });
    }

    private void refreshData() {
        historyList.clear();
        List<Song> history = PlayManager.getInstance().getHistory();
        if (history != null) historyList.addAll(history);
        adapter.notifyDataSetChanged();
        tvHistoryCount.setText("(" + historyList.size() + "首)");
    }

    /**
     * 更新API状态显示
     */
    private void updateApiStatus() {
        String apiName = ApiConfig.getCurrentApiName();
        String statusText;
        switch (apiName) {
            case ApiConfig.NETEASE_NAME:
                statusText = "当前: 网易云音乐";
                break;
            case ApiConfig.KUGOU_NAME:
                statusText = "当前: 酷狗音乐";
                break;
            case ApiConfig.ALAPI_NAME:
                statusText = "当前: ALAPI";
                break;
            default:
                statusText = "当前: " + apiName;
        }
        tvCurrentApiStatus.setText(statusText);
    }
}
