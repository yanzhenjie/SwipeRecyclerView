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
package com.yanzhenjie.recyclerview.swipe.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.activity.group.GroupActivity;
import com.yanzhenjie.recyclerview.swipe.sample.activity.header.HeaderViewActivity;
import com.yanzhenjie.recyclerview.swipe.sample.activity.load.RefreshLoadActivity;
import com.yanzhenjie.recyclerview.swipe.sample.activity.menu.MenuActivity;
import com.yanzhenjie.recyclerview.swipe.sample.activity.move.MoveActivity;
import com.yanzhenjie.recyclerview.swipe.sample.activity.nested.NestedActivity;

import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * Demo入口。
 * </p>
 * Created by YOLANDA on 2016/7/22.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        switch (position) {
            case 0: {
                startActivity(new Intent(this, MenuActivity.class));
                break;
            }
            case 1: {
                startActivity(new Intent(this, MoveActivity.class));
                break;
            }
            case 2: {
                startActivity(new Intent(this, HeaderViewActivity.class));
                break;
            }
            case 3: {
                startActivity(new Intent(this, RefreshLoadActivity.class));
                break;
            }
            case 4: {
                startActivity(new Intent(this, NestedActivity.class));
                break;
            }
            case 5: {
                startActivity(new Intent(this, GroupActivity.class));
                break;
            }
        }
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected List<String> createDataList() {
        return Arrays.asList(getResources().getStringArray(R.array.main_item));
    }
}
