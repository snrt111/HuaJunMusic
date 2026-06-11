package com.snrt.helloworld.api;

import com.snrt.helloworld.config.ApiConfig;
import com.snrt.helloworld.vo.ApiResponse;
import com.snrt.helloworld.vo.Artist;
import com.snrt.helloworld.vo.Playlist;
import com.snrt.helloworld.vo.SearchResult;
import com.snrt.helloworld.vo.Song;

import java.util.List;

/**
 * 音乐API服务接口
 * 定义所有音乐相关的API操作
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public interface MusicApiService {

    // ========== 搜索相关 ==========

    /**
     * 搜索歌曲
     *
     * @param keyword  搜索关键词
     * @param page     页码（从1开始）
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    ApiResponse<SearchResult> searchSongs(String keyword, int page, int pageSize);

    /**
     * 搜索歌曲（默认第一页）
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    default ApiResponse<SearchResult> searchSongs(String keyword) {
        return searchSongs(keyword, 1, ApiConfig.DEFAULT_LIMIT);
    }

    // ========== 歌手相关 ==========

    /**
     * 获取歌手列表
     *
     * @param type 歌手类型（1:男歌手, 2:女歌手, 3:乐队）
     * @param area 地区（7:华语, 96:欧美, 8:日本, 16:韩国, 0:其他）
     * @param limit 数量限制
     * @return 歌手列表
     */
    ApiResponse<List<Artist>> getArtistList(int type, int area, int limit);

    /**
     * 获取歌手热门歌曲
     *
     * @param artistId 歌手ID
     * @param limit    数量限制
     * @return 歌曲列表
     */
    ApiResponse<List<Song>> getArtistSongs(String artistId, int limit);

    /**
     * 获取歌手热门歌曲（使用默认限制）
     *
     * @param artistId 歌手ID
     * @return 歌曲列表
     */
    default ApiResponse<List<Song>> getArtistSongs(String artistId) {
        return getArtistSongs(artistId, ApiConfig.DEFAULT_LIMIT);
    }

    // ========== 歌曲相关 ==========

    /**
     * 获取歌曲播放URL
     *
     * @param songId  歌曲ID
     * @param quality 音质等级
     * @return 歌曲信息（包含URL）
     */
    ApiResponse<Song> getSongUrl(String songId, String quality);

    /**
     * 获取歌曲播放URL（默认音质）
     *
     * @param songId 歌曲ID
     * @return 歌曲信息
     */
    default ApiResponse<Song> getSongUrl(String songId) {
        return getSongUrl(songId, ApiConfig.DEFAULT_QUALITY);
    }

    /**
     * 批量获取歌曲播放URL
     *
     * @param songIds 歌曲ID列表
     * @param quality 音质等级
     * @return 歌曲URL映射（key: songId, value: url）
     */
    ApiResponse<List<Song>> getSongUrls(List<String> songIds, String quality);

    /**
     * 获取歌曲详情
     *
     * @param songId 歌曲ID
     * @return 歌曲详情
     */
    ApiResponse<Song> getSongDetail(String songId);

    /**
     * 获取歌曲歌词
     *
     * @param songId 歌曲ID
     * @return 歌词内容
     */
    ApiResponse<String> getSongLyric(String songId);

    // ========== 歌单相关 ==========

    /**
     * 获取歌单详情
     *
     * @param playlistId 歌单ID
     * @return 歌单详情
     */
    ApiResponse<Playlist> getPlaylistDetail(String playlistId);

    /**
     * 获取推荐歌单
     *
     * @param limit 数量限制
     * @return 歌单列表
     */
    ApiResponse<List<Playlist>> getRecommendedPlaylists(int limit);

    // ========== 排行榜相关 ==========

    /**
     * 获取新歌榜/热歌榜
     *
     * @param type  榜单类型（0:新歌, 1:热歌）
     * @param limit 数量限制
     * @return 歌曲列表
     */
    ApiResponse<List<Song>> getTopSongs(int type, int limit);

    /**
     * 获取推荐歌曲
     *
     * @param limit 数量限制
     * @return 歌曲列表
     */
    ApiResponse<List<Song>> getRecommendedSongs(int limit);
}
