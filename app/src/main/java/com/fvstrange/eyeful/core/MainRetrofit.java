package com.fvstrange.eyeful.core;

import android.util.Log;

import com.fvstrange.eyeful.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hasee on 2017/10/20.
 */

public class MainRetrofit
{
    final DataService mService;

    final Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").serializeNulls().create(); //Gson解析null替换为空字符串

    MainRetrofit()
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger()
        {
            @Override
            public void log(String message)
            {
                //LogUtil.e("okhttp", message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.connectTimeout(15, TimeUnit.SECONDS);//连接超时时间
        builder.readTimeout(20, TimeUnit.SECONDS);//读取超时时间
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.addInterceptor(logInterceptor).build())
                .baseUrl(MainFactory.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService=retrofit.create(DataService.class);
    }

    public DataService getService()
    {
        return mService;
    }
}
