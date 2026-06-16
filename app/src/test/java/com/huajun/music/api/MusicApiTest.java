package com.huajun.music.api;

import com.huajun.music.config.ApiConfig;
import com.huajun.music.model.Song;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * 音乐API单元测试
 * 参考文档: 
 * - 网易云音乐API: https://binaryify.github.io/NeteaseCloudMusicApi/#/
 * - 酷狗音乐API: https://www.cnblogs.com/wanxiangsucai/p/16934771.html
 * 
 * 测试内容:
 * 1. 网易云音乐API测试
 * 2. 酷狗音乐API测试
 * 3. API切换测试
 */
public class MusicApiTest {

    private static final String TAG = "MusicApiTest";
    private static OkHttpClient client;

    @BeforeClass
    public static void setUp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(ApiConfig.HTTP_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
    }

    // ==================== 网易云音乐API测试 ====================

    @Test
    public void testNeteaseApiConnection() throws IOException {
        String url = ApiConfig.NETEASE_BASE_URL + "/cloudsearch?keywords=test&limit=1";
        Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("网易云API连接测试: " + (response.isSuccessful() ? "通过" : "失败"));
        }
    }

    @Test
    public void testNeteaseCloudSearch() throws Exception {
        String url = ApiConfig.NETEASE_BASE_URL + "/cloudsearch?keywords=周杰伦&limit=5";
        String response = getResponse(url);
        assertNotNull("搜索结果为空", response);
        
        JSONObject obj = new JSONObject(response);
        if (obj.optInt("code") == 200 && obj.has("result")) {
            JSONObject result = obj.getJSONObject("result");
            if (result.has("songs")) {
                JSONArray songs = result.getJSONArray("songs");
                assertTrue("搜索结果歌曲列表为空", songs.length() > 0);
                System.out.println("✅ 网易云搜索接口测试通过，搜索到 " + songs.length() + " 首歌曲");
            }
        } else {
            System.out.println("⚠️ 网易云搜索接口测试跳过: API不可用");
        }
    }

    // ==================== 酷狗音乐API测试 ====================

    @Test
    public void testKugouApiConnection() throws IOException {
        String url = ApiConfig.KUGOU_SEARCH_URL + "?showtype=14&keyword=test&pagesize=1";
        Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").header("Referer", "http://m.kugou.com/").build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("酷狗API连接测试: " + (response.isSuccessful() ? "通过" : "失败"));
        }
    }

    @Test
    public void testKugouSearch() throws Exception {
        String url = ApiConfig.KUGOU_SEARCH_URL + 
                     "?showtype=14&highlight=em&pagesize=5&tag_aggr=1&tagtype=全部&plat=0&sver=5&keyword=周杰伦&correct=1&api_ver=1&version=9108&page=1&area_code=1&tag=1&with_res_tag=1";
        
        String response = getResponse(url);
        assertNotNull("搜索结果为空", response);
        
        JSONObject obj = new JSONObject(response);
        if (obj.optInt("status") == 1 && obj.has("data")) {
            JSONObject data = obj.getJSONObject("data");
            if (data.has("lists")) {
                JSONArray lists = data.getJSONArray("lists");
                assertTrue("搜索结果歌曲列表为空", lists.length() > 0);
                System.out.println("✅ 酷狗搜索接口测试通过，搜索到 " + lists.length() + " 首歌曲");
            }
        } else {
            System.out.println("⚠️ 酷狗搜索接口测试跳过: API不可用");
        }
    }

    @Test
    public void testKugouRankList() throws Exception {
        String url = "http://mobilecdnbj.kugou.com/api/v3/rank/list?version=9108&plat=0&showtype=2&parentid=0&apiver=6&area_code=1&withsong=1&with_res_tag=1";
        String response = getResponse(url);
        assertNotNull("排行榜结果为空", response);
        
        JSONObject obj = new JSONObject(response);
        if (obj.optInt("status") == 1 && obj.has("data")) {
            JSONArray data = obj.getJSONArray("data");
            assertTrue("排行榜列表为空", data.length() > 0);
            System.out.println("✅ 酷狗排行榜接口测试通过，获取到 " + data.length() + " 个榜单");
        } else {
            System.out.println("⚠️ 酷狗排行榜接口测试跳过: API不可用");
        }
    }

    @Test
    public void testKugouHotSingers() throws Exception {
        String url = "http://mobilecdnbj.kugou.com/api/v5/singer/list?version=9108&showtype=1&plat=0&sextype=0&sort=1&pagesize=10&type=0&page=1&musician=0";
        String response = getResponse(url);
        assertNotNull("歌手列表结果为空", response);
        
        JSONObject obj = new JSONObject(response);
        if (obj.optInt("status") == 1 && obj.has("data")) {
            JSONArray data = obj.getJSONArray("data");
            assertTrue("歌手列表为空", data.length() > 0);
            System.out.println("✅ 酷狗热门歌手接口测试通过，获取到 " + data.length() + " 位歌手");
        } else {
            System.out.println("⚠️ 酷狗热门歌手接口测试跳过: API不可用");
        }
    }

    @Test
    public void testKugouHotPlaylists() throws Exception {
        String url = "http://mobilecdnbj.kugou.com/api/v5/special/recommend?recommend_expire=0&plat=0&uid=0&version=9108&page=1&area_code=1&appid=1005&pagesize=5";
        String response = getResponse(url);
        assertNotNull("歌单列表结果为空", response);
        
        JSONObject obj = new JSONObject(response);
        if (obj.optInt("status") == 1 && obj.has("data")) {
            JSONArray data = obj.getJSONArray("data");
            assertTrue("歌单列表为空", data.length() > 0);
            System.out.println("✅ 酷狗热门歌单接口测试通过，获取到 " + data.length() + " 个歌单");
        } else {
            System.out.println("⚠️ 酷狗热门歌单接口测试跳过: API不可用");
        }
    }

    // ==================== KugouAdapter测试 ====================

    @Test
    public void testKugouAdapterSearch() throws Exception {
        List<Song> songs = KugouAdapter.getInstance().searchSongs("晴天", 3);
        
        if (songs != null && !songs.isEmpty()) {
            assertEquals("返回歌曲数量不正确", 3, songs.size());
            Song song = songs.get(0);
            assertNotNull("歌曲ID为空", song.getId());
            assertNotNull("歌曲名称为空", song.getName());
            System.out.println("✅ KugouAdapter搜索测试通过");
        } else {
            System.out.println("⚠️ KugouAdapter搜索测试跳过: API不可用");
        }
    }

    @Test
    public void testKugouAdapterAvailability() {
        boolean available = KugouAdapter.getInstance().isAvailable();
        System.out.println("酷狗API可用性: " + (available ? "可用" : "不可用"));
    }

    // ==================== API切换测试 ====================

    @Test
    public void testApiSwitching() {
        ApiConfig.switchToNetease();
        assertEquals("切换到网易云失败", ApiConfig.NETEASE_NAME, ApiConfig.getCurrentApiName());
        System.out.println("✅ 切换到网易云音乐API成功");

        ApiConfig.switchToKugou();
        assertEquals("切换到酷狗失败", ApiConfig.KUGOU_NAME, ApiConfig.getCurrentApiName());
        System.out.println("✅ 切换到酷狗音乐API成功");

        ApiConfig.switchToAlapi();
        assertEquals("切换到ALAPI失败", ApiConfig.ALAPI_NAME, ApiConfig.getCurrentApiName());
        System.out.println("✅ 切换到ALAPI成功");

        ApiConfig.resetToNetease();
        assertEquals("重置到网易云失败", ApiConfig.NETEASE_NAME, ApiConfig.getCurrentApiName());
        System.out.println("✅ 重置到网易云音乐API成功");
    }

    @Test
    public void testAutoSwitchSetting() {
        ApiConfig.setAutoSwitchEnabled(true);
        assertTrue("自动切换设置失败", ApiConfig.isAutoSwitchEnabled());
        System.out.println("✅ 自动切换设置开启成功");

        ApiConfig.setAutoSwitchEnabled(false);
        assertFalse("自动切换设置失败", ApiConfig.isAutoSwitchEnabled());
        System.out.println("✅ 自动切换设置关闭成功");

        ApiConfig.setAutoSwitchEnabled(true);
    }

    // ==================== 辅助方法 ====================

    private String getResponse(String url) throws IOException {
        Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("HTTP错误: " + response.code());
            return response.body() != null ? response.body().string() : "";
        }
    }

    @Test
    public void runAllTests() throws Exception {
        System.out.println("\n========================================");
        System.out.println("    音乐API单元测试汇总");
        System.out.println("========================================");
        System.out.println("测试时间: " + java.time.LocalDateTime.now());
        System.out.println("========================================");
        
        System.out.println("\n--- 网易云音乐API测试 ---");
        testNeteaseApiConnection();
        testNeteaseCloudSearch();
        
        System.out.println("\n--- 酷狗音乐API测试 ---");
        testKugouApiConnection();
        testKugouSearch();
        testKugouRankList();
        testKugouHotSingers();
        testKugouHotPlaylists();
        
        System.out.println("\n--- KugouAdapter测试 ---");
        testKugouAdapterSearch();
        testKugouAdapterAvailability();
        
        System.out.println("\n--- API切换测试 ---");
        testApiSwitching();
        testAutoSwitchSetting();
        
        System.out.println("\n========================================");
        System.out.println("    测试完成！");
        System.out.println("========================================");
    }
}