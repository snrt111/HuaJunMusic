package com.snrt.helloworld.config;

/**
 * API配置管理类
 * 支持开发环境和生产环境切换
 * 集中管理所有API相关配置
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public class ApiConfig {

    /**
     * 环境类型枚举
     */
    public enum Environment {
        DEVELOPMENT,
        PRODUCTION
    }

    // 当前环境配置，默认为开发环境
    private static Environment currentEnvironment = Environment.DEVELOPMENT;

    // ========== 开发环境配置 ==========
    private static final String DEV_BASE_URL = "http://10.0.2.2:3000";
    private static final int DEV_TIMEOUT = 10000; // 10秒超时

    // ========== 生产环境配置 ==========
    // 使用公共API服务或自部署服务
    private static final String PROD_BASE_URL = "https://netease-cloud-music-api-psi.vercel.app";
    private static final int PROD_TIMEOUT = 15000; // 15秒超时

    // ========== API路径常量 ==========
    public static final String PATH_ARTIST_LIST = "/artist/list";
    public static final String PATH_ARTIST_SONGS = "/artist/songs";
    public static final String PATH_SONG_URL = "/song/url/v1";
    public static final String PATH_LYRIC = "/lyric";
    public static final String PATH_SEARCH = "/cloudsearch";
    public static final String PATH_PLAYLIST_DETAIL = "/playlist/detail";
    public static final String PATH_TOP_SONGS = "/top/song";
    public static final String PATH_RECOMMEND_SONGS = "/recommend/songs";

    // ========== 默认参数 ==========
    public static final int DEFAULT_LIMIT = 50;
    public static final String DEFAULT_QUALITY = "standard";

    /**
     * 获取当前环境的Base URL
     *
     * @return 基础URL
     */
    public static String getBaseUrl() {
        switch (currentEnvironment) {
            case PRODUCTION:
                return PROD_BASE_URL;
            case DEVELOPMENT:
            default:
                return DEV_BASE_URL;
        }
    }

    /**
     * 获取当前环境的超时时间
     *
     * @return 超时时间（毫秒）
     */
    public static int getTimeout() {
        switch (currentEnvironment) {
            case PRODUCTION:
                return PROD_TIMEOUT;
            case DEVELOPMENT:
            default:
                return DEV_TIMEOUT;
        }
    }

    /**
     * 设置当前环境
     *
     * @param environment 环境类型
     */
    public static void setEnvironment(Environment environment) {
        currentEnvironment = environment;
    }

    /**
     * 获取当前环境
     *
     * @return 当前环境类型
     */
    public static Environment getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * 判断是否为开发环境
     *
     * @return true表示开发环境
     */
    public static boolean isDevelopment() {
        return currentEnvironment == Environment.DEVELOPMENT;
    }

    /**
     * 构建完整API URL
     *
     * @param path API路径
     * @return 完整URL
     */
    public static String buildUrl(String path) {
        return getBaseUrl() + path;
    }
}
