/*
 * AUTHOR：YanZhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.recyclerview.swipe.widget

import android.support.annotation.ColorInt

/**
 * Grid item decoration.
 *
 * Created by YanZhenjie on 2017/7/6.
 */
@Deprecated("use {@link DefaultItemDecoration} instead.")
class GridItemDecoration : DefaultItemDecoration {

  constructor(@ColorInt color: Int) : super(color)

  constructor(@ColorInt color: Int, dividerWidth: Int, dividerHeight: Int, vararg excludeViewType: Int) : super(color, dividerWidth, dividerHeight, *excludeViewType)

}
