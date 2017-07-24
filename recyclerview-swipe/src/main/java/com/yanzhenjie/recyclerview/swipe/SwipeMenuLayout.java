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
package com.yanzhenjie.recyclerview.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.TextView;

/**
 * Created by Yan Zhenjie on 2016/7/27.
 */
public class SwipeMenuLayout extends FrameLayout implements SwipeSwitch {

    public static final int DEFAULT_SCROLLER_DURATION = 200;

    private int mLeftViewId = 0;
    private int mContentViewId = 0;
    private int mRightViewId = 0;

    private float mOpenPercent = 0.5f;
    private int mScrollerDuration = DEFAULT_SCROLLER_DURATION;

    private int mScaledTouchSlop;
    private int mLastX;
    private int mLastY;
    private int mDownX;
    private int mDownY;
    private View mContentView;
    private SwipeLeftHorizontal mSwipeLeftHorizontal;
    private SwipeRightHorizontal mSwipeRightHorizontal;
    private SwipeHorizontal mSwipeCurrentHorizontal;
    private boolean shouldResetSwipe;
    private boolean mDragging;
    private boolean swipeEnable = true;
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mScaledMinimumFlingVelocity;
    private int mScaledMaximumFlingVelocity;


    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.recycler_swipe_SwipeMenuLayout);
        mLeftViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_leftViewId, mLeftViewId);
        mContentViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_contentViewId, mContentViewId);
        mRightViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_rightViewId, mRightViewId);
        typedArray.recycle();

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mScaledTouchSlop = configuration.getScaledTouchSlop();
        mScaledMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mScaledMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        mScroller = new OverScroller(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mLeftViewId != 0 && mSwipeLeftHorizontal == null) {
            View view = findViewById(mLeftViewId);
            mSwipeLeftHorizontal = new SwipeLeftHorizontal(view);
        }
        if (mRightViewId != 0 && mSwipeRightHorizontal == null) {
            View view = findViewById(mRightViewId);
            mSwipeRightHorizontal = new SwipeRightHorizontal(view);
        }
        if (mContentViewId != 0 && mContentView == null) {
            mContentView = findViewById(mContentViewId);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(16);
            errorView.setText("You may not have set the ContentView.");
            mContentView = errorView;
            addView(mContentView);
        }
    }

    /**
     * Set whether open swipe. Default is true.
     *
     * @param swipeEnable true open, otherwise false.
     */
    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    /**
     * Open the swipe function of the Item?
     *
     * @return open is true, otherwise is false.
     */
    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    /**
     * Set open percentage.
     *
     * @param openPercent such as 0.5F.
     */
    public void setOpenPercent(float openPercent) {
        this.mOpenPercent = openPercent;
    }

    /**
     * Get open percentage.
     *
     * @return such as 0.5F.
     */
    public float getOpenPercent() {
        return mOpenPercent;
    }

    /**
     * The duration of the set.
     *
     * @param scrollerDuration such as 500.
     */
    public void setScrollerDuration(int scrollerDuration) {
        this.mScrollerDuration = scrollerDuration;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercepted = super.onInterceptTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = mLastX = (int) ev.getX();
                mDownY = (int) ev.getY();
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                int disX = (int) (ev.getX() - mDownX);
                int disY = (int) (ev.getY() - mDownY);
                boolean i = Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY);
                return i;
            }
            case MotionEvent.ACTION_UP: {
                boolean isClick = mSwipeCurrentHorizontal != null
                        && mSwipeCurrentHorizontal.isClickOnContentView(getWidth(), ev.getX());
                if (isMenuOpen() && isClick) {
                    smoothCloseMenu();
                    return true;
                }
                return false;
            }
            case MotionEvent.ACTION_CANCEL: {
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                return false;
            }
        }
        return isIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(ev);
        int dx;
        int dy;
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!isSwipeEnable()) break;
                int disX = (int) (mLastX - ev.getX());
                int disY = (int) (mLastY - ev.getY());
                if (!mDragging && Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY)) {
                    mDragging = true;
                }
                if (mDragging) {
                    if (mSwipeCurrentHorizontal == null || shouldResetSwipe) {
                        if (disX < 0) {
                            if (mSwipeLeftHorizontal != null)
                                mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
                            else
                                mSwipeCurrentHorizontal = mSwipeRightHorizontal;
                        } else {
                            if (mSwipeRightHorizontal != null)
                                mSwipeCurrentHorizontal = mSwipeRightHorizontal;
                            else
                                mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
                        }
                    }
                    scrollBy(disX, 0);
                    mLastX = (int) ev.getX();
                    mLastY = (int) ev.getY();
                    shouldResetSwipe = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                dx = (int) (mDownX - ev.getX());
                dy = (int) (mDownY - ev.getY());
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                int velocity = Math.abs(velocityX);
                if (velocity > mScaledMinimumFlingVelocity) {
                    if (mSwipeCurrentHorizontal != null) {
                        int duration = getSwipeDuration(ev, velocity);
                        if (mSwipeCurrentHorizontal instanceof SwipeRightHorizontal) {
                            if (velocityX < 0) {
                                smoothOpenMenu(duration);
                            } else {
                                smoothCloseMenu(duration);
                            }
                        } else {
                            if (velocityX > 0) {
                                smoothOpenMenu(duration);
                            } else {
                                smoothCloseMenu(duration);
                            }
                        }
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                } else {
                    judgeOpenClose(dx, dy);
                }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                if (Math.abs(mDownX - ev.getX()) > mScaledTouchSlop
                        || Math.abs(mDownY - ev.getY()) > mScaledTouchSlop
                        || isLeftMenuOpen()
                        || isRightMenuOpen()) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                } else {
                    dx = (int) (mDownX - ev.getX());
                    dy = (int) (mDownY - ev.getY());
                    judgeOpenClose(dx, dy);
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * compute finish duration.
     *
     * @param ev       up event.
     * @param velocity velocity x.
     * @return finish duration.
     */
    private int getSwipeDuration(MotionEvent ev, int velocity) {
        int sx = getScrollX();
        int dx = (int) (ev.getX() - sx);
        final int width = mSwipeCurrentHorizontal.getMenuWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
        final float distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(dx) / width;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, mScrollerDuration);
        return duration;
    }

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    private void judgeOpenClose(int dx, int dy) {
        if (mSwipeCurrentHorizontal != null) {
            if (Math.abs(getScrollX()) >= (mSwipeCurrentHorizontal.getMenuView().getWidth() * mOpenPercent)) { // auto open
                if (Math.abs(dx) > mScaledTouchSlop || Math.abs(dy) > mScaledTouchSlop) { // swipe up
                    if (isMenuOpenNotEqual()) smoothCloseMenu();
                    else smoothOpenMenu();
                } else { // normal up
                    if (isMenuOpen()) smoothCloseMenu();
                    else smoothOpenMenu();
                }
            } else { // auto closeMenu
                smoothCloseMenu();
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mSwipeCurrentHorizontal == null) {
            super.scrollTo(x, y);
        } else {
            SwipeHorizontal.Checker checker = mSwipeCurrentHorizontal.checkXY(x, y);
            shouldResetSwipe = checker.shouldResetSwipe;
            if (checker.x != getScrollX()) {
                super.scrollTo(checker.x, checker.y);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset() && mSwipeCurrentHorizontal != null) {
            if (mSwipeCurrentHorizontal instanceof SwipeRightHorizontal) {
                scrollTo(Math.abs(mScroller.getCurrX()), 0);
                invalidate();
            } else {
                scrollTo(-Math.abs(mScroller.getCurrX()), 0);
                invalidate();
            }
        }
    }

    public boolean hasLeftMenu() {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal.canSwipe();
    }

    public boolean hasRightMenu() {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal.canSwipe();
    }

    @Override
    public boolean isMenuOpen() {
        return isLeftMenuOpen() || isRightMenuOpen();
    }

    @Override
    public boolean isLeftMenuOpen() {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal.isMenuOpen(getScrollX());
    }

    @Override
    public boolean isRightMenuOpen() {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal.isMenuOpen(getScrollX());
    }

    @Override
    public boolean isCompleteOpen() {
        return isLeftCompleteOpen() || isRightMenuOpen();
    }

    @Override
    public boolean isLeftCompleteOpen() {
        return mSwipeLeftHorizontal != null && !mSwipeLeftHorizontal.isCompleteClose(getScrollX());
    }

    @Override
    public boolean isRightCompleteOpen() {
        return mSwipeRightHorizontal != null && !mSwipeRightHorizontal.isCompleteClose(getScrollX());
    }

    @Override
    public boolean isMenuOpenNotEqual() {
        return isLeftMenuOpenNotEqual() || isRightMenuOpenNotEqual();
    }

    @Override
    public boolean isLeftMenuOpenNotEqual() {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal.isMenuOpenNotEqual(getScrollX());
    }

    @Override
    public boolean isRightMenuOpenNotEqual() {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal.isMenuOpenNotEqual(getScrollX());
    }

    @Override
    public void smoothOpenMenu() {
        smoothOpenMenu(mScrollerDuration);
    }

    @Override
    public void smoothOpenLeftMenu() {
        smoothOpenLeftMenu(mScrollerDuration);
    }

    @Override
    public void smoothOpenRightMenu() {
        smoothOpenRightMenu(mScrollerDuration);
    }

    @Override
    public void smoothOpenLeftMenu(int duration) {
        if (mSwipeLeftHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
            smoothOpenMenu(duration);
        }
    }

    @Override
    public void smoothOpenRightMenu(int duration) {
        if (mSwipeRightHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeRightHorizontal;
            smoothOpenMenu(duration);
        }
    }

    private void smoothOpenMenu(int duration) {
        if (mSwipeCurrentHorizontal != null) {
            mSwipeCurrentHorizontal.autoOpenMenu(mScroller, getScrollX(), duration);
            invalidate();
        }
    }

    @Override
    public void smoothCloseMenu() {
        smoothCloseMenu(mScrollerDuration);
    }

    @Override
    public void smoothCloseLeftMenu() {
        if (mSwipeLeftHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
            smoothCloseMenu();
        }
    }

    @Override
    public void smoothCloseRightMenu() {
        if (mSwipeRightHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeRightHorizontal;
            smoothCloseMenu();
        }
    }

    @Override
    public void smoothCloseMenu(int duration) {
        if (mSwipeCurrentHorizontal != null) {
            mSwipeCurrentHorizontal.autoCloseMenu(mScroller, getScrollX(), duration);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int contentViewHeight = 0;

        if (mContentView != null) {
            measureChildWithMargins(mContentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            contentViewHeight = mContentView.getMeasuredHeight();
        }

        if (mSwipeLeftHorizontal != null) {
            View leftMenu = mSwipeLeftHorizontal.getMenuView();
            int menuViewHeight = contentViewHeight == 0 ? leftMenu.getMeasuredHeightAndState() : contentViewHeight;

            int menuWidthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
            int menuHeightSpec = MeasureSpec.makeMeasureSpec(menuViewHeight, MeasureSpec.EXACTLY);
            leftMenu.measure(menuWidthSpec, menuHeightSpec);
        }

        if (mSwipeRightHorizontal != null) {
            View rightMenu = mSwipeRightHorizontal.getMenuView();
            int menuViewHeight = contentViewHeight == 0 ? rightMenu.getMeasuredHeightAndState() : contentViewHeight;

            int menuWidthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
            int menuHeightSpec = MeasureSpec.makeMeasureSpec(menuViewHeight, MeasureSpec.EXACTLY);
            rightMenu.measure(menuWidthSpec, menuHeightSpec);
        }

        if (contentViewHeight > 0) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), contentViewHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int contentViewHeight;
        if (mContentView != null) {
            int contentViewWidth = mContentView.getMeasuredWidthAndState();
            contentViewHeight = mContentView.getMeasuredHeightAndState();
            LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
            int start = getPaddingLeft();
            int top = getPaddingTop() + lp.topMargin;
            mContentView.layout(start, top, start + contentViewWidth, top + contentViewHeight);
        }

        if (mSwipeLeftHorizontal != null) {
            View leftMenu = mSwipeLeftHorizontal.getMenuView();
            int menuViewWidth = leftMenu.getMeasuredWidthAndState();
            int menuViewHeight = leftMenu.getMeasuredHeightAndState();
            LayoutParams lp = (LayoutParams) leftMenu.getLayoutParams();
            int top = getPaddingTop() + lp.topMargin;
            leftMenu.layout(-menuViewWidth, top, 0, top + menuViewHeight);
        }

        if (mSwipeRightHorizontal != null) {
            View rightMenu = mSwipeRightHorizontal.getMenuView();
            int menuViewWidth = rightMenu.getMeasuredWidthAndState();
            int menuViewHeight = rightMenu.getMeasuredHeightAndState();
            LayoutParams lp = (LayoutParams) rightMenu.getLayoutParams();
            int top = getPaddingTop() + lp.topMargin;

            int parentViewWidth = getMeasuredWidthAndState();
            rightMenu.layout(parentViewWidth, top, parentViewWidth + menuViewWidth, top + menuViewHeight);
        }
    }

}
