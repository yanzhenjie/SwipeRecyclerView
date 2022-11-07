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
package com.yanzhenjie.recyclerview.sample.activity.move;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.yanzhenjie.recyclerview.sample.R;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.Collections;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <p>
 * 拖拽Item，侧滑菜单一起使用，是Grid形式的。
 * </p>
 * Created by Yan Zhenjie on 2016/8/3.
 */
public class DragGridActivity extends BaseDragActivity {

    View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHeaderView = getLayoutInflater().inflate(R.layout.layout_header_switch, mRecyclerView, false);
        mRecyclerView.addHeaderView(mHeaderView);

        ToggleButton switchCompat = mHeaderView.findViewById(R.id.switch_compat);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 控制是否可以侧滑删除。
                mRecyclerView.setItemViewSwipeEnabled(isChecked);
            }
        });

        ToggleButton switchSwipeMenu = mHeaderView.findViewById(R.id.switch_swipemenu);
        switchSwipeMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 控制是否可以侧滑出菜单——全局控制。
                mRecyclerView.setSwipeItemMenuEnabled(isChecked);
            }
        });

        mRecyclerView.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(this, 2);
    }

    @Override
    protected OnItemMoveListener getItemMoveListener() {
        // 监听拖拽和侧滑删除，更新UI和数据源。
        return new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                // 不同的ViewType不能拖拽换位置。
                if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

                // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
                int fromPosition = srcHolder.getAdapterPosition() - mRecyclerView.getHeaderCount();
                int toPosition = targetHolder.getAdapterPosition() - mRecyclerView.getHeaderCount();

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++)
                        Collections.swap(mDataList, i, i + 1);
                } else {
                    for (int i = fromPosition; i > toPosition; i--)
                        Collections.swap(mDataList, i, i - 1);
                }

                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;// 返回true表示处理了，返回false表示你没有处理。
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                int adapterPosition = srcHolder.getAdapterPosition();
                int position = adapterPosition - mRecyclerView.getHeaderCount();

                if (mRecyclerView.getHeaderCount() > 0 && adapterPosition == 0) { // HeaderView。
                    mRecyclerView.removeHeaderView(mHeaderView);
                    Toast.makeText(DragGridActivity.this, "HeaderView被删除。", Toast.LENGTH_SHORT).show();
                } else { // 普通Item。
                    mDataList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    Toast.makeText(DragGridActivity.this, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
}