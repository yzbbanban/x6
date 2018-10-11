package com.example.x6.model;

import android.util.Log;

import com.example.x6.app.SerialApplication;
import com.example.x6.entity.Bucket;
import com.example.x6.entity.ResultCode;
import com.example.x6.entity.SendMessage;
import com.example.x6.service.SendService;
import com.example.x6.util.LogUtil;
import com.example.x6.util.retrofit.MyCallback;
import com.example.x6.util.retrofit.RetrofitUtils;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Query;

public class SendOperaModel {

    private static final String TAG = "SendOperaModel";

    public void send(Bucket bucket, final ICallBack callback) throws Exception {
        SendService request = RetrofitUtils.getRetrofit(SerialApplication.URL).create(SendService.class);
        Log.i(TAG, "send: " + bucket.toString());
        String idName = bucket.getIdName();
        String bucketName = bucket.getName();
        String bucketNumber = bucket.getBucketNumber();
        String bucketSendDate = bucket.getBucketSendDate();
        Double weight = bucket.getWeight();
        String bucketExpiryDate = bucket.getBucketExpiryDate();

        Call<ResultCode<String>> call = request.call(idName, bucketName, bucketNumber, bucketSendDate, weight, bucketExpiryDate);
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
