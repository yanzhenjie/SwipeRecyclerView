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
package com.yanzhenjie.recyclerview.swipe

import android.content.Context
import android.widget.LinearLayout

import java.util.ArrayList

/**
 * Swipe menu.
 *
 * Created by Yan Zhenjie on 2016/7/22.
 */
class SwipeMenu(private val mSwipeMenuLayout: SwipeMenuLayout, val viewType: Int) {

  /**
   * Orientation.
   *
   * @see SwipeMenu.HORIZONTAL*
   * @see SwipeMenu.VERTICAL
   */
  var orientation = HORIZONTAL
    set(orientation) {
      if (orientation != HORIZONTAL && orientation != VERTICAL) {
        throw IllegalArgumentException("Use SwipeMenu#HORIZONTAL or SwipeMenu#VERTICAL.")
      }
      field = orientation
    }

  private val mSwipeMenuItems = ArrayList<SwipeMenuItem>(2)

  val menuItems: List<SwipeMenuItem>
    get() = mSwipeMenuItems

  val context: Context
    get() = mSwipeMenuLayout.context


  /**
   * Set a percentage.
   *
   * @param openPercent such as 0.5F.
   */
  fun setOpenPercent(openPercent: Float) {
    if (openPercent != mSwipeMenuLayout.openPercent) {
      mSwipeMenuLayout.openPercent = when {
        openPercent > 1 -> 1F
        openPercent < 0 -> 0F
        else -> openPercent
      }
    }
  }

  /**
   * The duration of the set.
   *
   * @param scrollerDuration such 500.
   */
  fun setScrollerDuration(scrollerDuration: Int) {
    this.mSwipeMenuLayout.setScrollerDuration(scrollerDuration)
  }

  fun addMenuItem(item: SwipeMenuItem) {
    mSwipeMenuItems.add(item)
  }

  fun removeMenuItem(item: SwipeMenuItem) {
    mSwipeMenuItems.remove(item)
  }

  fun getMenuItem(index: Int): SwipeMenuItem {
    return mSwipeMenuItems[index]
  }

  companion object {

    const val HORIZONTAL = LinearLayout.HORIZONTAL
    const val VERTICAL = LinearLayout.VERTICAL
  }
}
