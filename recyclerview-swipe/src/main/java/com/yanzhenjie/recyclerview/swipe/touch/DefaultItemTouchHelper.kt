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

import android.support.v7.widget.helper.CompatItemTouchHelper

/**
 * Default item touch helper.
 *
 * Created by Yolanda on 2016/4/19.
 */
class DefaultItemTouchHelper : CompatItemTouchHelper {

  private val mDefaultItemTouchHelperCallback: DefaultItemTouchHelperCallback

  /**
   * Create default item touch helper.
   */
  constructor() : this(DefaultItemTouchHelperCallback())

  /**
   * Create default item touch helper.
   *
   * @param callback the behavior of ItemTouchHelper.
   */
  private constructor(callback: DefaultItemTouchHelperCallback): super(callback) {
    mDefaultItemTouchHelperCallback = callback
  }

  /**
   * Item move listener.
   */
  var onItemMoveListener: OnItemMoveListener?
    get() = mDefaultItemTouchHelperCallback.onItemMoveListener
    set(onItemMoveListener) {
      mDefaultItemTouchHelperCallback.onItemMoveListener = onItemMoveListener
    }

  /**
   * Item movement listener.
   */
  var onItemMovementListener: OnItemMovementListener?
    get() = mDefaultItemTouchHelperCallback.onItemMovementListener
    set(onItemMovementListener) {
      mDefaultItemTouchHelperCallback.onItemMovementListener = onItemMovementListener
    }

  /**
   * Long press drag enabled status.
   */
  var isLongPressDragEnabled: Boolean
    get() = mDefaultItemTouchHelperCallback.isLongPressDragEnabled
    set(canDrag) {
      mDefaultItemTouchHelperCallback.isLongPressDragEnabled = canDrag
    }

  /**
   * Item view swipe enabled status.
   */
  var isItemViewSwipeEnabled: Boolean
    get() = this.mDefaultItemTouchHelperCallback.isItemViewSwipeEnabled
    set(canSwipe) {
      mDefaultItemTouchHelperCallback.isItemViewSwipeEnabled = canSwipe
    }

  /**
   * Item state changed listener.
   */
  var onItemStateChangedListener: OnItemStateChangedListener?
    get() = this.mDefaultItemTouchHelperCallback.onItemStateChangedListener
    set(onItemStateChangedListener) {
      this.mDefaultItemTouchHelperCallback.onItemStateChangedListener = onItemStateChangedListener
    }

}
