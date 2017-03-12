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
package com.yanzhenjie.swiperecyclerview.activity.move;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMovementListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.yanzhenjie.swiperecyclerview.R;
import com.yanzhenjie.swiperecyclerview.adapter.MenuAdapter;
import com.yanzhenjie.swiperecyclerview.listener.OnItemClickListener;
import com.yanzhenjie.swiperecyclerview.view.ListViewDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>自定义拖拽规则的。</p>
 * Created by Yan Zhenjie on 2016/8/3.
 */
public class DragSwipeFlagsActivity extends AppCompatActivity {

    private Activity mContext;

    private ActionBar mActionBar;

    private List<String> mDataList;

    private MenuAdapter mMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mDataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (i == 0) {
                mDataList.add("我不能被拖拽，也不能滑动删除。");
            } else {
                mDataList.add("我是第" + i + "个。");
            }
        }
        SwipeMenuRecyclerView menuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        menuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        // 这个就不用添加菜单啦，因为滑动删除和菜单是冲突的。

        mMenuAdapter = new MenuAdapter(mDataList);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        menuRecyclerView.setAdapter(mMenuAdapter);

        menuRecyclerView.setLongPressDragEnabled(true);// 开启长安拖拽。
        menuRecyclerView.setItemViewSwipeEnabled(true);// 开启滑动删除。
        menuRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。
        menuRecyclerView.setOnItemMovementListener(onItemMovementListener);
        menuRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener);
    }

    /**
     * 当Item被移动之前。
     */
    public static OnItemMovementListener onItemMovementListener = new OnItemMovementListener() {
        /**
         * 当Item在移动之前，获取拖拽的方向。
         * @param recyclerView     {@link RecyclerView}.
         * @param targetViewHolder target ViewHolder.
         * @return
         */
        @Override
        public int onDragFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder) {
            // 我们让第一个不能拖拽。
            if (targetViewHolder.getAdapterPosition() == 0) {
                return OnItemMovementListener.INVALID;// 返回无效的方向。
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {// 如果是LinearLayoutManager。
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {// 横向的List。
                    return (OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT); // 只能左右拖拽。
                } else {// 竖向的List。
                    return OnItemMovementListener.UP | OnItemMovementListener.DOWN; // 只能上下拖拽。
                }
            }
//            else if (layoutManager instanceof GridLayoutManager) {// 如果是Grid。
//                return OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT | OnItemMovementListener.UP |
//                        OnItemMovementListener.DOWN; // 可以上下左右拖拽。
//            }
            return OnItemMovementListener.INVALID;// 返回无效的方向。
        }

        @Override
        public int onSwipeFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder) {
            // 我们让第一个不能滑动删除。
            if (targetViewHolder.getAdapterPosition() == 0) {
                return OnItemMovementListener.INVALID;// 返回无效的方向。
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {// 如果是LinearLayoutManager
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {// 横向的List。
                    return OnItemMovementListener.UP | OnItemMovementListener.DOWN; // 只能上下滑动删除。
                } else {// 竖向的List。
                    return OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT; // 只能左右滑动删除。
                }
            }
            return OnItemMovementListener.INVALID;// 其它均返回无效的方向。
        }
    };

    /**
     * 当Item移动的时候。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (toPosition == 0) {// 保证第一个不被挤走。
                return false;
            }
            if (fromPosition < toPosition)
                for (int i = fromPosition; i < toPosition; i++)
                    Collections.swap(mDataList, i, i + 1);
            else
                for (int i = fromPosition; i > toPosition; i--)
                    Collections.swap(mDataList, i, i - 1);

            mMenuAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            mDataList.remove(position);
            mMenuAdapter.notifyItemRemoved(position);
            Toast.makeText(mContext, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = (viewHolder, actionState) -> {
        if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
            mActionBar.setSubtitle("状态：拖拽");

            // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_pressed));
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
            mActionBar.setSubtitle("状态：滑动删除");
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
            mActionBar.setSubtitle("状态：手指松开");

            // 在手松开的时候还原背景。
            ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(this, R.drawable.select_white));
        }
    };

    /**
     * Item点击监听。
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(mContext, "我现在是第" + position + "条。", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}