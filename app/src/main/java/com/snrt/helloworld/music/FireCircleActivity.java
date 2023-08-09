package com.snrt.helloworld.music;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.snrt.helloworld.R;

public class FireCircleActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private FireCircleView2 fireCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_circle);

        fireCircleView = findViewById(R.id.fireCircleView);

//        // 初始化MediaPlayer
//        mediaPlayer = MediaPlayer.create(this, R.raw.yinyue);
//        mediaPlayer.setLooping(true);
//
//        // 初始化Visualizer
//        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
//        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
//        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
//            @Override
//            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
//
//            }
//
//            @Override
//            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
//                fireCircleView.updateFftData(fft);
//            }
//        }, Visualizer.getMaxCaptureRate() / 2, false, true);
//
//        // 开始播放音频和频谱监听
//        mediaPlayer.start();
//        visualizer.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 停止播放和频谱监听
//        visualizer.setEnabled(false);
//        visualizer.release();
//        mediaPlayer.release();
    }
}