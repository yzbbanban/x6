package com.example.x6.activity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x6.R;
import com.example.x6.adapter.GridViewAdapter;
import com.example.x6.app.SerialApplication;
import com.example.x6.common.Constants;
import com.example.x6.common.NetUtils;
import com.example.x6.constant.SerialConstant;
import com.example.x6.entity.ApiInfo;
import com.example.x6.entity.Bucket;
import com.example.x6.entity.BucketBill;
import com.example.x6.entity.SendMessage;
import com.example.x6.entity.SendOperaTime;
import com.example.x6.handler.OnServerChangeListener;
import com.example.x6.handler.ServerPresenter;
import com.example.x6.model.ICallBack;
import com.example.x6.model.SendOperaModel;
import com.example.x6.model.SharedPreModel;
import com.example.x6.serial.SerialPort;
import com.example.x6.util.StringUtil;
import com.example.x6.util.ToastUtil;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements ICallBack, OnServerChangeListener {
    private static final String TAG = "MainActivity";

    private ReadOpenThread openThread;
    private ReadCloseThread closeThread;

    private List<Bucket> dataList;

    private GridViewAdapter adapter;

    @BindView(R.id.gv_bucket)
    GridView mGridVIew;

//    @BindView(R.id.btn_opera)
//    Button btnOpera;

    //全关
    @BindView(R.id.btn_all_lock)
    Button btnAllLock;

    //全开
    @BindView(R.id.btn_all_unlock)
    Button btnAllUnlock;

    @BindView(R.id.btn_modify_pwd)
    Button btnModifyPwd;

    @BindView(R.id.bac)
    LinearLayout bac;

    private AlertDialog.Builder builder;

    private AlertDialog alertDialog;


    private SendOperaModel sendOperaModel;

    private Context context;

    private ServerPresenter serverPresenter;

    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            serverPresenter = new ServerPresenter(this, this);
            serverPresenter.startServer(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initData();
        adapter = new GridViewAdapter(this, dataList);

        mGridVIew.setAdapter(adapter);

        mGridVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                if ("admin".equals(SerialApplication.USER_NAME)) {
                    Bucket bucket = dataList.get(position);
                    String value = String.valueOf(position + 1);
                    id = "" + bucket.getId();
                    setLock(view, value, bucket.getId(), position);
                } else {
                    Toast.makeText(SerialApplication.getContext(), "此用户不能做开闭锁操作", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGridVIew.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bucket bucket = dataList.get(position);
                MainActivity.this.id = "" + bucket.getId();
                setBucketName(bucket.getName(), bucket.getId(), position);
                return true;
            }
        });

        initView();
        // showInterface("1111");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏， 在此处添加额外的处理代码
        } else if (this.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏， 在此处添加额外的处理代码
        }
        //检测实体键盘的状态：推出或者合上
        if (newConfig.hardKeyboardHidden
                == Configuration.HARDKEYBOARDHIDDEN_NO) {
            //实体键盘处于推出状态，在此处添加额外的处理代码
        } else if (newConfig.hardKeyboardHidden
                == Configuration.HARDKEYBOARDHIDDEN_YES) {
            //实体键盘处于合上状态，在此处添加额外的处理代码
        }
    }

    protected void initData() {
        if ("admin".equals(SerialApplication.USER_NAME)) {
            btnModifyPwd.setVisibility(View.VISIBLE);
        } else {
            btnModifyPwd.setVisibility(View.GONE);
        }
        context = this;
        ApiInfo apiInfo = SharedPreModel.getAPiSp(this);
        if (StringUtil.isNotBlank(apiInfo.getIp()) && StringUtil.isNotBlank(apiInfo.getUrl())) {
            SerialApplication.URL = "http://" + apiInfo.getIp() + "/" + apiInfo.getUrl() + "/";
        } else {
            Toast.makeText(SerialApplication.getContext(), "请先设置 ip ，接口路径", Toast.LENGTH_LONG).show();
        }

        //图标
//        int icno[] = { R.drawable.i1, R.drawable.i2, R.drawable.i3,
//                R.drawable.i4, R.drawable.i5, R.drawable.i6, R.drawable.i7,
//                R.drawable.i8, R.drawable.i9, R.drawable.i10, R.drawable.i11, R.drawable.i12 };
        //图标下的文字
        dataList = new ArrayList<>();
        int result = DataSupport.count(Bucket.class);
        if (result <= 0) {
            for (int i = 0; i < 32; i++) {

                Bucket bucket = new Bucket();
                String name = "";
                if (i < 9) {
                    name = "P0" + (i + 1);
                } else {
                    name = "P" + (i + 1);
                }
                bucket.setName(name);
                bucket.setIdName(name);
                bucket.setExplain(name);
                bucket.setUpdateTime(System.currentTimeMillis() / 1000);
                bucket.setStatus(0);
                bucket.save();
                dataList.add(bucket);
            }
        } else {
            dataList = DataSupport.findAll(Bucket.class);
        }
        //修改数据库的数据
        //P01~P14改为A-1~A-14，P15~P25改为B-1~B11，P26~P32改为C-1~C-7
        for (int i = 0; i < 32; i++) {
            Bucket bucket = new Bucket();
            String name = "";
            if (i <= 13) {
                //i=0~13
                if (i <= 9) {
                    name = "A0" + (i + 1);
                } else {
                    name = "A" + (i + 1);
                }
            } else if (i <= 24) {
                //i=14~24
                name = "B" + (i - 13);
            } else {
                //i=25~32
                name = "C" + (i - 24);
            }
            bucket.setId(i + 1);
            bucket.setIdName(name);
            bucket.setUpdateTime(System.currentTimeMillis() / 1000);
            //保存数据
            bucket.update(bucket.getId());
        }


        sendOperaModel = new SendOperaModel();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        tv_center.setText("对料管理系统");
        tv_right.setText("设置");
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUrl();
            }
        });
    }

    protected void initView() {
        initSerial();
    }

    public byte[] getValue(String code, String status) {
        String name = String.format(SerialConstant.BYTES_NUM, code, status);
        try {
            SerialConstant serialConstant = new SerialConstant();
            Field field = serialConstant.getClass().getField(name);
            return (byte[]) field.get(serialConstant);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

//
//    @OnClick(R.id.btn_opera)
//    public void btnOpera() {
//
//    }

    @OnClick(R.id.btn_all_lock)
    public void btnAllLock() {

        if ("admin".equals(SerialApplication.USER_NAME)) {
            //密码
            if (StringUtil.isBlank(SerialApplication.PWD)) {
                checkPassword();
            } else {

                Log.i(TAG, "btnAllLock: " + SerialApplication.PWD);
                white(getValue("0", "C"));
                lockResult("0");
                //全色
                if (StringUtil.isNotBlank(SerialApplication.PWD)) {
                    startTranslation(bac, Color.RED, 0);
                }
                SerialApplication.PWD = "";
            }
        } else {
            Toast.makeText(SerialApplication.getContext(), "该用户没有此权限", Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.btn_all_unlock)
    public void btnAllUnLock() {
        if ("admin".equals(SerialApplication.USER_NAME)) {
            //密码
            if (StringUtil.isBlank(SerialApplication.PWD)) {
                checkPassword();
            } else {

                Log.i(TAG, "btnAllLock: " + SerialApplication.PWD);
//        if (openThread == null) {
//            openThread = new ReadOpenThread("0");
//        }
//        openThread.start();
                //TODO
                white(getValue("0", "O"));
                lockResult("0");
                //全色
                if (StringUtil.isNotBlank(SerialApplication.PWD)) {
                    startTranslation(bac, Color.GREEN, 0);
                }
                SerialApplication.PWD = "";
            }
        } else {
            Toast.makeText(SerialApplication.getContext(), "该用户没有此权限", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_modify_pwd)
    public void btnModifyPwd() {
        if ("admin".equals(SerialApplication.USER_NAME)) {
            builder = new AlertDialog.Builder(this);
            View v = getLayoutInflater().inflate(R.layout.edit_pwd_dialog, null);
            builder.setView(v);
            alertDialog = builder.create();
            alertDialog.show();
            Button btnAdd = v.findViewById(R.id.btn_dialog_add);
            Button btnCancel = v.findViewById(R.id.btn_dialog_cancel);
            ImageButton ibtnClose = v.findViewById(R.id.ibtn_dialog_close);
            final EditText etDialogPwd = v.findViewById(R.id.et_dialog_pwd);

            //确认
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                ToastUtil.showShortToast("Add");
                    String pwd = etDialogPwd.getText().toString().trim();
                    if ("".equals(pwd)) {
                        ToastUtil.showShortToast("请输入密码");
                    } else {
                        SharedPreModel.saveAdminSp(MainActivity.this, "admin", pwd);
                        ToastUtil.showShortToast("修改完成");
                        alertDialog.dismiss();
                    }

                }
            });
            //取消
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtil.showShortToast("Cancel");
                    alertDialog.dismiss();
                }
            });
            ibtnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(SerialApplication.getContext(), "该用户没有此权限", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 开闭锁
     *
     * @param bytes
     */
    private void white(byte[] bytes) {
        try {
            ToastUtil.showLongToastTop(Arrays.toString(bytes));
            ttyS1OutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            Toast.makeText(SerialApplication.getContext(), "开锁上传 lockResult error-->" + str, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 读取状态
     *
     * @param bytes bytes
     * @return int
     */
    private int read(byte[] bytes) {
        try {
            ToastUtil.showLongToastTop(Arrays.toString(bytes));
            return ttyS1InputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private SerialPort serialttyS1;
    private InputStream ttyS1InputStream;
    private OutputStream ttyS1OutputStream;

    /* 打开串口 */
    private void initSerial() {
        try {
            serialttyS1 = new SerialPort(new File("/dev/ttyS0"), 9600, 0);
            ttyS1InputStream = serialttyS1.getInputStream();
            ttyS1OutputStream = serialttyS1.getOutputStream();
            SerialApplication.serialttyS1 = serialttyS1;
            SerialApplication.ttyS1InputStream = ttyS1InputStream;
            SerialApplication.ttyS1OutputStream = ttyS1OutputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void setSuccess(Object message) {
        ToastUtil.showLongToast(String.valueOf(message));
    }

    @Override
    public void setFailure(Object message) {
        ToastUtil.showLongToast(String.valueOf(message));
    }

    @Override
    public void onServerStarted(String ipAddress) {
        Log.i(TAG, "onServerStarted: " + "http://" + ipAddress + ":" + Constants.PORT_SERVER + Constants.POST_JSON);

    }

    @Override
    public void onServerStopped() {
        Log.i(TAG, "服务器停止了");
    }

    @Override
    public void onServerError(String errorMessage) {
        Log.i(TAG, "服务器错误：" + errorMessage);
    }

    private class ReadOpenThread extends Thread {

        private String message;

        public ReadOpenThread(String message) {
            ReadOpenThread.this.message = message;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                int count = 0;
                int size;
                try {
                    count++;
                    if (count > 3) {
                        return;
                    }
                    byte[] buffer = getValue(message, "O");

                    if (ttyS1InputStream == null) return;
                    if ("0".equals(message)) {
                        //全开
                        buffer = SerialConstant.BYTES_ALL_BACK;
                    }
                    size = ttyS1InputStream.read(buffer);
                    ToastUtil.showShortToastTop(String.valueOf(size));

                    if (size > 0) {
                        unlockResult(message);
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ToastUtil.showLongToastCenter("ReadOpenThread====>" + e.getMessage());
                    try {
                        openThread = null;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ToastUtil.showLongToastCenter("ReadOpenThread openThread==>" + ex.getMessage());
                    }
                    return;
                }
            }
        }
    }


    private class ReadCloseThread extends Thread {

        private String message;

        public ReadCloseThread(String message) {
            ReadCloseThread.this.message = message;
        }


        @Override
        public void run() {
            super.run();
            while (true) {
                int size;
                int count = 0;
                try {
                    count++;
                    if (count > 3) {
                        return;
                    }
                    byte[] buffer = getValue(message, "C");
                    if (ttyS1InputStream == null) return;
                    if ("0".equals(message)) {
                        //全关
                        buffer = SerialConstant.BYTES_ALL_BACK;
                    }
                    size = ttyS1InputStream.read(buffer);
                    ToastUtil.showShortToastTop(String.valueOf(size));
                    if (size > 0) {
                        lockResult(message);
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ToastUtil.showLongToastCenter("ReadCloseThread====>" + e.getMessage());
                    try {
                        closeThread = null;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ToastUtil.showLongToastCenter("ReadCloseThread closeThread==>" + ex.getMessage());
                    }
                    return;
                }
            }
        }

    }

    /**
     * 闭锁
     *
     * @param openCode 打开
     */
    private void lockResult(final String openCode) {
        try {
            ToastUtil.showLongToast(openCode);
//                SendMessage<SendOperaTime> sendMessage = new SendMessage<>();
//                sendMessage.setStatus("OK");
//                sendMessage.setData(new SendOperaTime(Integer.parseInt(openCode), System.currentTimeMillis() / 1000, "close"));
            Bucket bucket = DataSupport.find(Bucket.class, Long.parseLong(id));
            sendOperaModel.send(bucket, MainActivity.this);
            closeThread = null;
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            Toast.makeText(SerialApplication.getContext(), "开锁上传 lockResult error-->" + str, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 开锁
     *
     * @param closeCode 关闭
     */
    private void unlockResult(final String closeCode) {
        try {
            ToastUtil.showLongToast(closeCode);
            Bucket bucket = DataSupport.find(Bucket.class, Long.parseLong(id));
            sendOperaModel.send(bucket, MainActivity.this);
            openThread = null;
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            Toast.makeText(SerialApplication.getContext(), "开锁上传 unlockResult error-->" + str, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 密码解锁
     */
    private void checkPassword() {
        builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.edit_pwd_dialog, null);
        builder.setView(v);
        alertDialog = builder.create();
        alertDialog.show();
        Button btnAdd = v.findViewById(R.id.btn_dialog_add);
        Button btnCancel = v.findViewById(R.id.btn_dialog_cancel);
        ImageButton ibtnClose = v.findViewById(R.id.ibtn_dialog_close);
        final EditText etDialogPwd = v.findViewById(R.id.et_dialog_pwd);


        //确认
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ToastUtil.showShortToast("Add");
                String pwd = etDialogPwd.getText().toString().trim();
                if ("".equals(pwd)) {
                    ToastUtil.showShortToast("请输入密码");
                } else {
                    if ("222222".equals(pwd)) {
                        ToastUtil.showLongToast("已取得权限");
                        SerialApplication.PWD = "222222";
                    } else {
                        ToastUtil.showLongToast("密码输入错误");
                    }
                    alertDialog.dismiss();
                }

            }
        });
        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showShortToast("Cancel");
                alertDialog.dismiss();
            }
        });
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


    /**
     * 设置名字参数
     */
    private void setBucketName(String name, final Integer id, final Integer position) {
        builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.edit_bucket_dialog, null);
        builder.setView(v);
        alertDialog = builder.create();
        alertDialog.show();
        Button btnAdd = v.findViewById(R.id.btn_dialog_add);
        Button btnCancel = v.findViewById(R.id.btn_dialog_cancel);
        ImageButton ibtnClose = v.findViewById(R.id.ibtn_dialog_close);
        final EditText etDialogName = v.findViewById(R.id.et_dialog_name);
        final TextView tvDialogStatus = v.findViewById(R.id.tv_lock_status);

        if (StringUtil.isNotBlank(name)) {
            etDialogName.setText(name);
        }
        Bucket b = dataList.get(position);
        tvDialogStatus.setText(b.getStatus() == 1 ? "开锁" : "闭锁");
        if ("admin".equals(SerialApplication.USER_NAME)) {

        } else {
            etDialogName.setEnabled(false);
        }

        //添加或更新
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ToastUtil.showShortToast("Add");
                String name = etDialogName.getText().toString().trim();
                if ("".equals(name)) {
                    ToastUtil.showShortToast("请输入");
                } else {
                    Bucket bucket = new Bucket();
                    bucket.setName(name);
                    bucket.setId(id);
                    bucket.update(id);

                    saveBill(id);
                    dataList.get(position).setName(name);
                    adapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }

            }
        });
        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showShortToast("Cancel");
                alertDialog.dismiss();
            }
        });
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 设置接口参数
     */
    private void setUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.edit_url_dialog, null);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button btnAdd = v.findViewById(R.id.btn_dialog_add);
        Button btnCancel = v.findViewById(R.id.btn_dialog_cancel);
        ImageButton ibtnClose = v.findViewById(R.id.ibtn_dialog_close);
        final EditText etDialogIp = v.findViewById(R.id.et_dialog_ip);
        final EditText etDialogUrl = v.findViewById(R.id.et_dialog_url);
        TextView tvLocalIp = v.findViewById(R.id.tv_local_ip);
        tvLocalIp.setText(tvLocalIp.getText().toString() + NetUtils.getLocalIPAddress() + ":8888");

        ApiInfo apiInfo = SharedPreModel.getAPiSp(this);
        if (StringUtil.isNotBlank(apiInfo.getIp()) && StringUtil.isNotBlank(apiInfo.getUrl())) {
            etDialogIp.setText(apiInfo.getIp());
            etDialogUrl.setText(apiInfo.getUrl());
        }

        //添加或更新
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ToastUtil.showShortToast("Add");
                String ip = etDialogIp.getText().toString().trim();
                String url = etDialogUrl.getText().toString().trim();
                if ("".equals(ip) || "".equals(url)) {
                    ToastUtil.showShortToast("请输入");
                } else {
                    SerialApplication.URL = "http://" + ip + "/" + url + "/";
                    Log.i(TAG, "onClick: " + SerialApplication.URL);
                    SharedPreModel.saveApiSp(MainActivity.this, ip, url);
                    alertDialog.dismiss();
                }

            }
        });
        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showShortToast("Cancel");
                alertDialog.dismiss();
            }
        });
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


    private void startTranslation(View v, int color, int type) {
        int[] col = new int[10];
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                col[i] = color;
            } else {
                if (type == 0) {
                    col[i] = Color.parseColor("#7b7a7a");
                } else {
                    col[i] = Color.WHITE;
                }
            }
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(v, "backgroundColor", col);
        objectAnimator.setDuration(5000);
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.start();
    }

    /**
     * 解锁开锁
     *
     * @param message bucket 信息
     */
    private void setLock(final View vb, final String message, final Integer id, final Integer position) {
        builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.edit_lock_dialog, null);
        builder.setView(v);
        alertDialog = builder.create();
        alertDialog.show();
        Button btnLock = v.findViewById(R.id.btn_dialog_lock);
        Button btnUnlock = v.findViewById(R.id.btn_dialog_unlock);
        ImageButton ibtnClose = v.findViewById(R.id.ibtn_dialog_close);
        //闭锁
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                vb.setBackgroundColor(Color.RED);
                ToastUtil.showLongToast("btnLock==>" + message);
                Drawable drawableColor = new ColorDrawable(Color.RED);
                vb.setBackgroundDrawable(drawableColor);

                //TODO ====================================================
