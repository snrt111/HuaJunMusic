package com.huajun.music.api;

import android.util.Log;

import com.google.gson.Gson;
import com.huajun.music.config.ApiConfig;
import com.huajun.music.model.Album;
import com.huajun.music.model.Artist;
import com.huajun.music.model.Playlist;
import com.huajun.music.model.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicApi {

    private static final String TAG = "MusicApi";
    private static MusicApi instance;
    private final OkHttpClient client;
    private final Gson gson;

    private MusicApi() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(ApiConfig.HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(ApiConfig.HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        this.gson = new Gson();
    }

    public static synchronized MusicApi getInstance() {
        if (instance == null) instance = new MusicApi();
        return instance;
    }

    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("HTTP错误: " + response.code());
            return response.body() != null ? response.body().string() : "";
        }
    }

    public interface Callback<T> {
        void onSuccess(T data);
        void onError(String msg);
    }

    public void searchSongs(final String keyword, final int limit, final Callback<List<Song>> callback) {
        new Thread(() -> {
            try {
                List<Song> songs = searchSongsInternal(keyword, limit);
                if (callback != null) callback.onSuccess(songs);
            } catch (Exception e) {
                Log.e(TAG, "searchSongs error: " + e.getMessage());
                handleApiError(e, keyword, limit, callback);
            }
        }).start();
    }

    /**
     * 内部搜索方法，支持重试和切换
     */
    private List<Song> searchSongsInternal(String keyword, int limit) throws Exception {
        ApiConfig.ApiType currentApiType = ApiConfig.getCurrentApiType();
        
        try {
            switch (currentApiType) {
                case ALAPI:
                    // 使用ALAPI
                    return AlapiAdapter.getInstance().searchSongs(keyword, limit);
                case KUGOU:
                    // 使用酷狗音乐API
                    return KugouAdapter.getInstance().searchSongs(keyword, limit);
                default:
                    // 使用网易云音乐API
                    String url = ApiConfig.NETEASE_BASE_URL + "/cloudsearch?keywords=" + keyword + "&limit=" + limit;
                    String resp = get(url);
                    JSONObject obj = new JSONObject(resp);
                    List<Song> songs = new ArrayList<>();
                    
                    if (obj.has("result")) {
                        JSONObject result = obj.getJSONObject("result");
                        if (result.has("songs")) {
                            JSONArray arr = result.getJSONArray("songs");
                            songs = parseSongs(arr);
                            fetchSongUrls(songs);
                        }
                    }
                    return songs;
            }
        } catch (Exception e) {
            Log.e(TAG, "API调用失败: " + ApiConfig.getCurrentApiName() + ", 错误: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 处理API错误，尝试切换到备用API
     */
    private void handleApiError(Exception error, String keyword, int limit, Callback<List<Song>> callback) {
        if (!ApiConfig.isAutoSwitchEnabled()) {
            if (callback != null) callback.onError(error.getMessage());
            return;
        }

        // 如果已经超过最大重试次数，直接返回错误
        if (ApiConfig.incrementRetry()) {
            Log.e(TAG, "超过最大重试次数，放弃重试");
            if (callback != null) callback.onError(error.getMessage());
            ApiConfig.resetRetryCount();
            return;
        }

        // 尝试切换API
        ApiConfig.ApiType currentApiType = ApiConfig.getCurrentApiType();
        switch (currentApiType) {
            case NETEASE:
                // 网易云失败，尝试酷狗
                Log.w(TAG, "网易云API失败，切换到酷狗API");
                ApiConfig.switchToKugou();
                break;
            case KUGOU:
                // 酷狗失败，尝试ALAPI
                Log.w(TAG, "酷狗API失败，切换到ALAPI");
                ApiConfig.switchToAlapi();
                break;
            case ALAPI:
                // ALAPI失败，尝试网易云
                Log.w(TAG, "ALAPI失败，切换到网易云API");
                ApiConfig.switchToNetease();
                break;
        }

        // 检查新API是否可用
        boolean available = isCurrentApiAvailable();
        if (!available) {
            Log.e(TAG, "切换后的API不可用");
            // 继续尝试下一个API
            handleApiError(error, keyword, limit, callback);
            return;
        }

        // 重试请求
        try {
            List<Song> songs = searchSongsInternal(keyword, limit);
            Log.i(TAG, "切换API后调用成功: " + ApiConfig.getCurrentApiName());
            if (callback != null) callback.onSuccess(songs);
        } catch (Exception e) {
            Log.e(TAG, "切换API后仍失败: " + e.getMessage());
            // 继续尝试下一个API
            handleApiError(e, keyword, limit, callback);
        }
    }

    /**
     * 检查当前API是否可用
     */
    private boolean isCurrentApiAvailable() {
        ApiConfig.ApiType currentApiType = ApiConfig.getCurrentApiType();
        switch (currentApiType) {
            case ALAPI:
                return AlapiAdapter.getInstance().isAvailable();
            case KUGOU:
                return KugouAdapter.getInstance().isAvailable();
            default:
                // 网易云API默认认为可用
                return true;
        }
    }

    public void getTopSongs(final int limit, final Callback<List<Song>> callback) {
        new Thread(() -> {
            try {
                String url = ApiConfig.buildUrl("/top/song") + "?type=0&limit=" + limit;
                String resp = get(url);
                JSONObject obj = new JSONObject(resp);
                List<Song> songs = new ArrayList<>();
                if (obj.has("data")) {
                    JSONArray arr = obj.getJSONArray("data");
                    songs = parseSongs(arr);
                    fetchSongUrls(songs);
                }
                if (callback != null) callback.onSuccess(songs);
            } catch (Exception e) {
                Log.e(TAG, "getTopSongs error: " + e.getMessage());
                if (callback != null) callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getRecommendPlaylists(final int limit, final Callback<List<Playlist>> callback) {
        new Thread(() -> {
            try {
                String url = ApiConfig.buildUrl("/personalized") + "?limit=" + limit;
                String resp = get(url);
                JSONObject obj = new JSONObject(resp);
                List<Playlist> playlists = new ArrayList<>();
                if (obj.has("result")) {
                    JSONArray arr = obj.getJSONArray("result");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject item = arr.getJSONObject(i);
                        Playlist pl = new Playlist();
                        pl.setId(item.optString("id"));
                        pl.setName(item.optString("name"));
                        pl.setCoverImgUrl(item.optString("picUrl"));
                        pl.setPlayCount(item.optLong("playCount"));
                        pl.setTrackCount(item.optInt("trackCount"));
                        pl.setDescription(item.optString("copywriter"));
                        playlists.add(pl);
                    }
                }
                if (callback != null) callback.onSuccess(playlists);
            } catch (Exception e) {
                Log.e(TAG, "getRecommendPlaylists error: " + e.getMessage());
                if (callback != null) callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getPlaylistDetail(final String playlistId, final Callback<Playlist> callback) {
        new Thread(() -> {
            try {
                String url = ApiConfig.buildUrl("/playlist/detail") + "?id=" + playlistId;
                String resp = get(url);
                JSONObject obj = new JSONObject(resp);
                Playlist playlist = null;
                if (obj.has("playlist")) {
                    JSONObject plObj = obj.getJSONObject("playlist");
                    playlist = new Playlist();
                    playlist.setId(plObj.optString("id"));
                    playlist.setName(plObj.optString("name"));
                    playlist.setCoverImgUrl(plObj.optString("coverImgUrl"));
                    playlist.setDescription(plObj.optString("description"));
                    playlist.setTrackCount(plObj.optInt("trackCount"));
                    playlist.setPlayCount(plObj.optLong("playCount"));
                    List<Song> tracks = new ArrayList<>();
                    if (plObj.has("tracks")) {
                        JSONArray arr = plObj.getJSONArray("tracks");
                        int max = Math.min(arr.length(), 50);
                        for (int i = 0; i < max; i++) {
                            tracks.add(parseSong(arr.getJSONObject(i)));
                        }
                        fetchSongUrls(tracks);
                    }
                    playlist.setTracks(tracks);
                }
                if (callback != null) callback.onSuccess(playlist);
            } catch (Exception e) {
                Log.e(TAG, "getPlaylistDetail error: " + e.getMessage());
                if (callback != null) callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getArtistList(final int limit, final Callback<List<Artist>> callback) {
        new Thread(() -> {
            try {
                String url = ApiConfig.buildUrl("/artist/list") + "?type=-1&area=-1&limit=" + limit;
                String resp = get(url);
                JSONObject obj = new JSONObject(resp);
                List<Artist> artists = new ArrayList<>();
                if (obj.has("artists")) {
                    JSONArray arr = obj.getJSONArray("artists");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject item = arr.getJSONObject(i);
                        Artist artist = new Artist();
                        artist.setId(item.optString("id"));
                        artist.setName(item.optString("name"));
                        artist.setPicUrl(item.optString("picUrl"));
                        artists.add(artist);
                    }
                }
                if (callback != null) callback.onSuccess(artists);
            } catch (Exception e) {
                Log.e(TAG, "getArtistList error: " + e.getMessage());
                if (callback != null) callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getSongLyric(final String songId, final Callback<String> callback) {
        new Thread(() -> {
            try {
                String url = ApiConfig.buildUrl("/lyric") + "?id=" + songId;
                String resp = get(url);
                JSONObject obj = new JSONObject(resp);
                String lyric = "";
                if (obj.has("lrc") && !obj.isNull("lrc")) {
                    lyric = obj.getJSONObject("lrc").optString("lyric", "");
                }
                if (lyric.isEmpty() && obj.has("tlyric") && !obj.isNull("tlyric")) {
                    lyric = obj.getJSONObject("tlyric").optString("lyric", "");
                }
                if (callback != null) callback.onSuccess(lyric);
            } catch (Exception e) {
                Log.e(TAG, "getSongLyric error: " + e.getMessage());
                if (callback != null) callback.onError(e.getMessage());
            }
        }).start();
    }

    private void fetchSongUrls(List<Song> songs) {
        if (songs == null || songs.isEmpty()) return;
        try {
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < songs.size(); i++) {
                if (i > 0) ids.append(",");
                ids.append(songs.get(i).getId());
            }
            String url = ApiConfig.buildUrl("/song/url/v1") + "?id=" + ids.toString() + "&level=" + ApiConfig.DEFAULT_QUALITY;
            String resp = get(url);
            JSONObject obj = new JSONObject(resp);
            if (obj.has("data")) {
                JSONArray arr = obj.getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.getJSONObject(i);
                    String id = item.optString("id");
                    String songUrl = item.optString("url");
                    for (Song song : songs) {
                        if (song.getId().equals(id)) {
                            song.setUrl(songUrl);
                            song.setSize(item.optLong("size"));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "fetchSongUrls error: " + e.getMessage());
        }
    }

    private List<Song> parseSongs(JSONArray arr) throws Exception {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            songs.add(parseSong(arr.getJSONObject(i)));
        }
        return songs;
    }

    private Song parseSong(JSONObject obj) {
        Song song = new Song();
        song.setId(obj.optString("id"));
        song.setName(obj.optString("name"));
        song.setDuration(obj.optLong("dt"));
        List<Artist> artists = new ArrayList<>();
        if (obj.has("ar")) {
            JSONArray arArr = obj.optJSONArray("ar");
            if (arArr != null) {
                for (int i = 0; i < arArr.length(); i++) {
                    JSONObject ar = arArr.optJSONObject(i);
                    if (ar != null) {
                        Artist a = new Artist();
                        a.setId(ar.optString("id"));
                        a.setName(ar.optString("name"));
                        artists.add(a);
                    }
                }
            }
        }
        song.setArtists(artists);
        if (obj.has("al")) {
            JSONObject alObj = obj.optJSONObject("al");
            if (alObj != null) {
                Album album = new Album();
                album.setId(alObj.optString("id"));
                album.setName(alObj.optString("name"));
                album.setPicUrl(alObj.optString("picUrl"));
                song.setAlbum(album);
            }
        }
        return song;
    }
}
