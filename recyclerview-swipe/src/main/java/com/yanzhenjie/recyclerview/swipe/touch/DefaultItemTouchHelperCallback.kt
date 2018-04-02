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

import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView

/**
 * Default item touch helper callback.
 *
 * Created by Yolanda on 2016/4/19.
 */
class DefaultItemTouchHelperCallback : ItemTouchHelper.Callback() {

  var onItemMovementListener: OnItemMovementListener? = null

  var onItemMoveListener: OnItemMoveListener? = null

  var onItemStateChangedListener: OnItemStateChangedListener? = null

  private var isItemViewSwipeEnabled: Boolean = false

  private var isLongPressDragEnabled: Boolean = false

  fun setLongPressDragEnabled(canDrag: Boolean) {
    this.isLongPressDragEnabled = canDrag
  }

  override fun isLongPressDragEnabled(): Boolean {
    return isLongPressDragEnabled
  }

  fun setItemViewSwipeEnabled(canSwipe: Boolean) {
    this.isItemViewSwipeEnabled = canSwipe
  }

  override fun isItemViewSwipeEnabled(): Boolean {
    return isItemViewSwipeEnabled
  }

  override fun getMovementFlags(recyclerView: RecyclerView, targetViewHolder: RecyclerView.ViewHolder): Int {
    val movementListener = onItemMovementListener
    val layoutManager = recyclerView.layoutManager

    // header or footer view
    // disable drag and swipe as default
    if (recyclerView is SwipeMenuRecyclerView && (recyclerView.isHeaderViewHolder(targetViewHolder)
        || recyclerView.isFooterViewHolder(targetViewHolder))) {
      return ItemTouchHelper.Callback.makeMovementFlags(0, 0)
    }

    return when {
      // custom movement listener
      movementListener != null -> {
        val dragFlags = movementListener.onDragFlags(recyclerView, targetViewHolder)
        val swipeFlags = movementListener.onSwipeFlags(recyclerView, targetViewHolder)
        ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
      }

      // grid layout
      layoutManager is GridLayoutManager -> {
        if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
          val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          val swipeFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
          ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        } else {
          val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }
      }

      // linear layout
      layoutManager is LinearLayoutManager -> {
        if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
          val dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          val swipeFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
          ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)

        } else {
          val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
          val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }
      }

      // default
      else -> {
        ItemTouchHelper.Callback.makeMovementFlags(0, 0)
      }
    }
  }

  override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                           dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
    // 判断当前是否是swipe方式：侧滑。
    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
      //1.ItemView--ViewHolder; 2.侧滑条目的透明度程度关联谁？dX(delta增量，范围：当前条目-width~width)。
      val layoutManager = recyclerView.layoutManager
      var alpha = 1F

      if (layoutManager is LinearLayoutManager) {
        when(layoutManager.orientation) {
          LinearLayoutManager.HORIZONTAL -> alpha = 1 - Math.abs(dY) / viewHolder.itemView.height
          LinearLayoutManager.VERTICAL -> alpha = 1 - Math.abs(dX) / viewHolder.itemView.width
        }
      }
      viewHolder.itemView.alpha = alpha //1~0
    }
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }

  override fun onMove(recyclerView: RecyclerView, srcHolder: RecyclerView.ViewHolder, targetHolder: RecyclerView.ViewHolder): Boolean {
    return onItemMoveListener?.onItemMove(srcHolder, targetHolder) ?: false
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    // 回调刷新数据及界面。
    onItemMoveListener?.onItemDismiss(viewHolder)
  }

  override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
    super.onSelectedChanged(viewHolder, actionState)

    if (viewHolder != null && actionState != OnItemStateChangedListener.ACTION_STATE_IDLE) {
      onItemStateChangedListener?.onSelectedChanged(viewHolder, actionState)
    }
  }

  override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
    super.clearView(recyclerView, viewHolder)
    onItemStateChangedListener?.onSelectedChanged(viewHolder, OnItemStateChangedListener.ACTION_STATE_IDLE)
  }

}