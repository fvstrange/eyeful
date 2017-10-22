package com.fvstrange.eyeful.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fvstrange.eyeful.R;
import com.fvstrange.eyeful.data.Girl;
import com.fvstrange.eyeful.util.DataUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/10/21.
 */

public class GirlListAdapter extends RecyclerView.Adapter<GirlListAdapter.ViewHolder>
{
    private List<Girl> mGirlList;
    Context mContext;
    private static int SCREE_WIDTH = 0;

    public GirlListAdapter(Context context)
    {
        mContext=context;
        mGirlList=new ArrayList<>();
        SCREE_WIDTH = mContext.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girl_item, null);
        ImageView girlImage=(ImageView) view.findViewById(R.id.girl_image);
        girlImage.getLayoutParams().height = (int) (new Random().nextInt(150) + 250);
        girlImage.getLayoutParams().width = SCREE_WIDTH / 2;
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Girl girl=mGirlList.get(position);

        Glide.with(mContext)
                .load(girl.url)
                //.thumbnail( 0.1f )
                .into(holder.girlImage);

        holder.girlDate.setText(DataUtil.toDate(girl.publishedAt));
    }

    @Override
    public int getItemCount()
    {
        return mGirlList.size();
    }

    public Girl getGirl(int position)
    {
        return mGirlList.get(position);
    }

    /**
     * 在原集合基础上添加新数据
     * @param data new data
     */
    public void update(List<Girl> data){
        mGirlList.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 清除原有数据添加新数据
     * @param data
     */
    public void updateWithClear(List<Girl> data){
        mGirlList.clear();
        mGirlList.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.girl_image)
        ImageView girlImage;
        @BindView(R.id.girl_date)
        TextView girlDate;
        public ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
