package com.fvstrange.eyeful.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 结束所有活动
 * Created by hasee on 2017/10/20.
 */

public class ActivityCollector
{
    public static List<Activity> activities=new ArrayList<>();

    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activities.remove(activity);
    }

    public static void finishAll()
    {
        for(Activity activity:activities)
        {
            if(!activity.isFinishing())
                activity.finish();
        }
    }
}
