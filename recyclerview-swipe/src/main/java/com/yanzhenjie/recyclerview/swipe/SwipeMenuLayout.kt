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
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.OverScroller
import android.widget.TextView

/**
 * Swipe menu layout.
 *
 * Created by Yan Zhenjie on 2016/7/27.
 */
class SwipeMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle), SwipeSwitch {

  private var mLeftViewId = 0
  private var mContentViewId = 0
  private var mRightViewId = 0

  /**
   * Open percentage.
   */
  var openPercent = 0.5f

  private var mScrollerDuration = DEFAULT_SCROLLER_DURATION

  private val mScaledTouchSlop: Int
  private var mLastX: Int = 0
  private var mLastY: Int = 0
  private var mDownX: Int = 0
  private var mDownY: Int = 0
  private var mContentView: View? = null
  private var mSwipeLeftHorizontal: SwipeLeftHorizontal? = null
  private var mSwipeRightHorizontal: SwipeRightHorizontal? = null
  private var mSwipeCurrentHorizontal: SwipeHorizontal? = null
  private var shouldResetSwipe: Boolean = false
  private var mDragging: Boolean = false

  /**
   * Swipe enabled status.
   */
  var isSwipeEnable = true

  private val mScroller: OverScroller
  private var mVelocityTracker: VelocityTracker? = null
  private val mScaledMinimumFlingVelocity: Int
  private val mScaledMaximumFlingVelocity: Int

  override val isMenuOpen: Boolean
    get() = isLeftMenuOpen || isRightMenuOpen

  override val isLeftMenuOpen: Boolean
    get() = mSwipeLeftHorizontal != null && mSwipeLeftHorizontal!!.isMenuOpen(scrollX)

  override val isRightMenuOpen: Boolean
    get() = mSwipeRightHorizontal != null && mSwipeRightHorizontal!!.isMenuOpen(scrollX)

  override val isCompleteOpen: Boolean
    get() = isLeftCompleteOpen || isRightMenuOpen

  override val isLeftCompleteOpen: Boolean
    get() = mSwipeLeftHorizontal != null && !mSwipeLeftHorizontal!!.isCompleteClose(scrollX)

  override val isRightCompleteOpen: Boolean
    get() = mSwipeRightHorizontal != null && !mSwipeRightHorizontal!!.isCompleteClose(scrollX)

  override val isMenuOpenNotEqual: Boolean
    get() = isLeftMenuOpenNotEqual || isRightMenuOpenNotEqual

  override val isLeftMenuOpenNotEqual: Boolean
    get() = mSwipeLeftHorizontal != null && mSwipeLeftHorizontal!!.isMenuOpenNotEqual(scrollX)

  override val isRightMenuOpenNotEqual: Boolean
    get() = mSwipeRightHorizontal != null && mSwipeRightHorizontal!!.isMenuOpenNotEqual(scrollX)