//                if (openThread == null) {
//                    openThread = new ReadOpenThread(message);
//                }
//                openThread.start();
                //TODO ====================================================
                //特效
                startTranslation(vb, Color.RED, 1);
                //TODO ============================================dddddd========

                white(getValue(message, "C"));
                //保存到数据库，更新状态
                Bucket bucket = new Bucket();
                bucket.setId(id);
                bucket.setStatus(0);
                bucket.update(id);

                try {
                    saveBill(id);
                    dataList.get(position).setStatus(0);
                    adapter.notifyDataSetChanged();
                    lockResult(message);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw, true));
                    String str = sw.toString();
                    Toast.makeText(SerialApplication.getContext(), "闭锁上传 error-->" + str, Toast.LENGTH_LONG).show();
                }

            }
        });
        //开锁
        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                vb.setBackgroundColor(Color.parseColor("#008b27"));
                ToastUtil.showLongToast("btnUnlock==>" + message);
                Drawable drawableColor = new ColorDrawable(Color.parseColor("#008b27"));
                vb.setBackgroundDrawable(drawableColor);

                //TODO ====================================================
//                if (closeThread == null) {
//                    closeThread = new ReadCloseThread(message);
//                }
//                closeThread.start();
                //TODO ====================================================
                startTranslation(vb, Color.GREEN, 1);

                //TODO ============================================dddddd========
                white(getValue(message, "O"));
                Bucket bucket = new Bucket();
                bucket.setId(id);
                bucket.setStatus(1);
                bucket.update(id);

                try {
                    saveBill(id);
                    dataList.get(position).setStatus(1);
                    adapter.notifyDataSetChanged();
                    unlockResult(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw, true));
                    String str = sw.toString();
                    Toast.makeText(SerialApplication.getContext(), "开锁上传 error-->" + str, Toast.LENGTH_LONG).show();
                }

                alertDialog.dismiss();
            }
        });
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void saveBill(Integer id) {
        BucketBill bucketBill = new BucketBill();
        Bucket buc = DataSupport.find(Bucket.class, id);
        bucketBill.setCreateTime(System.currentTimeMillis() / 1000);
        bucketBill.setExplain("");
        bucketBill.setId(buc.getId());
        bucketBill.setIdName(buc.getIdName());
        bucketBill.setStatus(buc.getStatus());
        bucketBill.setIdName(buc.getIdName());
        bucketBill.save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverPresenter.unregister(this);
        serverPresenter = null;
        if (openThread != null) {
            openThread = null;
        }
        if (closeThread != null) {
            closeThread = null;
        }
    }
}
