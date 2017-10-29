package com.fvstrange.eyeful.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fvstrange.eyeful.R;
import com.fvstrange.eyeful.data.Girl;
import com.fvstrange.eyeful.presenter.GirlListPresenter;
import com.fvstrange.eyeful.ui.adapter.GirlListAdapter;
import com.fvstrange.eyeful.util.ActivityCollector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GirlListActivity extends BaseActivity implements GirlListAdapter.IClickItem
{
    private GirlListAdapter mAdapter;
    private GirlListPresenter mPresenter;
    private boolean mHasMoreData = true;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.girl_list_layout);
        ButterKnife.bind(this);
        initToolbar();
        initSwipeRefreshLayout();
        initPresenter();
        initRecyclerView();
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }
    /*
     * 初始化Toolbar,设置导航按钮。
     */
    private void initToolbar()
    {
        toolbar.setTitle("Girls");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

    }

    /*
     * 初始化SwipeRefreshLayout，并设置监听刷新事件。
     */
    private void initSwipeRefreshLayout()
    {
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if (prepareRefresh())
                    onRefreshStarted();
                else
                    hideRefresh();
            }
        });
    }

    private void initPresenter()
    {
        mPresenter = new GirlListPresenter(this);
    }

    /*
     * 初始化RecyclerView,设置显示方式为两列瀑布流。
     * 创建GirlListAdapter实例，完成适配器设置。
     * 设置RecyclerView下滑监听事件，当当前没有加载数据、屏幕滑到底部、有更多数据时加载新数据。
     */
    private void initRecyclerView()
    {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GirlListAdapter(this);
        mAdapter.setIClickItem(this);
        mRecyclerView.setAdapter(mAdapter);
        //取消RecyclerView动画效果。
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                boolean isBottom =
                        layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]
                                >= mAdapter.getItemCount() - 4;
                if (!mSwipeRefresh.isRefreshing() && isBottom && mHasMoreData)
                {
                    showRefresh();
                    mPresenter.addData();
                }
            }
        });
    }

    /*
     * 判断是否正在刷新。
     */
    private boolean isRefreshing()
    {
        return mSwipeRefresh.isRefreshing();
    }

    /*
     * 显示刷新图标。
     */
    private void showRefresh()
    {
        mSwipeRefresh.setRefreshing(true);
    }

    /*
     * 延时隐藏刷新图标，防止图标消失的太快。
     */
    private void hideRefresh()
    {
        mSwipeRefresh.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mSwipeRefresh != null)
                    mSwipeRefresh.setRefreshing(false);
            }
        }, 1200);
    }

    /*
     * 已获得数据，隐藏刷新图标。
     */
    public void getDataFinish()
    {
        hideRefresh();
    }

    /*
     * 判断是否重新加载数据。结果为true时显示刷新图标。
     */
    public boolean prepareRefresh()
    {
        if (mPresenter.shouldRefillGirls())
        {
            mPresenter.resetCurrentPage();
            if (!isRefreshing())
            {
                showRefresh();
            }
            return true;
        } else
        {
            return false;
        }
    }

    /*
     * 重新加载数据。
     */
    public void onRefreshStarted()
    {
        mPresenter.refillData();
    }

    public void appendMoreDataToView(List<Girl> data, int currentQuantity, int newQuantity)
    {
        mAdapter.update(data, currentQuantity, newQuantity);
    }

    public void fillData(List<Girl> data)
    {
        mAdapter.updateWithClear(data);
    }

    /*
     * 没有更多数据的提示信息。
     */
    public void showEmptyView()
    {
        Toast.makeText(GirlListActivity.this, "妹纸见底了，没有更多了~", Toast.LENGTH_SHORT).show();
    }

    public void hasNoMoreData()
    {
        mHasMoreData = false;
        showEmptyView();
    }

    /*
     * 加载失败显示提示信息。
     */
    public void showErrorView(Throwable throwable)
    {
        throwable.printStackTrace();

        final Snackbar errorSnack = Snackbar.make(mRecyclerView, "加载数据失败，请检查网络，点击重试。"
                , Snackbar.LENGTH_INDEFINITE);
        errorSnack.getView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                errorSnack.dismiss();
                onRefreshStarted();
            }
        });
        errorSnack.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.exit:
                ActivityCollector.finishAll();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mPresenter.refillData();
    }

    @Override
    public void onClickPhoto(int position)
    {
        Intent intent=new Intent(GirlListActivity.this, GirlDetailActivity.class);
        intent.putExtra("extra_position",position);
        startActivity(intent);
    }
}
