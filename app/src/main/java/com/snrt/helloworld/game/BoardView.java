package com.snrt.helloworld.game;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import com.snrt.helloworld.R;

public class BoardView extends View {

    // 定义棋盘大小
    int BOARD_SIZE = 15;
    int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

    // 定义棋子的类型
    int EMPTY = 0;
    int BLACK = 1;
    int WHITE = 2;

    // 定义当前玩家和游戏状态
    int currentPlayer = BLACK;
    boolean isGameFinished = false;

    private String gameName = "五子棋游戏";
    private String gameLog = "";
    private int cellWidth;
    private int cellHeight;

    private Paint blackPaint;
    private Paint whitePaint;
    private Paint emptyPaint;
    private Context context;
    private TextView gameLogTextView;

    private SoundPool soundPool;
    private int moveSoundId;
    private int winSoundId;
    private AlertDialog gameOverAlertDialog;

    public BoardView(Context context) {
        super(context);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint();
        initBoard();
        initSound();
    }

    private void initPaint() {
        // 初始化黑棋画笔
        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);

        // 初始化白棋画笔
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);

        // 初始化空画笔
        emptyPaint = new Paint();
        emptyPaint.setColor(Color.parseColor("#DDBB99"));
    }

    // 初始化音效
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initSound() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1) // 同时只能播放一个音效
                .setAudioAttributes(attributes)
                .build();

        // 加载音效文件
        moveSoundId = soundPool.load(getContext(), R.raw.move, 1);
        winSoundId = soundPool.load(getContext(), R.raw.win, 1);
    }

    // 在落子时播放音效
    private void playMoveSound() {
        soundPool.play(moveSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    // 在游戏胜利时播放胜利音效
    private void playWinSound() {
        soundPool.play(winSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    // 初始化棋盘
    private void initBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        // 将棋盘数组的所有元素设置为EMPTY，表示初始状态没有棋子
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    // 实现构造方法和必要的初始化

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    // 实现绘制棋盘和棋子的方法
    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制棋盘网格
        drawBoard(canvas);
        // 绘制棋子
        drawPieces(canvas);
    }


    private void drawBoard(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        cellWidth = width / BOARD_SIZE;
        cellHeight = height / BOARD_SIZE;

        Paint boardPaint = new Paint();
        boardPaint.setColor(Color.parseColor("#DDBB99")); // 设置背景色为棋盘木纹色
        canvas.drawRect(0, 0, width, height, boardPaint);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(2);

        // 绘制棋盘的横线
        for (int i = 0; i < BOARD_SIZE; i++) {
            int startY = i * cellHeight;
            int stopX = width;
            int stopY = startY;
            canvas.drawLine(0, startY, stopX, stopY, linePaint);
        }

        // 绘制棋盘的竖线
        for (int i = 0; i < BOARD_SIZE; i++) {
            int startX = i * cellWidth;
            int stopX = startX;
            int stopY = height;
            canvas.drawLine(startX, 0, stopX, stopY, linePaint);
        }

        // 绘制交叉点
        linePaint.setStyle(Paint.Style.FILL);
        int radius = 8; // 交叉点半径
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int centerX = j * cellWidth;
                int centerY = i * cellHeight;
                canvas.drawCircle(centerX, centerY, radius, linePaint);
            }
        }

        // 绘制游戏日志
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);
        int textWidth = (int) textPaint.measureText(gameLog);
        int textX = (width - textWidth) / 2;
        int textY = height + cellHeight / 2;
        canvas.drawText(gameLog, textX, textY, textPaint);
    }

    public void updateGameLog(String log) {
        this.gameLog = log;
        this.gameLogTextView.setText(gameLog);
    }

    private void drawPieces(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int cellSize = Math.min(width, height) / BOARD_SIZE;
        // 计算棋子半径
        int pieceRadius = cellSize / 2 - 4;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int centerX = j * cellSize + cellSize / 2;
                int centerY = i * cellSize + cellSize / 2;
                int piece = board[i][j];

                // 从画笔对象池获取画笔
                Paint paint = (piece == BLACK) ? blackPaint : ((piece == WHITE) ? whitePaint : emptyPaint) ;
                canvas.drawCircle(centerX, centerY, pieceRadius, paint);
            }
        }
    }


    // 实现处理用户点击事件的方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameFinished && event.getAction() == MotionEvent.ACTION_DOWN) {
            // 获取点击位置的行列坐标
            int col = (int) (event.getX() / cellWidth);
            int row = (int) (event.getY() / cellHeight);

            // 落子
            placePiece(row, col);
        }
        return true;
    }

    private void placePiece(int row, int col) {
        if (board[row][col] == EMPTY) {
            board[row][col] = currentPlayer;
            // 检查是否获胜
            if (checkWin(row, col, currentPlayer)) {
                isGameFinished = true;
                // 播放胜利音效
                playWinSound();
                // 显示胜利提示
                showWinMessage(currentPlayer);
            } else {
                // 切换玩家
                switchPlayer();
                // 更新游戏日志
                String log = (currentPlayer == BLACK) ? "黑棋下一步" : "白棋下一步";
                updateGameLog(log);
                // 播放落子音效
                playMoveSound();
                // 重绘界面
                invalidate();
            }
        }
    }

    // 显示胜利弹窗和重新开始功能
    private void showWinDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogWithBorder);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_win, null);

        TextView winMessageTextView = dialogView.findViewById(R.id.winMessageTextView);
        Button restartButton = dialogView.findViewById(R.id.restartButton);

        // 加载艺术字体
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "my_custom_font.ttf");
        winMessageTextView.setTypeface(typeface);

        winMessageTextView.setText(message);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                gameOverAlertDialog.cancel();
            }
        });

        builder.setView(dialogView)
                .setCancelable(false); // 设置对话框不可取消

        gameOverAlertDialog = builder.create();

        // 调整弹窗的高度为原来的两倍
        WindowManager.LayoutParams layoutParams = gameOverAlertDialog.getWindow().getAttributes();
        layoutParams.height = (int) (2 * layoutParams.height);
        gameOverAlertDialog.getWindow().setAttributes(layoutParams);

        gameOverAlertDialog.show();
    }

    private void showWinMessage(int player) {
        String message = (player == BLACK) ? "黑棋获胜！" : "白棋获胜！";
        showWinDialog(message);
    }

    // 实现检查是否获胜的方法
    // 检查是否获胜
    private boolean checkWin(int row, int col, int player) {
        // 水平方向检查
        int count = 0;
        for (int c = col - 4; c <= col + 4; c++) {
            if (c >= 0 && c < BOARD_SIZE && board[row][c] == player) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }

        // 垂直方向检查
        count = 0;
        for (int r = row - 4; r <= row + 4; r++) {
            if (r >= 0 && r < BOARD_SIZE && board[r][col] == player) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }

        // 主对角线方向检查
        count = 0;
        for (int i = -4; i <= 4; i++) {
            int r = row + i;
            int c = col + i;
            if (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == player) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }

        // 副对角线方向检查
        count = 0;
        for (int i = -4; i <= 4; i++) {
            int r = row + i;
            int c = col - i;
            if (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == player) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }

        return false;
    }

    // 实现切换玩家的方法
    private void switchPlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
    }


    // 实现重新开始游戏的方法
    public void restartGame() {
        // 重新初始化棋盘和游戏状态
        board = new int[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = BLACK;
        isGameFinished = false;
        // 强制重新绘制
        invalidate();
    }

    public void setLogTextView(TextView gameLogTextView) {
        this.gameLogTextView = gameLogTextView;
    }


    // 实现AI对手的方法（可选）


}



