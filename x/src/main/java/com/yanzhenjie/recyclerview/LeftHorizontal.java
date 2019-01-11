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
package com.yanzhenjie.recyclerview;

import android.view.View;
import android.widget.OverScroller;

/**
 * Created by Yan Zhenjie on 2016/7/22.
 */
class LeftHorizontal extends Horizontal {

    public LeftHorizontal(View menuView) {
        super(SwipeRecyclerView.LEFT_DIRECTION, menuView);
    }

    @Override
    public boolean isMenuOpen(int scrollX) {
        int i = -getMenuView().getWidth() * getDirection();
        return scrollX <= i && i != 0;
    }

    @Override
    public boolean isMenuOpenNotEqual(int scrollX) {
        return scrollX < -getMenuView().getWidth() * getDirection();
    }

    @Override
    public void autoOpenMenu(OverScroller scroller, int scrollX, int duration) {
        scroller.startScroll(Math.abs(scrollX), 0, getMenuView().getWidth() - Math.abs(scrollX), 0, duration);
    }

    @Override
    public void autoCloseMenu(OverScroller scroller, int scrollX, int duration) {
        scroller.startScroll(-Math.abs(scrollX), 0, Math.abs(scrollX), 0, duration);
    }

    @Override
    public Checker checkXY(int x, int y) {
        mChecker.x = x;
        mChecker.y = y;
        mChecker.shouldResetSwipe = false;
        if (mChecker.x == 0) {
            mChecker.shouldResetSwipe = true;
        }
        if (mChecker.x >= 0) {
            mChecker.x = 0;
        }
        if (mChecker.x <= -getMenuView().getWidth()) {
            mChecker.x = -getMenuView().getWidth();
        }
        return mChecker;
    }

    @Override
    public boolean isClickOnContentView(int contentViewWidth, float x) {
        return x > getMenuView().getWidth();
    }
}