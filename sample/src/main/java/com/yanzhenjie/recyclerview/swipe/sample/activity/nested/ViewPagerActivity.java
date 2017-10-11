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
package com.yanzhenjie.recyclerview.swipe.sample.activity.nested;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.fragment.MenuFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 侧滑菜单和ViewPager嵌套，事先说好ViewPager的左右滑动会失效。
 * </p>
 * Created by Yan Zhenjie on 2016/8/12.
 */
public class ViewPagerActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pager_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btn_one).setOnClickListener(mBtnClickListener);
        findViewById(R.id.btn_two).setOnClickListener(mBtnClickListener);
        findViewById(R.id.btn_three).setOnClickListener(mBtnClickListener);

        mViewPager = (ViewPager) findViewById(R.id.view_pager_menu);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setOffscreenPageLimit(2);

        List<Fragment> fragments = new ArrayList<>(3);
        fragments.add(Fragment.instantiate(this, MenuFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, MenuFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, MenuFragment.class.getName()));

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pagerAdapter);

        mPageChangeListener.onPageSelected(0);
    }

    /**
     * Button点击监听。
     */
    private View.OnClickListener mBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_one) {
                mViewPager.setCurrentItem(0, true);
            } else if (v.getId() == R.id.btn_two) {
                mViewPager.setCurrentItem(1, true);
            } else if (v.getId() == R.id.btn_three) {
                mViewPager.setCurrentItem(2, true);
            }
        }
    };

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mActionBar.setSubtitle("第" + position + "个");
            switch (position) {
                case 0: {
                    mViewPager.setBackgroundColor(ContextCompat.getColor(ViewPagerActivity.this, R.color.colorAccent));
                    break;
                }
                case 1: {
                    mViewPager.setBackgroundColor(ContextCompat.getColor(ViewPagerActivity.this, R.color.colorPrimary));
                    break;
                }
                case 2: {
                    mViewPager.setBackgroundColor(ContextCompat.getColor(ViewPagerActivity.this, R.color.green_normal));
                    break;
                }
            }
        }
    };

    private static class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
