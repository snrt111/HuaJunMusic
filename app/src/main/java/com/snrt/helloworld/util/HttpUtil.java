package com.snrt.helloworld.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snrt.helloworld.music.MusicVO;
import com.snrt.helloworld.vo.SongInfo;
import com.snrt.helloworld.vo.SongListVo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpUtil {


    public static final String HTTP_IP_PORT = "http://10.0.2.2:3000";
    public static final Integer LIMIT_COUNT = 50;

    private static String getData(String url) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();
        String string = null;
        try {
            Response response = client.newCall(request).execute();
            string = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return string;
    }

    private static Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    public static List<SongInfo> getSongerList(){
        String url = HTTP_IP_PORT + "/artist/list?type=1&area=7";
        String data = getData(url);
        SongListVo songListVo = gson.fromJson(data, SongListVo.class);
        List<SongInfo> songs = songListVo.getSongs();
        return songs;
    }

    public static List<MusicVO> getSongList(){
        String url = HTTP_IP_PORT + "/artist/songs?id=2517&limit="+LIMIT_COUNT;
        String data = getData(url);
        SongListVo songListVo = gson.fromJson(data, SongListVo.class);
        List<SongInfo> songs = songListVo.getSongs();
        List<MusicVO> result = new ArrayList<>();
        if(songs == null || songs.size() == 0) {
            return result;
        }
        List<String> ids = songs.stream().map(SongInfo::getId).collect(Collectors.toList());
        Map<String,String> songUrl = getSongUrl(ids);
        for (SongInfo song : songs) {
            MusicVO vo = new MusicVO();
            vo.setUrl(songUrl.get(song.getId()));
            vo.setId(song.getId());
            vo.setName(song.getName());
            vo.setDuration(song.getDt());
            vo.setLyric(getSongLyric(song.getId()));
            result.add(vo);
        }
        return result;

    }
    /**
     * id : 音乐 id
     * level: 播放音质等级, 分为 standard => 标准,higher => 较高, exhigh=>极高, lossless=>无损, hires=>Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
     *
     * @param id
     * @return
     */
    public static Map<String,String> getSongUrl(List<String> ids){
        String id = String.join(",", ids);
        String url = HTTP_IP_PORT + "/song/url/v1?id=" + id + "&level=standard";
        String data = getData(url);
        Map<String, String> urlMap = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                urlMap.put(object.getString("id"), object.getString("url"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return urlMap;
    }

    /**
     * /lyric?id=2063487880,77098
     *
     * @return
     */
    public static String getSongLyric(String id){
        String url = HTTP_IP_PORT + "/lyric?id=" + id;
        String data = getData(url);
        String lyric = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject json = jsonObject.getJSONObject("lrc");
            lyric = json.getString("lyric");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return lyric;
    }

}
