/*
 * Copyright 2016 Yan Zhenjie
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
package com.yanzhenjie.recyclerview.swipe.sample.activity.move;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.adapter.MainAdapter;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 拖拽Item + 侧滑删除，默认侧滑删除只支持List形式。
 * </p>
 * Created by Yan Zhenjie on 2016/8/3.
 */
public class DragSwipeListActivity extends BaseDragActivity {

    private MainAdapter mAdapter;
    private List<String> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = getAdapter();
        mDataList = getDataList();
    }

    @Override
    protected void addHeaderFooter(final SwipeMenuRecyclerView recyclerView) {
        View header = getLayoutInflater().inflate(R.layout.layout_header_switch, recyclerView, false);
        recyclerView.addHeaderView(header);

        SwitchCompat switchCompat = (SwitchCompat) header.findViewById(R.id.switch_compat);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 控制是否可以侧滑删除。
                recyclerView.setItemViewSwipeEnabled(isChecked);
            }
        });
    }

    @Override
    protected OnItemMoveListener getItemMoveListener() {
        // 监听拖拽和侧滑删除，更新UI和数据源。
        return onItemMoveListener;
    }

    /**
     * 监听拖拽和侧滑删除，更新UI和数据源。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            // 添加了HeadView时，通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = srcHolder.getAdapterPosition() - getRecyclerView().getHeaderItemCount();
            int toPosition = targetHolder.getAdapterPosition() - getRecyclerView().getHeaderItemCount();

            Collections.swap(mDataList, fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition() - getRecyclerView().getHeaderItemCount();
            if (position < 0) return; // 因为添加了HeadView，这里的HeadView是第0个，减掉后肯定是负数，所以不许删除。

            mDataList.remove(position);
            mAdapter.notifyItemRemoved(position);
            Toast.makeText(DragSwipeListActivity.this, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
        }
    };
}