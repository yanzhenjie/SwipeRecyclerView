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
package com.yanzhenjie.recyclerview.swipe.widget;

import android.support.annotation.ColorInt;

/**
 * <p>
 * RecyclerView's Item Split Line.
 * </p>
 * Created by Yan Zhenjie on 2016/7/27.
 *
 * @deprecated use {@link DefaultItemDecoration} instead.
 */
@Deprecated
public class ListItemDecoration extends DefaultItemDecoration {

    public ListItemDecoration(@ColorInt int color) {
        super(color);
    }

    public ListItemDecoration(@ColorInt int color, int dividerWidth, int dividerHeight, int... excludeViewType) {
        super(color, dividerWidth, dividerHeight, excludeViewType);
    }
}
