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
package com.yanzhenjie.recyclerview.swipe.sample.activity.menu;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * <p>
 * GridView形式的侧滑菜单。
 * </p>
 * Created by YanZhenjie on 2017/7/21.
 */
public class GridActivity extends ListActivity {

    /**
     * 没错，仅仅是改变RecyclerView的LayoutManager而已。
     */
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        if (mGridLayoutManager == null)
            mGridLayoutManager = new GridLayoutManager(this, 2);
        return mGridLayoutManager;
    }
}
