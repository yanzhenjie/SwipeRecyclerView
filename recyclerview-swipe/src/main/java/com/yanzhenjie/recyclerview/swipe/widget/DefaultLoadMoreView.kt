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
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.yanzhenjie.loading.LoadingView
import com.yanzhenjie.recyclerview.swipe.R
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView

/**
 * Default load more view.
 *
 * Created by YanZhenjie on 2017/7/21.
 */
class DefaultLoadMoreView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null) : LinearLayout(context, attrs), SwipeMenuRecyclerView.LoadMoreView, View.OnClickListener {

  private val loadingView: LoadingView
  private val messageView: TextView

  private var loadMoreListener: SwipeMenuRecyclerView.LoadMoreListener? = null

  init {
    layoutParams = ViewGroup.LayoutParams(-1, -2)
    gravity = Gravity.CENTER
    visibility = View.GONE

    val displayMetrics = resources.displayMetrics

    val minHeight = (displayMetrics.density * 60 + 0.5).toInt()
    minimumHeight = minHeight

    View.inflate(context, R.layout.recycler_swipe_view_load_more, this)
    loadingView = findViewById(R.id.loading_view) as LoadingView
    messageView = findViewById(R.id.tv_load_more_message) as TextView

    val color1 = ContextCompat.getColor(context, R.color.recycler_swipe_color_loading_color1)
    val color2 = ContextCompat.getColor(context, R.color.recycler_swipe_color_loading_color2)
    val color3 = ContextCompat.getColor(context, R.color.recycler_swipe_color_loading_color3)

    loadingView.setCircleColors(color1, color2, color3)

    setOnClickListener(this)
  }

  override fun onLoading() {
    visibility = View.VISIBLE
    loadingView.visibility = View.VISIBLE
    messageView.visibility = View.VISIBLE
    messageView.setText(R.string.recycler_swipe_load_more_message)
  }

  override fun onLoadFinish(dataEmpty: Boolean, hasMore: Boolean) {
    if (!hasMore) {
      visibility = View.VISIBLE

      if (dataEmpty) {
        loadingView.visibility = View.GONE
        messageView.visibility = View.VISIBLE
        messageView.setText(R.string.recycler_swipe_data_empty)
      } else {
        loadingView.visibility = View.GONE
        messageView.visibility = View.VISIBLE
        messageView.setText(R.string.recycler_swipe_more_not)
      }
    } else {
      visibility = View.INVISIBLE
    }
  }

  override fun onWaitToLoadMore(loadMoreListener: SwipeMenuRecyclerView.LoadMoreListener?) {
    this.loadMoreListener = loadMoreListener

    visibility = View.VISIBLE
    loadingView.visibility = View.GONE
    messageView.visibility = View.VISIBLE
    messageView.setText(R.string.recycler_swipe_click_load_more)
  }

  override fun onLoadError(errorCode: Int, errorMessage: String) {
    visibility = View.VISIBLE
    loadingView.visibility = View.GONE
    messageView.visibility = View.VISIBLE
    messageView.text = if (TextUtils.isEmpty(errorMessage)) context.getString(R.string.recycler_swipe_load_error) else errorMessage
  }

  override fun onClick(v: View) {
    loadMoreListener?.onLoadMore()
  }

}
