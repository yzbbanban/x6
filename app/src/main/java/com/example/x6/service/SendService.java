package com.example.x6.service;

import com.example.x6.constant.ServiceResult;
import com.example.x6.entity.ResultCode;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by brander on 2017/8/3.
 */

public interface SendService {
    @GET("send")
//    @FormUrlEncoded
//    Call<ResultCode<String>> call(@Field("send") String send);
    Call<ResultCode<String>> call(@Query("send") String send);
}