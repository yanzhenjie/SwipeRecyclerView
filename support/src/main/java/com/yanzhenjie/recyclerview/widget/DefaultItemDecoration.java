/*
 * Copyright 2017 Yan Zhenjie
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
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by YanZhenjie on 2017/8/14.
 */
public class DefaultItemDecoration extends RecyclerView.ItemDecoration {

    private final int mWidth;
    private final int mHeight;
    private final Drawer mDrawer;

    /**
     * @param color divider line color.
     */
    public DefaultItemDecoration(@ColorInt int color) {
        this(color, 4, 4);
    }

    /**
     * @param color line color.
     * @param width line width.
     * @param height line height.
     */
    public DefaultItemDecoration(@ColorInt int color, int width, int height) {
        this.mWidth = Math.round(width / 2F);
        this.mHeight = Math.round(height / 2F);
        this.mDrawer = new ColorDrawer(color, mWidth, mHeight);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
        @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = getOrientation(layoutManager);
            int position = parent.getChildLayoutPosition(view);
            int spanCount = getSpanCount(layoutManager);
            int childCount = layoutManager.getItemCount();

            if (orientation == RecyclerView.VERTICAL) {
                offsetVertical(outRect, position, spanCount, childCount);
            } else {
                offsetHorizontal(outRect, position, spanCount, childCount);
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            outRect.set(mWidth, mHeight, mWidth, mHeight); // |-|-
        }
    }

    private void offsetHorizontal(Rect outRect, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstColumn && lastColumn) { // xxxx
                outRect.set(0, 0, 0, 0);
            } else if (firstColumn) { // xx|x
                outRect.set(0, 0, mWidth, 0);
            } else if (lastColumn) { // |xxx
                outRect.set(mWidth, 0, 0, 0);
            } else { // |x|x
                outRect.set(mWidth, 0, mWidth, 0);
            }
        } else {
            if (firstColumn && firstRaw) { // xx|-
                outRect.set(0, 0, mWidth, mHeight);
            } else if (firstColumn && lastRaw) { // x-|x
                outRect.set(0, mHeight, mWidth, 0);
            } else if (lastColumn && firstRaw) { // |xx-
                outRect.set(mWidth, 0, 0, mHeight);
            } else if (lastColumn && lastRaw) { // |-xx
                outRect.set(mWidth, mHeight, 0, 0);
            } else if (firstColumn) { // x-|-
                outRect.set(0, mHeight, mWidth, mHeight);
            } else if (lastColumn) { // |-x-
                outRect.set(mWidth, mHeight, 0, mHeight);
            } else if (firstRaw) { // |x|-
                outRect.set(mWidth, 0, mWidth, mHeight);
            } else if (lastRaw) { // |-|x
                outRect.set(mWidth, mHeight, mWidth, 0);
            } else { // |-|-
                outRect.set(mWidth, mHeight, mWidth, mHeight);
            }
        }
    }

    private void offsetVertical(Rect outRect, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.VERTICAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastRaw) { // xxxx
                outRect.set(0, 0, 0, 0);
            } else if (firstRaw) { // xxx-
                outRect.set(0, 0, 0, mHeight);
            } else if (lastRaw) { // x-xx
                outRect.set(0, mHeight, 0, 0);
            } else { // x-x-
                outRect.set(0, mHeight, 0, mHeight);
            }
        } else {
            if (firstRaw && firstColumn) { // xx|-
                outRect.set(0, 0, mWidth, mHeight);
            } else if (firstRaw && lastColumn) { // |xx-
                outRect.set(mWidth, 0, 0, mHeight);
            } else if (lastRaw && firstColumn) { // x-|x
                outRect.set(0, mHeight, mWidth, 0);
            } else if (lastRaw && lastColumn) { // |-xx
                outRect.set(mWidth, mHeight, 0, 0);
            } else if (firstRaw) { // |x|-
                outRect.set(mWidth, 0, mWidth, mHeight);
            } else if (lastRaw) { // |-|x
                outRect.set(mWidth, mHeight, mWidth, 0);
            } else if (firstColumn) { // x-|-
                outRect.set(0, mHeight, mWidth, mHeight);
            } else if (lastColumn) { // |-x-
                outRect.set(mWidth, mHeight, 0, mHeight);
            } else { // |-|-
                outRect.set(mWidth, mHeight, mWidth, mHeight);
            }
        }
    }

    private int getOrientation(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager)layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager)layoutManager).getOrientation();
        }
        return RecyclerView.VERTICAL;
    }

    private int getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager)layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        }
        return 1;
    }

    private boolean isFirstRaw(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            return position < columnCount;
        } else {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        }
    }

    private boolean isLastRaw(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % columnCount;
                int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % columnCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / columnCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                    return rawCount == rawPosition;
                }
            }
        } else {
            if (columnCount == 1) return true;
            return (position + 1) % columnCount == 0;
        }
    }

    private boolean isFirstColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        } else {
            return position < columnCount;
        }
    }

    private boolean isLastColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return (position + 1) % columnCount == 0;
        } else {
            if (columnCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % columnCount;
                int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % columnCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / columnCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                    return rawCount == rawPosition;
                }
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        assert layoutManager != null;
        int orientation = getOrientation(layoutManager);
        int spanCount = getSpanCount(layoutManager);
        int childCount = layoutManager.getChildCount();

        if (layoutManager instanceof LinearLayoutManager) {
            canvas.save();
            for (int i = 0; i < childCount; i++) {
                View view = layoutManager.getChildAt(i);
                assert view != null;
                int position = parent.getChildLayoutPosition(view);

                if (orientation == RecyclerView.VERTICAL) {
                    drawVertical(canvas, view, position, spanCount, childCount);
                } else {
                    drawHorizontal(canvas, view, position, spanCount, childCount);
                }
            }
            canvas.restore();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            canvas.save();
            for (int i = 0; i < childCount; i++) {
                View view = layoutManager.getChildAt(i);
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
            canvas.restore();
        }
    }

    private void drawHorizontal(Canvas canvas, View view, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastColumn) { // xxxx
                // Nothing.
            } else if (firstColumn) { // xx|x
                mDrawer.drawRight(view, canvas);
            } else if (lastColumn) { // |xxx
                mDrawer.drawLeft(view, canvas);
            } else { // |x|x
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawRight(view, canvas);
            }
        } else {
            if (firstColumn && firstRaw) { // xx|-
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (firstColumn && lastRaw) { // x-|x
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else if (lastColumn && firstRaw) { // |xx-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastColumn && lastRaw) { // |-xx
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
            } else if (firstColumn) { // x-|-
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastColumn) { // |-x-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (firstRaw) { // |x|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw) { // |-|x
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else { // |-|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
        }
    }

    private void drawVertical(Canvas canvas, View view, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.VERTICAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastRaw) { // xxxx
                // Nothing.
            } else if (firstRaw) { // xxx-
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw) { // x-xx
                mDrawer.drawTop(view, canvas);
            } else { // x-x-
                mDrawer.drawTop(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
        } else {
            if (firstRaw && firstColumn) { // xx|-
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (firstRaw && lastColumn) { // |xx-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw && firstColumn) { // x-|x
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else if (lastRaw && lastColumn) { // |-xx
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
            } else if (firstRaw) { // |x|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastRaw) { // |-|x
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
            } else if (firstColumn) { // x-|-
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else if (lastColumn) { // |-x-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawBottom(view, canvas);
            } else { // |-|-
                mDrawer.drawLeft(view, canvas);
                mDrawer.drawTop(view, canvas);
                mDrawer.drawRight(view, canvas);
                mDrawer.drawBottom(view, canvas);
            }
        }
    }
}
