package com.snrt.helloworld.task;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.snrt.helloworld.music.MusicVO;
import com.snrt.helloworld.util.HttpUtil;
import com.snrt.helloworld.vo.Callback;
import com.snrt.helloworld.vo.LrcResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicTask extends AsyncTask<Integer, Integer, List<MusicVO>> {

    private static final String TAG = "MusicTask";

    private Callback callback;


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected List<MusicVO> doInBackground(Integer... integers) {
        return HttpUtil.getSongList();
    }

    @Override
    protected void onPostExecute(List<MusicVO> musicVOS) {
        super.onPostExecute(musicVOS);
        callback.setData(musicVOS);
    }
}
