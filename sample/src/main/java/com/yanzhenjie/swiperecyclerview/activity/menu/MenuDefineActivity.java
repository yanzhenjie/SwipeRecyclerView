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
package com.yanzhenjie.swiperecyclerview.activity.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeSwitch;
import com.yanzhenjie.swiperecyclerview.R;

/**
 * <p>利用SwipeMenuLayout自定义菜单。</p>
 * Created by Yan Zhenjie on 2016/8/4.
 */
public class MenuDefineActivity extends AppCompatActivity {

    private Activity mContext;

    private SwipeSwitch mSwipeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_define);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSwipeSwitch = (SwipeSwitch) findViewById(R.id.swipe_layout);
        TextView btnLeft = (TextView) findViewById(R.id.left_view);
        TextView btnRight = (TextView) findViewById(R.id.right_view);

        btnLeft.setOnClickListener(xOnClickListener);
        btnRight.setOnClickListener(xOnClickListener);
    }

    private View.OnClickListener xOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.left_view) {
                mSwipeSwitch.smoothCloseMenu();// 关闭菜单。
                Toast.makeText(mContext, "我是左面的", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.right_view) {
                mSwipeSwitch.smoothCloseMenu();// 关闭菜单。
                Toast.makeText(mContext, "我是右面的", Toast.LENGTH_SHORT).show();
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