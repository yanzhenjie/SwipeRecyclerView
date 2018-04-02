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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup

import com.yanzhenjie.recyclerview.swipe.touch.DefaultItemTouchHelper
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMovementListener
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener
import com.yanzhenjie.recyclerview.swipe.widget.DefaultLoadMoreView

import java.util.ArrayList

/**
 * Swipe menu recycler view.
 *
 * Created by Yan Zhenjie on 2016/7/27.
 */
class SwipeMenuRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

  protected var mScaleTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop
  protected var mOldSwipedLayout: SwipeMenuLayout? = null
  protected var mOldTouchedPosition = INVALID_POSITION

  private var mDownX: Int = 0
  private var mDownY: Int = 0

  private var allowSwipeDelete = false

  private var mDefaultItemTouchHelper: DefaultItemTouchHelper? = null

  private var mSwipeMenuEnabled = true
  private var mSwipeMenuCreator: SwipeMenuCreator? = null
  private var mSwipeMenuItemClickListener: SwipeMenuItemClickListener? = null
  private var mSwipeItemClickListener: SwipeItemClickListener? = null

  private var mAdapterWrapper: SwipeAdapterWrapper? = null

  /**
   * Long press drag enabled status.
   */
  var isLongPressDragEnabled: Boolean
    get() {
      initializeItemTouchHelper()
      return this.mDefaultItemTouchHelper?.isLongPressDragEnabled ?: false
    }
    set(canDrag) {
      initializeItemTouchHelper()
      this.mDefaultItemTouchHelper?.isLongPressDragEnabled = canDrag
    }

  /**
   * Item view swipe enabled status.
   *
   * <p>Note: swipe and menu conflict.</p>
   */
  var isItemViewSwipeEnabled: Boolean
    get() {
      initializeItemTouchHelper()
      return this.mDefaultItemTouchHelper?.isItemViewSwipeEnabled ?: false
    }
    set(canSwipe) {
      initializeItemTouchHelper()
      allowSwipeDelete = canSwipe
      this.mDefaultItemTouchHelper?.isItemViewSwipeEnabled = canSwipe
    }

  /**
   * Original adapter.
   */
  val originAdapter: Adapter<*>?
    get() = mAdapterWrapper?.originAdapter

  private val mAdapterDataObserver = object : AdapterDataObserver() {
    override fun onChanged() {
      mAdapterWrapper?.notifyDataSetChanged()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
      mAdapterWrapper?.notifyItemRangeChanged(positionStart + headerItemCount, itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
      mAdapterWrapper?.notifyItemRangeChanged(positionStart + headerItemCount, itemCount, payload)
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
      mAdapterWrapper?.notifyItemRangeInserted(positionStart + headerItemCount, itemCount)
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
      mAdapterWrapper?.notifyItemRangeRemoved(positionStart + headerItemCount, itemCount)
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
      mAdapterWrapper?.notifyItemMoved(fromPosition + headerItemCount, toPosition + headerItemCount)
    }
  }

  private val mHeaderViewList = ArrayList<View>()
  private val mFooterViewList = ArrayList<View>()

  /**
   * Header item count.
   */
  val headerItemCount: Int
    get() = mAdapterWrapper?.headerItemCount ?: 0

  /**
   * Footer item count.
   */
  val footerItemCount: Int
    get() = mAdapterWrapper?.footerItemCount ?: 0

  private var mScrollState = -1

  private var isLoadMore = false
  private var isAutoLoadMore = true
  private var isLoadError = false

  private var mDataEmpty = true
  private var mHasMore = false

  private var mLoadMoreView: LoadMoreView? = null
  private var mLoadMoreListener: LoadMoreListener? = null

  private fun initializeItemTouchHelper() {
    val touchHelper = mDefaultItemTouchHelper

    if (touchHelper == null) {
      mDefaultItemTouchHelper = DefaultItemTouchHelper().apply {
        attachToRecyclerView(this@SwipeMenuRecyclerView)
      }
    }
  }

  /**
   * Set OnItemMoveListener.
   *
   * @param onItemMoveListener [OnItemMoveListener].
   */
  fun setOnItemMoveListener(onItemMoveListener: OnItemMoveListener) {
    initializeItemTouchHelper()
    this.mDefaultItemTouchHelper?.onItemMoveListener = onItemMoveListener
  }

  /**
   * Set OnItemMovementListener.
   *
   * @param onItemMovementListener [OnItemMovementListener].
   */
  fun setOnItemMovementListener(onItemMovementListener: OnItemMovementListener) {
    initializeItemTouchHelper()
    this.mDefaultItemTouchHelper?.onItemMovementListener = onItemMovementListener
  }

  /**
   * Set OnItemStateChangedListener.
   *
   * @param onItemStateChangedListener [OnItemStateChangedListener].
   */
  fun setOnItemStateChangedListener(onItemStateChangedListener: OnItemStateChangedListener) {
    initializeItemTouchHelper()
    this.mDefaultItemTouchHelper?.onItemStateChangedListener = onItemStateChangedListener
  }

  /**
   * Start drag a item.
   *
   * @param viewHolder the ViewHolder to start dragging. It must be a direct child of RecyclerView.
   */
  fun startDrag(viewHolder: ViewHolder) {
    initializeItemTouchHelper()
    this.mDefaultItemTouchHelper?.startDrag(viewHolder)
  }

  /**
   * Star swipe a item.
   *
   * @param viewHolder the ViewHolder to start swiping. It must be a direct child of RecyclerView.
   */
  fun startSwipe(viewHolder: ViewHolder) {
    initializeItemTouchHelper()
    this.mDefaultItemTouchHelper?.startSwipe(viewHolder)
  }

  /**
   * Check the Adapter and throw an exception if it already exists.
   */
  private fun checkAdapterExist(message: String) {
    if (mAdapterWrapper != null)
      throw IllegalStateException(message)
  }

  /**
   * Set item click listener.
   */
  fun setSwipeItemClickListener(itemClickListener: SwipeItemClickListener?) {
    itemClickListener ?: return
    checkAdapterExist("Cannot set item click listener, setAdapter has already been called.")
    this.mSwipeItemClickListener = ItemClick(this, itemClickListener)
  }

  private class ItemClick(private val mRecyclerView: SwipeMenuRecyclerView, private val mCallback: SwipeItemClickListener) : SwipeItemClickListener {

    override fun onItemClick(itemView: View, position: Int) {
      val itemPosition = position - mRecyclerView.headerItemCount

      if (itemPosition >= 0)
        mCallback.onItemClick(itemView, itemPosition)
    }
  }

  /**
   * Set to create menu listener.
   */
  fun setSwipeMenuCreator(menuCreator: SwipeMenuCreator?) {
    menuCreator ?: return
    checkAdapterExist("Cannot set menu creator, setAdapter has already been called.")
    this.mSwipeMenuCreator = menuCreator
  }

  /**
   * Set to click menu listener.
   */
  fun setSwipeMenuItemClickListener(menuItemClickListener: SwipeMenuItemClickListener?) {
    menuItemClickListener ?: return
    checkAdapterExist("Cannot set menu item click listener, setAdapter has already been called.")
    this.mSwipeMenuItemClickListener = MenuItemClick(this, menuItemClickListener)
  }

  private class MenuItemClick(private val mRecyclerView: SwipeMenuRecyclerView, private val mCallback: SwipeMenuItemClickListener) : SwipeMenuItemClickListener {

    override fun onItemClick(menuBridge: SwipeMenuBridge) {
      val itemPosition = menuBridge.adapterPosition - mRecyclerView.headerItemCount

      if (itemPosition >= 0) {
        menuBridge.adapterPosition = itemPosition
        mCallback.onItemClick(menuBridge)
      }
    }
  }

  /**
   * Set swipe menu enabled.
   *
   * @param enabled enabled.
   */
  fun setSwipeMenuEnabled(enabled: Boolean) {
    mSwipeMenuEnabled = enabled
    mAdapterWrapper?.setSwipeMenuEnabled(enabled)
  }

  override fun setLayoutManager(layoutManager: LayoutManager) {
    if (layoutManager is GridLayoutManager) {
      val spanSizeLookupHolder = layoutManager.spanSizeLookup

      layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
          val adapterWrapper = mAdapterWrapper

          return if (adapterWrapper != null && (adapterWrapper.isHeaderView(position) || adapterWrapper.isFooterView(position))) {
            layoutManager.spanCount
          } else {
            spanSizeLookupHolder?.getSpanSize(position - headerItemCount) ?: 1
          }
        }
      }
    }
    super.setLayoutManager(layoutManager)
  }

  override fun setAdapter(adapter: Adapter<ViewHolder>?) {
    mAdapterWrapper?.originAdapter?.unregisterAdapterDataObserver(mAdapterDataObserver)

    if (adapter == null) {
      mAdapterWrapper = null
    } else {
      adapter.registerAdapterDataObserver(mAdapterDataObserver)

      val adapterWrapper = SwipeAdapterWrapper(context, adapter)

      adapterWrapper.setSwipeItemClickListener(mSwipeItemClickListener)
      adapterWrapper.setSwipeMenuCreator(mSwipeMenuCreator)
      adapterWrapper.setSwipeMenuEnabled(mSwipeMenuEnabled)
      adapterWrapper.setSwipeMenuItemClickListener(mSwipeMenuItemClickListener)

      if (mHeaderViewList.size > 0) {
        for (view in mHeaderViewList) {
          adapterWrapper.addHeaderView(view)
        }
      }
      if (mFooterViewList.size > 0) {
        for (view in mFooterViewList) {
          adapterWrapper.addFooterView(view)
        }
      }
      mAdapterWrapper = adapterWrapper
    }
    super.setAdapter(mAdapterWrapper)
  }

  override fun onDetachedFromWindow() {
    mAdapterWrapper?.originAdapter?.unregisterAdapterDataObserver(mAdapterDataObserver)
    super.onDetachedFromWindow()
  }

  /**
   * Add view at the headers.
   */
  fun addHeaderView(view: View) {
    mHeaderViewList.add(view)
    mAdapterWrapper?.addHeaderViewAndNotify(view)
  }

  /**
   * Remove view from header.
   */
  fun removeHeaderView(view: View) {
    mHeaderViewList.remove(view)
    mAdapterWrapper?.removeHeaderViewAndNotify(view)
  }

  /**
   * Returns header view holder status.
   *
   * @param holder view holder.
   */
  fun isHeaderViewHolder(holder: ViewHolder): Boolean {
    val adapterWrapper = this.mAdapterWrapper ?: return false
    val headerCount = adapterWrapper.headerItemCount
    val position = holder.adapterPosition

    return headerCount > 0 && position < headerCount
  }

  /**
   * Returns footer view holder status.
   *
   * @param holder view holder.
   */
  fun isFooterViewHolder(holder: ViewHolder): Boolean {
    val adapterWrapper = this.mAdapterWrapper ?: return false
    val itemCount = adapterWrapper.itemCount
    val footerCount = adapterWrapper.footerItemCount
    val position = holder.adapterPosition

    return footerCount > 0 && position < itemCount
        && position >= (itemCount - footerCount)
  }

  /**
   * Add view at the footer.
   */
  fun addFooterView(view: View) {
    mFooterViewList.add(view)
    mAdapterWrapper?.addFooterViewAndNotify(view)
  }

  fun removeFooterView(view: View) {
    mFooterViewList.remove(view)
    mAdapterWrapper?.removeFooterViewAndNotify(view)
  }

  /**
   * Get ViewType of item.
   */
  fun getItemViewType(position: Int): Int {
    return mAdapterWrapper?.getItemViewType(position) ?: 0
  }

  /**
   * open menu on left.
   *
   * @param position position.
   */
  fun smoothOpenLeftMenu(position: Int) {
    smoothOpenMenu(position, LEFT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION)
  }

  /**
   * open menu on left.
   *
   * @param position position.
   * @param duration time millis.
   */
  fun smoothOpenLeftMenu(position: Int, duration: Int) {
    smoothOpenMenu(position, LEFT_DIRECTION, duration)
  }

  /**
   * open menu on right.
   *
   * @param position position.
   */
  fun smoothOpenRightMenu(position: Int) {
    smoothOpenMenu(position, RIGHT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION)
  }

  /**
   * open menu on right.
   *
   * @param position position.
   * @param duration time millis.
   */
  fun smoothOpenRightMenu(position: Int, duration: Int) {
    smoothOpenMenu(position, RIGHT_DIRECTION, duration)
  }

  /**
   * open menu.
   *
   * @param position  position.
   * @param duration  time millis.
   *
   * @see LEFT_DIRECTION
   * @see RIGHT_DIRECTION
   */
  fun smoothOpenMenu(position: Int, direction: Int, duration: Int) {
    mOldSwipedLayout?.apply {
      if (isMenuOpen) {
        smoothCloseMenu()
      }
    }

    val itemPosition = position + headerItemCount
    val holder = findViewHolderForAdapterPosition(position)

    if (holder != null) {
      val itemView = getSwipeMenuView(holder.itemView)

      if (itemView is SwipeMenuLayout) {
        mOldSwipedLayout = itemView

        when (direction) {
          RIGHT_DIRECTION -> {
            mOldTouchedPosition = itemPosition
            mOldSwipedLayout?.smoothOpenRightMenu(duration)
          }
          LEFT_DIRECTION -> {
            mOldTouchedPosition = itemPosition
            mOldSwipedLayout?.smoothOpenLeftMenu(duration)
          }
        }
      }
    }
  }

  /**
   * Close menu.
   */
  fun smoothCloseMenu() {
    val oldSwipe = mOldSwipedLayout

    if (oldSwipe != null && oldSwipe.isMenuOpen) {
      oldSwipe.smoothCloseMenu()
    }
  }

  override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
    var isIntercepted = super.onInterceptTouchEvent(e)

    if (allowSwipeDelete) {
      // swipe and menu conflict.
      return isIntercepted
    }
    if (e.pointerCount > 1) {
      return true
    }

    val action = e.action
    val x = e.x.toInt()
    val y = e.y.toInt()

    when (action) {
      MotionEvent.ACTION_DOWN -> {
        mDownX = x
        mDownY = y
        isIntercepted = false

        val touchingPosition = getChildAdapterPosition(findChildViewUnder(x.toFloat(), y.toFloat()))
        val oldSwipe = mOldSwipedLayout

        if (touchingPosition != mOldTouchedPosition && oldSwipe != null && oldSwipe.isMenuOpen) {
          oldSwipe.smoothCloseMenu()
          isIntercepted = true
        }

        if (isIntercepted) {
          mOldSwipedLayout = null
          mOldTouchedPosition = INVALID_POSITION
        } else {
          val holder = findViewHolderForAdapterPosition(touchingPosition)

          if (holder != null) {
            val itemView = getSwipeMenuView(holder.itemView)

            if (itemView is SwipeMenuLayout) {
              mOldSwipedLayout = itemView
              mOldTouchedPosition = touchingPosition
            }
          }
        }
      }

      // They are sensitive to retain sliding and inertia.
      MotionEvent.ACTION_MOVE -> {
        isIntercepted = handleUnDown(x, y, isIntercepted)
        val oldSwipe = mOldSwipedLayout
        val viewParent = parent

        if (oldSwipe != null && viewParent != null) {
          val disX = mDownX - x

          // 向左滑，显示右侧菜单，或者关闭左侧菜单。
          val showRightCloseLeft = disX > 0 && (oldSwipe.hasRightMenu() || oldSwipe.isLeftCompleteOpen)
          // 向右滑，显示左侧菜单，或者关闭右侧菜单。
          val showLeftCloseRight = disX < 0 && (oldSwipe.hasLeftMenu() || oldSwipe.isRightCompleteOpen)

          viewParent.requestDisallowInterceptTouchEvent(showRightCloseLeft || showLeftCloseRight)
        }
        isIntercepted = handleUnDown(x, y, isIntercepted)
      }
      MotionEvent.ACTION_UP,
      MotionEvent.ACTION_CANCEL -> {
        isIntercepted = handleUnDown(x, y, isIntercepted)
      }
    }
    return isIntercepted
  }

  private fun handleUnDown(x: Int, y: Int, defaultValue: Boolean): Boolean {
    val disX = mDownX - x
    val disY = mDownY - y

    // swipe
    if (Math.abs(disX) > mScaleTouchSlop && Math.abs(disX) > Math.abs(disY)) {
      return false
    }
    // click
    return if (Math.abs(disY) < mScaleTouchSlop && Math.abs(disX) < mScaleTouchSlop) false else defaultValue
  }

  override fun onTouchEvent(e: MotionEvent): Boolean {
    val action = e.action
    val oldSwipe = mOldSwipedLayout

    when (action) {
      MotionEvent.ACTION_MOVE -> if (oldSwipe != null && oldSwipe.isMenuOpen) {
        oldSwipe.smoothCloseMenu()
      }
    }
    return super.onTouchEvent(e)
  }

  private fun getSwipeMenuView(itemView: View): View {
    if (itemView is SwipeMenuLayout) {
      return itemView
    }
    val unvisited = ArrayList<View>()
    unvisited.add(itemView)

    while (!unvisited.isEmpty()) {
      val child = unvisited.removeAt(0) as? ViewGroup ?: continue

      if (child is SwipeMenuLayout) {
        return child
      }
      val childCount = child.childCount
      (0 until childCount).mapTo(unvisited) { child.getChildAt(it) }
    }
    return itemView
  }

  override fun onScrollStateChanged(state: Int) {
    this.mScrollState = state
  }

  override fun onScrolled(dx: Int, dy: Int) {
    val layoutManager = layoutManager

    if (layoutManager != null && layoutManager is LinearLayoutManager) {
      val itemCount = layoutManager.itemCount

      if (itemCount <= 0) {
        return
      }
      val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

      if (itemCount == lastVisiblePosition + 1 && (mScrollState == SCROLL_STATE_DRAGGING || mScrollState == SCROLL_STATE_SETTLING)) {
        dispatchLoadMore()
      }

    } else if (layoutManager != null && layoutManager is StaggeredGridLayoutManager) {
      val itemCount = layoutManager.itemCount

      if (itemCount <= 0) {
        return
      }
      val lastVisiblePositionArray = layoutManager.findLastCompletelyVisibleItemPositions(null)
      val lastVisiblePosition = lastVisiblePositionArray[lastVisiblePositionArray.size - 1]

      if (itemCount == lastVisiblePosition + 1 && (mScrollState == SCROLL_STATE_DRAGGING || mScrollState == SCROLL_STATE_SETTLING)) {
        dispatchLoadMore()
      }
    }
  }

  private fun dispatchLoadMore() {
    if (isLoadError) return

    if (!isAutoLoadMore) {
      mLoadMoreView?.onWaitToLoadMore(mLoadMoreListener)

    } else {
      if (isLoadMore || mDataEmpty || !mHasMore) {
        return
      }
      isLoadMore = true
      mLoadMoreView?.onLoading()
      mLoadMoreListener?.onLoadMore()
    }
  }

  /**
   * Use the default to load more View.
   */
  fun useDefaultLoadMore() {
    val defaultLoadMoreView = DefaultLoadMoreView(context)
    addFooterView(defaultLoadMoreView)
    setLoadMoreView(defaultLoadMoreView)
  }

  /**
   * Load more view.
   */
  fun setLoadMoreView(loadMoreView: LoadMoreView) {
    mLoadMoreView = loadMoreView
  }

  /**
   * Load more listener.
   */
  fun setLoadMoreListener(loadMoreListener: LoadMoreListener) {
    mLoadMoreListener = loadMoreListener
  }

  /**
   * Automatically load more automatically.
   *
   *
   * Non-auto-loading mode, you can to click on the item to load.
   *
   *
   * @param autoLoadMore you can use false.
   * @see LoadMoreView.onWaitToLoadMore
   */
  fun setAutoLoadMore(autoLoadMore: Boolean) {
    isAutoLoadMore = autoLoadMore
  }

  /**
   * Load more done.
   *
   * @param dataEmpty data is empty ?
   * @param hasMore   has more data ?
   */
  fun loadMoreFinish(dataEmpty: Boolean, hasMore: Boolean) {
    isLoadMore = false
    isLoadError = false

    mDataEmpty = dataEmpty
    mHasMore = hasMore

    mLoadMoreView?.onLoadFinish(dataEmpty, hasMore)
  }

  /**
   * Called when data is loaded incorrectly.
   *
   * @param errorCode    Error code, will be passed to the LoadView, you can according to it to customize the prompt information.
   * @param errorMessage Error message.
   */
  fun loadMoreError(errorCode: Int, errorMessage: String) {
    isLoadMore = false
    isLoadError = true

    mLoadMoreView?.onLoadError(errorCode, errorMessage)
  }

  interface LoadMoreView {

    /**
     * Show progress.
     */
    fun onLoading()

    /**
     * Load finish, handle result.
     */
    fun onLoadFinish(dataEmpty: Boolean, hasMore: Boolean)

    /**
     * Non-auto-loading mode, you can to click on the item to load.
     */
    fun onWaitToLoadMore(loadMoreListener: LoadMoreListener?)

    /**
     * Load error.
     */
    fun onLoadError(errorCode: Int, errorMessage: String)
  }

  interface LoadMoreListener {

    /**
     * More data should be requested.
     */
    fun onLoadMore()
  }

  companion object {

    /**
     * Left menu.
     */
    const val LEFT_DIRECTION = 1
    /**
     * Right menu.
     */
    const val RIGHT_DIRECTION = -1

    /**
     * Invalid position.
     */
    private const val INVALID_POSITION = -1
  }

}
