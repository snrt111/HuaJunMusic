package com.snrt.helloworld;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.snrt.helloworld.game.GameActivity;
import com.snrt.helloworld.music.MusicHandler;
import com.snrt.helloworld.util.PermissionUtil;
import com.snrt.helloworld.video.VideoHandler;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Map<Integer, Handler> handlerMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMenuData();
        requestPermissions();
        initView();
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (!PermissionUtil.checkPermission(this, permissions)) {
            //没有权限，申请权限
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
        }
    }

    private void initView() {
        TextView textView = findViewById(R.id.web_chatgpt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInBrowser();
            }
        });

    }

    private void openInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://chat-shared2.zhile.io/"));
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.checkGrant(grantResults)) {
            Log.e(TAG, "权限申请成功");
        } else {
            Log.e(TAG, "权限申请失败");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initMenuData() {
        handlerMap.put(R.id.music, new MusicHandler(this));
        handlerMap.put(R.id.video, new VideoHandler(this));
        handlerMap.put(R.id.game, () -> startActivity(new Intent(this, GameActivity.class)));
        handlerMap.put(R.id.map, () -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://chat-shared2.zhile.io/"))));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        handlerMap.get(itemId).handler();
        return true;
    }

}