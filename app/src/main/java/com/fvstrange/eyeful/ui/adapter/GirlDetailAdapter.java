package com.fvstrange.eyeful.ui.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fvstrange.eyeful.R;
import com.fvstrange.eyeful.data.Girl;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hasee on 2017/10/26.
 */

public class GirlDetailAdapter extends PagerAdapter
{
    private List<Girl> mGirlList;
    private Context mContext;
    private IClickItem mIClickItem;
    private View mCurrentView;

    public void setmIClickItem(IClickItem iClickItem)
    {
        mIClickItem=iClickItem;
    }

    public GirlDetailAdapter(Context context)
    {
        mContext=context;
        mGirlList=new ArrayList<>();
    }

    public void update(List<Girl> data)
    {
        mGirlList.addAll(data);
        notifyDataSetChanged();
    }

    public void updateWithClear(List<Girl> data)
    {
        mGirlList.clear();
        mGirlList.addAll(data);
        notifyDataSetChanged();
    }

    public Girl getGirl(int position)
    {
        return mGirlList.get(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        mCurrentView = (View)object;
    }

    /*
     * 获取当前子view实例。
     */
    public View getPrimaryItem() {
        return mCurrentView;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        PhotoViewAttacher.OnPhotoTapListener listener = new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y)
            {
                mIClickItem.onClickItem();
            }
        };
        Girl girl = mGirlList.get(position);
        final PhotoView photoView = new PhotoView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setLayoutParams(layoutParams);
        photoView.setOnPhotoTapListener(listener);
        SimpleTarget simpleTarget = new SimpleTarget<BitmapDrawable> ()
        {
            @Override
            public void onResourceReady(BitmapDrawable resource, Transition transition)
            {
                photoView.setImageDrawable(resource);
            }
        };
        RequestOptions options = new RequestOptions()
                .centerCrop()
                //.placeholder(R.drawable.bg_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.bg_error);
        Glide.with(mContext)
                .load(girl.url)
                .apply(options)
                .into(simpleTarget);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getCount()
    {
        return mGirlList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    public interface IClickItem
    {
        void onClickItem();
    }

}
