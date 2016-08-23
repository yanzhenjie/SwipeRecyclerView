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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;

/**
 * Created by Yan Zhenjie on 2016/7/26.
 */
public class SwipeMenuItem {

    private Context mContext;
    private String title;
    private Drawable icon;
    private Drawable background;
    private int titleColor = -1;
    private int titleSize = -1;
    private int width = -2;
    private int height = -2;
    public static final int BOLD = 1;
    public static final int BOLD_ITALIC = 3;
    public static final int ITALIC = 2;
    public static final int NORMAL = 0;
    private int typeface=NORMAL;

    public SwipeMenuItem(Context context) {
        mContext = context;
    }

    public SwipeMenuItem setTextSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public int getTextSize() {
        return titleSize;
    }

    public SwipeMenuItem setTextColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public int getTitleColor() {
        return titleColor;
    }
    
    public int getTypeface() {
        return typeface;
    }

    public int setTypeface() {
        return typeface;
    }

    public SwipeMenuItem setText(String title) {
        this.title = title;
        return this;
    }

    public SwipeMenuItem setText(int resId) {
        setText(mContext.getString(resId));
        return this;
    }

    public String getText() {
        return title;
    }

    public SwipeMenuItem setImage(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public SwipeMenuItem setImage(int resId) {
        return setImage(ResCompat.getDrawable(mContext, resId));
    }

    public Drawable getImage() {
        return icon;
    }

    public Drawable getBackground() {
        return background;
    }

    public SwipeMenuItem setBackgroundDrawable(Drawable background) {
        this.background = background;
        return this;
    }

    public SwipeMenuItem setBackgroundDrawable(int resId) {
        this.background = ResCompat.getDrawable(mContext, resId);
        return this;
    }

    public SwipeMenuItem setBackgroundColor(int color) {
        this.background = new ColorDrawable(color);
        return this;
    }

    public int getWidth() {
        return width;
    }

    public SwipeMenuItem setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public SwipeMenuItem setHeight(int height) {
        this.height = height;
        return this;
    }
}
