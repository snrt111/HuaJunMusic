package com.huajun.music.config;

public class ApiConfig {

    public static final String BASE_URL = "https://netease-cloud-music-api-psi.vercel.app";

    public static final int HTTP_TIMEOUT = 15000;

    public static final int DEFAULT_LIMIT = 30;

    public static final String DEFAULT_QUALITY = "standard";

    public static String buildUrl(String path) {
        return BASE_URL + path;
    }
}
