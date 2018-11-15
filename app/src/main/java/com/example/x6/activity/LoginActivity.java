package com.example.x6.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x6.R;
import com.example.x6.app.SerialApplication;
import com.example.x6.entity.User;
import com.example.x6.model.SharedPreModel;
import com.example.x6.ui.CleanEditText;
import com.example.x6.util.StringUtil;
import com.example.x6.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_login_username)
    CleanEditText etLoginUsername;
    @BindView(R.id.et_login_password)
    CleanEditText etLoginPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_init)
    Button btnInit;

    private AlertDialog.Builder builder;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        User user = SharedPreModel.getAdminSp(this);
        if (StringUtil.isNotBlank(user.getName()) && StringUtil.isNotBlank(user.getPassword())) {
            etLoginUsername.setText(user.getName());
//            etLoginPassword.setText(user.getPassword());
        }

    }

    @OnClick(R.id.btn_login)
    public void btnLogin() {
        login();
    }


    @OnLongClick(R.id.btn_init)
    public boolean btnInit() {
        init();
        return true;
    }

    private void init() {
        //将密码都重置
        builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.edit_init_pwd_dialog, null);
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
                    ToastUtil.showShortToast("请输入恢复出厂设置密码");
                } else if ("*#06#".equals(pwd)) {
                    SharedPreModel.saveAdminSp(LoginActivity.this, "admin", "123456");
                    ToastUtil.showShortToast("恢复出厂设置完成");
                    alertDialog.dismiss();
                }else {
                    ToastUtil.showShortToast("密码错误");
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


    private void login() {

        if ("admin".equals(etLoginUsername.getText().toString())) {
            //密码判断
            User user = SharedPreModel.getAdminSp(this);
            if (user == null || user.getPassword() == null || "".equals(user.getPassword())) {
                //初始化
                if ("123456".equals(etLoginPassword.getText().toString())) {
                    SerialApplication.USER_NAME = "admin";
                    SharedPreModel.saveAdminSp(this, "admin", "123456");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            } else {
                String pwd = user.getPassword();
                if (pwd.equals(etLoginPassword.getText().toString())) {
                    SerialApplication.USER_NAME = "admin";
                    SharedPreModel.saveAdminSp(this, "admin", pwd);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    ToastUtil.showLongToast("用户名或密码错误");
                }
            }
        } else if ("user".equals(etLoginUsername.getText().toString())) {
            //空则为游客
            if ("".equals(etLoginPassword.getText().toString())) {
                SerialApplication.USER_NAME = "user";
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        } else {
            ToastUtil.showLongToast("用户名或密码错误");
        }
    }


}
