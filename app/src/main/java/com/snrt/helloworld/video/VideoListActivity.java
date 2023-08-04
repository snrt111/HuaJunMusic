package com.snrt.helloworld.video;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.snrt.helloworld.R;
import com.snrt.helloworld.util.VideoUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Video> videoList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initData();
        initView();
    }
    private void initData() {
        videoList = getVideos();
    }

    private List<Video> getVideos() {
        List<Video> videoList = new ArrayList<Video>();
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE};
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(collection, projection, null, null, sortOrder)) {
            while (cursor.moveToNext()) {
                Video video = VideoUtil.getVideoFromCursor(cursor);
                videoList.add(video);
            }
        }
        return videoList;
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_video_list);
        //创建默认的线性LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(this, videoList);
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Long id = videoList.get(position).getId();
                    play(id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void play(Long id) throws IOException {
        Intent intent = new Intent(this, VideoActivity.class);
        Bundle bundle = new Bundle();                           //创建Bundle对象
        bundle.putLong("id", id);     //装入数据
//        bundle.putSerializable("list", (Serializable) videoList);
        intent.putExtras(bundle);                                //把Bundle塞入Intent里面
        startActivity(intent);
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        List<Video> data;

        MyAdapter.OnItemClickListener listener;

        public MyAdapter(Context context, List<Video> data) {
            this.context = context;
            this.data = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.music_item, null);
            //引入自定义列表项的资源文件
            MyAdapter.MyHolder myHolder = new MyAdapter.MyHolder(view);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyAdapter.MyHolder myHolder = (MyAdapter.MyHolder) holder;
            //  将数据映射到控件中
            Video music = this.data.get(position);
            myHolder.icon.setImageResource(R.drawable.music);
            myHolder.name.setText(music.getName());
            myHolder.time.setText(String.valueOf(music.getSize()));
            myHolder.setUri(music.getUri());
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(myHolder.itemView, myHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public void setOnItemClickListener(MyAdapter.OnItemClickListener listener) {
            this.listener = listener;
        }

        //创建OnItemClickListener接口
        interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        class MyHolder extends RecyclerView.ViewHolder {
            private final TextView name;
            private final TextView time;
            private final ImageView icon;

            private Uri uri;

            private View itemView;

            public MyHolder(View view) {
                super(view);
                itemView = view;
                name = view.findViewById(R.id.tv_name);
                time = view.findViewById(R.id.tv_time);
                icon = view.findViewById(R.id.iv_icon);
            }

            public Uri getUri() {
                return uri;
            }

            public void setUri(Uri uri) {
                this.uri = uri;
            }
        }
    }
}