package com.snrt.helloworld.api;

import com.snrt.helloworld.config.ApiConfig;
import com.snrt.helloworld.vo.ApiResponse;
import com.snrt.helloworld.vo.Artist;
import com.snrt.helloworld.vo.Playlist;
import com.snrt.helloworld.vo.SearchResult;
import com.snrt.helloworld.vo.Song;

import java.util.List;

/**
 * 音乐API使用示例
 * 演示如何使用新的API架构
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public class MusicApiExample {

    /**
     * 示例1：搜索歌曲
     */
    public void exampleSearchSongs() {
        // 获取API服务实例
        MusicApiService apiService = MusicApiFactory.getMusicApi();

        // 搜索歌曲
        ApiResponse<SearchResult> response = apiService.searchSongs("周杰伦", 1, 20);

        if (response.isSuccess()) {
            SearchResult result = response.getData();
            System.out.println("找到 " + result.getTotal() + " 首歌曲");

            List<Song> songs = result.getSongs();
            for (Song song : songs) {
                System.out.println("歌曲: " + song.getName());
                System.out.println("歌手: " + song.getArtistName());
                System.out.println("专辑: " + (song.getAlbum() != null ? song.getAlbum().getName() : "未知"));
                System.out.println("时长: " + song.getFormattedDuration());
                System.out.println("---");
            }
        } else {
            System.out.println("搜索失败: " + response.getMessage());
        }
    }

    /**
     * 示例2：获取歌手歌曲
     */
    public void exampleGetArtistSongs() {
        MusicApiService apiService = MusicApiFactory.getMusicApi();

        // 获取歌手ID 2517的歌曲（默认50首）
        ApiResponse<List<Song>> response = apiService.getArtistSongs("2517");

        if (response.isSuccess()) {
            List<Song> songs = response.getData();
            for (Song song : songs) {
                System.out.println("歌曲: " + song.getName());
                System.out.println("URL: " + song.getUrl());
                System.out.println("歌词: " + song.getLyric());
            }
        }
    }

    /**
     * 示例3：获取歌曲详情和播放URL
     */
    public void exampleGetSongDetail() {
        MusicApiService apiService = MusicApiFactory.getMusicApi();

        String songId = "2063487880";

        // 获取歌曲详情
        ApiResponse<Song> detailResponse = apiService.getSongDetail(songId);
        if (detailResponse.isSuccess()) {
            Song song = detailResponse.getData();
            System.out.println("歌曲名: " + song.getName());
            System.out.println("歌手: " + song.getArtistName());
        }

        // 获取播放URL（标准音质）
        ApiResponse<Song> urlResponse = apiService.getSongUrl(songId, "standard");
        if (urlResponse.isSuccess()) {
            System.out.println("播放URL: " + urlResponse.getData().getUrl());
        }

        // 获取歌词
        ApiResponse<String> lyricResponse = apiService.getSongLyric(songId);
        if (lyricResponse.isSuccess()) {
            System.out.println("歌词: " + lyricResponse.getData());
        }
    }

    /**
     * 示例4：获取歌单详情
     */
    public void exampleGetPlaylist() {
        MusicApiService apiService = MusicApiFactory.getMusicApi();

        // 获取歌单详情
        ApiResponse<Playlist> response = apiService.getPlaylistDetail("3778678");

        if (response.isSuccess()) {
            Playlist playlist = response.getData();
            System.out.println("歌单名: " + playlist.getName());
            System.out.println("描述: " + playlist.getDescription());
            System.out.println("歌曲数: " + playlist.getTrackCount());

            List<Song> tracks = playlist.getTracks();
            if (tracks != null) {
                for (Song song : tracks) {
                    System.out.println("- " + song.getName());
                }
            }
        }
    }

    /**
     * 示例5：获取歌手列表
     */
    public void exampleGetArtists() {
        MusicApiService apiService = MusicApiFactory.getMusicApi();

        // 获取华语男歌手（type=1, area=7）
        ApiResponse<List<Artist>> response = apiService.getArtistList(1, 7, 30);

        if (response.isSuccess()) {
            List<Artist> artists = response.getData();
            for (Artist artist : artists) {
                System.out.println("歌手: " + artist.getName());
                System.out.println("头像: " + artist.getPicUrl());
            }
        }
    }

    /**
     * 示例6：切换环境配置
     */
    public void exampleSwitchEnvironment() {
        // 切换到生产环境
        ApiConfig.setEnvironment(ApiConfig.Environment.PRODUCTION);

        // 重新初始化API服务
        MusicApiFactory.reinitialize();

        // 现在使用的是生产环境配置
        MusicApiService apiService = MusicApiFactory.getMusicApi();

        // 切换回开发环境
        ApiConfig.setEnvironment(ApiConfig.Environment.DEVELOPMENT);
        MusicApiFactory.reinitialize();
    }

    /**
     * 示例7：获取排行榜
     */
    public void exampleGetTopSongs() {
        MusicApiService apiService = MusicApiFactory.getMusicApi();

        // 获取新歌榜（type=0）
        ApiResponse<List<Song>> response = apiService.getTopSongs(0, 20);

        if (response.isSuccess()) {
            List<Song> songs = response.getData();
            System.out.println("=== 新歌榜 ===");
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                System.out.println((i + 1) + ". " + song.getName() + " - " + song.getArtistName());
            }
        }
    }
}
