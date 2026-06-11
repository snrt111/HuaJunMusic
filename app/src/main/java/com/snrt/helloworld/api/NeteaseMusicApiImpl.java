package com.snrt.helloworld.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snrt.helloworld.config.ApiConfig;
import com.snrt.helloworld.vo.Album;
import com.snrt.helloworld.vo.ApiResponse;
import com.snrt.helloworld.vo.Artist;
import com.snrt.helloworld.vo.Playlist;
import com.snrt.helloworld.vo.SearchResult;
import com.snrt.helloworld.vo.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网易云音乐API实现类
 * 实现MusicApiService接口，提供网易云音乐API的具体实现
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public class NeteaseMusicApiImpl implements MusicApiService {

    private static final String TAG = "NeteaseMusicApi";

    // Gson实例
    private final Gson gson;

    // OkHttpClient实例
    private final OkHttpClient httpClient;

    // 单例实例
    private static NeteaseMusicApiImpl instance;

    /**
     * 获取单例实例
     *
     * @return NeteaseMusicApiImpl实例
     */
    public static synchronized NeteaseMusicApiImpl getInstance() {
        if (instance == null) {
            instance = new NeteaseMusicApiImpl();
        }
        return instance;
    }

    private NeteaseMusicApiImpl() {
        // 初始化Gson
        this.gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();

        // 初始化OkHttpClient
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(ApiConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(ApiConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 发送HTTP GET请求
     *
     * @param url 请求URL
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP错误: " + response.code());
            }
            return response.body() != null ? response.body().string() : "";
        }
    }

    @Override
    public ApiResponse<SearchResult> searchSongs(String keyword, int page, int pageSize) {
        try {
            // 构建搜索URL
            String url = ApiConfig.buildUrl(ApiConfig.PATH_SEARCH) +
                    "?keywords=" + keyword +
                    "&offset=" + ((page - 1) * pageSize) +
                    "&limit=" + pageSize;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            // 解析搜索结果
            SearchResult result = new SearchResult();
            result.setKeyword(keyword);
            result.setPage(page);
            result.setPageSize(pageSize);

            if (jsonObject.has("result")) {
                JSONObject resultObj = jsonObject.getJSONObject("result");

                // 获取总数
                if (resultObj.has("songCount")) {
                    result.setTotal(resultObj.getInt("songCount"));
                }

                // 获取歌曲列表
                if (resultObj.has("songs")) {
                    JSONArray songsArray = resultObj.getJSONArray("songs");
                    List<Song> songs = parseSongsFromArray(songsArray);
                    result.setSongs(songs);
                }

                // 判断是否还有更多
                result.setHasMore(result.getSongs() != null &&
                        result.getSongs().size() == pageSize);
            }

            return ApiResponse.success(result);

        } catch (Exception e) {
            Log.e(TAG, "搜索歌曲失败: " + e.getMessage(), e);
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<Artist>> getArtistList(int type, int area, int limit) {
        try {
            String url = ApiConfig.buildUrl(ApiConfig.PATH_ARTIST_LIST) +
                    "?type=" + type +
                    "&area=" + area +
                    "&limit=" + limit;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            List<Artist> artists = new ArrayList<>();

            if (jsonObject.has("artists")) {
                JSONArray artistsArray = jsonObject.getJSONArray("artists");
                for (int i = 0; i < artistsArray.length(); i++) {
                    JSONObject artistObj = artistsArray.getJSONObject(i);
                    Artist artist = new Artist();
                    artist.setId(artistObj.optString("id"));
                    artist.setName(artistObj.optString("name"));
                    artist.setPicUrl(artistObj.optString("picUrl"));
                    artist.setAlias(artistObj.optString("alias"));
                    artists.add(artist);
                }
            }

            return ApiResponse.success(artists);

        } catch (Exception e) {
            Log.e(TAG, "获取歌手列表失败: " + e.getMessage(), e);
            return ApiResponse.error("获取歌手列表失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<Song>> getArtistSongs(String artistId, int limit) {
        try {
            String url = ApiConfig.buildUrl(ApiConfig.PATH_ARTIST_SONGS) +
                    "?id=" + artistId +
                    "&limit=" + limit;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            List<Song> songs = new ArrayList<>();

            if (jsonObject.has("songs")) {
                JSONArray songsArray = jsonObject.getJSONArray("songs");
                songs = parseSongsFromArray(songsArray);

                // 获取歌曲URL
                if (!songs.isEmpty()) {
                    List<String> ids = new ArrayList<>();
                    for (Song song : songs) {
                        ids.add(song.getId());
                    }

                    ApiResponse<List<Song>> urlResponse = getSongUrls(ids, ApiConfig.DEFAULT_QUALITY);
                    if (urlResponse.isSuccess() && urlResponse.getData() != null) {
                        // 将URL映射回歌曲对象
                        for (Song songWithUrl : urlResponse.getData()) {
                            for (Song song : songs) {
                                if (song.getId().equals(songWithUrl.getId())) {
                                    song.setUrl(songWithUrl.getUrl());
                                    break;
                                }
                            }
                        }
                    }

                    // 获取歌词
                    for (Song song : songs) {
                        ApiResponse<String> lyricResponse = getSongLyric(song.getId());
                        if (lyricResponse.isSuccess()) {
                            song.setLyric(lyricResponse.getData());
                        }
                    }
                }
            }

            return ApiResponse.success(songs);

        } catch (Exception e) {
            Log.e(TAG, "获取歌手歌曲失败: " + e.getMessage(), e);
            return ApiResponse.error("获取歌手歌曲失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Song> getSongUrl(String songId, String quality) {
        try {
            String url = ApiConfig.buildUrl(ApiConfig.PATH_SONG_URL) +
                    "?id=" + songId +
                    "&level=" + quality;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            Song song = new Song();
            song.setId(songId);

            if (jsonObject.has("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                if (dataArray.length() > 0) {
                    JSONObject dataObj = dataArray.getJSONObject(0);
                    song.setUrl(dataObj.optString("url"));
                    song.setSize(dataObj.optLong("size"));
                    song.setQuality(dataObj.optString("level"));
                }
            }

            return ApiResponse.success(song);

        } catch (Exception e) {
            Log.e(TAG, "获取歌曲URL失败: " + e.getMessage(), e);
            return ApiResponse.error("获取歌曲URL失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<Song>> getSongUrls(List<String> songIds, String quality) {
        try {
            String idStr = String.join(",", songIds);
            String url = ApiConfig.buildUrl(ApiConfig.PATH_SONG_URL) +
                    "?id=" + idStr +
                    "&level=" + quality;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            List<Song> songs = new ArrayList<>();

            if (jsonObject.has("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataObj = dataArray.getJSONObject(i);
                    Song song = new Song();
                    song.setId(dataObj.optString("id"));
                    song.setUrl(dataObj.optString("url"));
                    song.setSize(dataObj.optLong("size"));
                    song.setQuality(dataObj.optString("level"));
                    songs.add(song);
                }
            }

            return ApiResponse.success(songs);

        } catch (Exception e) {
            Log.e(TAG, "批量获取歌曲URL失败: " + e.getMessage(), e);
            return ApiResponse.error("批量获取歌曲URL失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Song> getSongDetail(String songId) {
        try {
            // 使用搜索接口获取歌曲详情
            String url = ApiConfig.buildUrl(ApiConfig.PATH_SEARCH) +
                    "?keywords=" + songId +
                    "&limit=1";

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            Song song = null;

            if (jsonObject.has("result") && jsonObject.getJSONObject("result").has("songs")) {
                JSONArray songsArray = jsonObject.getJSONObject("result").getJSONArray("songs");
                if (songsArray.length() > 0) {
                    song = parseSongFromJson(songsArray.getJSONObject(0));
                }
            }

            if (song != null) {
                return ApiResponse.success(song);
            } else {
                return ApiResponse.error("未找到歌曲详情");
            }

        } catch (Exception e) {
            Log.e(TAG, "获取歌曲详情失败: " + e.getMessage(), e);
            return ApiResponse.error("获取歌曲详情失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> getSongLyric(String songId) {
        try {
            String url = ApiConfig.buildUrl(ApiConfig.PATH_LYRIC) + "?id=" + songId;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            String lyric = "";

            // 优先获取翻译歌词
            if (jsonObject.has("tlyric") && !jsonObject.isNull("tlyric")) {
                JSONObject tlyricObj = jsonObject.getJSONObject("tlyric");
                if (tlyricObj.has("lyric")) {
                    lyric = tlyricObj.getString("lyric");
                }
            }

            // 如果没有翻译歌词，获取原歌词
            if (lyric.isEmpty() && jsonObject.has("lrc") && !jsonObject.isNull("lrc")) {
                JSONObject lrcObj = jsonObject.getJSONObject("lrc");
                if (lrcObj.has("lyric")) {
                    lyric = lrcObj.getString("lyric");
                }
            }

            return ApiResponse.success(lyric);

        } catch (Exception e) {
            Log.e(TAG, "获取歌词失败: " + e.getMessage(), e);
            return ApiResponse.error("获取歌词失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Playlist> getPlaylistDetail(String playlistId) {
        try {
            String url = ApiConfig.buildUrl(ApiConfig.PATH_PLAYLIST_DETAIL) +
                    "?id=" + playlistId;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            Playlist playlist = new Playlist();

            if (jsonObject.has("playlist")) {
                JSONObject playlistObj = jsonObject.getJSONObject("playlist");
                playlist.setId(playlistObj.optString("id"));
                playlist.setName(playlistObj.optString("name"));
                playlist.setDescription(playlistObj.optString("description"));
                playlist.setCoverImgUrl(playlistObj.optString("coverImgUrl"));
                playlist.setTrackCount(playlistObj.optInt("trackCount"));
                playlist.setPlayCount(playlistObj.optLong("playCount"));

                // 解析歌曲列表
                if (playlistObj.has("tracks")) {
                    JSONArray tracksArray = playlistObj.getJSONArray("tracks");
                    List<Song> songs = parseSongsFromArray(tracksArray);
                    playlist.setTracks(songs);
                }
            }

            return ApiResponse.success(playlist);

        } catch (Exception e) {
            Log.e(TAG, "获取歌单详情失败: " + e.getMessage(), e);
            return ApiResponse.error("获取歌单详情失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<Playlist>> getRecommendedPlaylists(int limit) {
        // 使用热门歌单作为推荐
        // 实际实现需要调用相应的API
        return ApiResponse.success(new ArrayList<>());
    }

    @Override
    public ApiResponse<List<Song>> getTopSongs(int type, int limit) {
        try {
            String url = ApiConfig.buildUrl(ApiConfig.PATH_TOP_SONGS) +
                    "?type=" + type +
                    "&limit=" + limit;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            List<Song> songs = new ArrayList<>();

            if (jsonObject.has("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                songs = parseSongsFromArray(dataArray);
            }

            return ApiResponse.success(songs);

        } catch (Exception e) {
            Log.e(TAG, "获取排行榜失败: " + e.getMessage(), e);
            return ApiResponse.error("获取排行榜失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<Song>> getRecommendedSongs(int limit) {
        try {
            String url = ApiConfig.buildUrl(ApiConfig.PATH_RECOMMEND_SONGS) +
                    "?limit=" + limit;

            String response = get(url);
            JSONObject jsonObject = new JSONObject(response);

            List<Song> songs = new ArrayList<>();

            if (jsonObject.has("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                songs = parseSongsFromArray(dataArray);
            }

            return ApiResponse.success(songs);

        } catch (Exception e) {
            Log.e(TAG, "获取推荐歌曲失败: " + e.getMessage(), e);
            return ApiResponse.error("获取推荐歌曲失败: " + e.getMessage());
        }
    }

    // ========== 私有辅助方法 ==========

    /**
     * 从JSON数组解析歌曲列表
     *
     * @param songsArray JSON数组
     * @return 歌曲列表
     * @throws JSONException JSON解析异常
     */
    private List<Song> parseSongsFromArray(JSONArray songsArray) throws JSONException {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < songsArray.length(); i++) {
            JSONObject songObj = songsArray.getJSONObject(i);
            Song song = parseSongFromJson(songObj);
            songs.add(song);
        }
        return songs;
    }

    /**
     * 从JSON对象解析歌曲信息
     *
     * @param songObj JSON对象
     * @return 歌曲对象
     * @throws JSONException JSON解析异常
     */
    private Song parseSongFromJson(JSONObject songObj) throws JSONException {
        Song song = new Song();
        song.setId(songObj.optString("id"));
        song.setName(songObj.optString("name"));
        song.setDuration(songObj.optLong("dt"));

        // 解析歌手信息
        if (songObj.has("ar") || songObj.has("artists")) {
            JSONArray artistsArray = songObj.has("ar") ?
                    songObj.getJSONArray("ar") : songObj.getJSONArray("artists");
            List<Artist> artists = new ArrayList<>();
            for (int j = 0; j < artistsArray.length(); j++) {
                JSONObject artistObj = artistsArray.getJSONObject(j);
                Artist artist = new Artist();
                artist.setId(artistObj.optString("id"));
                artist.setName(artistObj.optString("name"));
                artists.add(artist);
            }
            song.setArtists(artists);
        }

        // 解析专辑信息
        if (songObj.has("al") || songObj.has("album")) {
            JSONObject albumObj = songObj.has("al") ?
                    songObj.getJSONObject("al") : songObj.getJSONObject("album");
            Album album = new Album();
            album.setId(albumObj.optString("id"));
            album.setName(albumObj.optString("name"));
            album.setPicUrl(albumObj.optString("picUrl"));
            song.setAlbum(album);
            // 设置歌曲封面为专辑封面
            song.setPicUrl(album.getPicUrl());
        }

        return song;
    }
}
