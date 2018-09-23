package com.example.x6.model;

import android.util.Log;

import com.example.x6.app.SerialApplication;
import com.example.x6.entity.ResultCode;
import com.example.x6.entity.SendMessage;
import com.example.x6.service.SendService;
import com.example.x6.util.LogUtil;
import com.example.x6.util.retrofit.MyCallback;
import com.example.x6.util.retrofit.RetrofitUtils;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Response;

public class SendOperaModel {

    private static final String TAG = "SendOperaModel";

    public void send(SendMessage sendMessage, final ICallBack callback) {
        SendService request = RetrofitUtils.getRetrofit(SerialApplication.URL).create(SendService.class);
        String req=new Gson().toJson(sendMessage);
        Call<ResultCode<String>> call = request.call(req);
        call.enqueue(new MyCallback<ResultCode<String>>() {
            @Override
            public void onSuc(Response<ResultCode<String>> response) {
                Log.i(TAG, "onSuc-->: " + response.code());
                if ("200".equals(response.body().getCode())) {
                    LogUtil.info(TAG, response.body().getMessage());
                    LogUtil.info(TAG, response.body().getCode());
                    callback.setSuccess(response.body().getMessage());
                } else {
                    callback.setSuccess(response.body().getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Log.i(TAG, "onFail: " + message);
                callback.setFailure(message);
            }
        });
    }
}
