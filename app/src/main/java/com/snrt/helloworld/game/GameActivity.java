package com.snrt.helloworld.game;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.snrt.helloworld.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameBoardView(this));
    }
}