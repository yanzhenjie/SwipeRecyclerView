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

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

/**
 *
 * https://github.com/yanzhenjie/StickyScrollView
 * Created by YanZhenjie on 2017/7/20.
 */
class StickyNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = android.R.attr.scrollViewStyle) : NestedScrollView(context, attrs, defStyle) {

  private var stickyViews: ArrayList<View>? = null
  private var currentlyStickingView: View? = null
  private var stickyViewTopOffset: Float = 0.toFloat()
  private val invalidateRunnable = object : Runnable {
    override fun run() {
      if (currentlyStickingView != null) {
        val l = getLeftForViewRelativeOnlyChild(currentlyStickingView)
        val t = getBottomForViewRelativeOnlyChild(currentlyStickingView)
        val r = getRightForViewRelativeOnlyChild(currentlyStickingView)
        val b = (scrollY + (currentlyStickingView!!.height + stickyViewTopOffset)).toInt()
        invalidate(l, t, r, b)
      }
      postDelayed(this, 16)
    }
  }
  private var stickyViewLeftOffset: Int = 0
  private var redirectTouchesToStickyView: Boolean = false
  private var clippingToPadding: Boolean = false
  private var clipToPaddingHasBeenSet: Boolean = false
  private var mShadowHeight = DEFAULT_SHADOW_HEIGHT
  private var mShadowDrawable: Drawable? = null
  private var hasNotDoneActionDown = true

  private var mOnViewStickyListeners: MutableList<OnViewStickyListener>? = null

  interface OnViewStickyListener {
    fun onSticky(view: View?)
    fun onUnSticky(view: View)
  }

  init {
    setup()
  }

  fun addOnViewStickyListener(stickyListener: OnViewStickyListener) {
    var listeners = mOnViewStickyListeners

    if (listeners == null) {
      listeners = ArrayList()
      mOnViewStickyListeners = listeners
    }
    listeners.add(stickyListener)
  }

  fun removeOnViewStickyListener(stickyListener: OnViewStickyListener) {
    mOnViewStickyListeners?.remove(stickyListener)
  }

  fun clearOnViewStickyListener() {
    mOnViewStickyListeners?.clear()
  }

  fun setShadowHeight(height: Int) {
    mShadowHeight = height
  }

  fun setShadowDrawable(shadowDrawable: Drawable) {
    mShadowDrawable = shadowDrawable
  }

  fun setup() {
    stickyViews = ArrayList()
  }

  private fun getLeftForViewRelativeOnlyChild(view: View?) = getBoundaryOffsetForViewRelativeOnlyChild(view, { it.left })
  private fun getTopForViewRelativeOnlyChild(view: View?) = getBoundaryOffsetForViewRelativeOnlyChild(view, { it.top })
  private fun getRightForViewRelativeOnlyChild(view: View?) = getBoundaryOffsetForViewRelativeOnlyChild(view, { it.right })
  private fun getBottomForViewRelativeOnlyChild(view: View?) = getBoundaryOffsetForViewRelativeOnlyChild(view, { it.bottom })

  /**
   * Returns boundary offset for view relative only child.
   *
   * @param view view.
   * @param getBoundaryOffset get boundary offset: fun(view): boundaryOffset.
   */
  private fun getBoundaryOffsetForViewRelativeOnlyChild(view: View?, getBoundaryOffset: (View) -> Int): Int {
    var childView = view ?: return 0
    var childParent = childView.parent
    var childLeft = getBoundaryOffset(childView)

    while (childParent != null && childParent != getChildAt(0) && childParent is View) {
      childView = childParent
      childParent = childView.parent
      childLeft += getBoundaryOffset(childView)
    }
    return childLeft
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    super.onLayout(changed, l, t, r, b)
    if (!clipToPaddingHasBeenSet) {
      clippingToPadding = true
    }
    notifyHierarchyChanged()
  }

  override fun setClipToPadding(clipToPadding: Boolean) {
    super.setClipToPadding(clipToPadding)
    clippingToPadding = clipToPadding
    clipToPaddingHasBeenSet = true
  }

