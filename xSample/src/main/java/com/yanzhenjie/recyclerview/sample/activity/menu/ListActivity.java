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
package com.yanzhenjie.recyclerview.sample.activity.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
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

/**
 * <p>
 * ListView形式的侧滑菜单，支持横向和竖向的。
 * </p>
 * Created by Yan Zhenjie on 2016/7/27.
 */
public class ListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(ListActivity.this).setBackground(R.drawable.selector_green)
                    .setImage(R.drawable.ic_action_add)
                    .setWidth(width)
                    .setHeight(height);
                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。

                SwipeMenuItem closeItem = new SwipeMenuItem(ListActivity.this).setBackground(R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_close)
                    .setWidth(width)
                    .setHeight(height);
                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(ListActivity.this).setBackground(R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_delete)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(ListActivity.this).setBackground(R.drawable.selector_green)
                    .setText("添加")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(ListActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(ListActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
            }
        }
    };

    // ---------- 开发者只需要关注上面的代码 ---------- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_open_rv_menu) {
            mRecyclerView.smoothOpenRightMenu(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}