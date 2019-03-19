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
package com.yanzhenjie.recyclerview;

/**
 * Created by YanZhenjie on 2017/7/20.
 */
public class SwipeMenuBridge {

    private final Controller mController;
    private final int mDirection;
    private final int mPosition;

    public SwipeMenuBridge(Controller controller, int direction, int position) {
        this.mController = controller;
        this.mDirection = direction;
        this.mPosition = position;
    }

    @SwipeRecyclerView.DirectionMode
    public int getDirection() {
        return mDirection;
    }

    /**
     * Get the position of button in the menu.
     */
    public int getPosition() {
        return mPosition;
    }

    public void closeMenu() {
        mController.smoothCloseMenu();
    }
}