package com.snrt.helloworld.music;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snrt.helloworld.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MyAdapter";
    private final MusicListActivity musicListActivity;
    Context context;
    List<MusicVO> data;
    OnItemClickListener listener;

    public MyAdapter(MusicListActivity musicListActivity, Context context, List<MusicVO> data) {
        this.musicListActivity = musicListActivity;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(musicListActivity.getApplicationContext()).inflate(R.layout.music_item, null);
        //引入自定义列表项的资源文件
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        //  将数据映射到控件中
        MusicVO music = this.data.get(position);
        myHolder.icon.setImageResource(R.drawable.music);
        myHolder.name.setText(music.getName());
        Long time = Long.valueOf(music.getDuration());
        DateFormat dateFormat = new SimpleDateFormat("mm:ss");
        String format = dateFormat.format(new Date(time));
        Log.e(TAG, "onBindViewHolder: " + time);
        myHolder.time.setText(format);
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

    public void setOnItemClickListener(OnItemClickListener listener) {
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
