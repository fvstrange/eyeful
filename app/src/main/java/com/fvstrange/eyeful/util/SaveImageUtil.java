package com.fvstrange.eyeful.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

/**
 * Created by hasee on 2017/10/28.
 */

public class SaveImageUtil
{
    public static String getSDPath()
    {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(hasSDCard)
        {
            return Environment.getExternalStorageDirectory().toString() + "/Eyeful";
        }
        else
            return "/data/data/com.fvstrange.eyeful/Eyeful";
    }
}
