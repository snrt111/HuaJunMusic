package com.huajun.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.huajun.music.R;
import com.huajun.music.manager.PlayManager;
import com.huajun.music.model.Song;
import com.huajun.music.ui.activity.PlayerActivity;

public class MusicService extends Service {

    public static final String CHANNEL_ID = "music_playback_channel";
    public static final int NOTIFICATION_ID = 1001;

    public static final String ACTION_TOGGLE = "com.huajun.music.ACTION_TOGGLE";
    public static final String ACTION_NEXT = "com.huajun.music.ACTION_NEXT";
    public static final String ACTION_PREV = "com.huajun.music.ACTION_PREV";
    public static final String ACTION_CLOSE = "com.huajun.music.ACTION_CLOSE";

    private PlayManager playManager;
    private NotificationReceiver receiver;
    private Song lastSong;
    private Bitmap currentCover;

    @Override
    public void onCreate() {
        super.onCreate();
        playManager = PlayManager.getInstance();
        playManager.init(getApplicationContext());
        createNotificationChannel();

        receiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TOGGLE);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PREV);
        filter.addAction(ACTION_CLOSE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }

        // 立即显示初始通知（mediaPlayback 前台服务要求 5 秒内启动）
        startForeground(NOTIFICATION_ID, buildNotification());

        playManager.getCurrentSong().observeForever(songObserver);
        playManager.getIsPlaying().observeForever(playingObserver);
    }

    private final Observer<Song> songObserver = song -> {
        if (song != null) {
            lastSong = song;
            loadCoverAndNotify(song);
        }
    };

    private final Observer<Boolean> playingObserver = playing -> {
        if (lastSong != null) updateNotification();
    };

    private void loadCoverAndNotify(Song song) {
        if (song.getPicUrl() != null && !song.getPicUrl().isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(song.getPicUrl())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            currentCover = resource;
                            updateNotification();
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}
                    });
        } else {
            currentCover = null;
            updateNotification();
        }
    }

    private void updateNotification() {
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    private Notification buildNotification() {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_music);
        if (lastSong != null) {
            views.setTextViewText(R.id.notif_title, lastSong.getName());
            views.setTextViewText(R.id.notif_artist, lastSong.getArtistName());
            if (currentCover != null) {
                views.setImageViewBitmap(R.id.notif_cover, currentCover);
            } else {
                views.setImageViewResource(R.id.notif_cover, R.drawable.ic_music_note);
            }
        }
        int playIcon = (playManager.getIsPlaying().getValue() != null && playManager.getIsPlaying().getValue())
                ? R.drawable.ic_pause : R.drawable.ic_play;
        views.setImageViewResource(R.id.notif_play_pause, playIcon);

        Intent toggleIntent = new Intent(ACTION_TOGGLE);
        Intent nextIntent = new Intent(ACTION_NEXT);
        Intent prevIntent = new Intent(ACTION_PREV);
        Intent closeIntent = new Intent(ACTION_CLOSE);

        PendingIntent togglePi = PendingIntent.getBroadcast(this, 0, toggleIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent nextPi = PendingIntent.getBroadcast(this, 1, nextIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent prevPi = PendingIntent.getBroadcast(this, 2, prevIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent closePi = PendingIntent.getBroadcast(this, 3, closeIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.notif_play_pause, togglePi);
        views.setOnClickPendingIntent(R.id.notif_next, nextPi);
        views.setOnClickPendingIntent(R.id.notif_prev, prevPi);
        views.setOnClickPendingIntent(R.id.notif_close, closePi);

        Intent contentIntent = new Intent(this, PlayerActivity.class);
        PendingIntent contentPi = PendingIntent.getActivity(this, 4, contentIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setCustomContentView(views)
                .setContentIntent(contentPi)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "音乐播放", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("华军音乐播放控制");
            channel.enableLights(false);
            channel.enableVibration(false);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
        playManager.getCurrentSong().removeObserver(songObserver);
        playManager.getIsPlaying().removeObserver(playingObserver);
        playManager.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_TOGGLE.equals(action)) {
                playManager.playPause();
            } else if (ACTION_NEXT.equals(action)) {
                playManager.next();
            } else if (ACTION_PREV.equals(action)) {
                playManager.previous();
            } else if (ACTION_CLOSE.equals(action)) {
                stopSelf();
            }
        }
    }
}
