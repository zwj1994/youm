package com.bs.youmin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.bs.youmin.R;
import com.bs.youmin.entity.User;
import com.bs.youmin.util.SaveUserUtil;
import com.bs.youmin.util.ServerInterface;
import com.bs.youmin.util.encryption.rsa.RSACoder;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.util.Date;
import java.util.UUID;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Mrzhu on 2017/8/24.
 */
public class LoadActivity extends AppCompatActivity {

    /**
     * 进度文本控件
     */
    TextView progressMsg_tv;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(LoadActivity.this, MainActivity.class));
            finish();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        progressMsg_tv = (TextView) findViewById(R.id.progressMsg);
        //请求服务器公钥中
        requestPublicKey();
    }




    /**
     * 绑定密钥到服务器
     */
    private void bindKey(){
        try {
            UUID uuid = UUID.randomUUID();
            final String key = uuid.toString() + new Date().getTime();
            progressMsg_tv.setText("绑定密钥到服务器中...");
            RequestParams params = new RequestParams();
            User user = SaveUserUtil.loadAccount(LoadActivity.this);
            user.setKey(key);
            SaveUserUtil.saveAccount(LoadActivity.this, user);
            byte[] encodedData = RSACoder.encryptByPublicKey(key.getBytes(), user.getPublicKey());
            String encodeKey = new String(encodedData,"ISO-8859-1");
            params.put("identifier", user.getIdentifier());
            params.put("key", encodeKey);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(ServerInterface.BINDKEY, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String str = new String(responseBody);
                    if (null != str && str.equals("ok")) {
                        progressMsg_tv.setText("密钥绑定成功");
                        mHandler.sendEmptyMessageDelayed(1,300);
                    } else {
                        progressMsg_tv.setText("密钥绑定失败");
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressMsg_tv.setText("没有发现可用的网络");
                    Log.i("=============","没有网络");
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            progressMsg_tv.setText("密钥绑定失败");
        }

    }

    /**
     * 请求服务器公钥中
     */
    private void requestPublicKey() {
        UUID uuid = UUID.randomUUID();
        final String identifier = uuid.toString() + new Date().getTime();
        progressMsg_tv.setText("申请服务器公钥中...");
        RequestParams params = new RequestParams();
        params.put("identifier", identifier);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ServerInterface.REQUEST_PUBLIC_KEY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progressMsg_tv.setText("已获取服务器公钥");
                    String str = new String(responseBody);
                    if (str != null) {
                            Gson gson = new  Gson();
                            User user = gson.fromJson(str,User.class);
                            user.setIdentifier(identifier);
                            SaveUserUtil.saveAccount(LoadActivity.this, user);
                            //绑定密钥到服务器
                            bindKey();
                    } else {
                        progressMsg_tv.setText("获取服务器公钥失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    progressMsg_tv.setText("获取服务器公钥失败");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressMsg_tv.setText("没有发现可用的网络");
                Log.i("=============","没有网络");
            }
        });
    }


}