  override fun addView(child: View) {
    super.addView(child)
    findStickyViews(child)
  }

  override fun addView(child: View, index: Int) {
    super.addView(child, index)
    findStickyViews(child)
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
    super.addView(child, index, params)
    findStickyViews(child)
  }

  override fun addView(child: View, width: Int, height: Int) {
    super.addView(child, width, height)
    findStickyViews(child)
  }

  override fun addView(child: View, params: ViewGroup.LayoutParams) {
    super.addView(child, params)
    findStickyViews(child)
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    val stickingView = currentlyStickingView ?: return
    val shadowDrawable = mShadowDrawable

    canvas.save()
    canvas.translate(
        (paddingLeft + stickyViewLeftOffset).toFloat(),
        scrollY.toFloat()+ stickyViewTopOffset + (if (clippingToPadding) paddingTop else 0).toFloat())

    canvas.clipRect(
        0,
        if (clippingToPadding) -stickyViewTopOffset.toInt() else 0,
        width - stickyViewLeftOffset,
        stickingView.height + mShadowHeight + 1
    )
    if (shadowDrawable != null) {
      val left = 0
      val top = stickingView.height
      val right = stickingView.width
      val bottom = stickingView.height + mShadowHeight

      shadowDrawable.setBounds(left, top, right, bottom)
      shadowDrawable.draw(canvas)
    }

    canvas.clipRect(
        0,
        if (clippingToPadding) -stickyViewTopOffset.toInt() else 0,
        width,
        stickingView.height
    )
    if (getStringTagForView(stickingView).contains(FLAG_HASTRANSPARENCY)) {
      showView(stickingView)
      stickingView.draw(canvas)
      hideView(stickingView)
    } else {
      stickingView.draw(canvas)
    }
    canvas.restore()
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    val stickingView = currentlyStickingView

    if (ev.action == MotionEvent.ACTION_DOWN) {
      redirectTouchesToStickyView = true
    }
    if (redirectTouchesToStickyView) {
      redirectTouchesToStickyView = currentlyStickingView != null

      if (redirectTouchesToStickyView) {
        redirectTouchesToStickyView = stickingView != null && ev.y <= stickingView.height + stickyViewTopOffset &&
            ev.x >= getLeftForViewRelativeOnlyChild(currentlyStickingView) &&
            ev.x <= getRightForViewRelativeOnlyChild(currentlyStickingView)
      }
    } else if (currentlyStickingView == null) {
      redirectTouchesToStickyView = false
    }
    if (redirectTouchesToStickyView) {
      ev.offsetLocation(0f, -1 * (scrollY + stickyViewTopOffset - getTopForViewRelativeOnlyChild(currentlyStickingView)))
    }
    return super.dispatchTouchEvent(ev)
  }

