/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.recyclerview.widget;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by YanZhenjie on 2018/4/20.
 */
class Drawer {

    private final Drawable mDivider;
    private final int mWidth;
    private final int mHeight;

    public Drawer(Drawable divider, int width, int height) {
        this.mDivider = divider;
        this.mWidth = width;
        this.mHeight = height;
    }

    /**
     * Draw the divider on the left side of the Item.
     */
    public void drawLeft(View view, Canvas c) {
        int left = view.getLeft() - mWidth;
        int top = view.getTop() - mHeight;
        int right = left + mWidth;
        int bottom = view.getBottom() + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }

    /**
     * Draw the divider on the top side of the Item.
     */
    public void drawTop(View view, Canvas c) {
        int left = view.getLeft() - mWidth;
        int top = view.getTop() - mHeight;
        int right = view.getRight() + mWidth;
        int bottom = top + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }

    /**
     * Draw the divider on the top side of the Item.
     */
    public void drawRight(View view, Canvas c) {
        int left = view.getRight();
        int top = view.getTop() - mHeight;
        int right = left + mWidth;
        int bottom = view.getBottom() + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }

    /**
     * Draw the divider on the top side of the Item.
     */
    public void drawBottom(View view, Canvas c) {
        int left = view.getLeft() - mWidth;
        int top = view.getBottom();
        int right = view.getRight() + mWidth;
        int bottom = top + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }
}