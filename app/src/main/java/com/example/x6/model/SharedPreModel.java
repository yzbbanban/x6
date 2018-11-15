package com.example.x6.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.x6.entity.ApiInfo;
import com.example.x6.entity.User;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreModel {

    private static final String TAG = "SharedPreModel";

    /**
     * 保存用户
     *
     * @param name     用户
     * @param password 密码
     */
    public static void saveUserSp(Context context, String name, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("password", password);
        editor.commit();
    }

    /**
     * 保存用户
     *
     * @param name     用户
     * @param password 密码
     */
    public static void saveAdminSp(Context context, String name, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences("admin", MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("password", password);
        editor.commit();
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public static User getUserSp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("user", MODE_PRIVATE);
        String name = preferences.getString("name", "");
        String password = preferences.getString("password", "");
        User user = new User(name, password);
        return user;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public static User getAdminSp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("admin", MODE_PRIVATE);
        String name = preferences.getString("name", "");
        String password = preferences.getString("password", "");
        User user = new User(name, password);
        return user;
    }

    /**
     * 保存api 信息
     *
     * @param ip 用户
     * @param ip 密码
     */
    public static void saveApiSp(Context context, String ip, String url) {
        SharedPreferences.Editor editor = context.getSharedPreferences("api", MODE_PRIVATE).edit();
        editor.putString("ip", ip);
        editor.putString("url", url);
        editor.commit();
    }

    /**
     * 获取 api 信息
     *
     * @return api 信息
     */
    public static ApiInfo getAPiSp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("api", MODE_PRIVATE);
        String ip = preferences.getString("ip", "");
        String url = preferences.getString("url", "");
        ApiInfo apiInfo = new ApiInfo(ip, url);
        return apiInfo;
    }

}
