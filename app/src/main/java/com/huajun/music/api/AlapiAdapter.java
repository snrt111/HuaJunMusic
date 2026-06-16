package com.huajun.music.api;

import android.util.Log;

import com.huajun.music.config.ApiConfig;
import com.huajun.music.model.Album;
import com.huajun.music.model.Artist;
import com.huajun.music.model.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ALAPI适配器 - 处理ALAPI的特殊逻辑
 * ALAPI文档: https://www.free-api.com/doc/369
 */
public class AlapiAdapter {

    private static final String TAG = "AlapiAdapter";
    private static AlapiAdapter instance;
    private final OkHttpClient client;

    private AlapiAdapter() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
    }

    public static synchronized AlapiAdapter getInstance() {
        if (instance == null) instance = new AlapiAdapter();
        return instance;
    }

    /**
     * ALAPI搜索歌曲
     * @param keyword 搜索关键词
     * @param limit 返回数量
     * @return 歌曲列表
     */
    public List<Song> searchSongs(String keyword, int limit) throws Exception {
        String url = ApiConfig.buildBackupUrl("/search") + 
                     "?keyword=" + keyword + 
                     "&limit=" + limit + 
                     "&token=" + ApiConfig.getBackupToken();
        
        String resp = get(url);
        JSONObject obj = new JSONObject(resp);
        
        // ALAPI返回格式: {code: 200, msg: "success", data: {songs: [...], songCount: 600}}
        if (obj.optInt("code") != 200) {
            throw new Exception("ALAPI错误: " + obj.optString("msg"));
        }
        
        List<Song> songs = new ArrayList<>();
        JSONObject data = obj.optJSONObject("data");
        if (data != null && data.has("songs")) {
            JSONArray arr = data.getJSONArray("songs");
            songs = parseAlapiSongs(arr);
        }
        
        return songs;
    }

    /**
     * 解析ALAPI返回的歌曲数据
     */
    private List<Song> parseAlapiSongs(JSONArray arr) throws Exception {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            songs.add(parseAlapiSong(arr.getJSONObject(i)));
        }
        return songs;
    }

    /**
     * 解析单个ALAPI歌曲数据
     */
    private Song parseAlapiSong(JSONObject obj) {
        Song song = new Song();
        song.setId(String.valueOf(obj.optLong("id")));
        song.setName(obj.optString("name"));
        song.setDuration(obj.optLong("duration"));
        
        // 解析艺术家
        List<Artist> artists = new ArrayList<>();
        if (obj.has("artists")) {
            JSONArray arArr = obj.optJSONArray("artists");
            if (arArr != null) {
                for (int i = 0; i < arArr.length(); i++) {
                    JSONObject ar = arArr.optJSONObject(i);
                    if (ar != null) {
                        Artist a = new Artist();
                        a.setId(String.valueOf(ar.optLong("id")));
                        a.setName(ar.optString("name"));
                        a.setPicUrl(ar.optString("img1v1Url"));
                        artists.add(a);
                    }
                }
            }
        }
        song.setArtists(artists);
        
        // 解析专辑
        if (obj.has("album")) {
            JSONObject alObj = obj.optJSONObject("album");
            if (alObj != null) {
                Album album = new Album();
                album.setId(String.valueOf(alObj.optLong("id")));
                album.setName(alObj.optString("name"));
                // ALAPI的专辑图片可能需要额外获取
                song.setAlbum(album);
            }
        }
        
        return song;
    }

    /**
     * HTTP GET请求
     */
    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP错误: " + response.code());
            }
            return response.body() != null ? response.body().string() : "";
        }
    }

    /**
     * 检查ALAPI是否可用
     * @return 是否可用
     */
    public boolean isAvailable() {
        try {
            String token = ApiConfig.getBackupToken();
            if (token == null || token.isEmpty()) {
                Log.w(TAG, "ALAPI token未设置");
                return false;
            }
            
            // 测试搜索请求
            String url = ApiConfig.buildBackupUrl("/search") + 
                         "?keyword=test&limit=1&token=" + token;
            String resp = get(url);
            JSONObject obj = new JSONObject(resp);
            return obj.optInt("code") == 200;
        } catch (Exception e) {
            Log.e(TAG, "ALAPI不可用: " + e.getMessage());
            return false;
        }
    }
}