  override fun onTouchEvent(ev: MotionEvent): Boolean {
    if (redirectTouchesToStickyView) {
      ev.offsetLocation(0f, scrollY + stickyViewTopOffset - getTopForViewRelativeOnlyChild(currentlyStickingView))
    }
    if (ev.action == MotionEvent.ACTION_DOWN) {
      hasNotDoneActionDown = false
    }
    if (hasNotDoneActionDown) {
      val down = MotionEvent.obtain(ev)
      down.action = MotionEvent.ACTION_DOWN
      super.onTouchEvent(down)
      hasNotDoneActionDown = false
    }
    if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
      hasNotDoneActionDown = true
    }
    return super.onTouchEvent(ev)
  }

  override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
    super.onScrollChanged(l, t, oldl, oldt)
    doTheStickyThing()
  }

  private fun doTheStickyThing() {
    var viewThatShouldStick: View? = null
    var approachingView: View? = null

    stickyViews?.forEach {
      val viewTop = getTopForViewRelativeOnlyChild(it) - scrollY + if (clippingToPadding) 0 else paddingTop

      if (viewTop <= 0) {
        if (viewThatShouldStick == null || viewTop > getTopForViewRelativeOnlyChild(viewThatShouldStick) - scrollY + if (clippingToPadding) 0 else paddingTop) {
          viewThatShouldStick = it
        }
      } else {
        if (approachingView == null || viewTop < getTopForViewRelativeOnlyChild(approachingView) - scrollY + if (clippingToPadding) 0 else paddingTop) {
          approachingView = it
        }
      }
    }
    val oldStickingView = currentlyStickingView
    val newStickingView = viewThatShouldStick

    if (newStickingView != null) {
      stickyViewTopOffset = if (approachingView == null) {
        0F
      } else {
        Math.min(0, getTopForViewRelativeOnlyChild(approachingView) - scrollY + if (clippingToPadding) 0 else paddingTop - newStickingView.height).toFloat()
      }

      if (newStickingView !== oldStickingView) {
        if (oldStickingView != null) {
          mOnViewStickyListeners?.forEach { it.onUnSticky(oldStickingView) }
          stopStickingCurrentlyStickingView()
        }
        // only compute the left offset when we start sticking.
        stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(newStickingView)
        startStickingView(newStickingView)

        mOnViewStickyListeners?.forEach { it.onSticky(newStickingView) }
      }
    } else if (oldStickingView != null) {
      mOnViewStickyListeners?.forEach { it.onUnSticky(oldStickingView) }
      stopStickingCurrentlyStickingView()
    }
  }

  private fun startStickingView(viewThatShouldStick: View?) {
    currentlyStickingView = viewThatShouldStick

    if (viewThatShouldStick != null) {
      if (getStringTagForView(viewThatShouldStick).contains(FLAG_HASTRANSPARENCY)) {
        hideView(viewThatShouldStick)
      }
      if (getStringTagForView(viewThatShouldStick).contains(FLAG_NONCONSTANT)) {
        post(invalidateRunnable)
      }
    }
  }

  private fun stopStickingCurrentlyStickingView() {
    if (getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARENCY)) {
      showView(currentlyStickingView)
    }
    currentlyStickingView = null
    removeCallbacks(invalidateRunnable)
  }

  override fun onDetachedFromWindow() {
    removeCallbacks(invalidateRunnable)
    super.onDetachedFromWindow()
  }

  /**
   * Notify that the sticky attribute has been added or removed from one or more views in the View hierarchy
   */
  fun notifyStickyAttributeChanged() {
    notifyHierarchyChanged()
  }

  private fun notifyHierarchyChanged() {
    if (currentlyStickingView != null) {
      stopStickingCurrentlyStickingView()
    }
    stickyViews?.clear()
    findStickyViews(getChildAt(0))
    doTheStickyThing()
    invalidate()
  }

  private fun findStickyViews(v: View) {
    if (!detainStickyView(v) && v is ViewGroup) {
      for (i in 0 until v.childCount) {
        findStickyViews(v.getChildAt(i))
      }
    }
  }

  private fun detainStickyView(view: View): Boolean {
    val tag = getStringTagForView(view)

    if (tag.contains(STICKY_TAG)) {
      stickyViews?.add(view)
      return true
    }
    return false
  }

  private fun getStringTagForView(v: View?): String {
    val tagObject = v?.tag ?: return ""
    return tagObject.toString()
  }

  private fun hideView(v: View) {
    v.alpha = 0f
  }

  private fun showView(v: View?) {
    v?.alpha = 1f
  }

  companion object {

    /**
     * Tag for views that should stick and have constant drawing. e.g. TextViews, ImageViews etc
     */
    const val STICKY_TAG = "sticky"

    /**
     * Flag for views that should stick and have non-constant drawing. e.g. Buttons, ProgressBars etc
     */
    const val FLAG_NONCONSTANT = "-nonconstant"

    /**
     * Flag for views that have aren't fully opaque
     */
    const val FLAG_HASTRANSPARENCY = "-hastransparency"

    /**
     * Default height of the shadow peeking out below the stuck view.
     */
    private const val DEFAULT_SHADOW_HEIGHT = 10 // dp;
  }

}