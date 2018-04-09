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

import android.content.Context
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Swipe adapter wrapper.
 *
 * Created by YanZhenjie on 2017/7/20.
 */
class SwipeAdapterWrapper internal constructor(
    context: Context,
    val originAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private val mHeaderViews = SparseArrayCompat<View>()
  private val mFootViews = SparseArrayCompat<View>()
  private val mInflater: LayoutInflater = LayoutInflater.from(context)

  private var mSwipeMenuEnabled = true
  private var mSwipeMenuCreator: SwipeMenuCreator? = null
  private var mSwipeMenuItemClickListener: SwipeMenuItemClickListener? = null
  private var mSwipeItemClickListener: SwipeItemClickListener? = null

  private val contentItemCount: Int
    get() = originAdapter.itemCount

  val headerItemCount: Int
    get() = mHeaderViews.size()

  val footerItemCount: Int
    get() = mFootViews.size()

  /* SwipeMenuLayout cache for toggle swipe menu enabled */

  private val swipeMenuLayoutCache = ArrayList<WeakReference<SwipeMenuLayout>>()

  /**
   * Set to create menu listener.
   *
   * @param swipeMenuCreator listener.
   */
  internal fun setSwipeMenuCreator(swipeMenuCreator: SwipeMenuCreator?) {
    this.mSwipeMenuCreator = swipeMenuCreator
  }

  /**
   * Set swipe menu enabled.
   *
   * @param enabled enabled.
   */
  internal fun setSwipeMenuEnabled(enabled: Boolean) {
    mSwipeMenuEnabled = enabled
    updateSwipeMenuEnabledForCache()
  }

  /**
   * Set to click menu listener.
   *
   * @param swipeMenuItemClickListener listener.
   */
  internal fun setSwipeMenuItemClickListener(swipeMenuItemClickListener: SwipeMenuItemClickListener?) {
    this.mSwipeMenuItemClickListener = swipeMenuItemClickListener
  }

  internal fun setSwipeItemClickListener(swipeItemClickListener: SwipeItemClickListener?) {
    mSwipeItemClickListener = swipeItemClickListener
  }

  override fun getItemCount(): Int {
    return headerItemCount + contentItemCount + footerItemCount
  }

  override fun getItemViewType(position: Int) = when {
    isHeaderView(position) -> mHeaderViews.keyAt(position)
    isFooterView(position) -> mFootViews.keyAt(position - headerItemCount - contentItemCount)
    else -> originAdapter.getItemViewType(position - headerItemCount)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    if (mHeaderViews.get(viewType) != null) {
      return ViewHolder(mHeaderViews.get(viewType))
    } else if (mFootViews.get(viewType) != null) {
      return ViewHolder(mFootViews.get(viewType))
    }
    val viewHolder = originAdapter.onCreateViewHolder(parent, viewType)

    mSwipeItemClickListener?.let { listener ->
      viewHolder.itemView.setOnClickListener { listener.onItemClick(it, viewHolder.adapterPosition) }
    }

    val menuCreator = mSwipeMenuCreator

    if (menuCreator == null) {
      val itemView = viewHolder.itemView

      if (itemView is SwipeMenuLayout) {
        itemView.isSwipeEnable = mSwipeMenuEnabled
        swipeMenuLayoutCache.add(WeakReference(itemView))
      }
      return viewHolder
    }

    val swipeMenuLayout = mInflater.inflate(R.layout.recycler_swipe_view_item, parent, false) as SwipeMenuLayout
    val swipeLeftMenu = SwipeMenu(swipeMenuLayout, viewType)
    val swipeRightMenu = SwipeMenu(swipeMenuLayout, viewType)

    menuCreator.onCreateMenu(swipeLeftMenu, swipeRightMenu, viewType)

    val leftMenuCount = swipeLeftMenu.menuItems.size
    if (leftMenuCount > 0) {
      val swipeLeftMenuView = swipeMenuLayout.findViewById(R.id.swipe_left) as SwipeMenuView

      swipeLeftMenuView.orientation = swipeLeftMenu.orientation
      swipeLeftMenuView.createMenu(swipeLeftMenu, swipeMenuLayout, mSwipeMenuItemClickListener, SwipeMenuRecyclerView.LEFT_DIRECTION)
    }

    val rightMenuCount = swipeRightMenu.menuItems.size
    if (rightMenuCount > 0) {
      val swipeRightMenuView = swipeMenuLayout.findViewById(R.id.swipe_right) as SwipeMenuView

      swipeRightMenuView.orientation = swipeRightMenu.orientation
      swipeRightMenuView.createMenu(swipeRightMenu, swipeMenuLayout, mSwipeMenuItemClickListener, SwipeMenuRecyclerView.RIGHT_DIRECTION)
    }
    swipeMenuLayout.isSwipeEnable = mSwipeMenuEnabled
    swipeMenuLayoutCache.add(WeakReference(swipeMenuLayout))

    val viewGroup = swipeMenuLayout.findViewById(R.id.swipe_content) as ViewGroup
    viewGroup.addView(viewHolder.itemView)

    try {
      val itemView = getSupperClass(viewHolder.javaClass).getDeclaredField("itemView")
      if (!itemView.isAccessible) itemView.isAccessible = true
      itemView.set(viewHolder, swipeMenuLayout)
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return viewHolder
  }

  private fun getSupperClass(clazz: Class<*>): Class<*> {
    val supperClass = clazz.superclass

    return if (supperClass != null && supperClass != Any::class.java) {
      getSupperClass(supperClass)
    } else {
      clazz
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>?) {
    if (isHeaderView(position) || isFooterView(position)) {
      return
    }
    val itemView = holder.itemView

    if (itemView is SwipeMenuLayout) {
      val childCount = itemView.childCount

      (0 until childCount)
          .map { itemView.getChildAt(it) }
          .forEach { (it as? SwipeMenuView)?.bindViewHolder(holder) }
    }

    originAdapter.onBindViewHolder(holder, position - headerItemCount, payloads)
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
    originAdapter.onAttachedToRecyclerView(recyclerView)
  }

  override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
    holder ?: return
    val position = holder.adapterPosition

    if (isHeaderView(position) || isFooterView(position)) {
      val lp = holder.itemView.layoutParams
      if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
        lp.isFullSpan = true
      }
    } else {
      originAdapter.onViewAttachedToWindow(holder)
    }
  }

  fun isHeaderView(position: Int): Boolean {
    return position in 0..(headerItemCount - 1)
  }

  fun isFooterView(position: Int): Boolean {
    return position >= headerItemCount + contentItemCount
  }

  fun addHeaderView(view: View) {
    mHeaderViews.put(headerItemCount + BASE_ITEM_TYPE_HEADER, view)
  }

  fun addHeaderViewAndNotify(view: View) {
    mHeaderViews.put(headerItemCount + BASE_ITEM_TYPE_HEADER, view)
    notifyItemInserted(headerItemCount - 1)
  }

  fun removeHeaderViewAndNotify(view: View) {
    val headerIndex = mHeaderViews.indexOfValue(view)
    mHeaderViews.removeAt(headerIndex)
    notifyItemRemoved(headerIndex)
  }

  fun addFooterView(view: View) {
    mFootViews.put(footerItemCount + BASE_ITEM_TYPE_FOOTER, view)
  }

  fun addFooterViewAndNotify(view: View) {
    mFootViews.put(footerItemCount + BASE_ITEM_TYPE_FOOTER, view)
    notifyItemInserted(headerItemCount + contentItemCount + footerItemCount - 1)
  }

  fun removeFooterViewAndNotify(view: View) {
    val footerIndex = mFootViews.indexOfValue(view)
    mFootViews.removeAt(footerIndex)
    notifyItemRemoved(headerItemCount + contentItemCount + footerIndex)
  }

  internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

  override fun setHasStableIds(hasStableIds: Boolean) {
    originAdapter.setHasStableIds(hasStableIds)
  }

  override fun getItemId(position: Int): Long {
    return if (!isHeaderView(position) && !isFooterView(position)) {
      originAdapter.getItemId(position)
    } else super.getItemId(position)
  }

  override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
    val position = holder!!.adapterPosition

    if (!isHeaderView(position) && !isFooterView(position)) {
      originAdapter.onViewRecycled(holder)
    }
  }

  override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder?): Boolean {
    val position = holder!!.adapterPosition

    return if (!isHeaderView(position) && !isFooterView(position)) {
      originAdapter.onFailedToRecycleView(holder)
    } else {
      false
    }
  }

  override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
    val position = holder!!.adapterPosition

    if (!isHeaderView(position) && !isFooterView(position)) {
      originAdapter.onViewDetachedFromWindow(holder)
    }
  }

  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
    originAdapter.onDetachedFromRecyclerView(recyclerView)
  }

  private fun updateSwipeMenuEnabledForCache() {
    val removeItems = ArrayList<WeakReference<SwipeMenuLayout>>()
    val swipeEnabled = mSwipeMenuEnabled

    synchronized(swipeMenuLayoutCache) {
      var menuLayout: SwipeMenuLayout?

      for (refLayout in swipeMenuLayoutCache) {
        menuLayout = refLayout.get()

        if (menuLayout == null) {
          removeItems.add(refLayout)
        } else {
          menuLayout.isSwipeEnable = swipeEnabled
        }
      }
      swipeMenuLayoutCache.removeAll(removeItems)
      removeItems.clear()
    }
  }

  companion object {

    private const val BASE_ITEM_TYPE_HEADER = 100000
    private const val BASE_ITEM_TYPE_FOOTER = 200000
  }
}