package com.ysbing.retrofitdemo.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.ysbing.retrofitdemo.api.APIService;
import com.ysbing.retrofitdemo.bean.DemoBean;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 *
 * @author ysbing
 */
public class Teach1Activity extends Activity implements View.OnClickListener {

    private MyHandler handler;
    private Context mContext;

    //IP地址
    private EditText mIpText;
    //请求的结果，IP信息
    private TextView mIpInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach1);
        mContext = this;
        handler = new MyHandler();
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mIpText = (EditText) findViewById(R.id.text_ip);
        mIpInfoText = (TextView) findViewById(R.id.text_ip_info);
        findViewById(R.id.button_query_execute).setOnClickListener(this);
        findViewById(R.id.button_query_call_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_query_execute:
                retrofitExecute();
                break;
            case R.id.button_query_call_back:
                retrofitCallBack();
                break;
        }
    }


    /**
     * retrofit使用Execute方法获取数据，要开启线程
     */
    private void retrofitExecute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ip = mIpText.getText().toString();
                if (ip.length() == 0) {
                    Toast.makeText(mContext, "请输入IP地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                String baseUrl = "http://ip.taobao.com";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                APIService apiService = retrofit.create(APIService.class);
                Call<DemoBean> call = apiService.getIpInfo(ip);
                try {
                    Response<DemoBean> response = call.execute();
                    DemoBean demoBean = response.body();
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", demoBean);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 回调使用
     */
    private void retrofitCallBack() {
        String ip = mIpText.getText().toString();
        if (ip.length() == 0) {
            Toast.makeText(mContext, "请输入IP地址", Toast.LENGTH_SHORT).show();
            return;
        }
        String baseUrl = "http://ip.taobao.com";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        Call<DemoBean> call = apiService.getIpInfo(ip);
        call.enqueue(new Callback<DemoBean>() {
            @Override
            public void onResponse(Response<DemoBean> response, Retrofit retrofit) {
                DemoBean demoBean = response.body();
                String ipInfo = String.format("这里是用回调的方式获取的数据，IP是：%s，我在%s%s%s", demoBean.data.ip, demoBean.data.country, demoBean.data.region, demoBean.data.city);
                mIpInfoText.setText(ipInfo);
            }

            @Override
            public void onFailure(Throwable t) {
                String err = "很遗憾失败了，错误是：" + t.toString();
                mIpInfoText.setText(err);
            }
        });
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle != null) {
                DemoBean demoBean = (DemoBean) bundle.getSerializable("data");
                String ipInfo = String.format("这里是阻塞的方式获取的数据，IP是：%s，我在%s%s%s", demoBean.data.ip, demoBean.data.country, demoBean.data.region, demoBean.data.city);
                Log.i("DebugLog", "Json获取的数据>>>"+ipInfo);
                mIpInfoText.setText(ipInfo);
            }
        }
    }
}