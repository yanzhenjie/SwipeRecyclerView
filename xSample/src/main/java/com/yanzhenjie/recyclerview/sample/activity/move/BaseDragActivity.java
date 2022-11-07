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
package com.yanzhenjie.recyclerview.sample.activity.move;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.sample.R;
import com.yanzhenjie.recyclerview.sample.activity.BaseActivity;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.touch.OnItemStateChangedListener;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <p>
 * Item拖拽排序，Item侧滑删除基础通用。
 * </p>
 * Created by YanZhenjie on 2017/7/22.
 */
public abstract class BaseDragActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setOnItemMenuClickListener(mItemMenuClickListener); // Item的Menu点击。
        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator); // 菜单创建器。

        // 在设置adapter之前给子类一个机会。
        beforeSettingAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);

        mRecyclerView.setOnItemMoveListener(getItemMoveListener());// 监听拖拽和侧滑删除，更新UI和数据源。
        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。

    }

    protected abstract OnItemMoveListener getItemMoveListener();
    protected void beforeSettingAdapter() { }

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                mActionBar.setSubtitle("状态：拖拽");

                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                viewHolder.itemView.setBackgroundColor(
                    ContextCompat.getColor(BaseDragActivity.this, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                mActionBar.setSubtitle("状态：滑动删除");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                mActionBar.setSubtitle("状态：手指松开");

                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView,
                    ContextCompat.getDrawable(BaseDragActivity.this, R.drawable.select_white));
            }
        }
    };

    /**
     * 菜单创建器。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(BaseDragActivity.this).setBackground(
                    R.drawable.selector_green).setImage(R.drawable.ic_action_add).setWidth(width).setHeight(height);
                swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。

                SwipeMenuItem closeItem = new SwipeMenuItem(BaseDragActivity.this).setBackground(
                    R.drawable.selector_red).setImage(R.drawable.ic_action_close).setWidth(width).setHeight(height);

                swipeLeftMenu.addMenuItem(closeItem); // 添加一个按钮到左侧菜单。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(BaseDragActivity.this).setBackground(
                    R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_delete)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

                SwipeMenuItem closeItem = new SwipeMenuItem(BaseDragActivity.this).setBackground(
                    R.drawable.selector_purple).setImage(R.drawable.ic_action_close).setWidth(width).setHeight(height);
                swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(BaseDragActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(BaseDragActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
            }
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