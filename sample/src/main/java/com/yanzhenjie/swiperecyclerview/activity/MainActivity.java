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
package com.yanzhenjie.swiperecyclerview.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yanzhenjie.swiperecyclerview.R;
import com.yanzhenjie.swiperecyclerview.activity.menu.MenuCardActivity;
import com.yanzhenjie.swiperecyclerview.activity.menu.MenuDefineActivity;
import com.yanzhenjie.swiperecyclerview.activity.menu.MenuDrawerActivity;
import com.yanzhenjie.swiperecyclerview.activity.menu.MenuHorizontalActivity;
import com.yanzhenjie.swiperecyclerview.activity.menu.MenuVerticalActivity;
import com.yanzhenjie.swiperecyclerview.activity.menu.MenuViewPagerActivity;
import com.yanzhenjie.swiperecyclerview.activity.menu.MenuViewTypeActivity;
import com.yanzhenjie.swiperecyclerview.activity.move.DragGridMenuActivity;
import com.yanzhenjie.swiperecyclerview.activity.move.DragListMenuActivity;
import com.yanzhenjie.swiperecyclerview.activity.move.DragSwipeFlagsActivity;
import com.yanzhenjie.swiperecyclerview.activity.move.DragSwipeListActivity;
import com.yanzhenjie.swiperecyclerview.activity.move.DragTouchListActivity;
import com.yanzhenjie.swiperecyclerview.adapter.MainItemAdapter;
import com.yanzhenjie.swiperecyclerview.listener.OnItemClickListener;

import java.util.Arrays;
import java.util.List;


/**
 * Created by YOLANDA on 2016/7/22.
 */
public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(10, 10, 10, 15);
            }
        });

        List<String> titles = Arrays.asList(getResources().getStringArray(R.array.main_item));
        List<String> descriptions = Arrays.asList(getResources().getStringArray(R.array.main_item_des));
        MainItemAdapter mainItemAdapter = new MainItemAdapter(titles, descriptions);
        mainItemAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mainItemAdapter);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0: {
                startActivity(new Intent(this, MenuHorizontalActivity.class));
                break;
            }
            case 1: {
                startActivity(new Intent(this, MenuVerticalActivity.class));
                break;
            }
            case 2: {
                startActivity(new Intent(this, MenuViewTypeActivity.class));
                break;
            }
            case 3: {
                startActivity(new Intent(this, MenuViewPagerActivity.class));
                break;
            }
            case 4: {
                startActivity(new Intent(this, MenuDrawerActivity.class));
                break;
            }
            case 5: {
                startActivity(new Intent(this, MenuCardActivity.class));
                break;
            }
            case 6: {
                startActivity(new Intent(this, MenuDefineActivity.class));
                break;
            }
            case 7: {
                startActivity(new Intent(this, RefreshLoadMoreActivity.class));
                break;
            }
            case 8: {
                startActivity(new Intent(this, DragListMenuActivity.class));
                break;
            }
            case 9: {
                startActivity(new Intent(this, DragGridMenuActivity.class));
                break;
            }
            case 10: {
                startActivity(new Intent(this, DragTouchListActivity.class));
                break;
            }
            case 11: {
                startActivity(new Intent(this, DragSwipeListActivity.class));
                break;
            }
            case 12: {
                startActivity(new Intent(this, DragSwipeFlagsActivity.class));
                break;
            }
        }
    }

}
