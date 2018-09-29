package com.example.x6.handler;

import android.util.Log;

import com.example.x6.activity.MainActivity;
import com.example.x6.app.SerialApplication;
import com.example.x6.constant.SerialConstant;
import com.example.x6.entity.Bucket;
import com.example.x6.entity.ResultCode;
import com.example.x6.model.ICallBack;
import com.example.x6.model.SendOperaModel;
import com.example.x6.util.LogUtil;
import com.example.x6.util.ToastUtil;
import com.google.gson.Gson;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

/**
 * 作者：leavesC
 * 时间：2018/4/5 16:30
 * 描述：https://github.com/leavesC/AndroidServer
 * https://www.jianshu.com/u/9df45b87cfdf
 * 比对
 */
public class CompareHandler implements RequestHandler {

    private static final String TAG = "CompareHandler";


    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        ResultCode resultCode = new ResultCode();
        Gson gson = new Gson();

        try {
            Map<String, String> params = HttpRequestParser.parseParams(httpRequest);
//            LogUtil.info(TAG, new Gson().toJson(httpContext));
//            LogUtil.info(TAG, httpRequest.toString());

            String id = params.get("id");
            // PRI313,30111111,2017.06.29,20,2017.06.29
            String name = params.get("name");
            Log.i(TAG, "handle: " + id);
            Log.i(TAG, "handle: " + name);
            Bucket bucket = DataSupport.find(Bucket.class, Long.parseLong(id));

            String result = "";
            if (bucket == null) {
                resultCode.setCode("500");
                resultCode.setMessage("error");
                result = gson.toJson(resultCode);
            } else {
                String[] bucketParams = name.split(",");
                if (bucketParams[0].equals(bucket.getName())) {
                    resultCode.setCode("200");
                    resultCode.setMessage("success");
                    result = gson.toJson(resultCode);
                    //保存数据
                    bucket.setName(bucketParams[0]);
                    bucket.setBucketNumber(bucketParams[1]);
                    bucket.setBucketSendDate(bucketParams[2]);
                    bucket.setWeight(Double.parseDouble(bucketParams[3]));
                    bucket.setBucketExpiryDate(bucketParams[4]);
                    bucket.update(bucket.getId());


                } else {
                    resultCode.setCode("500");
                    resultCode.setMessage("error");
                    result = gson.toJson(resultCode);
                }
            }

            StringEntity stringEntity = new StringEntity(result, "utf-8");
            httpResponse.setStatusCode(200);
            httpResponse.setEntity(stringEntity);
        } catch (Exception e) {
            httpResponse.setStatusCode(500);
            resultCode.setCode("500");
            resultCode.setMessage("error");
            StringEntity stringEntity = new StringEntity(gson.toJson(resultCode), "utf-8");
            httpResponse.setEntity(stringEntity);
        }
    }

}
