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

import android.view.View
import android.widget.OverScroller

/**
 * Swipe right horizontal.
 *
 * Created by Yan Zhenjie on 2016/7/22.
 */
internal class SwipeRightHorizontal(menuView: View) : SwipeHorizontal(SwipeMenuRecyclerView.RIGHT_DIRECTION, menuView) {

  override fun isMenuOpen(scrollX: Int): Boolean {
    val i = -menuView.width * direction
    return scrollX >= i && i != 0
  }

  override fun isMenuOpenNotEqual(scrollX: Int): Boolean {
    return scrollX > -menuView.width * direction
  }

  override fun autoOpenMenu(scroller: OverScroller, scrollX: Int, duration: Int) {
    scroller.startScroll(Math.abs(scrollX), 0, menuView.width - Math.abs(scrollX), 0, duration)
  }

  override fun autoCloseMenu(scroller: OverScroller, scrollX: Int, duration: Int) {
    scroller.startScroll(-Math.abs(scrollX), 0, Math.abs(scrollX), 0, duration)
  }

  override fun checkXY(x: Int, y: Int): SwipeHorizontal.Checker {
    mChecker.x = x
    mChecker.y = y
    mChecker.shouldResetSwipe = false

    when {
      x == 0 -> mChecker.shouldResetSwipe = true
      x < 0 -> mChecker.x = 0
      x > menuView.width -> mChecker.x = menuView.width
    }
    return mChecker
  }

  override fun isClickOnContentView(contentViewWidth: Int, x: Float): Boolean {
    return x < contentViewWidth - menuView.width
  }
}
