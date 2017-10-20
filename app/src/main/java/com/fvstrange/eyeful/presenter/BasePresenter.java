package com.fvstrange.eyeful.presenter;

import com.fvstrange.eyeful.core.DataService;
import com.fvstrange.eyeful.core.MainFactory;

/**
 * Created by hasee on 2017/10/20.
 */

public class BasePresenter
{
    public static final DataService mService= MainFactory.getDataServiceInstance();
}
