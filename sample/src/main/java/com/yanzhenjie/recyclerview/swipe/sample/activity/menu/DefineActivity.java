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
package com.yanzhenjie.recyclerview.swipe.sample.activity.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuLayout;
import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.activity.BaseActivity;
import com.yanzhenjie.recyclerview.swipe.sample.adapter.BaseAdapter;

import java.util.List;

/**
 * <p>
 * 利用SwipeMenuLayout自定义菜单。
 * </p>
 * Created by Yan Zhenjie on 2016/8/4.
 */
public class DefineActivity extends BaseActivity {

    private SwipeMenuLayout mSwipeMenuLayout;

    @Override
    protected int getContentView() {
        return R.layout.activity_menu_define;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mRecyclerView.setAdapter(mAdapter);

        mSwipeMenuLayout = (SwipeMenuLayout) findViewById(R.id.swipe_layout);
        TextView btnLeft = (TextView) findViewById(R.id.left_view);
        TextView btnRight = (TextView) findViewById(R.id.right_view);

        btnLeft.setOnClickListener(xOnClickListener);
        btnRight.setOnClickListener(xOnClickListener);
    }

    @Override
    protected BaseAdapter createAdapter() {
        return new DefineAdapter(this);
    }

    /**
     * 就是这个适配器的Item的Layout需要处理，其实和CardView的方式一模一样。
     */
    private static class DefineAdapter extends BaseAdapter<ViewHolder> {

        DefineAdapter(Context context) {
            super(context);
        }

        @Override
        public void notifyDataSetChanged(List<String> dataList) {
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_define, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Button mLeftBtn, mMiddleBtn, mRightBtn;

        ViewHolder(View itemView) {
            super(itemView);

            mLeftBtn = (Button) itemView.findViewById(R.id.left_view);
            mMiddleBtn = (Button) itemView.findViewById(R.id.btn_start);
            mRightBtn = (Button) itemView.findViewById(R.id.right_view);
            mLeftBtn.setOnClickListener(this);
            mMiddleBtn.setOnClickListener(this);
            mRightBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_view: {
                    Toast.makeText(v.getContext(), "我是第" + getAdapterPosition() + "个Item的左边的Button", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.btn_start: {
                    Toast.makeText(v.getContext(), "我是第" + getAdapterPosition() + "个Item的中间的Button", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.right_view: {
                    Toast.makeText(v.getContext(), "我是第" + getAdapterPosition() + "个Item的右边的Button", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    /**
     * 一般的点击事件。
     */
    private View.OnClickListener xOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.left_view) {
                mSwipeMenuLayout.smoothCloseMenu();// 关闭菜单。
                Toast.makeText(DefineActivity.this, "我是左面的", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.right_view) {
                mSwipeMenuLayout.smoothCloseMenu();// 关闭菜单。
                Toast.makeText(DefineActivity.this, "我是右面的", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onItemClick(View itemView, int position) {
        Toast.makeText(this, "第" + position + "个", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected List<String> createDataList() {
        return null;
    }
}