package com.a99zan.zsccpsapp.webapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.a99zan.zsccpsapp.webapp.App.MyApp;
import com.a99zan.zsccpsapp.webapp.MainActivity;
import com.a99zan.zsccpsapp.webapp.R;
import com.a99zan.zsccpsapp.webapp.Utils.ToastUtil;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.module.MsgForwardFilter;
import com.netease.nim.uikit.business.session.module.MsgRevokeFilter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et1)
    EditText et1;
    @BindView(R.id.et2)
    EditText et2;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private String account;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.et2, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        account = et1.getText().toString();
        token = et2.getText().toString();
        MyApp.getInstance().account = account;
        MyApp.getInstance().token = token;
        LoginInfo info = new LoginInfo(account, token);
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        NimUIKit.loginSuccess(account);
                        ToastUtil.showShort(LoginActivity.this, "登录成功");
                        NimUIKit.setMsgForwardFilter(new MsgForwardFilter() {
                            @Override
                            public boolean shouldIgnore(IMMessage message) {
                                return false;
                            }
                        });
                        NimUIKit.setMsgRevokeFilter(new MsgRevokeFilter() {
                            @Override
                            public boolean shouldIgnore(IMMessage message) {
                                return false;
                            }
                        });
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }

                    @Override
                    public void onFailed(int i) {
                        ToastUtil.showShort(LoginActivity.this, "登录失败");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        ToastUtil.showShort(LoginActivity.this, "登录出错");
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }
}
