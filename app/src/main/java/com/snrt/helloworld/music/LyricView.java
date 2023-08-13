package com.snrt.helloworld.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LyricView extends TextView {
    public static final int LINE_HEIGHT = 80;
    public static final int DEFAULT_TEXT_SIZE = 40;
    public static final int HIGHLIGHT_TEXT_SIZE = 50;
    private List<LyricParser.LyricLine> lyrics = new ArrayList<>();
    // 歌词数据
    private int currentLyricIndex;
    // 当前显示的歌词索引
    private Paint textPaint;
    private Paint highlightPaint;

    public LyricView(Context context) {
        this(context, null);
    }

    public LyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() { // 初始化画笔和颜色
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(DEFAULT_TEXT_SIZE);

        highlightPaint = new Paint();
        highlightPaint.setTextSize(HIGHLIGHT_TEXT_SIZE);
        highlightPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); // 绘制当前歌词和前后几行歌词

        int centerY = getHeight() / 2;
        // 绘制歌词
        for (int i = 0; i < lyrics.size(); i++) {
            LyricParser.LyricLine lyric = lyrics.get(i);
            float textWidth = textPaint.measureText(lyric.getContent());
            float x = (getWidth() - textWidth) / 2;
            float y = centerY + (i - currentLyricIndex) * LINE_HEIGHT;

            if (i == currentLyricIndex) {
                canvas.drawText(lyric.getContent(), x, y, highlightPaint);
            } else {
                canvas.drawText(lyric.getContent(), x, y, textPaint);
            }
        }
    }

    public void setLyrics(List<LyricParser.LyricLine> lyrics) {
        this.lyrics = lyrics;
    }

    public void setCurrentLyricIndex(int index) {
        this.currentLyricIndex = index;
        invalidate();
    }
    public void setCurrentProgress(long progressInMillis) {
        // Calculate the current line based on the progress
        // For example, you can divide the progress by a factor to determine the line
        currentLyricIndex = calculateCurrentLine(this.lyrics, progressInMillis);
        setCurrentLyricIndex(currentLyricIndex);
    }

    public int calculateCurrentLine(List<LyricParser.LyricLine> lyricLines, long currentProgressInMillis) {
        for (int i = 0; i < lyricLines.size(); i++) {
            LyricParser.LyricLine lyricLine = lyricLines.get(i);
            long startTime = lyricLine.getTimeInMillis();

            if (i < lyricLines.size() - 1) {
                long endTime = lyricLines.get(i + 1).getTimeInMillis();

                if (currentProgressInMillis >= startTime && currentProgressInMillis < endTime) {
                    return i;
                }
            } else {
                // Handle the last line
                if (currentProgressInMillis >= startTime) {
                    return i;
                }
            }
        }

        // Default to the first line if no match is found
        return 0;
    }


}


