package com.snrt.helloworld.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SpectrogramView extends View {

    private byte[] fftData;
    private Paint paint;
    private int numRings = 10;
    private float[] ringHeights = new float[numRings];
    private Paint[] ringPaints = new Paint[numRings];

    // 在音频处理初始化的地方，设置一个初始的 maxEnergy 值
    double maxEnergy = 1.0; // 初始值，可以根据实际情况进行调整

    float maxRingHeight = 100.0f; // 可根据实际情况进行调整

    private int[] colors = {Color.parseColor("#FF0000"), // 红色
            Color.parseColor("#FFFF00"), // 黄色
            Color.parseColor("#00FF00"), // 绿色
            // 添加更多颜色...
    };

    public SpectrogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // 初始化火焰圈绘制参数
        for (int i = 0; i < numRings; i++) {
            ringPaints[i] = new Paint();
            ringPaints[i].setStyle(Paint.Style.FILL);
            // 计算颜色索引，例如根据频率段数量均匀分布颜色
            int colorIndex = i * colors.length / numRings;
            // 设置不同频率段的颜色
            ringPaints[i].setColor(colors[colorIndex]);
        }
    }

    private void init() {
        fftData = new byte[0];
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
    }

    public void updateFFTData(byte[] fft) {
        fftData = fft;
        // 计算频率段的能量
        for (int i = 0; i < numRings; i++) {


            double sampleRate = 44100.0; // 音频采样率，单位为 Hz
            int fftSize = fftData.length ; // FFT 大小，通常为缓冲区大小的两倍

// 希望分析的频率范围，例如从 20Hz 到 20000Hz
            double minFrequency = 20.0; // 最低频率，单位为 Hz
            double maxFrequency = 20000.0; // 最高频率，单位为 Hz

// 计算频率步长，即每个频率点对应的频率间隔
            double frequencyStep = sampleRate / fftSize;

// 计算频率段的起始索引和结束索引
            int startIndex = (int) Math.round(minFrequency / frequencyStep);
            int endIndex = Math.min((int) Math.round(maxFrequency / frequencyStep), fftSize / 2 - 1);

            double energy = 0;
            for (int j = startIndex; j <= endIndex; j++) {
                double real = fftData[j * 2];
                double imag = fftData[j * 2 + 1];
                energy += real * real + imag * imag;
            }

            // 更新频率段的能量，可以使用能量来控制火焰圈的高度
            ringHeights[i] = (float) (energy / maxEnergy);
        }

        // 更新绘制
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (fftData.length == 0) {
            return;
        }

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int maxRadius = Math.min(centerX, centerY);

        for (int i = 0; i < numRings; i++) {
            float radius = maxRadius * (ringHeights[i] / maxRingHeight);
            float alpha = (float) i / numRings * 255; // 根据火焰圈位置设置透明度
            ringPaints[i].setAlpha((int) alpha);

            RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawArc(rectF, 0, 360, true, ringPaints[i]);
        }
    }

}
