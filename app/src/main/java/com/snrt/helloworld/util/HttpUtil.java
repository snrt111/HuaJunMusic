package com.snrt.helloworld.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snrt.helloworld.api.MusicApiFactory;
import com.snrt.helloworld.api.MusicApiService;
import com.snrt.helloworld.config.ApiConfig;
import com.snrt.helloworld.music.MusicVO;
import com.snrt.helloworld.vo.ApiResponse;
import com.snrt.helloworld.vo.Artist;
import com.snrt.helloworld.vo.SearchResult;
import com.snrt.helloworld.vo.Song;
import com.snrt.helloworld.vo.SongInfo;
import com.snrt.helloworld.vo.SongListVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HTTP工具类（兼容版本）
 * 保留原有接口以兼容旧代码，内部使用新的API架构
 *
 * @deprecated 建议使用 {@link MusicApiFactory#getMusicApi()} 获取API服务
 * @author HuaJun Music Team
 * @version 2.0
 */
public class HttpUtil {

    /**
     * @deprecated 使用 ApiConfig.getBaseUrl() 替代
     */
    @Deprecated
    public static final String HTTP_IP_PORT = ApiConfig.getBaseUrl();

    /**
     * @deprecated 使用 ApiConfig.DEFAULT_LIMIT 替代
     */
    @Deprecated
    public static final Integer LIMIT_COUNT = ApiConfig.DEFAULT_LIMIT;

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(ApiConfig.getTimeout(), TimeUnit.MILLISECONDS)
            .readTimeout(ApiConfig.getTimeout(), TimeUnit.MILLISECONDS)
            .build();

    /**
     * 发送HTTP GET请求
     *
     * @param url 请求URL
     * @return 响应字符串
     * @deprecated 建议使用 MusicApiService 接口
     */
    @Deprecated
    public static String getData(String url) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : "";
        } catch (IOException e) {
            throw new RuntimeException("HTTP请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取歌手列表
     *
     * @return 歌手信息列表
     * @deprecated 建议使用 MusicApiService.getArtistList()
     */
    @Deprecated
    public static List<SongInfo> getSongerList() {
        MusicApiService apiService = MusicApiFactory.getMusicApi();
        ApiResponse<List<Artist>> response = apiService.getArtistList(1, 7, 30);

        List<SongInfo> result = new ArrayList<>();
        if (response.isSuccess() && response.getData() != null) {
            for (Artist artist : response.getData()) {
                SongInfo info = new SongInfo();
                info.setId(artist.getId());
                info.setName(artist.getName());
                result.add(info);
            }
        }
        return result;
    }

    /**
     * 获取指定歌手的歌曲列表
     * 默认使用歌手ID 2517（薛之谦）
     *
     * @return 音乐VO列表
     * @deprecated 建议使用 MusicApiService.getArtistSongs()
     */
    @Deprecated
    public static List<MusicVO> getSongList() {
        // 默认获取歌手ID 2517的歌曲
        return getSongListByArtist("2517", LIMIT_COUNT);
    }

    /**
     * 获取指定歌手的歌曲列表
     *
     * @param artistId 歌手ID
     * @param limit    数量限制
     * @return 音乐VO列表
     */
    public static List<MusicVO> getSongListByArtist(String artistId, int limit) {
        MusicApiService apiService = MusicApiFactory.getMusicApi();
        ApiResponse<List<Song>> response = apiService.getArtistSongs(artistId, limit);

        List<MusicVO> result = new ArrayList<>();
        if (response.isSuccess() && response.getData() != null) {
            for (Song song : response.getData()) {
                MusicVO vo = convertSongToMusicVO(song);
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 搜索歌曲
     *
     * @param keyword 搜索关键词
     * @param limit   数量限制
     * @return 音乐VO列表
     */
    public static List<MusicVO> searchSongs(String keyword, int limit) {
        MusicApiService apiService = MusicApiFactory.getMusicApi();
        ApiResponse<SearchResult> response = apiService.searchSongs(keyword, 1, limit);

        List<MusicVO> result = new ArrayList<>();
        if (response.isSuccess() && response.getData() != null) {
            SearchResult searchResult = response.getData();
            if (searchResult.getSongs() != null) {
                // 获取歌曲URL
                List<String> ids = new ArrayList<>();
                for (Song song : searchResult.getSongs()) {
                    ids.add(song.getId());
                }

                ApiResponse<List<Song>> urlResponse = apiService.getSongUrls(ids, ApiConfig.DEFAULT_QUALITY);
                Map<String, String> urlMap = new HashMap<>();
                if (urlResponse.isSuccess() && urlResponse.getData() != null) {
                    for (Song song : urlResponse.getData()) {
                        urlMap.put(song.getId(), song.getUrl());
                    }
                }

                // 获取歌词并构建MusicVO
                for (Song song : searchResult.getSongs()) {
                    song.setUrl(urlMap.get(song.getId()));

                    // 获取歌词
                    ApiResponse<String> lyricResponse = apiService.getSongLyric(song.getId());
                    if (lyricResponse.isSuccess()) {
                        song.setLyric(lyricResponse.getData());
                    }

                    MusicVO vo = convertSongToMusicVO(song);
                    result.add(vo);
                }
            }
        }
        return result;
    }

    /**
     * 获取歌曲播放URL
     *
     * @param ids 歌曲ID列表
     * @return URL映射（key: songId, value: url）
     * @deprecated 建议使用 MusicApiService.getSongUrls()
     */
    @Deprecated
    public static Map<String, String> getSongUrl(List<String> ids) {
        MusicApiService apiService = MusicApiFactory.getMusicApi();
        ApiResponse<List<Song>> response = apiService.getSongUrls(ids, ApiConfig.DEFAULT_QUALITY);

        Map<String, String> urlMap = new HashMap<>();
        if (response.isSuccess() && response.getData() != null) {
            for (Song song : response.getData()) {
                urlMap.put(song.getId(), song.getUrl());
            }
        }
        return urlMap;
    }

    /**
     * 获取歌曲歌词
     *
     * @param id 歌曲ID
     * @return 歌词内容
     * @deprecated 建议使用 MusicApiService.getSongLyric()
     */
    @Deprecated
    public static String getSongLyric(String id) {
        MusicApiService apiService = MusicApiFactory.getMusicApi();
        ApiResponse<String> response = apiService.getSongLyric(id);
        return response.isSuccess() ? response.getData() : "";
    }

    /**
     * 将Song对象转换为MusicVO对象
     * 用于兼容旧的UI层代码
     *
     * @param song Song对象
     * @return MusicVO对象
     */
    private static MusicVO convertSongToMusicVO(Song song) {
        MusicVO vo = new MusicVO();
        vo.setId(song.getId());
        vo.setName(song.getName());
        vo.setUrl(song.getUrl());
        vo.setLyric(song.getLyric());

        // 处理时长
        if (song.getDuration() != null) {
            vo.setDuration(String.valueOf(song.getDuration()));
        }

        // 处理文件大小
        if (song.getSize() != null) {
            vo.setSize(String.valueOf(song.getSize()));
        }

        return vo;
    }

    /**
     * 将MusicVO列表转换为Song列表
     * 用于新的API架构
     *
     * @param musicVOList MusicVO列表
     * @return Song列表
     */
    public static List<Song> convertMusicVOListToSongs(List<MusicVO> musicVOList) {
        List<Song> songs = new ArrayList<>();
        if (musicVOList == null) {
            return songs;
        }

        for (MusicVO vo : musicVOList) {
            songs.add(convertMusicVOToSong(vo));
        }
        return songs;
    }

    /**
     * 将MusicVO对象转换为Song对象
     *
     * @param vo MusicVO对象
     * @return Song对象
     */
    public static Song convertMusicVOToSong(MusicVO vo) {
        Song song = new Song();
        song.setId(vo.getId());
        song.setName(vo.getName());
        song.setUrl(vo.getUrl());
        song.setLyric(vo.getLyric());

        if (vo.getDuration() != null) {
            try {
                song.setDuration(Long.parseLong(vo.getDuration()));
            } catch (NumberFormatException e) {
                // 忽略转换错误
            }
        }

        if (vo.getSize() != null) {
            try {
                song.setSize(Long.parseLong(vo.getSize()));
            } catch (NumberFormatException e) {
                // 忽略转换错误
            }
        }

        return song;
    }
}
