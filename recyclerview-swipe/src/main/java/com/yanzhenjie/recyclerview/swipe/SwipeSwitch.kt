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

/**
 * Swipe switch.
 *
 * Created by Yan Zhenjie on 2016/7/27.
 */
interface SwipeSwitch {

  /**
   * The menu is open?
   *
   * @return true, otherwise false.
   */
  val isMenuOpen: Boolean

  /**
   * The menu is open on the left?
   *
   * @return true, otherwise false.
   */
  val isLeftMenuOpen: Boolean

  /**
   * The menu is open on the right?
   *
   * @return true, otherwise false.
   */
  val isRightMenuOpen: Boolean

  /**
   * The menu is completely open?
   *
   * @return true, otherwise false.
   */
  val isCompleteOpen: Boolean

  /**
   * The menu is completely open on the left?
   *
   * @return true, otherwise false.
   */
  val isLeftCompleteOpen: Boolean

  /**
   * The menu is completely open on the right?
   *
   * @return true, otherwise false.
   */
  val isRightCompleteOpen: Boolean

  /**
   * The menu is open?
   *
   * @return true, otherwise false.
   */
  val isMenuOpenNotEqual: Boolean

  /**
   * The menu is open on the left?
   *
   * @return true, otherwise false.
   */
  val isLeftMenuOpenNotEqual: Boolean

  /**
   * The menu is open on the right?
   *
   * @return true, otherwise false.
   */
  val isRightMenuOpenNotEqual: Boolean

  /**
   * Open the current menu.
   */
  fun smoothOpenMenu()

  /**
   * Open the menu on left.
   */
  fun smoothOpenLeftMenu()

  /**
   * Open the menu on right.
   */
  fun smoothOpenRightMenu()

  /**
   * Open the menu on left for the duration.
   *
   * @param duration duration time.
   */
  fun smoothOpenLeftMenu(duration: Int)

  /**
   * Open the menu on right for the duration.
   *
   * @param duration duration time.
   */
  fun smoothOpenRightMenu(duration: Int)

  // ---------- closeMenu. ---------- //

  /**
   * Smooth closed the menu.
   */
  fun smoothCloseMenu()

  /**
   * Smooth closed the menu on the left.
   */
  fun smoothCloseLeftMenu()

  /**
   * Smooth closed the menu on the right.
   */
  fun smoothCloseRightMenu()

  /**
   * Smooth closed the menu for the duration.
   *
   * @param duration duration time.
   */
  fun smoothCloseMenu(duration: Int)

}
