package com.example.x6.app;

import android.content.Context;

import com.example.x6.serial.SerialPort;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

import java.io.InputStream;
import java.io.OutputStream;

public class SerialApplication extends LitePalApplication {

    public static String PWD = "";
    public static String URL = "";
    public static String USER_NAME = "";


    public static SerialPort serialttyS1;
    public static InputStream ttyS1InputStream;
    public static OutputStream ttyS1OutputStream;


    public static boolean isOnAppStore = false;

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Connector.getDatabase();
    }


    /**
     * 获取全局上下文对象
     *
     * @return
     */
    public static Context getAppContext() {
        return context;
    }
}
