package com.snrt.helloworld.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class FireCircleView2 extends View {

    private Paint circlePaint;

    public FireCircleView2(Context context) {
        super(context);
        init();
    }

    public FireCircleView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FireCircleView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.YELLOW);
        circlePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;
        int radius = canvas.getWidth() / 4;

        // 创建火焰圈的路径
        Path flamePath = createFlamePath(centerX, centerY, radius);

        // 应用透视变换
//        applyPerspectiveTransformation(flamePath, centerX, centerY, radius);

        // 绘制火焰圈
        canvas.drawPath(flamePath, circlePaint);
    }

    private Path createFlamePath(int centerX, int centerY, int radius) {
        Path path = new Path();
//
//        // 在这里根据你想要的火焰形状来构建路径
//        // 这里使用了示例的火焰形状
//
//        // 上半部分火焰
//        path.moveTo(centerX, centerY - radius);
//        path.lineTo(centerX - radius / 4, centerY - radius / 2);
//        path.lineTo(centerX + radius / 4, centerY - radius / 2);
//        path.lineTo(centerX, centerY - radius);
//        path.close();
//
//        // 下半部分火焰
//        path.moveTo(centerX, centerY);
//        path.lineTo(centerX - radius / 4, centerY + radius / 2);
//        path.lineTo(centerX + radius / 4, centerY + radius / 2);
//        path.lineTo(centerX, centerY);
//        path.close();

        path.addCircle(centerX,centerY,radius, Path.Direction.CW);

        return path;
    }

    private void applyPerspectiveTransformation(Path path, int centerX, int centerY, int radius) {
        // 设置透视效果的斜切系数
        float skewFactor = 0.2f;

        // 创建透视变换矩阵
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.setSkew(skewFactor, 0);
        matrix.postScale(1, 0.5f, centerX, centerY);

        // 对路径进行变换
        path.transform(matrix);
    }
}
