package com.fvstrange.eyeful.data;


import java.util.Comparator;

/**
 * Created by hasee on 2017/10/21.
 */

public class GirlComparator implements Comparator<Girl>
{
    @Override
    public int compare(Girl girl, Girl girl2)
    {
        return girl2.publishedAt.compareTo(girl.publishedAt);
    }
}
