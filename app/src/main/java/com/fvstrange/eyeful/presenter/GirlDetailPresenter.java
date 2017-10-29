package com.fvstrange.eyeful.presenter;

import android.graphics.Bitmap;

import com.fvstrange.eyeful.data.Girl;
import com.fvstrange.eyeful.data.GirlComparator;
import com.fvstrange.eyeful.data.GirlData;
import com.fvstrange.eyeful.ui.activity.GirlDetailActivity;
import com.fvstrange.eyeful.util.LogUtil;
import com.fvstrange.eyeful.util.SaveImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by hasee on 2017/10/26.
 */

public class GirlDetailPresenter extends BasePresenter
{
    GirlDetailActivity mContext;
    private int mCurrentPosition = -1;
    private int mMaxPosition = -1;
    private static final int PAGE_SIZE = 10;
    private boolean hasMoreData=true;

    public GirlDetailPresenter(GirlDetailActivity context)
    {
        mContext=context;
    }

    public void setPosition(int position)
    {
        mCurrentPosition = position;
    }

    public void hasNoMoreData()
    {
        hasMoreData=false;
    }

    public int calculateDue(int count)
    {
        int i = count / 10;
        return (i + 1)*PAGE_SIZE;
    }

    public void refillData(final int position)
    {
        final int count = calculateDue(position + 1);

        mService.getGirlData(count,1)
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
                        } else if (girls.size() < count) {
                            mContext.fillData(girls);
                            hasNoMoreData();
                        } else if (girls.size() == count) {
                            mContext.fillData(girls);
                        }
                        if(!girls.isEmpty())
                        {
                            mMaxPosition = girls.size() - 1;
                            mContext.moveToPosition(position);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e)
                    {
                        mContext.showErrorView(e);
                    }
                });
    }

    public void addData()
    {
        int pages = (mMaxPosition + 1)/10 + 1;
        mService.getGirlData(PAGE_SIZE,pages)
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
                            hasNoMoreData();
                        } else if (girls.size() < PAGE_SIZE) {
                            mContext.appendMoreDataToView(girls);
                            hasNoMoreData();
                        } else if (girls.size() == PAGE_SIZE) {
                            mContext.appendMoreDataToView(girls);
                        }
                        if(!girls.isEmpty())
                        {
                            mMaxPosition += girls.size();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e)
                    {
                        mContext.showErrorView(e);
                    }
                });
    }

    /*
     * 判断是否请求新数据。
     */
    public boolean shouldAddNewData()
    {
        return hasMoreData && (mCurrentPosition + 1 >= mMaxPosition);
    }

    /*
     * 保存图片。保存形式:日期.jpeg
     * 保存成功提醒。
     * 保存失败提醒。
     */
    public void saveImage(String strFileName, PhotoView photoView)
    {
        Bitmap bitmap = photoView.getDrawingCache();
        int len = bitmap.getByteCount();
        LogUtil.d("GirlDetailPresenterLen",len+"");
        String strPath = SaveImageUtil.getSDPath();
        try
        {
            File destDir = new File(strPath);
            if (!destDir.exists())
            {
                destDir.mkdirs();
            }
            File imageFile = new File(strPath + "/" + strFileName);
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);

            fos.flush();
            fos.close();
            mContext.saveImageSuccessOrNot(true);

        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mContext.saveImageSuccessOrNot(false);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mContext.saveImageSuccessOrNot(false);
        }
    }
}
