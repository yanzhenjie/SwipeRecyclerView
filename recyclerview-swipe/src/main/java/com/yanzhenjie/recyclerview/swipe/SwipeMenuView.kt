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
import android.support.v4.view.ViewCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Swipe menu view.
 *
 * Created by Yan Zhenjie on 2016/7/26.
 */
class SwipeMenuView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

  private var mAdapterViewHolder: RecyclerView.ViewHolder? = null
  private var mSwipeSwitch: SwipeSwitch? = null
  private var mItemClickListener: SwipeMenuItemClickListener? = null

  private var mDirection: Int = 0

  fun createMenu(
      swipeMenu: SwipeMenu,
      swipeSwitch: SwipeSwitch,
      swipeMenuItemClickListener: SwipeMenuItemClickListener?,
      direction: Int) {
    removeAllViews()

    this.mSwipeSwitch = swipeSwitch
    this.mItemClickListener = swipeMenuItemClickListener
    this.mDirection = direction

    val items = swipeMenu.menuItems

    for (i in items.indices) {
      val item = items[i]
      val params = LinearLayout.LayoutParams(item.getWidth(), item.getHeight())
      val parent = LinearLayout(context)

      params.weight = item.getWeight().toFloat()

      parent.id = i
      parent.gravity = Gravity.CENTER
      parent.orientation = LinearLayout.VERTICAL
      parent.layoutParams = params
      parent.setOnClickListener(this)

      ViewCompat.setBackground(parent, item.getBackground())
      addView(parent)

      val menuBridge = SwipeMenuBridge(direction, i, swipeSwitch, parent)
      parent.tag = menuBridge

      if (item.getImage() != null) {
        val iv = createIcon(item)
        menuBridge.imageView = iv
        parent.addView(iv)
      }

      if (!TextUtils.isEmpty(item.getText())) {
        val tv = createTitle(item)
        menuBridge.textView = tv
        parent.addView(tv)
      }
    }
  }

  fun bindViewHolder(adapterVIewHolder: RecyclerView.ViewHolder) {
    this.mAdapterViewHolder = adapterVIewHolder
  }

  private fun createIcon(item: SwipeMenuItem): ImageView {
    val imageView = ImageView(context)
    imageView.setImageDrawable(item.getImage())
    return imageView
  }

  private fun createTitle(item: SwipeMenuItem): TextView {
    val textView = TextView(context)
    val textSize = item.getTextSize()
    val textColor = item.titleColor
    val textAppearance = item.getTextAppearance()
    val typeface = item.getTextTypeface()

    textView.text = item.getText()
    textView.gravity = Gravity.CENTER

    if (textSize > 0) {
      textView.setTextSize(item.textSizeUnit, textSize.toFloat())
    }

    if (textColor != null) {
      textView.setTextColor(textColor)
    }
    if (textAppearance != 0) {
      TextViewCompat.setTextAppearance(textView, textAppearance)
    }
    if (typeface != null) {
      textView.typeface = typeface
    }
    return textView
  }

  override fun onClick(v: View) {
    val clickListener = mItemClickListener ?: return
    val swipeSwitch = mSwipeSwitch ?: return
    val holder = mAdapterViewHolder ?: return
    val menuBridge = v.tag

    if (swipeSwitch.isMenuOpen && menuBridge is SwipeMenuBridge) {
      menuBridge.adapterPosition = holder.adapterPosition
      clickListener.onItemClick(menuBridge)
    }
  }
}