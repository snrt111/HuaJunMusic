package com.snrt.helloworld.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GameBoardView extends View {

    private int numRows = 15;
    private int numCols = 15;
    private int cellSize;
    private Paint linePaint;
    // Add more variables for game logic
    private int[][] gameBoard = new int[numRows][numCols]; // 0: Empty, 1: Player 1, 2: Player 2
    private boolean isPlayer1Turn = true;

    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;

    private int currentTouchRow = -1;
    private int currentTouchCol = -1;

    public GameBoardView(Context context) {
        super(context);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w, h) / Math.max(numRows, numCols);
    }


    private void drawBoard(Canvas canvas) {
        for (int i = 0; i <= numRows; i++) {
            int y = i * cellSize;
            canvas.drawLine(0, y, getWidth(), y, linePaint);
        }
        for (int i = 0; i <= numCols; i++) {
            int x = i * cellSize;
            canvas.drawLine(x, 0, x, getHeight(), linePaint);
        }
    }

    // Implement touch event handling and game logic methods

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int col = (int) (event.getX() / cellSize);
            int row = (int) (event.getY() / cellSize);

            if (gameBoard[row][col] == 0) {
                currentTouchRow = row;
                currentTouchCol = col;
                gameBoard[row][col] = isPlayer1Turn ? PLAYER_1 : PLAYER_2;
                invalidate(); // Redraw the view

                if (checkWin(row, col)) {
                    String winner = isPlayer1Turn ? "Player 1" : "Player 2";
                    Toast.makeText(getContext(),winner +"获胜",Toast.LENGTH_LONG).show();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetGame();
                        }
                    }, 2000); // Delay in milliseconds
                } else {
                    isPlayer1Turn = !isPlayer1Turn;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                gameBoard[row][col] = 0;
            }
        }
        currentTouchRow = -1;
        currentTouchCol = -1;
        isPlayer1Turn = true;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);

        // Draw current touch indicator
        if (currentTouchRow != -1 && currentTouchCol != -1) {
            int centerX = currentTouchCol * cellSize + cellSize / 2;
            int centerY = currentTouchRow * cellSize + cellSize / 2;
            Paint indicatorPaint = new Paint();
            indicatorPaint.setColor(isPlayer1Turn ? Color.BLUE : Color.RED);
            indicatorPaint.setAlpha(100); // Set transparency
            canvas.drawCircle(centerX, centerY, cellSize / 3, indicatorPaint);
        }

        // Draw placed pieces
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (gameBoard[row][col] == PLAYER_1) {
                    drawPiece(canvas, col * cellSize, row * cellSize, Color.BLUE);
                } else if (gameBoard[row][col] == PLAYER_2) {
                    drawPiece(canvas, col * cellSize, row * cellSize, Color.RED);
                }
            }
        }
    }

    private void drawPiece(Canvas canvas, float x, float y, int color) {
        Paint piecePaint = new Paint();
        piecePaint.setColor(color);
        canvas.drawCircle(x + cellSize / 2, y + cellSize / 2, cellSize / 3, piecePaint);
    }

    private boolean checkWin(int row, int col) {
        int player = gameBoard[row][col];

        // Check horizontal
        int count = 1;
        for (int i = col - 1; i >= 0 && gameBoard[row][i] == player; i--) {
            count++;
        }
        for (int i = col + 1; i < numCols && gameBoard[row][i] == player; i++) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        // Check vertical
        count = 1;
        for (int i = row - 1; i >= 0 && gameBoard[i][col] == player; i--) {
            count++;
        }
        for (int i = row + 1; i < numRows && gameBoard[i][col] == player; i++) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        // Check diagonal (top-left to bottom-right)
        count = 1;
        for (int i = 1; row - i >= 0 && col - i >= 0 && gameBoard[row - i][col - i] == player; i++) {
            count++;
        }
        for (int i = 1; row + i < numRows && col + i < numCols && gameBoard[row + i][col + i] == player; i++) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        // Check diagonal (bottom-left to top-right)
        count = 1;
        for (int i = 1; row + i < numRows && col - i >= 0 && gameBoard[row + i][col - i] == player; i++) {
            count++;
        }
        for (int i = 1; row - i >= 0 && col + i < numCols && gameBoard[row - i][col + i] == player; i++) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        return false;
    }


}

