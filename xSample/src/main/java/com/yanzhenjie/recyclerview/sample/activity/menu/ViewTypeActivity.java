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
import com.yanzhenjie.recyclerview.sample.adapter.BaseAdapter;
import com.yanzhenjie.recyclerview.sample.adapter.MainAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 根据ViewType自定义菜单。
 * </p>
 * Created by Yan Zhenjie on 2016/7/27.
 */
public class ViewTypeActivity extends BaseActivity {

    private static final int VIEWTYPE_TWO = 1;
    private static final int VIEWTYPE_THREE = 2;
    private static final int VIEWTYPE_OTHER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setOnItemMenuClickListener(mItemMenuClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
    }

    @Override
    protected BaseAdapter createAdapter() {
        return new MainAdapter(this) {
            @Override
            public int getItemViewType(int position) {
                if (position % 3 == 0) {
                    return VIEWTYPE_THREE;
                } else if (position % 2 == 0) {
                    return VIEWTYPE_TWO;
                } else {
                    return VIEWTYPE_OTHER;
                }
            }
        };
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

            // 1. 根据ViewType来决定哪一个item该如何添加菜单。
            // 2. 更多的开发者需要的是根据position，因为不同的ViewType之间不会有缓存优化效果。
            int viewType = mAdapter.getItemViewType(position);
            if (viewType == VIEWTYPE_THREE) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(ViewTypeActivity.this).setBackground(
                    R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_delete)
                    .setText("删除")
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

                SwipeMenuItem closeItem = new SwipeMenuItem(ViewTypeActivity.this).setBackground(
                    R.drawable.selector_purple).setImage(R.drawable.ic_action_close).setWidth(width).setHeight(height);
                swipeRightMenu.addMenuItem(closeItem); // 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(ViewTypeActivity.this).setBackground(
                    R.drawable.selector_green)
                    .setText("添加")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            } else if (viewType == VIEWTYPE_TWO) {
                SwipeMenuItem closeItem = new SwipeMenuItem(ViewTypeActivity.this).setBackground(
                    R.drawable.selector_purple).setImage(R.drawable.ic_action_close).setWidth(width).setHeight(height);
                swipeRightMenu.addMenuItem(closeItem); // 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(ViewTypeActivity.this).setBackground(
                    R.drawable.selector_green)
                    .setText("添加")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            } else if (viewType == VIEWTYPE_OTHER) {
                SwipeMenuItem addItem = new SwipeMenuItem(ViewTypeActivity.this).setBackground(
                    R.drawable.selector_green).setImage(R.drawable.ic_action_add).setWidth(width).setHeight(height);
                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。

                SwipeMenuItem closeItem = new SwipeMenuItem(ViewTypeActivity.this).setBackground(
                    R.drawable.selector_red).setText("删除").setTextColor(Color.WHITE).setWidth(width).setHeight(height);
                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
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
                Toast.makeText(ViewTypeActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(ViewTypeActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
            }
        }
    };

    // ---------- 开发者只需要关注上面的代码 ---------- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // https://blog.csdn.net/weixin_42451611/article/details/117592218
        getMenuInflater().inflate(R.menu.menu_more_tests, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_open_rv_menu:
                mRecyclerView.smoothOpenRightMenu(0);
                break;
            case R.id.menu_open_rv_menu2:
                mRecyclerView.smoothOpenLeftMenu(1);
                break;
            case R.id.menu_close_rv_menu:
                mRecyclerView.smoothCloseMenu();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected List<String> createDataList() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                strings.add("我右侧有3个菜单");
            } else if (i % 2 == 0) {
                strings.add("我右侧有2个菜单");
            } else {
                strings.add("我左侧有2个菜单");
            }
        }
        return strings;
    }
}