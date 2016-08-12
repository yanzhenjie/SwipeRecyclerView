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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yanzhenjie.swiperecyclerview.R;
import com.yanzhenjie.swiperecyclerview.adapter.MenuPagerAdapter;
import com.yanzhenjie.swiperecyclerview.fragment.MenuFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan Zhenjie on 2016/8/12.
 */
public class ViewPagerMenuActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private MenuPagerAdapter mMenuPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.view_pager_menu);

        List<Fragment> fragments = new ArrayList<>(3);
        fragments.add(new MenuFragment());
        fragments.add(new MenuFragment());
        fragments.add(new MenuFragment());

        mMenuPagerAdapter = new MenuPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mMenuPagerAdapter);
    }
}
