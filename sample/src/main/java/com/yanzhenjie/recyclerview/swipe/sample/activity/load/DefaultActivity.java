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
package com.yanzhenjie.recyclerview.swipe.sample.activity.load;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 默认的加载更多的View。
 * </p>
 * Created by YanZhenjie on 2017/7/21.
 */
public class DefaultActivity extends AppCompatActivity {

    private SwipeRefreshLayout mRefreshLayout;
    private SwipeMenuRecyclerView mRecyclerView;
    private MainAdapter mAdapter;
    private List<String> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_loadmore);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。

        mRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        mRecyclerView.setSwipeItemClickListener(mItemClickListener); // RecyclerView Item点击监听。

        mRecyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。

        mAdapter = new MainAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // 请求服务器加载数据。
        loadData();
    }

    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 1000); // 延时模拟请求服务器。
        }
    };

    /**
     * 加载更多。
     */
    private SwipeMenuRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeMenuRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<String> strings = createDataList(mAdapter.getItemCount());
                    mDataList.addAll(strings);
                    // notifyItemRangeInserted()或者notifyDataSetChanged().
                    mAdapter.notifyItemRangeInserted(mDataList.size() - strings.size(), strings.size());

                    // 数据完更多数据，一定要掉用这个方法。
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    mRecyclerView.loadMoreFinish(false, true);

                    // 如果加载失败调用下面的方法，传入errorCode和errorMessage。
                    // errorCode随便传，你自定义LoadMoreView时可以根据errorCode判断错误类型。
                    // errorMessage是会显示到loadMoreView上的，用户可以看到。
                    // mRecyclerView.loadMoreError(0, "请求网络失败");
                }
            }, 1000);
        }
    };

    /**
     * Item点击监听。
     */
    private SwipeItemClickListener mItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            Toast.makeText(DefaultActivity.this, "第" + position + "个", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 第一次加载数据。
     */
    private void loadData() {
        mDataList = createDataList(0);
        mAdapter.notifyDataSetChanged(mDataList);

        mRefreshLayout.setRefreshing(false);

        // 第一次加载数据：一定要调用这个方法，否则不会触发加载更多。
        // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
        // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
        mRecyclerView.loadMoreFinish(false, true);
    }

    protected List<String> createDataList(int start) {
        List<String> strings = new ArrayList<>();
        for (int i = start; i < start + 100; i++) {
            strings.add("第" + i + "个Item");
        }
        return strings;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
