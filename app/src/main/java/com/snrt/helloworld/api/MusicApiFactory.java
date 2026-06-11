package com.snrt.helloworld.api;

import com.snrt.helloworld.config.ApiConfig;

/**
 * 音乐API工厂类
 * 用于创建和管理音乐API服务实例
 * 支持切换不同的API提供商
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public class MusicApiFactory {

    /**
     * API提供商类型枚举
     */
    public enum ApiProvider {
        NETEASE_CLOUD,  // 网易云音乐
        // 可以扩展其他提供商：QQ_MUSIC, KUGOU, KUWO 等
    }

    // 当前使用的API提供商
    private static ApiProvider currentProvider = ApiProvider.NETEASE_CLOUD;

    // 网易云音乐API实例
    private static MusicApiService neteaseApiInstance;

    /**
     * 获取音乐API服务实例
     * 根据当前配置的提供商返回对应的实现
     *
     * @return MusicApiService实例
     */
    public static MusicApiService getMusicApi() {
        switch (currentProvider) {
            case NETEASE_CLOUD:
            default:
                if (neteaseApiInstance == null) {
                    neteaseApiInstance = NeteaseMusicApiImpl.getInstance();
                }
                return neteaseApiInstance;
        }
    }

    /**
     * 设置API提供商
     *
     * @param provider API提供商
     */
    public static void setProvider(ApiProvider provider) {
        currentProvider = provider;
        // 切换提供商时重置实例
        neteaseApiInstance = null;
    }

    /**
     * 获取当前API提供商
     *
     * @return 当前提供商
     */
    public static ApiProvider getCurrentProvider() {
        return currentProvider;
    }

    /**
     * 重新初始化API服务
     * 用于环境切换后刷新配置
     */
    public static void reinitialize() {
        neteaseApiInstance = null;
    }
}
