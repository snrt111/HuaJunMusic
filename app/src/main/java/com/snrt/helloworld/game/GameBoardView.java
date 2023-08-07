package com.snrt.helloworld.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.snrt.helloworld.R;

public class GameBoardView extends LinearLayout {
    private TextView gameNameTextView;
    private TextView gameLogTextView;
    private BoardView boardView;

    public GameBoardView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_game_view, this, true);
        gameNameTextView = findViewById(R.id.gameNameTextView);
        gameLogTextView = findViewById(R.id.gameLogTextView);
        boardView = findViewById(R.id.boardView);

        // 设置游戏名称和游戏日志
        // 加载艺术字体
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "my_custom_font.ttf");
        gameNameTextView.setTypeface(typeface);
        gameNameTextView.setText("五子棋游戏");
        gameLogTextView.setTypeface(typeface);
        boardView.setLogTextView(gameLogTextView);
    }

}

