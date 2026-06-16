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
 * 酷狗音乐API适配器
 * 参考文档: https://www.cnblogs.com/wanxiangsucai/p/16934771.html
 */
public class KugouAdapter {

    private static final String TAG = "KugouAdapter";
    private static KugouAdapter instance;
    private final OkHttpClient client;

    private KugouAdapter() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
    }

    public static synchronized KugouAdapter getInstance() {
        if (instance == null) instance = new KugouAdapter();
        return instance;
    }

    /**
     * 搜索歌曲
     * @param keyword 搜索关键词
     * @param limit 返回数量
     * @return 歌曲列表
     */
    public List<Song> searchSongs(String keyword, int limit) throws Exception {
        String url = ApiConfig.KUGOU_SEARCH_URL + 
                     "?showtype=14&highlight=em&pagesize=" + limit + 
                     "&tag_aggr=1&tagtype=全部&plat=0&sver=5&keyword=" + keyword + 
                     "&correct=1&api_ver=1&version=9108&page=1&area_code=1&tag=1&with_res_tag=1";
        
        String resp = get(url);
        JSONObject obj = new JSONObject(resp);
        
        // 酷狗API返回格式: {status: 1, data: {lists: [...]}}
        if (obj.optInt("status") != 1) {
            throw new Exception("酷狗API错误: " + obj.optString("error"));
        }
        
        List<Song> songs = new ArrayList<>();
        JSONObject data = obj.optJSONObject("data");
        if (data != null && data.has("lists")) {
            JSONArray arr = data.getJSONArray("lists");
            songs = parseKugouSongs(arr);
            // 获取歌曲播放URL
            fetchSongUrls(songs);
        }
        
        return songs;
    }

    /**
     * 解析酷狗返回的歌曲数据
     */
    private List<Song> parseKugouSongs(JSONArray arr) throws Exception {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            songs.add(parseKugouSong(arr.getJSONObject(i)));
        }
        return songs;
    }

    /**
     * 解析单个酷狗歌曲数据
     */
    private Song parseKugouSong(JSONObject obj) {
        Song song = new Song();
        song.setId(String.valueOf(obj.optLong("Audioid")));
        song.setName(obj.optString("SongName"));
        song.setDuration(obj.optLong("Duration") * 1000); // 酷狗返回的是秒，转换为毫秒
        
        // 解析艺术家
        List<Artist> artists = new ArrayList<>();
        Artist artist = new Artist();
        artist.setId(String.valueOf(obj.optLong("SingerId")));
        artist.setName(obj.optString("SingerName"));
        artists.add(artist);
        song.setArtists(artists);
        
        // 解析专辑
        Album album = new Album();
        album.setId(String.valueOf(obj.optLong("AlbumID")));
        album.setName(obj.optString("AlbumName"));
        album.setCoverUrl(obj.optString("AlbumImg"));
        song.setAlbum(album);
        
        // 保存酷狗特有字段用于获取播放URL
        song.setHash(obj.optString("FileHash"));
        song.setAlbumId(String.valueOf(obj.optLong("AlbumID")));
        
        return song;
    }

    /**
     * 获取歌曲播放URL
     * 酷狗需要单独获取播放链接
     */
    private void fetchSongUrls(List<Song> songs) {
        if (songs == null || songs.isEmpty()) return;
        
        for (Song song : songs) {
            try {
                String hash = song.getHash();
                String albumId = song.getAlbumId();
                
                if (hash != null && !hash.isEmpty() && albumId != null && !albumId.isEmpty()) {
                    String url = ApiConfig.KUGOU_SONG_URL + 
                                 "?album_audio_id=" + song.getId() + 
                                 "&behavior=play&cmd=25&album_id=" + albumId + 
                                 "&hash=" + hash + "&userid=0&pid=2&version=9108&area_code=1&appid=1005";
                    
                    String resp = get(url);
                    JSONObject obj = new JSONObject(resp);
                    
                    if (obj.has("data") && obj.getJSONArray("data").length() > 0) {
                        JSONObject data = obj.getJSONArray("data").getJSONObject(0);
                        String playUrl = data.optString("play_url");
                        if (playUrl != null && !playUrl.isEmpty()) {
                            song.setUrl(playUrl);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "获取酷狗歌曲URL失败: " + e.getMessage());
            }
        }
    }

    /**
     * 获取歌词
     * @param hash 歌曲hash
     * @param songId 歌曲ID
     * @return 歌词内容
     */
    public String getLyric(String hash, String songId) throws Exception {
        String url = ApiConfig.KUGOU_LYRIC_URL + 
                     "?ver=1&hash=" + hash + "&songid=" + songId + "&dtype=0";
        
        String resp = get(url);
        JSONObject obj = new JSONObject(resp);
        
        if (obj.optInt("status") != 1) {
            throw new Exception("获取歌词失败");
        }
        
        JSONObject data = obj.optJSONObject("data");
        if (data != null) {
            String content = data.optString("content");
            // 处理歌词格式转换
            return convertLyricFormat(content);
        }
        
        return "";
    }

    /**
     * 转换酷狗歌词格式
     */
    private String convertLyricFormat(String content) {
        if (content == null || content.isEmpty()) return "";
        
        // 酷狗歌词格式: [00:00.000]歌词内容
        // 转换为标准格式: [00:00.00]歌词内容
        return content.replaceAll("\\[(\\d{2}):(\\d{2})\\.(\\d{3})\\]", "[$1:$2.$3]");
    }

    /**
     * 获取热门歌单
     * @param limit 返回数量
     * @return 歌单列表JSON
     */
    public String getHotPlaylists(int limit) throws Exception {
        String url = "http://mobilecdnbj.kugou.com/api/v5/special/recommend?recommend_expire=0&plat=0&uid=0&version=9108&page=1&area_code=1&appid=1005&pagesize=" + limit;
        return get(url);
    }

    /**
     * 获取排行榜列表
     * @return 排行榜列表JSON
     */
    public String getRankList() throws Exception {
        String url = "http://mobilecdnbj.kugou.com/api/v3/rank/list?version=9108&plat=0&showtype=2&parentid=0&apiver=6&area_code=1&withsong=1&with_res_tag=1";
        return get(url);
    }

    /**
     * 获取热门歌手
     * @param limit 返回数量
     * @return 歌手列表JSON
     */
    public String getHotSingers(int limit) throws Exception {
        String url = "http://mobilecdnbj.kugou.com/api/v5/singer/list?version=9108&showtype=1&plat=0&sextype=0&sort=1&pagesize=" + limit + "&type=0&page=1&musician=0";
        return get(url);
    }

    /**
     * HTTP GET请求
     */
    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Referer", "http://m.kugou.com/")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP错误: " + response.code());
            }
            return response.body() != null ? response.body().string() : "";
        }
    }

    /**
     * 检查酷狗API是否可用
     * @return 是否可用
     */
    public boolean isAvailable() {
        try {
            String url = "http://msearchcdn.kugou.com/api/v3/search/song?showtype=14&keyword=test&pagesize=1";
            String resp = get(url);
            JSONObject obj = new JSONObject(resp);
            return obj.optInt("status") == 1;
        } catch (Exception e) {
            Log.e(TAG, "酷狗API不可用: " + e.getMessage());
            return false;
        }
    }
}