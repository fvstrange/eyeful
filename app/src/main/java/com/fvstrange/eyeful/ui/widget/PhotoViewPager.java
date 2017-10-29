package com.fvstrange.eyeful.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hasee on 2017/10/26.
 */

public class PhotoViewPager extends ViewPager
{
    public PhotoViewPager(Context context) {
        super(context);
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event)
    {
        try
        {
            return super.onInterceptHoverEvent(event);
        }
        catch (IllegalArgumentException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }
}
