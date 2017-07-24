/*
 * AUTHOR：YanZhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.recyclerview.swipe.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by YanZhenjie on 2017/7/6.
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private int mExcludeViewType;

    /**
     * @param color decoration line color.
     */
    public GridItemDecoration(@ColorInt int color) {
        this(color, 2, 2, -1);
    }

    /**
     * @param color           line color.
     * @param dividerWidth    line width.
     * @param dividerHeight   line height.
     * @param excludeViewType don't need to draw the ViewType of the item of the split line.
     */
    public GridItemDecoration(@ColorInt int color, int dividerWidth, int dividerHeight, int excludeViewType) {
        mDivider = new ColorDrawable(color);
        mDividerWidth = dividerWidth;
        mDividerHeight = dividerHeight;
        mExcludeViewType = excludeViewType;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);

        if (parent.getAdapter().getItemViewType(position) == mExcludeViewType) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int columnCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        boolean isFirstRaw = isFirstRaw(position, columnCount);
        boolean lastRaw = isLastRaw(position, columnCount, childCount);
        boolean lastColumn = isLastColumn(position, columnCount);

        if (isFirstRaw && lastColumn) { // left, right, bottom
            outRect.set(mDividerWidth, 0, mDividerWidth, mDividerHeight);
        } else if (isFirstRaw) { // left, bottom
            outRect.set(mDividerWidth, 0, 0, mDividerHeight);
        } else if (lastRaw && lastColumn) { // draw left, right.
            outRect.set(mDividerWidth, 0, mDividerWidth, 0);
        } else if (lastRaw) { // left.
            outRect.set(mDividerWidth, 0, 0, 0);
        } else if (lastColumn) { // left, right, bottom.
            outRect.set(mDividerWidth, 0, mDividerWidth, mDividerHeight);
        } else { // left, bottom.
            outRect.set(mDividerWidth, 0, 0, mDividerHeight);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }

    private boolean isFirstRaw(int position, int columnCount) {
        return position < columnCount;
    }

    private boolean isLastRaw(int position, int columnCount, int childCount) {
        if (columnCount == 1)
            return position + 1 == childCount;
        else {
            int lastRawItemCount = childCount % columnCount;
            int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

            int rawPositionJudge = (position + 1) % columnCount;
            if (rawPositionJudge == 0) {
                int rawPosition = (position + 1) / columnCount;
                return rawCount == rawPosition;
            } else {
                int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                return rawCount == rawPosition;
            }
        }
    }

    private boolean isLastColumn(int position, int columnCount) {
        if (columnCount == 1)
            return true;
        return (position + 1) % columnCount == 0;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int left = child.getLeft();
            final int top = child.getBottom();
            final int right = child.getRight();
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int left = child.getRight();
            final int top = child.getTop();
            final int right = left + mDividerWidth;
            final int bottom = child.getBottom();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

}
