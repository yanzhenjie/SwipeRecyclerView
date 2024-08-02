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
package com.yanzhenjie.recyclerview.sample.activity.load;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.sample.R;
import com.yanzhenjie.recyclerview.sample.adapter.MainAdapterDiff;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 默认的加载更多的View - 使用AsyncListDiffer提升刷新效率！
 * Created by QM-LU on 2022/10/22.
 */
public class DefaultActivityDiff extends AppCompatActivity {

    private SwipeRefreshLayout mRefreshLayout;
    private SwipeRecyclerView mRecyclerView;
    private MainAdapterDiff mAdapter;
    private List<String> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_loadmore);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener); // 刷新监听。

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        mRecyclerView.setOnItemClickListener(mItemClickListener); // RecyclerView Item点击监听。

        mRecyclerView.useDefaultLoadMore(); // 使用默认的加载更多的View。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。

        mAdapter = new MainAdapterDiff(this);
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
                    List<String> newList = new ArrayList<>();
                    List<String> strings = createDataList(mAdapter.getItemCount());
                    newList.addAll(mAdapter.getList());
                    newList.addAll(strings);
                    mAdapter.submitList(newList);

                    mRecyclerView.loadMoreFinish(false, true);
                    mRefreshLayout.setRefreshing(false);
                }
            }, 500); // 延时模拟请求服务器。
        }
    };

    /**
     * 加载更多。
     */
    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<String> newList = new ArrayList<>();
                    List<String> strings = createDataList(mAdapter.getItemCount());
                    newList.addAll(mAdapter.getList());
                    newList.addAll(strings);
                    mAdapter.submitList(newList);

                    // 数据完更多数据，一定要调用这个方法。
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
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            Toast.makeText(DefaultActivityDiff.this, "第" + position + "个", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 第一次加载数据。
     */
    private void loadData() {
        List<String> strings = createDataList(0);
        mAdapter.submitList(strings);

        mRefreshLayout.setRefreshing(false);

        // 第一次加载数据：一定要调用这个方法，否则不会触发加载更多。
        // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
        // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提高性能，如果不能判断则传true。
        mRecyclerView.loadMoreFinish(false, true);
    }

    protected List<String> createDataList(int start) {
        List<String> strings = new ArrayList<>();
        for (int i = start; i < start + 20; i++) {
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