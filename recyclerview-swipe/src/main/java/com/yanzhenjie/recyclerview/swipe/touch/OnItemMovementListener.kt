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
package com.yanzhenjie.recyclerview.swipe.touch

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Item movement listener.
 *
 * Created by Yan Zhenjie on 2016/8/1.
 */
interface OnItemMovementListener {

  /**
   * Can drag and drop the ViewHolder?
   *
   * @param recyclerView     [RecyclerView].
   * @param targetViewHolder target ViewHolder.
   * @return use [.LEFT], [.UP], [.RIGHT], [.DOWN].
   */
  fun onDragFlags(recyclerView: RecyclerView, targetViewHolder: RecyclerView.ViewHolder): Int

  /**
   * Can swipe and drop the ViewHolder?
   *
   * @param recyclerView     [RecyclerView].
   * @param targetViewHolder target ViewHolder.
   * @return use [.LEFT], [.UP], [.RIGHT], [.DOWN].
   */
  fun onSwipeFlags(recyclerView: RecyclerView, targetViewHolder: RecyclerView.ViewHolder): Int

  companion object {

    const val INVALID = 0

    const val LEFT = ItemTouchHelper.LEFT

    const val UP = ItemTouchHelper.UP

    const val RIGHT = ItemTouchHelper.RIGHT

    const val DOWN = ItemTouchHelper.DOWN
  }

}
