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
package com.yanzhenjie.recyclerview.swipe

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * Swipe menu bridge.
 *
 * Created by YanZhenjie on 2017/7/20.
 */
class SwipeMenuBridge internal constructor(
    val direction: Int,
    val position: Int,
    private val mSwipeSwitch: SwipeSwitch,
    private val mViewRoot: View) {

  var adapterPosition: Int = 0
    internal set

  internal var textView: TextView? = null
  internal var imageView: ImageView? = null

  fun setBackgroundDrawable(@DrawableRes resId: Int): SwipeMenuBridge {
    return setBackgroundDrawable(ContextCompat.getDrawable(mViewRoot.context, resId))
  }

  fun setBackgroundDrawable(background: Drawable): SwipeMenuBridge {
    ViewCompat.setBackground(mViewRoot, background)
    return this
  }

  fun setBackgroundColorResource(@ColorRes color: Int): SwipeMenuBridge {
    return setBackgroundColor(ContextCompat.getColor(mViewRoot.context, color))
  }

  fun setBackgroundColor(@ColorInt color: Int): SwipeMenuBridge {
    mViewRoot.setBackgroundColor(color)
    return this
  }

  fun setImage(resId: Int): SwipeMenuBridge {
    return setImage(ContextCompat.getDrawable(mViewRoot.context, resId))
  }

  fun setImage(icon: Drawable): SwipeMenuBridge {
    imageView?.setImageDrawable(icon)
    return this
  }

  fun setText(resId: Int): SwipeMenuBridge {
    return setText(mViewRoot.context.getString(resId))
  }

  fun setText(title: String): SwipeMenuBridge {
    textView?.text = title
    return this
  }

  fun closeMenu() {
    mSwipeSwitch.smoothCloseMenu()
  }
}
