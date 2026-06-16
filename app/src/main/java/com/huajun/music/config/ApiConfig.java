package com.huajun.music.config;

/**
 * API配置类 - 管理多个音乐API提供商
 * 支持网易云音乐API、ALAPI、酷狗音乐API的切换
 */
public class ApiConfig {

    // ==================== 网易云音乐API配置 ====================
    public static final String NETEASE_BASE_URL = "https://netease-cloud-music-api-psi.vercel.app";
    public static final String NETEASE_NAME = "NetEase";

    // ==================== ALAPI配置 ====================
    public static final String ALAPI_BASE_URL = "https://v2.alapi.cn/api/music";
    public static final String ALAPI_NAME = "ALAPI";
    public static final String ALAPI_TOKEN = ""; // 需要用户申请token

    // ==================== 酷狗音乐API配置 ====================
    public static final String KUGOU_BASE_URL = "http://msearchcdn.kugou.com";
    public static final String KUGOU_SEARCH_URL = "http://msearchcdn.kugou.com/api/v3/search/song";
    public static final String KUGOU_SONG_URL = "http://trackercdnbj.kugou.com/i/v2/";
    public static final String KUGOU_LYRIC_URL = "http://mobileservice.kugou.com/api/v3/lyric/get";
    public static final String KUGOU_NAME = "KuGou";

    // 当前使用的API基础URL
    private static String currentBaseUrl = NETEASE_BASE_URL;
    private static String currentApiName = NETEASE_NAME;

    // 通用配置
    public static final int HTTP_TIMEOUT = 15000;
    public static final int DEFAULT_LIMIT = 30;
    public static final String DEFAULT_QUALITY = "standard";

    // API切换配置
    private static boolean autoSwitchEnabled = true;
    private static int maxRetryCount = 2;
    private static int currentRetryCount = 0;

    /**
     * API类型枚举
     */
    public enum ApiType {
        NETEASE,
        ALAPI,
        KUGOU
    }

    /**
     * 构建完整的API URL
     * @param path API路径
     * @return 完整URL
     */
    public static String buildUrl(String path) {
        return currentBaseUrl + path;
    }

    /**
     * 获取当前API类型
     * @return API类型
     */
    public static ApiType getCurrentApiType() {
        switch (currentApiName) {
            case ALAPI_NAME:
                return ApiType.ALAPI;
            case KUGOU_NAME:
                return ApiType.KUGOU;
            default:
                return ApiType.NETEASE;
        }
    }

    /**
     * 切换到指定API
     * @param apiType API类型
     */
    public static void switchApi(ApiType apiType) {
        switch (apiType) {
            case ALAPI:
                currentBaseUrl = ALAPI_BASE_URL;
                currentApiName = ALAPI_NAME;
                break;
            case KUGOU:
                currentBaseUrl = KUGOU_BASE_URL;
                currentApiName = KUGOU_NAME;
                break;
            default:
                currentBaseUrl = NETEASE_BASE_URL;
                currentApiName = NETEASE_NAME;
        }
        currentRetryCount = 0;
    }

    /**
     * 切换到网易云音乐API
     */
    public static void switchToNetease() {
        switchApi(ApiType.NETEASE);
    }

    /**
     * 切换到ALAPI
     */
    public static void switchToAlapi() {
        switchApi(ApiType.ALAPI);
    }

    /**
     * 切换到酷狗音乐API
     */
    public static void switchToKugou() {
        switchApi(ApiType.KUGOU);
    }

    /**
     * 重置到网易云音乐API
     */
    public static void resetToNetease() {
        switchApi(ApiType.NETEASE);
    }

    /**
     * 获取当前API名称
     * @return API名称
     */
    public static String getCurrentApiName() {
        return currentApiName;
    }

    /**
     * 获取当前基础URL
     * @return 基础URL
     */
    public static String getCurrentBaseUrl() {
        return currentBaseUrl;
    }

    /**
     * 设置是否启用自动切换
     * @param enabled 是否启用
     */
    public static void setAutoSwitchEnabled(boolean enabled) {
        autoSwitchEnabled = enabled;
    }

    /**
     * 是否启用自动切换
     * @return 是否启用
     */
    public static boolean isAutoSwitchEnabled() {
        return autoSwitchEnabled;
    }

    /**
     * 增加重试计数
     * @return 是否超过最大重试次数
     */
    public static boolean incrementRetry() {
        currentRetryCount++;
        return currentRetryCount >= maxRetryCount;
    }

    /**
     * 重置重试计数
     */
    public static void resetRetryCount() {
        currentRetryCount = 0;
    }

    /**
     * 设置ALAPI Token
     * @param token API token
     */
    public static void setAlapiToken(String token) {
        // 可以存储到SharedPreferences中
    }

    /**
     * 获取ALAPI Token
     * @return API token
     */
    public static String getAlapiToken() {
        // 可以从SharedPreferences中读取
        return ALAPI_TOKEN;
    }
    
    /**
     * 构建备用API URL
     * @param path API路径
     * @return 完整URL
     */
    public static String buildBackupUrl(String path) {
        return ALAPI_BASE_URL + path;
    }
    
    /**
     * 获取备用API Token（ALAPI Token）
     * @return API token
     */
    public static String getBackupToken() {
        return getAlapiToken();
    }
}
