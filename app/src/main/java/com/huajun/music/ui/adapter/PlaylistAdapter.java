package com.huajun.music.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huajun.music.R;
import com.huajun.music.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final Context context;
    private final List<Playlist> playlists;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist pl = playlists.get(holder.getAdapterPosition());
        holder.tvName.setText(pl.getName());
        holder.tvCount.setText(pl.getTrackCount() + "首");
        if (pl.getCoverImgUrl() != null && !pl.getCoverImgUrl().isEmpty()) {
            Glide.with(context).load(pl.getCoverImgUrl()).into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_music_note);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return playlists.size(); }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvName, tvCount;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCount = itemView.findViewById(R.id.tv_count);
        }
    }
}
