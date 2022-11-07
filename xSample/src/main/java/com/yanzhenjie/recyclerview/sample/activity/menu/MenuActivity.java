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
package com.yanzhenjie.recyclerview.sample.activity.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yanzhenjie.recyclerview.sample.R;
import com.yanzhenjie.recyclerview.sample.activity.BaseActivity;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 侧滑菜单的演示。
 * </p>
 * Created by YanZhenjie on 2017/7/21.
 */
public class MenuActivity extends BaseActivity {

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
                startActivity(new Intent(this, ListActivity.class));
                break;
            }
            case 1: {
                startActivity(new Intent(this, GridActivity.class));
                break;
            }
            case 2: {
                startActivity(new Intent(this, ViewTypeActivity.class));
                break;
            }
            case 3: {
                startActivity(new Intent(this, VerticalActivity.class));
                break;
            }
            case 4: {
                startActivity(new Intent(this, DefineActivity.class));
                break;
            }
            case 5: {
                startActivity(new Intent(this, ListPlusActivity.class));
                break;
            }
            case 6: {
                startActivity(new Intent(this, ListEmptyViewActivity.class));
                break;
            }
        }
    }

    @Override
    protected List<String> createDataList() {
        return Arrays.asList(getResources().getStringArray(R.array.menu_item));
    }
}