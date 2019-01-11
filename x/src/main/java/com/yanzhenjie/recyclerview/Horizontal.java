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
import android.view.ViewGroup;
import android.widget.OverScroller;

/**
 * Created by Yan Zhenjie on 2016/7/22.
 */
abstract class Horizontal {

    private int direction;
    private View menuView;
    protected Checker mChecker;

    public Horizontal(int direction, View menuView) {
        this.direction = direction;
        this.menuView = menuView;
        mChecker = new Checker();
    }

    public boolean canSwipe() {
        if (menuView instanceof ViewGroup) {
            return ((ViewGroup)menuView).getChildCount() > 0;
        }
        return false;
    }

    public boolean isCompleteClose(int scrollX) {
        int i = -getMenuView().getWidth() * getDirection();
        return scrollX == 0 && i != 0;
    }

    public abstract boolean isMenuOpen(int scrollX);

    public abstract boolean isMenuOpenNotEqual(int scrollX);

    public abstract void autoOpenMenu(OverScroller scroller, int scrollX, int duration);

    public abstract void autoCloseMenu(OverScroller scroller, int scrollX, int duration);

    public abstract Checker checkXY(int x, int y);

    public abstract boolean isClickOnContentView(int contentViewWidth, float x);

    public int getDirection() {
        return direction;
    }

    public View getMenuView() {
        return menuView;
    }

    public int getMenuWidth() {
        return menuView.getWidth();
    }

    public static final class Checker {

        public int x;
        public int y;
        public boolean shouldResetSwipe;
    }

}