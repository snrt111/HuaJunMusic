package com.huajun.music.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huajun.music.R;
import com.huajun.music.api.MusicApi;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Song;
import com.huajun.music.ui.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivBack, ivClear;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private View loading;

    private final List<Song> songs = new ArrayList<>();
    private SongListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        ivBack = findViewById(R.id.iv_back);
        ivClear = findViewById(R.id.iv_clear);
        recyclerView = findViewById(R.id.recycler_view);
        tvEmpty = findViewById(R.id.tv_empty);
        loading = findViewById(R.id.loading);

        ivBack.setOnClickListener(v -> finish());
        ivClear.setOnClickListener(v -> etSearch.setText(""));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                doSearch();
                return true;
            }
            return false;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongListAdapter(this, songs);
        adapter.setOnItemClickListener(position -> {
            PlayManager.getInstance().playSongs(songs, position);
        });
        recyclerView.setAdapter(adapter);
    }

    private void doSearch() {
        String keyword = etSearch.getText().toString().trim();
        if (keyword.isEmpty()) return;
        loading.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        MusicApi.getInstance().searchSongs(keyword, 50, new MusicApi.Callback<List<Song>>() {
            @Override
            public void onSuccess(List<Song> data) {
                runOnUiThread(() -> {
                    loading.setVisibility(View.GONE);
                    songs.clear();
                    if (data != null) {
                        for (Song s : data) {
                            if (s.getUrl() != null && !s.getUrl().isEmpty()) songs.add(s);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (songs.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
                });
            }
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "搜索失败: " + msg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
