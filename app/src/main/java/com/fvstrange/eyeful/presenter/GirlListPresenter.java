package com.fvstrange.eyeful.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.fvstrange.eyeful.data.Girl;
import com.fvstrange.eyeful.data.GirlComparator;
import com.fvstrange.eyeful.data.GirlData;
import com.fvstrange.eyeful.ui.activity.GirlListActivity;
import com.fvstrange.eyeful.util.LogUtil;

import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hasee on 2017/10/21.
 */

public class GirlListPresenter extends BasePresenter
{
    GirlListActivity mContext;

    private int mCurrentPage = 1;
    private static final int PAGE_SIZE = 10; //请求个数

    public GirlListPresenter(GirlListActivity context)
    {
        mContext=context;
    }

    public void resetCurrentPage(){
        mCurrentPage = 1;
    }

    /**
     * 当前加载页数小于等于2时，才去执行重新加载的数据请求。
     * 当加载页数超过2，此时的刷新为假刷新操作。
     */
    public boolean shouldRefillGirls(){
        return mCurrentPage <= 2;
    }

    public void refillData()
    {
        mService.getGirlData(PAGE_SIZE,mCurrentPage)
                .map(new Function<GirlData, List<Girl>>()
                {
                    @Override
                    public List apply(@NonNull GirlData girlData) throws Exception
                    {
                        return girlData.results;
                    }
                })
                .flatMap(new Function<List<Girl>, ObservableSource<Girl>>()
                {
                    @Override
                    public ObservableSource<Girl> apply(@NonNull List<Girl> girls) throws Exception
                    {
                        return Observable.fromIterable(girls);
                    }
                })
                .toSortedList(new GirlComparator())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Girl>>()
                {
                    private Disposable disposable;
                    @Override
                    public void onSubscribe(@NonNull Disposable d)
                    {
                        disposable=d;
                    }

                    @Override
                    public void onSuccess(@NonNull List<Girl> girls)
                    {
                        if (girls.isEmpty()) {
                            mContext.showEmptyView();
                        } else if (girls.size() < PAGE_SIZE) {
                            mContext.fillData(girls);
                            mContext.hasNoMoreData();
                        } else if (girls.size() == PAGE_SIZE) {
                            mContext.fillData(girls);
                            mCurrentPage++;
                        }
                        mContext.getDataFinish();
                    }

                    @Override
                    public void onError(@NonNull Throwable e)
                    {
                        mContext.showErrorView(e);
                        mContext.getDataFinish();
                    }
                });
    }

    public void addData()
    {
        mService.getGirlData(PAGE_SIZE,mCurrentPage)
                .map(new Function<GirlData, List<Girl>>()
                {
                    @Override
                    public List apply(@NonNull GirlData girlData) throws Exception
                    {
                        return girlData.results;
                    }
                })
                .flatMap(new Function<List<Girl>, ObservableSource<Girl>>()
                {
                    @Override
                    public ObservableSource<Girl> apply(@NonNull List<Girl> girls) throws Exception
                    {
                        return Observable.fromIterable(girls);
                    }
                })
                .toSortedList(new GirlComparator())
                .subscribeOn(Schedulers.io()) //不要在主线程中进行Http请求
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Girl>>()
                {
                    private Disposable disposable;
                    @Override
                    public void onSubscribe(@NonNull Disposable d)
                    {
                        disposable=d;
                    }

                    @Override
                    public void onSuccess(@NonNull List<Girl> girls)
                    {
                        if (girls.isEmpty()) {
                            mContext.hasNoMoreData();
                        } else if (girls.size() < PAGE_SIZE) {
                            mContext.appendMoreDataToView(girls,(mCurrentPage-1)*10,girls.size());
                            mContext.hasNoMoreData();
                        } else if (girls.size() == PAGE_SIZE) {
                            mContext.appendMoreDataToView(girls,(mCurrentPage-1)*10,girls.size());
                            mCurrentPage++;
                        }
                        mContext.getDataFinish();
                    }

                    @Override
                    public void onError(@NonNull Throwable e)
                    {
                        mContext.showErrorView(e);
                        mContext.getDataFinish();
                    }
                });
    }

}