  init {

    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.recycler_swipe_SwipeMenuLayout)
    mLeftViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_leftViewId, mLeftViewId)
    mContentViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_contentViewId, mContentViewId)
    mRightViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_rightViewId, mRightViewId)
    typedArray.recycle()

    val configuration = ViewConfiguration.get(getContext())
    mScaledTouchSlop = configuration.scaledTouchSlop
    mScaledMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity
    mScaledMaximumFlingVelocity = configuration.scaledMaximumFlingVelocity

    mScroller = OverScroller(getContext())
  }

  override fun onFinishInflate() {
    super.onFinishInflate()

    if (mLeftViewId != 0 && mSwipeLeftHorizontal == null) {
      val view = findViewById(mLeftViewId)
      mSwipeLeftHorizontal = SwipeLeftHorizontal(view)
    }
    if (mRightViewId != 0 && mSwipeRightHorizontal == null) {
      val view = findViewById(mRightViewId)
      mSwipeRightHorizontal = SwipeRightHorizontal(view)
    }
    if (mContentViewId != 0 && mContentView == null) {
      mContentView = findViewById(mContentViewId)
    } else {
      val errorView = TextView(context)
      errorView.isClickable = true
      errorView.gravity = Gravity.CENTER
      errorView.textSize = 16f
      errorView.text = "You may not have set the ContentView."

      mContentView = errorView
      addView(mContentView)
    }
  }

  /**
   * The duration of the set.
   *
   * @param scrollerDuration such as 500.
   */
  fun setScrollerDuration(scrollerDuration: Int) {
    this.mScrollerDuration = scrollerDuration
  }

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    val isIntercepted = super.onInterceptTouchEvent(ev)
    val action = ev.action

    when (action) {
      MotionEvent.ACTION_DOWN -> {
        mLastX = ev.x.toInt()
        mDownX = mLastX
        mDownY = ev.y.toInt()
        return false
      }
      MotionEvent.ACTION_MOVE -> {
        val disX = (ev.x - mDownX).toInt()
        val disY = (ev.y - mDownY).toInt()
        return Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY)
      }
      MotionEvent.ACTION_UP -> {
        val swipeHorizontal = mSwipeCurrentHorizontal
        val isClick = swipeHorizontal != null && swipeHorizontal.isClickOnContentView(width, ev.x)

        if (isMenuOpen && isClick) {
          smoothCloseMenu()
          return true
        }
        return false
      }
      MotionEvent.ACTION_CANCEL -> {
        if (!mScroller.isFinished)
          mScroller.abortAnimation()
        return false
      }
    }
    return isIntercepted
  }

  override fun onTouchEvent(ev: MotionEvent): Boolean {
    val velocityTracker = getVelocityTracker()
    velocityTracker.addMovement(ev)

    val dx: Int
    val dy: Int
    val action = ev.action
    when (action) {
      MotionEvent.ACTION_DOWN -> {
        mLastX = ev.x.toInt()
        mLastY = ev.y.toInt()
      }
      MotionEvent.ACTION_MOVE -> {
        if (isSwipeEnable) {
          val disX = (mLastX - ev.x).toInt()
          val disY = (mLastY - ev.y).toInt()

          if (!mDragging && Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY)) {
            mDragging = true
          }
          if (mDragging) {
            if (mSwipeCurrentHorizontal == null || shouldResetSwipe) {
              val swipeLeft = mSwipeLeftHorizontal
              val swipeRight = mSwipeRightHorizontal

              mSwipeCurrentHorizontal = when {
                disX < 0 -> when {
                  swipeLeft != null -> swipeLeft
                  else -> swipeRight
                }
                else -> when {
                  swipeRight != null -> swipeRight
                  else -> swipeLeft
                }
              }
            }
            scrollBy(disX, 0)
            mLastX = ev.x.toInt()
            mLastY = ev.y.toInt()
            shouldResetSwipe = false
          }
        }
      }
      MotionEvent.ACTION_UP -> {
        dx = (mDownX - ev.x).toInt()
        dy = (mDownY - ev.y).toInt()

        mDragging = false
        velocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity.toFloat())

        val velocityX = velocityTracker.xVelocity.toInt()
        val velocity = Math.abs(velocityX)

        if (velocity > mScaledMinimumFlingVelocity) {
          if (mSwipeCurrentHorizontal != null) {
            val duration = getSwipeDuration(ev, velocity)

            if (mSwipeCurrentHorizontal is SwipeRightHorizontal) {
              if (velocityX < 0) {
                smoothOpenMenu(duration)
              } else {
                smoothCloseMenu(duration)
              }
            } else {
              if (velocityX > 0) {
                smoothOpenMenu(duration)
              } else {
                smoothCloseMenu(duration)
              }
            }
            ViewCompat.postInvalidateOnAnimation(this)
          }
        } else {
          judgeOpenClose(dx, dy)
        }

        velocityTracker.clear()
        velocityTracker.recycle()
        mVelocityTracker = null

        if (Math.abs(mDownX - ev.x) > mScaledTouchSlop
            || Math.abs(mDownY - ev.y) > mScaledTouchSlop
            || isLeftMenuOpen
            || isRightMenuOpen) {
          ev.action = MotionEvent.ACTION_CANCEL
          super.onTouchEvent(ev)
          return true
        }
      }
      MotionEvent.ACTION_CANCEL -> {
        mDragging = false

        if (!mScroller.isFinished) {
          mScroller.abortAnimation()
        } else {
          dx = (mDownX - ev.x).toInt()
          dy = (mDownY - ev.y).toInt()
          judgeOpenClose(dx, dy)
        }
      }
    }
    return super.onTouchEvent(ev)
  }

  private fun getVelocityTracker(): VelocityTracker {
    var tracker = mVelocityTracker

    return when (tracker) {
      null -> {
        tracker = VelocityTracker.obtain()
        mVelocityTracker = tracker
        tracker
      }
      else -> tracker
    }
  }

  /**
   * compute finish duration.
   *
   * @param ev       up event.
   * @param velocity velocity x.
   * @return finish duration.
   */
  private fun getSwipeDuration(ev: MotionEvent, velocity: Int): Int {
    val swipeCurrent = mSwipeCurrentHorizontal ?: return 0
    val sx = scrollX
    val dx = (ev.x - sx).toInt()
    val width = swipeCurrent.menuWidth
    val halfWidth = width / 2
    val distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width)
    val distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio)
    var duration: Int

    duration = when {
      velocity > 0 -> 4 * Math.round(1000 * Math.abs(distance / velocity))
      else -> {
        val pageDelta = Math.abs(dx).toFloat() / width
        ((pageDelta + 1) * 100).toInt()
      }
    }
    duration = Math.min(duration, mScrollerDuration)
    return duration
  }

  internal fun distanceInfluenceForSnapDuration(f: Float): Float {
    var result = f
    result -= 0.5f // center the values about 0.
    result *= (0.3f * Math.PI / 2.0f).toFloat()
    return Math.sin(result.toDouble()).toFloat()
  }

  private fun judgeOpenClose(dx: Int, dy: Int) {
    val swipeCurrent = mSwipeCurrentHorizontal ?: return

    if (Math.abs(scrollX) >= swipeCurrent.menuView.width * openPercent) { // auto open
      if (Math.abs(dx) > mScaledTouchSlop || Math.abs(dy) > mScaledTouchSlop) { // swipe up
        when {
          isMenuOpenNotEqual -> smoothCloseMenu()
          else -> smoothOpenMenu()
        }

      } else { // normal up
        when {
          isMenuOpen -> smoothCloseMenu()
          else -> smoothOpenMenu()
        }
      }
    } else { // auto closeMenu
      smoothCloseMenu()
    }
  }

  override fun scrollTo(x: Int, y: Int) {
    val swipeCurrent = mSwipeCurrentHorizontal

    if (swipeCurrent == null) {
      super.scrollTo(x, y)

    } else {
      val checker = swipeCurrent.checkXY(x, y)
      shouldResetSwipe = checker.shouldResetSwipe

      if (checker.x != scrollX) {
        super.scrollTo(checker.x, checker.y)
      }
    }
  }

  override fun computeScroll() {
    val swipeCurrent = mSwipeCurrentHorizontal ?: return

    if (mScroller.computeScrollOffset()) {
      if (swipeCurrent is SwipeRightHorizontal) {
        scrollTo(Math.abs(mScroller.currX), 0)
        invalidate()
      } else {
        scrollTo(-Math.abs(mScroller.currX), 0)
        invalidate()
      }
    }
  }

  fun hasLeftMenu(): Boolean {
    return mSwipeLeftHorizontal?.canSwipe() ?: false
  }

  fun hasRightMenu(): Boolean {
    return mSwipeRightHorizontal?.canSwipe() ?: false
  }

  override fun smoothOpenMenu() {
    smoothOpenMenu(mScrollerDuration)
  }

  override fun smoothOpenLeftMenu() {
    smoothOpenLeftMenu(mScrollerDuration)
  }

  override fun smoothOpenRightMenu() {
    smoothOpenRightMenu(mScrollerDuration)
  }

  override fun smoothOpenLeftMenu(duration: Int) {
    val swipeMenu = mSwipeLeftHorizontal ?: return
    mSwipeCurrentHorizontal = swipeMenu
    smoothOpenMenu(duration)
  }

  override fun smoothOpenRightMenu(duration: Int) {
    val swipeMenu = mSwipeRightHorizontal ?: return
    mSwipeCurrentHorizontal = swipeMenu
    smoothOpenMenu(duration)
  }

  private fun smoothOpenMenu(duration: Int) {
    val swipeMenu = mSwipeCurrentHorizontal ?: return
    swipeMenu.autoOpenMenu(mScroller, scrollX, duration)
    invalidate()
  }

  override fun smoothCloseMenu() {
    smoothCloseMenu(mScrollerDuration)
  }

  override fun smoothCloseLeftMenu() {
    val swipeMenu = mSwipeLeftHorizontal ?: return
    mSwipeCurrentHorizontal = swipeMenu
    smoothCloseMenu()
  }

  override fun smoothCloseRightMenu() {
    val swipeMenu = mSwipeRightHorizontal ?: return
    mSwipeCurrentHorizontal = swipeMenu
    smoothCloseMenu()
  }

  override fun smoothCloseMenu(duration: Int) {
    val swipeMenu = mSwipeCurrentHorizontal ?: return
    swipeMenu.autoCloseMenu(mScroller, scrollX, duration)
    invalidate()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    var contentViewHeight = 0
    val contentView = mContentView
    val swipeLeft = mSwipeLeftHorizontal
    val swipeRight = mSwipeRightHorizontal

    if (contentView != null) {
      measureChildWithMargins(contentView, widthMeasureSpec, 0, heightMeasureSpec, 0)
      contentViewHeight = contentView.measuredHeight
    }

    if (swipeLeft != null) {
      val leftMenu = swipeLeft.menuView
      val menuViewHeight = if (contentViewHeight == 0) leftMenu.measuredHeightAndState else contentViewHeight

      val menuWidthSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.AT_MOST)
      val menuHeightSpec = View.MeasureSpec.makeMeasureSpec(menuViewHeight, View.MeasureSpec.EXACTLY)
      leftMenu.measure(menuWidthSpec, menuHeightSpec)
    }

    if (swipeRight != null) {
      val rightMenu = swipeRight.menuView
      val menuViewHeight = if (contentViewHeight == 0) rightMenu.measuredHeightAndState else contentViewHeight

      val menuWidthSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.AT_MOST)
      val menuHeightSpec = View.MeasureSpec.makeMeasureSpec(menuViewHeight, View.MeasureSpec.EXACTLY)
      rightMenu.measure(menuWidthSpec, menuHeightSpec)
    }

    if (contentViewHeight > 0) {
      setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), contentViewHeight)
    }
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val contentViewHeight: Int
    val contentView = mContentView
    val swipeLeft = mSwipeLeftHorizontal
    val swipeRight = mSwipeRightHorizontal

    if (contentView != null) {
      val contentViewWidth = contentView.measuredWidthAndState
      contentViewHeight = contentView.measuredHeightAndState

      val lp = contentView.layoutParams as FrameLayout.LayoutParams
      val start = paddingLeft
      val top = paddingTop + lp.topMargin

      contentView.layout(start, top, start + contentViewWidth, top + contentViewHeight)
    }

    if (swipeLeft != null) {
      val leftMenu = swipeLeft.menuView
      val menuViewWidth = leftMenu.measuredWidthAndState
      val menuViewHeight = leftMenu.measuredHeightAndState
      val lp = leftMenu.layoutParams as FrameLayout.LayoutParams
      val top = paddingTop + lp.topMargin

      leftMenu.layout(-menuViewWidth, top, 0, top + menuViewHeight)
    }

    if (swipeRight != null) {
      val rightMenu = swipeRight.menuView
      val menuViewWidth = rightMenu.measuredWidthAndState
      val menuViewHeight = rightMenu.measuredHeightAndState
      val lp = rightMenu.layoutParams as FrameLayout.LayoutParams
      val top = paddingTop + lp.topMargin

      val parentViewWidth = measuredWidthAndState
      rightMenu.layout(parentViewWidth, top, parentViewWidth + menuViewWidth, top + menuViewHeight)
    }
  }

  companion object {

    val DEFAULT_SCROLLER_DURATION = 200
  }

}
