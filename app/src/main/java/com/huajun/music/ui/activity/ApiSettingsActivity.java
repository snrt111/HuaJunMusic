package com.huajun.music.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huajun.music.R;
import com.huajun.music.api.AlapiAdapter;
import com.huajun.music.api.KugouAdapter;
import com.huajun.music.config.ApiConfig;

/**
 * API设置页面 - 配置音乐API相关参数
 * 支持网易云音乐API、酷狗音乐API、ALAPI三种API切换
 */
public class ApiSettingsActivity extends AppCompatActivity {

    private RadioGroup rgApiSelector;
    private RadioButton rbNetease;
    private RadioButton rbKugou;
    private RadioButton rbAlapi;
    
    private EditText etAlapiToken;
    private Switch switchAutoSwitch;
    
    private Button btnSave;
    private Button btnTestApi;
    private Button btnResetToNetease;
    
    private TextView tvCurrentApiStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_settings);

        initViews();
        loadCurrentSettings();
        setupListeners();
    }

    private void initViews() {
        rgApiSelector = findViewById(R.id.rg_api_selector);
        rbNetease = findViewById(R.id.rb_netease);
        rbKugou = findViewById(R.id.rb_kugou);
        rbAlapi = findViewById(R.id.rb_alapi);
        
        etAlapiToken = findViewById(R.id.et_alapi_token);
        switchAutoSwitch = findViewById(R.id.switch_auto_switch);
        
        btnSave = findViewById(R.id.btn_save);
        btnTestApi = findViewById(R.id.btn_test_api);
        btnResetToNetease = findViewById(R.id.btn_reset_to_netease);
        
        tvCurrentApiStatus = findViewById(R.id.tv_current_api_status);
    }

    private void loadCurrentSettings() {
        // 加载当前API选择
        String currentApi = ApiConfig.getCurrentApiName();
        switch (currentApi) {
            case ApiConfig.NETEASE_NAME:
                rbNetease.setChecked(true);
                break;
            case ApiConfig.KUGOU_NAME:
                rbKugou.setChecked(true);
                break;
            case ApiConfig.ALAPI_NAME:
                rbAlapi.setChecked(true);
                break;
        }
        
        // 加载ALAPI Token
        etAlapiToken.setText(ApiConfig.getAlapiToken());
        
        // 加载自动切换设置
        switchAutoSwitch.setChecked(ApiConfig.isAutoSwitchEnabled());
        
        // 更新状态显示
        updateApiStatus();
    }

    private void setupListeners() {
        // API选择切换
        rgApiSelector.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_netease:
                    ApiConfig.switchToNetease();
                    Toast.makeText(this, "已切换到网易云音乐API", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rb_kugou:
                    ApiConfig.switchToKugou();
                    Toast.makeText(this, "已切换到酷狗音乐API", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rb_alapi:
                    ApiConfig.switchToAlapi();
                    Toast.makeText(this, "已切换到ALAPI", Toast.LENGTH_SHORT).show();
                    break;
            }
            updateApiStatus();
        });

        // 保存设置
        btnSave.setOnClickListener(v -> saveSettings());

        // 测试当前API
        btnTestApi.setOnClickListener(v -> testCurrentApi());

        // 重置到网易云音乐API
        btnResetToNetease.setOnClickListener(v -> resetToNeteaseApi());
    }

    /**
     * 更新API状态显示
     */
    private void updateApiStatus() {
        String apiName = ApiConfig.getCurrentApiName();
        String statusText = "当前API: " + apiName;
        
        switch (apiName) {
            case ApiConfig.NETEASE_NAME:
                statusText += " (网易云音乐)";
                break;
            case ApiConfig.KUGOU_NAME:
                statusText += " (酷狗音乐)";
                break;
            case ApiConfig.ALAPI_NAME:
                statusText += " (ALAPI)";
                break;
        }
        
        tvCurrentApiStatus.setText(statusText);
    }

    /**
     * 保存设置
     */
    private void saveSettings() {
        String token = etAlapiToken.getText().toString().trim();
        boolean autoSwitch = switchAutoSwitch.isChecked();

        // 保存ALAPI Token
        if (!TextUtils.isEmpty(token)) {
            ApiConfig.setAlapiToken(token);
        }

        // 保存自动切换设置
        ApiConfig.setAutoSwitchEnabled(autoSwitch);

        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
    }

    /**
     * 测试当前API连接
     */
    private void testCurrentApi() {
        String currentApi = ApiConfig.getCurrentApiName();
        
        new Thread(() -> {
            boolean available = false;
            
            switch (currentApi) {
                case ApiConfig.NETEASE_NAME:
                    // 测试网易云API
                    available = testNeteaseApi();
                    break;
                case ApiConfig.KUGOU_NAME:
                    // 测试酷狗API
                    available = KugouAdapter.getInstance().isAvailable();
                    break;
                case ApiConfig.ALAPI_NAME:
                    // 测试ALAPI
                    String token = etAlapiToken.getText().toString().trim();
                    if (TextUtils.isEmpty(token)) {
                        runOnUiThread(() -> 
                            Toast.makeText(this, "请先输入ALAPI Token", Toast.LENGTH_SHORT).show());
                        return;
                    }
                    ApiConfig.setAlapiToken(token);
                    available = AlapiAdapter.getInstance().isAvailable();
                    break;
            }
            
            final boolean finalAvailable = available;
            runOnUiThread(() -> {
                if (finalAvailable) {
                    Toast.makeText(this, currentApi + " 连接成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, currentApi + " 连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * 测试网易云音乐API
     */
    private boolean testNeteaseApi() {
        try {
            String url = ApiConfig.NETEASE_BASE_URL + "/cloudsearch?keywords=test&limit=1";
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .build();
            
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .build();
            
            try (okhttp3.Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 重置到网易云音乐API
     */
    private void resetToNeteaseApi() {
        ApiConfig.resetToNetease();
        rbNetease.setChecked(true);
        updateApiStatus();
        Toast.makeText(this, "已重置到网易云音乐API", Toast.LENGTH_SHORT).show();
    }
}