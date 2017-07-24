/*
 * Copyright 2017 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.recyclerview.swipe.sample.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.swipe.widget.GridItemDecoration;
import com.yanzhenjie.recyclerview.swipe.widget.ListItemDecoration;

import java.util.List;

/**
 * <p>
 * Demo中很多代码都是通用的，所以封装一下，不然就冗余了。
 * </p>
 * Created by YanZhenjie on 2017/7/21.
 */
public abstract class BaseActivity extends AppCompatActivity implements SwipeItemClickListener {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private SwipeMenuRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

        if (displayHomeAsUpEnabled()) mActionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.addItemDecoration(getItemDecoration());
        mRecyclerView.setAdapter(getAdapter());

        // RecyclerView的Item的点击事件。
        mRecyclerView.setSwipeItemClickListener(this);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * 获取Content View.
     */
    protected int getLayoutId() {
        return R.layout.activity;
    }

    /**
     * 获取RecyclerView。
     */
    protected final SwipeMenuRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 获取RecyclerView的布局管理器。
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        if (mLayoutManager == null)
            mLayoutManager = new LinearLayoutManager(this);
        return mLayoutManager;
    }

    /**
     * 获取RecyclerView的Item分割线。
     */
    protected RecyclerView.ItemDecoration getItemDecoration() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return new GridItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
        } else {
            return new ListItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
        }
    }

    /**
     * 获取RecyclerView的适配器。
     */
    protected RecyclerView.Adapter getAdapter() {
        if (mAdapter == null)
            mAdapter = new MainAdapter(getItemList());
        return mAdapter;
    }

    /**
     * 获取界面需要展示的数据。
     */
    protected abstract List<String> getItemList();

    protected boolean displayHomeAsUpEnabled() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}