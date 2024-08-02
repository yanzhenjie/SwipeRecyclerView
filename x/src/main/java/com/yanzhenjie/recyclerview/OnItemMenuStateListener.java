/*
 * Copyright 2022 Lu Qiming
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
package com.yanzhenjie.recyclerview;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Qiming on 2022/5/7.
 */
public interface OnItemMenuStateListener {

    int OPEN = 1;
    int CLOSED = 2;

    /**
     * @param viewHolder the ViewHolder of the current item.
     * @param menuState the menu status.
     */
    void onMenuState(RecyclerView.ViewHolder viewHolder, int menuState);
}