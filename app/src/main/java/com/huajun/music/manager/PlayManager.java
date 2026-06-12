package com.huajun.music.manager;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.huajun.music.model.PlayMode;
import com.huajun.music.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayManager {

    private static final String TAG = "PlayManager";
    private static PlayManager instance;

    private ExoPlayer player;
    private Context context;
    private boolean initialized = false;

    private List<Song> songList = new ArrayList<>();
    private int currentIndex = -1;
    private PlayMode playMode = PlayMode.SEQUENCE;

    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Long> currentPosition = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> duration = new MutableLiveData<>(0L);
    private final MutableLiveData<PlayMode> currentPlayMode = new MutableLiveData<>(PlayMode.SEQUENCE);
    private final MutableLiveData<List<Song>> songQueue = new MutableLiveData<>(new ArrayList<>());

    private final List<Song> history = new ArrayList<>();

    private PlayManager() {}

    public static synchronized PlayManager getInstance() {
        if (instance == null) instance = new PlayManager();
        return instance;
    }

    public void init(Context ctx) {
        if (initialized || ctx == null) return;
        this.context = ctx.getApplicationContext();
        this.player = new ExoPlayer.Builder(this.context).build();
        this.player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean playing) {
                isPlaying.postValue(playing);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    onCurrentSongEnded();
                }
            }
        });
        initialized = true;
        startPositionMonitor();
    }

    private void startPositionMonitor() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    if (player != null && player.isPlaying()) {
                        currentPosition.postValue(player.getCurrentPosition());
                        duration.postValue(player.getDuration());
                    }
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public ExoPlayer getPlayer() { return player; }

    public LiveData<Song> getCurrentSong() { return currentSong; }
    public LiveData<Boolean> getIsPlaying() { return isPlaying; }
    public LiveData<Long> getCurrentPosition() { return currentPosition; }
    public LiveData<Long> getDuration() { return duration; }
    public LiveData<PlayMode> getPlayMode() { return currentPlayMode; }
    public LiveData<List<Song>> getSongQueue() { return songQueue; }

    public Song getCurrentSongData() {
        if (currentIndex >= 0 && currentIndex < songList.size()) return songList.get(currentIndex);
        return null;
    }

    public List<Song> getSongList() { return songList; }
    public int getCurrentIndex() { return currentIndex; }

    public void playSongs(List<Song> songs, int index) {
        if (songs == null || songs.isEmpty()) return;
        this.songList = new ArrayList<>(songs);
        this.songQueue.postValue(new ArrayList<>(songList));
        playAt(index);
    }

    public void addAndPlay(Song song) {
        if (song == null) return;
        if (!songList.contains(song)) {
            songList.add(song);
            songQueue.postValue(new ArrayList<>(songList));
        }
        int idx = songList.indexOf(song);
        playAt(idx);
    }

    public void playAt(int index) {
        if (index < 0 || index >= songList.size()) return;
        currentIndex = index;
        Song song = songList.get(currentIndex);
        currentSong.postValue(song);
        addToHistory(song);
        prepareAndPlay(song);
    }

    private void prepareAndPlay(Song song) {
        if (song == null || song.getUrl() == null || song.getUrl().isEmpty()) {
            Log.w(TAG, "Song has no URL, skipping: " + song.getName());
            return;
        }
        try {
            ensureMusicService();
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getUrl())
                    .setMediaMetadata(new MediaMetadata.Builder()
                            .setTitle(song.getName())
                            .setArtist(song.getArtistName())
                            .build())
                    .build();
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e(TAG, "play error: " + e.getMessage());
        }
    }

    private void ensureMusicService() {
        if (context == null) return;
        try {
            Intent serviceIntent = new Intent();
            serviceIntent.setComponent(new ComponentName(
                    context.getPackageName(),
                    "com.huajun.music.service.MusicService"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to start MusicService: " + e.getMessage());
        }
    }

    public void playPause() {
        if (player == null) return;
        if (player.isPlaying()) {
            player.pause();
        } else {
            if (player.getMediaItemCount() == 0) {
                Song s = getCurrentSongData();
                if (s != null) prepareAndPlay(s);
            } else {
                player.setPlayWhenReady(true);
            }
        }
    }

    public void next() {
        if (songList.isEmpty()) return;
        int next;
        switch (playMode) {
            case RANDOM:
                next = (int) (Math.random() * songList.size());
                break;
            case SINGLE:
                player.seekTo(0);
                player.setPlayWhenReady(true);
                return;
            default:
                next = (currentIndex + 1) % songList.size();
        }
        playAt(next);
    }

    public void previous() {
        if (songList.isEmpty()) return;
        if (player.getCurrentPosition() > 3000) {
            player.seekTo(0);
            return;
        }
        int prev = (currentIndex - 1 + songList.size()) % songList.size();
        playAt(prev);
    }

    public void togglePlayMode() {
        playMode = playMode.next();
        currentPlayMode.postValue(playMode);
    }

    public PlayMode getCurrentPlayMode() { return playMode; }

    public void seekTo(long positionMs) {
        if (player != null) player.seekTo(positionMs);
    }

    private void onCurrentSongEnded() {
        if (playMode == PlayMode.SINGLE) {
            player.seekTo(0);
            player.setPlayWhenReady(true);
        } else {
            next();
        }
    }

    private void addToHistory(Song song) {
        if (song == null) return;
        history.remove(song);
        history.add(0, song);
        if (history.size() > 100) history.remove(history.size() - 1);
    }

    public List<Song> getHistory() { return history; }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
