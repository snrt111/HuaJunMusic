package com.snrt.helloworld.music;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.snrt.helloworld.R;
import com.snrt.helloworld.task.MusicTask;
import com.snrt.helloworld.util.HttpUtil;
import com.snrt.helloworld.vo.Callback;
import com.snrt.helloworld.vo.SongInfo;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends AppCompatActivity {
    private static final String TAG = "MusicListActivity";

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MyAdapter mAdapter;
    private MediaPlayer mediaPlayer;
    private List<MusicVO> musics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        getNetData();
//        musics.clear();
//        musics.addAll(getMusics());
    }

    private void getNetData() {
        MusicTask musicTask = new MusicTask();
        musicTask.setCallback(new Callback<List<MusicVO>>() {
            @Override
            public void setData(List<MusicVO> data) {
                musics.clear();
                musics.addAll(data);
                mAdapter.notifyDataSetChanged();
            }
        });
        musicTask.execute();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_music_list);
        //创建默认的线性LayoutManager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //创建并设置Adapter
        mAdapter = new MyAdapter(this, musics);
        recyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MusicListActivity.this, MusicActivity.class);
                intent.putExtra("data", (Serializable) musics);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void play(Uri uri) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(MusicListActivity.this, uri);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }


    private List<MusicVO> getMusics() {
        List<MusicVO> videoList = new ArrayList<MusicVO>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
        };

        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                Long duration = cursor.getLong(durationColumn);
                Long size = cursor.getLong(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                if (title != null && title.length() > 0) {
                    name = title;
                }
                MusicVO video = new MusicVO(String.valueOf(id), name, String.valueOf(duration), String.valueOf(size));

                if (!videoList.contains(video)) {
                    if (Long.valueOf(video.getDuration()) > 0) {
                        videoList.add(video);
                    }
                }
                // Stores column values and the contentUri in a local object
                // that represents the media file.

            }
        }
        return videoList;
    }

}