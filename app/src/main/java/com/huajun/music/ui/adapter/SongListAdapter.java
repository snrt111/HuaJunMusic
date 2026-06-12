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
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Song;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {

    private final Context context;
    private final List<Song> songs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SongListAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        int pos = holder.getAdapterPosition();
        holder.tvIndex.setText(String.valueOf(pos + 1));
        holder.tvTitle.setText(song.getName());
        holder.tvArtist.setText(song.getArtistName() + " - " + song.getAlbumName());
        holder.tvDuration.setText(song.getFormattedDuration());

        Song current = PlayManager.getInstance().getCurrentSong().getValue();
        boolean isCurrent = current != null && current.getId() != null && current.getId().equals(song.getId());
        holder.ivPlaying.setVisibility(isCurrent ? View.VISIBLE : View.GONE);

        if (song.getPicUrl() != null && !song.getPicUrl().isEmpty()) {
            Glide.with(context).load(song.getPicUrl()).into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_music_note);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(pos);
        });
    }

    @Override
    public int getItemCount() { return songs.size(); }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvTitle, tvArtist, tvDuration;
        ImageView ivCover, ivPlaying;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_index);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            ivCover = itemView.findViewById(R.id.iv_cover);
            ivPlaying = itemView.findViewById(R.id.iv_playing);
        }
    }
}
