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
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.Px
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue

/**
 * Swipe menu item.
 *
 * Created by Yan Zhenjie on 2016/7/26.
 */
class SwipeMenuItem(private val mContext: Context) {
  private var background: Drawable? = null
  private var icon: Drawable? = null
  private var title: String? = null

  var titleColor: ColorStateList? = null
    private set

  private var titleSize: Int = 0

  var textSizeUnit = DEFAULT_TEXT_SIZE_UNIT
    private set

  private var textTypeface: Typeface? = null
  private var textAppearance: Int = 0
  private var width = -2
  private var height = -2
  private var weight = 0

  fun setBackground(@DrawableRes resId: Int): SwipeMenuItem {
    return setBackground(ContextCompat.getDrawable(mContext, resId))
  }

  fun setBackground(background: Drawable): SwipeMenuItem {
    this.background = background
    return this
  }

  fun setBackgroundColorResource(@ColorRes color: Int): SwipeMenuItem {
    return setBackgroundColor(ContextCompat.getColor(mContext, color))
  }

  fun setBackgroundColor(@ColorInt color: Int): SwipeMenuItem {
    this.background = ColorDrawable(color)
    return this
  }

  fun getBackground(): Drawable? {
    return background
  }

  fun setImage(resId: Int): SwipeMenuItem {
    return setImage(ContextCompat.getDrawable(mContext, resId))
  }

  fun setImage(icon: Drawable): SwipeMenuItem {
    this.icon = icon
    return this
  }

  fun getImage(): Drawable? {
    return icon
  }

  fun setText(resId: Int): SwipeMenuItem {
    return setText(mContext.getString(resId))
  }

  fun setText(title: String): SwipeMenuItem {
    this.title = title
    return this
  }

  fun getText(): String? {
    return title
  }

  fun setTextColorResource(@ColorRes titleColor: Int): SwipeMenuItem {
    return setTextColor(ContextCompat.getColor(mContext, titleColor))
  }

  fun setTextColor(@ColorInt titleColor: Int): SwipeMenuItem {
    this.titleColor = ColorStateList.valueOf(titleColor)
    return this
  }

  fun setTextSize(@Px titleSize: Int): SwipeMenuItem {
    return setTextSize(DEFAULT_TEXT_SIZE_UNIT, titleSize)
  }

  /**
   * Set text size with unit.
   *
   * @param unit unit like [TypedValue.COMPLEX_UNIT_PX], [TypedValue.COMPLEX_UNIT_SP], default as px.
   * @param textSize text size.
   */
  fun setTextSize(unit: Int, textSize: Int): SwipeMenuItem {
    this.titleSize = textSize
    this.textSizeUnit = unit
    return this
  }

  fun getTextSize(): Int {
    return titleSize
  }

  fun setTextAppearance(@StyleRes textAppearance: Int): SwipeMenuItem {
    this.textAppearance = textAppearance
    return this
  }

  fun getTextAppearance(): Int {
    return textAppearance
  }

  fun setTextTypeface(textTypeface: Typeface): SwipeMenuItem {
    this.textTypeface = textTypeface
    return this
  }

  fun getTextTypeface(): Typeface? {
    return textTypeface
  }

  fun setWidth(width: Int): SwipeMenuItem {
    this.width = width
    return this
  }

  fun getWidth(): Int {
    return width
  }

  fun setHeight(height: Int): SwipeMenuItem {
    this.height = height
    return this
  }

  fun getHeight(): Int {
    return height
  }

  fun setWeight(weight: Int): SwipeMenuItem {
    this.weight = weight
    return this
  }

  fun getWeight(): Int {
    return weight
  }

  companion object {

    private const val DEFAULT_TEXT_SIZE_UNIT = TypedValue.COMPLEX_UNIT_PX
  }
}
