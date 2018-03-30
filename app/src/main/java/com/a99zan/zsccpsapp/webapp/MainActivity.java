package com.a99zan.zsccpsapp.webapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a99zan.zsccpsapp.webapp.App.MyApp;
import com.a99zan.zsccpsapp.webapp.Fragment.ContactListFragment;
import com.a99zan.zsccpsapp.webapp.Fragment.RecentContactsFragment;
import com.a99zan.zsccpsapp.webapp.Utils.ToastUtil;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.et)
    EditText et;
    @BindView(R.id.btn_tianjia)
    Button btnTianjia;
    @BindView(R.id.btn2)
    Button liaotian;

    private WebView webView;

    private long exitTime = 0;

    private String liaotianAccount = "";

    private List<Fragment> fragmentList = new ArrayList<>();
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MyApp.getInstance().addActivity(this);

        init();

        tongzhi();

    }

    private void tongzhi() {
        Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
            @Override
            public void onEvent(final SystemMessage systemMessage) {
                if (systemMessage.getType() == SystemMessageType.AddFriend) {
                    AddFriendNotify attachData = (AddFriendNotify) systemMessage.getAttachObject();
                    if (attachData != null) {
                        // 针对不同的事件做处理
                        if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                            // 对方直接添加你为好友
                            ToastUtil.showShort(MainActivity.this, "1");
                        } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                            // 对方通过了你的好友验证请求
                            ToastUtil.showShort(MainActivity.this, "2");
                        } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                            // 对方拒绝了你的好友验证请求
                            ToastUtil.showShort(MainActivity.this, "3");
                        } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                            // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                            // 通过message.getContent()获取好友验证请求的附言
                            ToastUtil.showShort(MainActivity.this, "4");
                            final String fromAccount = systemMessage.getFromAccount();
                            NIMClient.getService(FriendService.class).ackAddFriendRequest(fromAccount, true).setCallback(new RequestCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ToastUtil.showShort(MainActivity.this, "已通过"+fromAccount+"的请求");
                                }

                                @Override
                                public void onFailed(int i) {

                                }

                                @Override
                                public void onException(Throwable throwable) {

                                }
                            });
                        }
                    }
                }
            }
        };
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, true);
    }

    /**
     * 选择隐藏与显示的Fragment
     * @param index 显示的Frgament的角标
     */
    private void switchFragment(int index){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        for(int i = 0; i < fragmentList.size(); i++){
            if (index == i){
                fragmentTransaction.show(fragmentList.get(index));
            }else {
                fragmentTransaction.hide(fragmentList.get(i));
            }
        }
        fragmentTransaction.commit();
    }

    private void init() {

        Fragment fragment2 = new ContactListFragment();
        Fragment fragment1 = new RecentContactsFragment();

        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

        fragmentTransaction.add(R.id.fl_container,fragment1);
        fragmentTransaction.add(R.id.fl_container,fragment2);

        fragmentTransaction.show(fragment1).hide(fragment2);
        fragmentTransaction.commit();
    }

    @OnClick({R.id.btn, R.id.btn1, R.id.et, R.id.btn_tianjia, R.id.btn2})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn:
                switchFragment(0);
                break;
            case R.id.btn1:
                switchFragment(1);
                break;
            case R.id.et:

                break;
            case R.id.btn_tianjia:
                addFriend();
                break;
            case R.id.btn2:
                liaotian();
                break;
        }
    }

    private void addFriend() {
        liaotianAccount = et.getText().toString();
        final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
        String msg = "好友请求附言";
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(liaotianAccount, verifyType, msg))
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ToastUtil.showShort(MainActivity.this, "已发起添加好友请求！");
                    }

                    @Override
                    public void onFailed(int i) {
                        ToastUtil.showShort(MainActivity.this, "发起失败！");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        ToastUtil.showShort(MainActivity.this, "发起出错！");
                    }
                });
    }

    private void liaotian() {
        liaotianAccount = et.getText().toString();
        NimUIKit.setAccount(MyApp.getInstance().account);
        NimUIKit.startP2PSession(MainActivity.this, liaotianAccount);
    }

    @Override
    public void onBackPressed() {
        backtrack();
    }

    /**
     * 退出销毁所有activity
     */
    private void backtrack() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            MyApp.getInstance().exit();
            exitTime = 0;
        }
    }

}
