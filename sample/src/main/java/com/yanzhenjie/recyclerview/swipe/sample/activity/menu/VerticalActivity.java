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
package com.yanzhenjie.recyclerview.swipe.sample.activity.menu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.activity.BaseActivity;
import com.yanzhenjie.recyclerview.swipe.sample.adapter.BaseAdapter;

import java.util.List;

/**
 * <p>
 * 竖向的菜单。
 * </p>
 * Created by YanZhenjie on 2017/7/22.
 */
public class VerticalActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
    }

    @Override
    public BaseAdapter createAdapter() {
        // 这里只是让Item的高度变高一点，竖向排布的菜单看起来就好看些。
        return new VerticalAdapter(this);
    }

    /**
     * 菜单创建器。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            /*
             * 设置竖向菜单的注意点：
             * 1. SwipeMenu.setOrientation(SwipeMenu.VERTICAL);这个是控制Item中的Menu的方向的。
             * 2. SwipeMenuItem.setHeight(0).setWeight(1); // 高度为0，权重为1，和LinearLayout一样。
             * 3. 菜单高度是和Item Content的高度一样的，你可以设置Item的padding和margin。
             */

            swipeLeftMenu.setOrientation(SwipeMenu.VERTICAL);
            swipeRightMenu.setOrientation(SwipeMenu.VERTICAL);
            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(VerticalActivity.this)
                        .setBackground(R.drawable.selector_green)
                        .setImage(R.mipmap.ic_action_add)
                        .setWidth(width)
                        .setHeight(0)
                        .setWeight(1);
                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。

                SwipeMenuItem closeItem = new SwipeMenuItem(VerticalActivity.this)
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_close)
                        .setWidth(width)
                        .setHeight(0)
                        .setWeight(1);
                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(VerticalActivity.this)
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(0)
                        .setWeight(1);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(VerticalActivity.this)
                        .setBackground(R.drawable.selector_green)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(0)
                        .setWeight(1);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };

    private static class VerticalAdapter extends BaseAdapter<ViewHolder> {

        private List<String> mDataList;

        public VerticalAdapter(Context context) {
            super(context);
        }

        @Override
        public void notifyDataSetChanged(List<String> dataList) {
            this.mDataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_vertical, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(mDataList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataList == null ? 0 : mDataList.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTv1;

        public ViewHolder(View itemView) {
            super(itemView);

            mTv1 = (TextView) itemView.findViewById(R.id.tv_title);
        }

        void setData(String data) {
            mTv1.setText(data);
        }
    }

}
