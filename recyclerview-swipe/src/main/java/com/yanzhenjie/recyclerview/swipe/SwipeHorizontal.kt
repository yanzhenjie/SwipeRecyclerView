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
import android.view.ViewGroup
import android.widget.OverScroller

/**
 * Swipe horizontal.
 *
 * Created by Yan Zhenjie on 2016/7/22.
 */
internal abstract class SwipeHorizontal(val direction: Int, val menuView: View) {
  protected var mChecker: Checker

  val menuWidth: Int
    get() = menuView.width

  init {
    mChecker = Checker()
  }

  fun canSwipe(): Boolean {
    return if (menuView is ViewGroup) {
      menuView.childCount > 0
    } else false
  }

  fun isCompleteClose(scrollX: Int): Boolean {
    val i = -menuView.width * direction
    return scrollX == 0 && i != 0
  }

  abstract fun isMenuOpen(scrollX: Int): Boolean

  abstract fun isMenuOpenNotEqual(scrollX: Int): Boolean

  abstract fun autoOpenMenu(scroller: OverScroller, scrollX: Int, duration: Int)

  abstract fun autoCloseMenu(scroller: OverScroller, scrollX: Int, duration: Int)

  abstract fun checkXY(x: Int, y: Int): Checker

  abstract fun isClickOnContentView(contentViewWidth: Int, x: Float): Boolean

  class Checker {
    var x: Int = 0
    var y: Int = 0
    var shouldResetSwipe: Boolean = false
  }

}
