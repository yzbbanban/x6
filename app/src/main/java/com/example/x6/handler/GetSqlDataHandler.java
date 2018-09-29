package com.example.x6.handler;

import android.util.Log;

import com.example.x6.entity.BucketBill;
import com.example.x6.util.LogUtil;
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
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：leavesC
 * 时间：2018/4/5 16:30
 * 描述：https://github.com/leavesC/AndroidServer
 * https://www.jianshu.com/u/9df45b87cfdf
 * 查询列表
 */
public class GetSqlDataHandler implements RequestHandler {

    private static final String TAG = "GetSqlDataHandler";


    @RequestMapping(method = {RequestMethod.GET})
    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        try {
            Map<String, String> params = HttpRequestParser.parseParams(httpRequest);

            String pageNum = params.get("pageNum");
            String pageSize = params.get("pageSize");
            int pN = Integer.parseInt(pageNum);
            int pS = Integer.parseInt(pageSize);
            if (pN == 1) {
                pN = 0;
            } else {
                pN = (pN - 1) * pS;
            }

            Log.i(TAG, "handle: " + pageNum);
            Log.i(TAG, "handle: " + pageSize);
            int count = DataSupport.count(BucketBill.class);
            Map<String, Object> map = new HashMap<>();
            String result = "";
            if (count <= 0) {
                result = "{\"code\":200,\"message\":\"success\",\"result\":\"\"}";
            } else {
                List<BucketBill> bucketBillList = DataSupport.limit(pS).offset(pN).order("createTime").find(BucketBill.class);
                map.put("count", count);
                map.put("list", bucketBillList);
            }
            StringEntity stringEntity = new StringEntity("{\"code\":200,\"message\":\"success\",\"result\":" + new Gson().toJson(map) + "}", "utf-8");
            httpResponse.setStatusCode(200);
            httpResponse.setEntity(stringEntity);
        } catch (Exception e) {
            httpResponse.setStatusCode(500);
            StringEntity stringEntity = new StringEntity("{\"code\":500,\"message\":\"error\",\"result\":\"\"}", "utf-8");
            httpResponse.setEntity(stringEntity);
        }
    }


}
