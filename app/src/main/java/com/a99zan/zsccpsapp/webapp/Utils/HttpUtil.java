package com.a99zan.zsccpsapp.webapp.Utils;

import android.util.Log;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 99zan on 2018/1/17.
 */

public class HttpUtil {

    private static HttpUtil downloadUtil;
    private final OkHttpClient okHttpClient;
    public static String BASE_URL = "http://jxptw.99zan.vip/";

    public static HttpUtil getInstance() {
        if (downloadUtil == null) {
            synchronized (HttpUtil.class){
                if(downloadUtil == null){
                    downloadUtil = new HttpUtil();
                }
            }
        }
        return downloadUtil;
    }

    private HttpUtil() {
        okHttpClient = new OkHttpClient();
    }

    private void get(String url, Callback callback){
        Request.Builder requestBuilder = new Request.Builder().url(BASE_URL+url);
        //可以省略，默认是GET请求
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();
        Call mcall= okHttpClient.newCall(request);
        mcall.enqueue(callback);
    }

    public void post(String url, RequestBody requestBody, Callback callback){
        Request request = new Request.Builder()
                .url(BASE_URL+url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public void rxGet(final String url, Observer<String> observer){
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                get(url, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String result = response.body().string();
                        Log.e("222", result+"2222222222");
                        e.onNext(result);
                        e.onComplete();
                    }
                });
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
