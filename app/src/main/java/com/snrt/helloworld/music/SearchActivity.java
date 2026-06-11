package com.snrt.helloworld.music;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snrt.helloworld.R;
import com.snrt.helloworld.util.HttpUtil;
import com.snrt.helloworld.vo.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲搜索Activity
 * 提供歌曲搜索功能
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    // 搜索相关视图
    private EditText etSearchInput;
    private ImageButton btnSearch;
    private ImageButton btnClear;
    private ProgressBar progressBar;
    private TextView tvSearchHint;
    private TextView tvNoResult;

    // RecyclerView相关
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MyAdapter mAdapter;

    // 数据
    private List<MusicVO> searchResults = new ArrayList<>();

    // 搜索任务
    private SearchTask currentSearchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initRecyclerView();
        initListeners();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        etSearchInput = findViewById(R.id.et_search_input);
        btnSearch = findViewById(R.id.btn_search);
        btnClear = findViewById(R.id.btn_clear);
        progressBar = findViewById(R.id.progress_bar);
        tvSearchHint = findViewById(R.id.tv_search_hint);
        tvNoResult = findViewById(R.id.tv_no_result);
        recyclerView = findViewById(R.id.rv_search_results);

        // 初始状态显示搜索提示
        showSearchHint();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(this, this, searchResults);
        recyclerView.setAdapter(mAdapter);

        // 设置点击事件
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 跳转到音乐播放页面
                Intent intent = new Intent(SearchActivity.this, MusicActivity.class);
                intent.putExtra("data", (Serializable) searchResults);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化监听器
     */
    private void initListeners() {
        // 搜索按钮点击
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // 清空按钮点击
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchInput.setText("");
                searchResults.clear();
                mAdapter.notifyDataSetChanged();
                showSearchHint();
            }
        });

        // 输入框文本变化监听
        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 根据输入内容显示/隐藏清空按钮
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 输入框回车键监听（软键盘搜索按钮）
        etSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        String keyword = etSearchInput.getText().toString().trim();

        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        // 取消之前的搜索任务
        if (currentSearchTask != null && currentSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
            currentSearchTask.cancel(true);
        }

        // 显示加载状态
        showLoading();

        // 执行搜索
        currentSearchTask = new SearchTask();
        currentSearchTask.setCallback(new Callback<List<MusicVO>>() {
            @Override
            public void setData(List<MusicVO> data) {
                searchResults.clear();

                if (data != null && !data.isEmpty()) {
                    searchResults.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    showResults();
                } else {
                    showNoResult();
                }
            }
        });
        currentSearchTask.execute(keyword);
    }

    /**
     * 显示搜索提示
     */
    private void showSearchHint() {
        tvSearchHint.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * 显示加载中
     */
    private void showLoading() {
        tvSearchHint.setVisibility(View.GONE);
        tvNoResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * 显示搜索结果
     */
    private void showResults() {
        tvSearchHint.setVisibility(View.GONE);
        tvNoResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示无结果
     */
    private void showNoResult() {
        tvSearchHint.setVisibility(View.GONE);
        tvNoResult.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消正在进行的搜索任务
        if (currentSearchTask != null && currentSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
            currentSearchTask.cancel(true);
        }
    }

    /**
     * 搜索异步任务
     */
    private static class SearchTask extends AsyncTask<String, Void, List<MusicVO>> {

        private Callback<List<MusicVO>> callback;

        public void setCallback(Callback<List<MusicVO>> callback) {
            this.callback = callback;
        }

        @Override
        protected List<MusicVO> doInBackground(String... params) {
            if (params.length == 0 || params[0] == null) {
                return new ArrayList<>();
            }

            String keyword = params[0];
            try {
                // 调用新的搜索API
                return HttpUtil.searchSongs(keyword, 50);
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<MusicVO> result) {
            super.onPostExecute(result);
            if (callback != null) {
                callback.setData(result);
            }
        }
    }
}
