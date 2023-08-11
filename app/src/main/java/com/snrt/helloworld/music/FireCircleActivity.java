package com.snrt.helloworld.music;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.snrt.helloworld.R;

import java.io.IOException;

public class FireCircleActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private FireCircleView2 fireCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_circle);

        fireCircleView = findViewById(R.id.fireCircleView);

        // 初始化MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {



            AssetFileDescriptor fd = getAssets().openFd("tiantang.mp3");
            mediaPlayer.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(), fd.getLength());//经过笔者的测试
//            mediaPlayer.setDataSource(this,Uri.parse("file:///assets/tiantang.mp3"));
            mediaPlayer.prepare();
            // 开始播放音频和频谱监听
            mediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止播放和频谱监听
        mediaPlayer.release();
    }
}