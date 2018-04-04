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
package com.yanzhenjie.recyclerview.swipe.sample.activity.menu

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.yanzhenjie.recyclerview.swipe.SwipeMenuLayout
import com.yanzhenjie.recyclerview.swipe.sample.R
import com.yanzhenjie.recyclerview.swipe.sample.activity.BaseActivity
import com.yanzhenjie.recyclerview.swipe.sample.adapter.BaseAdapter
import java.util.Collections.emptyList

/**
 *
 *
 * 利用SwipeMenuLayout自定义菜单。
 *
 * Created by Yan Zhenjie on 2016/8/4.
 */
class DefineActivity : BaseActivity() {

  private var mSwipeMenuLayout: SwipeMenuLayout? = null

  /**
   * 一般的点击事件。
   */
  private val xOnClickListener = View.OnClickListener { v ->
    val swipeLayout = mSwipeMenuLayout

    if (swipeLayout != null) {
      when {
        v.id == R.id.left_view -> {
          swipeLayout.smoothCloseMenu()// 关闭菜单。
          Toast.makeText(this@DefineActivity, "我是左面的", Toast.LENGTH_SHORT).show()
        }
        v.id == R.id.right_view -> {
          swipeLayout.smoothCloseMenu()// 关闭菜单。
          Toast.makeText(this@DefineActivity, "我是右面的", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  override fun getContentView(): Int {
    return R.layout.activity_menu_define
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mRecyclerView.adapter = mAdapter
    mSwipeMenuLayout = findViewById(R.id.swipe_layout) as SwipeMenuLayout

    val btnLeft = findViewById(R.id.left_view) as TextView
    val btnRight = findViewById(R.id.right_view) as TextView

    btnLeft.setOnClickListener(xOnClickListener)
    btnRight.setOnClickListener(xOnClickListener)
  }

  override fun createAdapter(): BaseAdapter<*> {
    return DefineAdapter(this)
  }

  /**
   * 就是这个适配器的Item的Layout需要处理，其实和CardView的方式一模一样。
   */
  private class DefineAdapter internal constructor(context: Context) : BaseAdapter<ViewHolder>(context) {

    override fun notifyDataSetChanged(dataList: List<String>) {}

    override fun getItemCount(): Int {
      return 100
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu_define, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
  }

  internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var mLeftBtn: Button = itemView.findViewById(R.id.left_view) as Button
    private var mMiddleBtn: Button = itemView.findViewById(R.id.btn_start) as Button
    private var mRightBtn: Button = itemView.findViewById(R.id.right_view) as Button

    init {
      mLeftBtn.setOnClickListener(this)
      mMiddleBtn.setOnClickListener(this)
      mRightBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
      when (v.id) {
        R.id.left_view -> Toast.makeText(v.context, "我是第" + adapterPosition + "个Item的左边的Button", Toast.LENGTH_SHORT).show()
        R.id.btn_start -> Toast.makeText(v.context, "我是第" + adapterPosition + "个Item的中间的Button", Toast.LENGTH_SHORT).show()
        R.id.right_view -> Toast.makeText(v.context, "我是第" + adapterPosition + "个Item的右边的Button", Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun onItemClick(itemView: View, position: Int) {
    Toast.makeText(this, "第" + position + "个", Toast.LENGTH_SHORT).show()
  }

  override fun createDataList(): List<String> {
    return emptyList()
  }
}