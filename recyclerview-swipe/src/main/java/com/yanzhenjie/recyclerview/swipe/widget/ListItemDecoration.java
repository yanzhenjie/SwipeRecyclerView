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
package com.yanzhenjie.recyclerview.swipe.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * <p>
 * RecyclerView's Item Split Line.
 * </p>
 * Created by Yan Zhenjie on 2016/7/27.
 */
public class ListItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mDividerSize;
    private int mExcludeViewType;

    /**
     * @param color line color.
     */
    public ListItemDecoration(@ColorInt int color) {
        this(color, 2, -1);
    }

    /**
     * @param color           line color.
     * @param dividerSize     line height.
     * @param excludeViewType
     */
    public ListItemDecoration(@ColorInt int color, int dividerSize, int excludeViewType) {
        mDivider = new ColorDrawable(color);
        mDividerSize = dividerSize;
        mExcludeViewType = excludeViewType;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int orientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
        int position = parent.getChildLayoutPosition(view);

        if (parent.getAdapter().getItemViewType(position) == mExcludeViewType) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int childCount = parent.getAdapter().getItemCount();
        boolean lastRaw = position == childCount - 1;
        switch (orientation) {
            case LinearLayoutManager.HORIZONTAL: {
                if (lastRaw) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, mDividerSize, 0);
                }
                break;
            }
            case LinearLayoutManager.VERTICAL: {
                if (lastRaw) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, mDividerSize);
                }
                break;
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int left = child.getLeft();
            final int top = child.getBottom();
            final int right = child.getRight();
            final int bottom = top + mDividerSize;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
