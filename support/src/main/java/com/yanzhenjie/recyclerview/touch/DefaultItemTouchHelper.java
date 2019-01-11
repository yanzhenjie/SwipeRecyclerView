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
package com.yanzhenjie.recyclerview.touch;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Yolanda on 2016/4/19.
 */
public class DefaultItemTouchHelper extends ItemTouchHelper {

    private ItemTouchHelperCallback mItemTouchHelperCallback;

    /**
     * Create default item touch helper.
     */
    public DefaultItemTouchHelper() {
        this(new ItemTouchHelperCallback());
    }

    /**
     * @param callback the behavior of ItemTouchHelper.
     */
    private DefaultItemTouchHelper(ItemTouchHelperCallback callback) {
        super(callback);
        mItemTouchHelperCallback = callback;
    }

    /**
     * Set OnItemMoveListener.
     *
     * @param onItemMoveListener {@link OnItemMoveListener}.
     */
    public void setOnItemMoveListener(OnItemMoveListener onItemMoveListener) {
        mItemTouchHelperCallback.setOnItemMoveListener(onItemMoveListener);
    }

    /**
     * Get OnItemMoveListener.
     *
     * @return {@link OnItemMoveListener}.
     */
    public OnItemMoveListener getOnItemMoveListener() {
        return mItemTouchHelperCallback.getOnItemMoveListener();
    }

    /**
     * Set OnItemMovementListener.
     *
     * @param onItemMovementListener {@link OnItemMovementListener}.
     */
    public void setOnItemMovementListener(OnItemMovementListener onItemMovementListener) {
        mItemTouchHelperCallback.setOnItemMovementListener(onItemMovementListener);
    }

    /**
     * Get OnItemMovementListener.
     *
     * @return {@link OnItemMovementListener}.
     */
    public OnItemMovementListener getOnItemMovementListener() {
        return mItemTouchHelperCallback.getOnItemMovementListener();
    }

    /**
     * Set can long press drag.
     *
     * @param canDrag drag true, otherwise is can't.
     */
    public void setLongPressDragEnabled(boolean canDrag) {
        mItemTouchHelperCallback.setLongPressDragEnabled(canDrag);
    }

    /**
     * Get can long press drag.
     *
     * @return drag true, otherwise is can't.
     */
    public boolean isLongPressDragEnabled() {
        return mItemTouchHelperCallback.isLongPressDragEnabled();
    }

    /**
     * Set can long press swipe.
     *
     * @param canSwipe swipe true, otherwise is can't.
     */
    public void setItemViewSwipeEnabled(boolean canSwipe) {
        mItemTouchHelperCallback.setItemViewSwipeEnabled(canSwipe);
    }

    /**
     * Get can long press swipe.
     *
     * @return swipe true, otherwise is can't.
     */
    public boolean isItemViewSwipeEnabled() {
        return this.mItemTouchHelperCallback.isItemViewSwipeEnabled();
    }

    /**
     * Set OnItemStateChangedListener.
     *
     * @param onItemStateChangedListener {@link OnItemStateChangedListener}.
     */
    public void setOnItemStateChangedListener(OnItemStateChangedListener onItemStateChangedListener) {
        this.mItemTouchHelperCallback.setOnItemStateChangedListener(onItemStateChangedListener);
    }

    /**
     * Get OnItemStateChangedListener.
     *
     * @return {@link OnItemStateChangedListener}.
     */
    public OnItemStateChangedListener getOnItemStateChangedListener() {
        return this.mItemTouchHelperCallback.getOnItemStateChangedListener();
    }

}