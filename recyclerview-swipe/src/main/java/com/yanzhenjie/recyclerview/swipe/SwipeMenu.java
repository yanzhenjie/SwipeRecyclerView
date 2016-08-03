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
package com.yanzhenjie.recyclerview.swipe;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan Zhenjie on 2016/7/22.
 */
public class SwipeMenu {

    private SwipeMenuLayout mSwipeMenuLayout;

    private int mViewType;

    private List<SwipeMenuItem> mSwipeMenuItems;

    SwipeMenu(SwipeMenuLayout swipeMenuLayout, int viewType) {
        this.mSwipeMenuLayout = swipeMenuLayout;
        this.mViewType = viewType;
        this.mSwipeMenuItems = new ArrayList<>();
    }

    /**
     * Set a percentage.
     *
     * @param openPercent such as 0.5F.
     */
    public void setOpenPercent(float openPercent) {
        openPercent = openPercent > 1 ? 1 : (openPercent < 0 ? 0 : openPercent);
        mSwipeMenuLayout.setOpenPercent(openPercent);
    }

    /**
     * The duration of the set.
     *
     * @param scrollerDuration such 500.
     */
    public void setScrollerDuration(int scrollerDuration) {
        this.mSwipeMenuLayout.setScrollerDuration(scrollerDuration);
    }

    public void addMenuItem(SwipeMenuItem item) {
        mSwipeMenuItems.add(item);
    }

    public void removeMenuItem(SwipeMenuItem item) {
        mSwipeMenuItems.remove(item);
    }

    public List<SwipeMenuItem> getMenuItems() {
        return mSwipeMenuItems;
    }

    public SwipeMenuItem getMenuItem(int index) {
        return mSwipeMenuItems.get(index);
    }

    public Context getContext() {
        return mSwipeMenuLayout.getContext();
    }

    public int getViewType() {
        return mViewType;
    }
}
