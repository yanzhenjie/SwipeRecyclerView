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
package com.yanzhenjie.recyclerview.swipe.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView

import java.util.ArrayList

/**
 * Default item decoration.
 *
 * Created by YanZhenjie on 2017/8/14.
 *
 * @param color           line color.
 * @param dividerWidth    line width.
 * @param dividerHeight   line height.
 * @param excludeViewType don't need to draw the ViewType of the item of the split line.
 */
open class DefaultItemDecoration @JvmOverloads constructor(
    @ColorInt color: Int,
    private val dividerWidth: Int = 2,
    private val dividerHeight: Int = 2,
    vararg excludeViewType: Int = intArrayOf(-1)) : RecyclerView.ItemDecoration() {

  private val divider: Drawable
  private val excludeTypeList = ArrayList<Int>()

  private val halfDividerWidth = dividerWidth / 2
  private val halfDividerHeight = dividerHeight / 2

  init {
    divider = ColorDrawable(color)
    excludeViewType.forEach { excludeTypeList.add(it) }
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
    val position = parent.getChildAdapterPosition(view)

    if (position < 0) {
      return
    }

    if (excludeTypeList.contains(parent.adapter.getItemViewType(position))) {
      outRect.set(0, 0, 0, 0)
      return
    }

    val columnCount = getSpanCount(parent)
    val childCount = parent.adapter.itemCount

    val firstRaw = isFirstRaw(position, columnCount)
    val lastRaw = isLastRaw(position, columnCount, childCount)
    val firstColumn = isFirstColumn(position, columnCount)
    val lastColumn = isLastColumn(position, columnCount)

    if (columnCount == 1) {
      when {
        firstRaw -> outRect.set(0, 0, 0, halfDividerHeight)
        lastRaw -> outRect.set(0, halfDividerHeight, 0, 0)
        else -> outRect.set(0, halfDividerHeight, 0, halfDividerHeight)
      }
    } else {
      when {
        // right, bottom
        firstRaw && firstColumn -> outRect.set(0, 0, halfDividerWidth, halfDividerHeight)
        // left, right
        firstRaw && lastColumn -> outRect.set(halfDividerWidth, 0, 0, halfDividerHeight)
        // left, right, bottom
        firstRaw -> outRect.set(halfDividerWidth, 0, halfDividerWidth, halfDividerHeight)
        // top, right
        lastRaw && firstColumn -> outRect.set(0, halfDividerHeight, halfDividerWidth, 0)
        // left, top
        lastRaw && lastColumn -> outRect.set(halfDividerWidth, halfDividerHeight, 0, 0)
        // left, top, right
        lastRaw -> outRect.set(halfDividerWidth, halfDividerHeight, halfDividerWidth, 0)
        // top, right, bottom
        firstColumn -> outRect.set(0, halfDividerHeight, halfDividerWidth, halfDividerHeight)
        // left, top, bottom
        lastColumn -> outRect.set(halfDividerWidth, halfDividerHeight, 0, halfDividerHeight)
        // left, bottom.
        else -> outRect.set(halfDividerWidth, halfDividerHeight, halfDividerWidth, halfDividerHeight)
      }
    }
  }

  private fun getSpanCount(parent: RecyclerView): Int {
    val layoutManager = parent.layoutManager

    return when (layoutManager) {
      is GridLayoutManager -> layoutManager.spanCount
      is StaggeredGridLayoutManager -> layoutManager.spanCount
      else -> 1
    }
  }

  private fun isFirstRaw(position: Int, columnCount: Int): Boolean {
    return position < columnCount
  }

  private fun isLastRaw(position: Int, columnCount: Int, childCount: Int): Boolean {
    return when (columnCount) {
      1 -> position + 1 == childCount
      else -> {
        val lastRawItemCount = childCount % columnCount
        val rawCount = (childCount - lastRawItemCount) / columnCount + if (lastRawItemCount > 0) 1 else 0
        val rawPositionJudge = (position + 1) % columnCount

        val rawPosition = when (rawPositionJudge) {
          0 -> (position + 1) / columnCount
          else -> (position + 1 - rawPositionJudge) / columnCount + 1
        }
        rawCount == rawPosition
      }
    }
  }

  private fun isFirstColumn(position: Int, columnCount: Int): Boolean {
    return if (columnCount == 1) true else position % columnCount == 0
  }

  private fun isLastColumn(position: Int, columnCount: Int): Boolean {
    return if (columnCount == 1) true else (position + 1) % columnCount == 0
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
    drawHorizontal(c, parent)
    drawVertical(c, parent)
  }

  fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
    canvas.save()
    val childCount = parent.childCount

    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val childPosition = parent.getChildAdapterPosition(child)

      if (childPosition < 0 || excludeTypeList.contains(parent.adapter.getItemViewType(childPosition))
          || child is SwipeMenuRecyclerView.LoadMoreView) {
        continue
      }
      val left = child.left
      val top = child.bottom
      val right = child.right
      val bottom = top + dividerHeight

      divider.setBounds(left, top, right, bottom)
      divider.draw(canvas)
    }
    canvas.restore()
  }

  fun drawVertical(canvas: Canvas, parent: RecyclerView) {
    canvas.save()
    val childCount = parent.childCount

    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val childPosition = parent.getChildAdapterPosition(child)

      if (childPosition < 0 || excludeTypeList.contains(parent.adapter.getItemViewType(childPosition))
          || child is SwipeMenuRecyclerView.LoadMoreView) {
        continue
      }
      val left = child.right
      val top = child.top
      val right = left + dividerWidth
      val bottom = child.bottom

      divider.setBounds(left, top, right, bottom)
      divider.draw(canvas)
    }
    canvas.restore()
  }

}
