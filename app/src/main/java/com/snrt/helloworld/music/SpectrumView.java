package com.snrt.helloworld.music;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class SpectrumView extends View {

    private byte[] fftData;
    private Paint paint;

    public SpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        fftData = new byte[0];
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
    }

    public void updateFFTData(byte[] fft) {
        fftData = fft;
        invalidate(); // Trigger a redraw of the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(fftData.length == 0) {
            return;
        }
        show(canvas);
    }

    private void show4(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / fftData.length;

        for (int i = 0; i < fftData.length; i++) {
            int value = Math.abs(fftData[i]) + 128; // Take absolute value and adjust for visualization
            value = (int) (Math.log(value) * 20); // Apply logarithmic scale for better visibility
            float barHeight = value * height / 256.0f;

            // Set a vibrant color based on the value
            int color = Color.HSVToColor(new float[]{(float) value * 2, 1.0f, 1.0f});

            paint.setColor(color);

            float left = i * barWidth;
            float top = height - barHeight;
            float right = left + barWidth;
            float bottom = height;

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private void show3(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / (fftData.length*2);

        for (int i = 0; i < fftData.length*2; i++) {
            int value = fftData[i] + 128; // Adjust the value for visualization
            float barHeight = value * height / 128.0f;

            float left = i * barWidth;
            float top = height - barHeight;
            float right = left + barWidth;
            float bottom = height;

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private void show2(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / (fftData.length / 2);

        for (int i = 0; i < fftData.length / 2; i++) {
            int magnitude = fftData[i] + 128;
            int barHeight = magnitude * height / 128;

            canvas.drawRect(i * barWidth, height - barHeight, (i + 1) * barWidth, height, paint);
        }
    }

    private void show1(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / fftData.length;

        for (int i = 0; i < fftData.length; i++) {
            int value = fftData[i] + 128; // Adjust the value for visualization
            float barHeight = value * height / 256.0f;

            float left = i * barWidth;
            float top = height - barHeight;
            float right = left + barWidth;
            float bottom = height;

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private void show(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / fftData.length * 2 ;

        for (int i = 0; i < fftData.length; i++) {
            int value = Math.abs(fftData[i]) + 128; // Take absolute value and adjust for visualization
            float barHeight = value * height / 256.0f;

            // Set a gradient color based on the value
            int startColor = Color.HSVToColor(new float[]{(float) value * 2, 1.0f, 1.0f});
            int endColor = Color.HSVToColor(new float[]{(float) value * 2, 0.5f, 1.0f});
            Shader shader = new LinearGradient(0, 0, 0, barHeight, startColor, endColor, Shader.TileMode.CLAMP);
            paint.setShader(shader);

            float left = i * barWidth;
            float top = height - barHeight;
            float right = left + barWidth;
            float bottom = height;

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }
}
