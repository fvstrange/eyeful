package com.fvstrange.eyeful.core;

/**
 * Created by hasee on 2017/10/20.
 */

public class MainFactory
{
    public static final String BASE_URL = "http://gank.io/api/";

    private static DataService mService;

    public static DataService getDataServiceInstance()
    {
        synchronized (MainFactory.class)
        {
            if(mService==null)
                mService=new MainRetrofit().getService();
        }
        return mService;
    }
}
