package com.snrt.helloworld.task;

import android.location.GnssAntennaInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snrt.helloworld.music.MusicVO;
import com.snrt.helloworld.vo.Callback;
import com.snrt.helloworld.vo.DataVo;
import com.snrt.helloworld.vo.InfoVO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicTask extends AsyncTask<Integer, Integer, List<MusicVO>> {

    private static final String TAG = "MusicTask";

    private Callback callback;

    OkHttpClient httpClient = new OkHttpClient();

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    String url ="http://mobilecdnbj.kugou.com/api/v3/singer/song?sorttype=2&version=9108&identity=3&plat=0&pagesize=100&singerid=548441&area_code=1&page=1&with_res_tag=1";
    @Override
    protected List<MusicVO> doInBackground(Integer... integers) {
        List<MusicVO> result = new ArrayList<>();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = httpClient.newCall(request).execute();
            final String body = response.body().string();
            Log.i(TAG, "onResponse: "+body);
            String replace = body.replace("<!--KG_TAG_RES_START-->", "");
            Log.i(TAG, "onResponse: "+ replace);
            Gson gson = new GsonBuilder().serializeNulls().create();

            JSONObject jsonObject = new JSONObject(replace);
            String data = jsonObject.getString("data");
            DataVo dataVo = gson.fromJson(data, DataVo.class);
            List<InfoVO> info = dataVo.getInfo();
            Log.i(TAG, "onResponse: "+ info);

            Uri uri = Uri.parse("");
            long id = 0;
            for (InfoVO infoVO : info) {
                result.add(new MusicVO(id, infoVO.getFilename(),infoVO.getDuration(),infoVO.getFilesize()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<MusicVO> musicVOS) {
        super.onPostExecute(musicVOS);
        callback.setData(musicVOS);
    }
}
