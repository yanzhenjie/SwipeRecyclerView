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

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Yan Zhenjie on 2016/8/12.
 */
public interface OnItemStateChangedListener {

    /**
     * ItemTouchHelper is in idle state. At this state, either there is no related motion event by the user or latest
     * motion events have not yet triggered a swipe or drag.
     */
    int ACTION_STATE_IDLE = ItemTouchHelper.ACTION_STATE_IDLE;

    /**
     * A View is currently being swiped.
     */
    int ACTION_STATE_SWIPE = ItemTouchHelper.ACTION_STATE_SWIPE;

    /**
     * A View is currently being dragged.
     */
    int ACTION_STATE_DRAG = ItemTouchHelper.ACTION_STATE_DRAG;

    /**
     * Called when the ViewHolder swiped or dragged by the ItemTouchHelper is changed.
     *
     * @param viewHolder The new ViewHolder that is being swiped or dragged. Might be null if it is cleared.
     * @param actionState One of {@link OnItemStateChangedListener#ACTION_STATE_IDLE}, {@link
     *     OnItemStateChangedListener#ACTION_STATE_SWIPE} or {@link OnItemStateChangedListener#ACTION_STATE_DRAG}.
     */
    void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);
}