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
import android.support.annotation.IntDef;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan Zhenjie on 2016/7/22.
 */
public class SwipeMenu {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private SwipeMenuLayout mSwipeMenuLayout;

    private int mViewType;

    private int orientation = HORIZONTAL;

    private List<SwipeMenuItem> mSwipeMenuItems = new ArrayList<>(2);

    public SwipeMenu(SwipeMenuLayout swipeMenuLayout, int viewType) {
        this.mSwipeMenuLayout = swipeMenuLayout;
        this.mViewType = viewType;
    }

    /**
     * Set a percentage.
     *
     * @param openPercent such as 0.5F.
     */
    public void setOpenPercent(float openPercent) {
        if (openPercent != mSwipeMenuLayout.getOpenPercent()) {
            openPercent = openPercent > 1 ? 1 : (openPercent < 0 ? 0 : openPercent);
            mSwipeMenuLayout.setOpenPercent(openPercent);
        }
    }

    /**
     * The duration of the set.
     *
     * @param scrollerDuration such 500.
     */
    public void setScrollerDuration(int scrollerDuration) {
        this.mSwipeMenuLayout.setScrollerDuration(scrollerDuration);
    }

    /**
     * Set the menu orientation.
     *
     * @param orientation use {@link SwipeMenu#HORIZONTAL} or {@link SwipeMenu#VERTICAL}.
     * @see SwipeMenu#HORIZONTAL
     * @see SwipeMenu#VERTICAL
     */
    public void setOrientation(@OrientationMode int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL)
            throw new IllegalArgumentException("Use SwipeMenu#HORIZONTAL or SwipeMenu#VERTICAL.");
        this.orientation = orientation;
    }

    /**
     * Get the menu orientation.
     *
     * @return {@link SwipeMenu#HORIZONTAL} or {@link SwipeMenu#VERTICAL}.
     */
    @OrientationMode
    public int getOrientation() {
        return orientation;
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
