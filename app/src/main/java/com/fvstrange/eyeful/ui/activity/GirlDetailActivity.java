package com.fvstrange.eyeful.ui.activity;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fvstrange.eyeful.R;
import com.fvstrange.eyeful.data.Girl;
import com.fvstrange.eyeful.presenter.GirlDetailPresenter;
import com.fvstrange.eyeful.ui.adapter.GirlDetailAdapter;
import com.fvstrange.eyeful.ui.widget.PhotoViewPager;
import com.fvstrange.eyeful.util.DateUtil;
import com.wingsofts.byeburgernavigationview.ByeBurgerBehavior;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

public class GirlDetailActivity extends BaseActivity implements GirlDetailAdapter.IClickItem
{

    GirlDetailAdapter mAdapter;
    GirlDetailPresenter mPresenter;
    private boolean isShowAboutToolbar = true;

    @BindView(R.id.view_pager_photo)
    PhotoViewPager mViewPager;
    @BindView(R.id.girl_detail_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.girl_detail_layout);
        ButterKnife.bind(this);
        initToolbar();
        initPresenter();
        initData();
    }

    /*
     * 初始化Toolbar,设置返回按钮。
     */
    private void initToolbar()
    {
        mToolbar.setTitle("查看图片");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.girl_detail_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                saveImage();
                break;
            default:
        }
        return true;
    }

    /*
     * 保存ViewPager当前未知的图片到手机。
     */
    private void saveImage()
    {
        Girl girl=mAdapter.getGirl(mViewPager.getCurrentItem());
        String strFileName= DateUtil.toHyphenDate(girl.publishedAt) + ".jpeg";
        PhotoView photoView=(PhotoView)mAdapter.getPrimaryItem();
        mPresenter.saveImage(strFileName, photoView);
    }

    /*
     * 图片保存成功、失败提醒。
     */
    public void saveImageSuccessOrNot(boolean value)
    {
        if (value)
            Toast.makeText(GirlDetailActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(GirlDetailActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
    }

    /*
     * ViewPager设置适配器。
     * 获取GirlListActivity传递过来的位置信息。
     * ViewPager更新数据并跳转到指定位置。
     * Viewpager添加OnPageChangeListener监听事件。当位置合适添加新数据。
     */
    private void initData()
    {
        mAdapter = new GirlDetailAdapter(this);
        mAdapter.setmIClickItem(this);
        mViewPager.setAdapter(mAdapter);
        Intent intent = getIntent();
        final int position = intent.getIntExtra("extra_position", 0);
        mPresenter.setPosition(position);
        mPresenter.refillData(position);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                mPresenter.setPosition(position);
                if (mPresenter.shouldAddNewData())
                    mPresenter.addData();
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    private void initPresenter()
    {
        mPresenter = new GirlDetailPresenter(this);
    }

    /*
     * 加载失败信息提示。
     */
    public void showErrorView(Throwable throwable)
    {
        throwable.printStackTrace();
        Toast.makeText(GirlDetailActivity.this, "加载失败，请检查网络！", Toast.LENGTH_SHORT).show();
    }

    /*
     * 请求并添加新数据。
     */
    public void appendMoreDataToView(List<Girl> data)
    {
        mAdapter.update(data);
    }

    /*
     * 删除原有数据，请求并添加新数据。
     */
    public void fillData(List<Girl> data)
    {
        mAdapter.updateWithClear(data);
    }

    /*
     * ViewPager跳转到指定位置。
     */
    public void moveToPosition(int position)
    {
        mViewPager.setCurrentItem(position);
    }

    /*
     * 控制Toolbar的显示与隐藏。
     */
    private void showOrHide()
    {
        if(isShowAboutToolbar)
        {
            ByeBurgerBehavior.from(mToolbar).hide();
            isShowAboutToolbar = false;
        }
        else
        {
            ByeBurgerBehavior.from(mToolbar).show();
            isShowAboutToolbar = true;
        }
    }

    @Override
    public void onClickItem()
    {
        showOrHide();
    }
}
