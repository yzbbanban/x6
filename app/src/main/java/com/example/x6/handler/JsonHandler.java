package com.example.x6.handler;

import android.util.Log;
import android.widget.Toast;

import com.example.x6.activity.MainActivity;
import com.example.x6.app.SerialApplication;
import com.example.x6.constant.SerialConstant;
import com.example.x6.entity.Bucket;
import com.example.x6.entity.BucketBill;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

/**
 * 解锁
 */
public class JsonHandler implements RequestHandler, ICallBack {

    private static final String TAG = "JsonHandler";

    private SendOperaModel sendOperaModel = new SendOperaModel();


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
            String status = params.get("status").toUpperCase();
            String isStatus = params.get("isStatus");
            Log.i(TAG, "handle id: " + id);
            Log.i(TAG, "handle status: " + status);
            Log.i(TAG, "handle isStatus: " + isStatus);

            if ("O".equals(status)) {
                white(getValue(id, status.toUpperCase()), Integer.parseInt(id), 1, isStatus);
            } else {
                white(getValue(id, status.toUpperCase()), Integer.parseInt(id), 0, isStatus);
            }
            resultCode.setCode("200");
            resultCode.setMessage("success");
            StringEntity stringEntity = new StringEntity(gson.toJson(resultCode), "utf-8");
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


    public byte[] getValue(String code, String status) {
        String name = String.format(SerialConstant.BYTES_NUM, code, status);
        try {
            SerialConstant serialConstant = new SerialConstant();
            Field field = serialConstant.getClass().getField(name);
            return (byte[]) field.get(serialConstant);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 开闭锁
     *
     * @param bytes
     */
    private void white(byte[] bytes, Integer id, Integer status, String isStatus) {
        try {
            ToastUtil.showLongToastTop(Arrays.toString(bytes));
//            TODO
            SerialApplication.ttyS1OutputStream.write(bytes);
            Bucket bucket = new Bucket();
            bucket.setId(id);
            bucket.setStatus(status);
            bucket.update(id);

            BucketBill bucketBill = new BucketBill();
            Bucket buc = DataSupport.find(Bucket.class, bucket.getId());
            bucketBill.setCreateTime(System.currentTimeMillis() / 1000);
            bucketBill.setExplain("");
            bucketBill.setId(buc.getId());
            bucketBill.setIdName(buc.getIdName());
            bucketBill.setStatus(buc.getStatus());
            bucketBill.setIdName(buc.getIdName());
            bucketBill.setBucketExpiryDate(buc.getBucketExpiryDate());
            bucketBill.setBucketNumber(buc.getBucketNumber());
            bucketBill.setBucketSendDate(buc.getBucketSendDate());
            bucketBill.setBucketNumber(buc.getBucketNumber());
            bucketBill.setWeight(buc.getWeight());
            bucketBill.save();

            //只有比对后 才回传值
            if ("1".equals(isStatus)) {
                sendOperaModel.send(buc, JsonHandler.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setSuccess(Object message) {
        Log.i(TAG, "handler setSuccess: " + message);
    }

    @Override
    public void setFailure(Object message) {
        Log.i(TAG, "handler setFailure: " + message);
    }
}
