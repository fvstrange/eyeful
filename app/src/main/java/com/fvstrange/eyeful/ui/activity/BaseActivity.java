package com.fvstrange.eyeful.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fvstrange.eyeful.util.ActivityCollector;
import com.fvstrange.eyeful.util.LogUtil;

/**
 * Created by hasee on 2017/10/20.
 */

public class BaseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LogUtil.d("BaseActivity",getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
