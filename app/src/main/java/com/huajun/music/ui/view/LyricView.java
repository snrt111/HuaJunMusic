package com.huajun.music.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.huajun.music.model.LyricLine;
import com.huajun.music.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class LyricView extends View {

    private List<LyricLine> lines = new ArrayList<>();
    private int currentLine = 0;
    private long currentTime = 0;

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float lineHeight;
    private float textSize;

    public LyricView(Context context) { this(context, null); }
    public LyricView(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public LyricView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        textSize = DensityUtil.dp2px(context, 14);
        lineHeight = DensityUtil.dp2px(context, 32);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.parseColor("#80FFFFFF"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        highlightPaint.setTextSize(textSize * 1.1f);
        highlightPaint.setColor(Color.WHITE);
        highlightPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setLyrics(List<LyricLine> lyrics) {
        this.lines = lyrics != null ? lyrics : new ArrayList<>();
        this.currentLine = 0;
        invalidate();
    }

    public void setCurrentTime(long time) {
        this.currentTime = time;
        int newLine = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (time >= lines.get(i).getTime()) newLine = i;
            else break;
        }
        if (newLine != currentLine) {
            currentLine = newLine;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lines.isEmpty()) {
            canvas.drawText("暂无歌词", (float) getWidth() / 2, (float) getHeight() / 2, textPaint);
            return;
        }
        float centerY = (float) getHeight() / 2;
        float centerX = (float) getWidth() / 2;
        for (int i = 0; i < lines.size(); i++) {
            float y = centerY + (i - currentLine) * lineHeight;
            if (y < -lineHeight || y > getHeight() + lineHeight) continue;
            Paint paint = (i == currentLine) ? highlightPaint : textPaint;
            canvas.drawText(lines.get(i).getText(), centerX, y, paint);
        }
    }
}
