package com.yanzhenjie.recyclerview.swipe;

/**
 * @author 1075209054@qq.com<br/>
 * CREATED 2018/9/3 13:49
 */
public interface SwipeItemIsShowCallback {
    /**
     * Control the item menu display
     *
     * @param position item pos
     * @return true display
     */
    boolean isShow(int position);
}
