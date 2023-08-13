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



//            AssetFileDescriptor fd = getAssets().openFd("tiantang.mp3");
//            mediaPlayer.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(), fd.getLength());//经过笔者的测试
////            mediaPlayer.setDataSource(this,Uri.parse("file:///assets/tiantang.mp3"));
            mediaPlayer.setDataSource("http://m801.music.126.net/20230813143103/5bbf8b88b6aac2310ccd73e7d1d7cf08/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/7906385495/3fe6/3101/afd2/24a263f3131cd868757b7cedfea3d897.mp3?authSecret=000001868dd9");
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