package com.snrt.helloworld.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FireCircleView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Paint circlePaint;
    private byte[] fftData;

    public FireCircleView(Context context) {
        super(context);
        init();
    }

    public FireCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FireCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        circlePaint = new Paint();
        circlePaint.setStrokeWidth(5);
        circlePaint.setColor(Color.RED);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDrawing();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 不需要实现
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawing();
    }

    private void startDrawing() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Canvas canvas = surfaceHolder.lockCanvas();
                        if (canvas != null) {
                            drawFireCircle(canvas);
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void stopDrawing() {
        surfaceHolder.getSurface().release();
        surfaceHolder = null;
    }

    public void updateFftData(byte[] fftData) {
        this.fftData = fftData;
        postInvalidate(); // 请求重绘
    }

    private void drawFireCircle(Canvas canvas) {
        if (fftData != null) {
            float centerX = canvas.getWidth() / 2f;
            float centerY = canvas.getHeight() / 2f;
            float radius = Math.min(centerX, centerY) - 20;

            canvas.drawColor(Color.BLACK);

            float[] amplitudes = new float[fftData.length / 2];
            for (int i = 0; i < amplitudes.length; i++) {
                byte rfk = fftData[2 * i];
                byte ifk = fftData[2 * i + 1];
                float magnitude = (float) Math.hypot(rfk, ifk);
                amplitudes[i] = magnitude;
            }

            for (int i = 0; i < amplitudes.length; i++) {
                float angle = (float) (i * 2 * Math.PI / amplitudes.length);
                float amplitude = Math.abs(amplitudes[i]) / 128f * radius;

                float startX = centerX + (float) Math.cos(angle) * amplitude;
                float startY = centerY + (float) Math.sin(angle) * amplitude;
                float endX = centerX + (float) Math.cos(angle) * (amplitude + 20);
                float endY = centerY + (float) Math.sin(angle) * (amplitude + 20);

                canvas.drawLine(startX, startY, endX, endY, circlePaint);
            }
        }
    }
}
