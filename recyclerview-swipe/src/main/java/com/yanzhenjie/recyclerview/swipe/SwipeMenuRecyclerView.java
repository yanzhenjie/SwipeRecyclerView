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
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yanzhenjie.recyclerview.swipe.touch.DefaultItemTouchHelper;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMovementListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultLoadMoreView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan Zhenjie on 2016/7/27.
 */
public class SwipeMenuRecyclerView extends RecyclerView {

    /**
     * Left menu.
     */
    public static final int LEFT_DIRECTION = 1;
    /**
     * Right menu.
     */
    public static final int RIGHT_DIRECTION = -1;

    @IntDef({LEFT_DIRECTION, RIGHT_DIRECTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DirectionMode {
    }

    /**
     * Invalid position.
     */
    private static final int INVALID_POSITION = -1;

    protected int mScaleTouchSlop;
    protected SwipeMenuLayout mOldSwipedLayout;
    protected int mOldTouchedPosition = INVALID_POSITION;

    private int mDownX;
    private int mDownY;

    private boolean allowSwipeDelete = false;

    private DefaultItemTouchHelper mDefaultItemTouchHelper;

    private SwipeMenuCreator mSwipeMenuCreator;
    private SwipeMenuItemClickListener mSwipeMenuItemClickListener;
    private SwipeItemClickListener mSwipeItemClickListener;

    private SwipeAdapterWrapper mAdapterWrapper;

    public SwipeMenuRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeMenuRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private void initializeItemTouchHelper() {
        if (mDefaultItemTouchHelper == null) {
            mDefaultItemTouchHelper = new DefaultItemTouchHelper();
            mDefaultItemTouchHelper.attachToRecyclerView(this);
        }
    }

    /**
     * Set OnItemMoveListener.
     *
     * @param onItemMoveListener {@link OnItemMoveListener}.
     */
    public void setOnItemMoveListener(OnItemMoveListener onItemMoveListener) {
        initializeItemTouchHelper();
        this.mDefaultItemTouchHelper.setOnItemMoveListener(onItemMoveListener);
    }

    /**
     * Set OnItemMovementListener.
     *
     * @param onItemMovementListener {@link OnItemMovementListener}.
     */
    public void setOnItemMovementListener(OnItemMovementListener onItemMovementListener) {
        initializeItemTouchHelper();
        this.mDefaultItemTouchHelper.setOnItemMovementListener(onItemMovementListener);
    }

    /**
     * Set OnItemStateChangedListener.
     *
     * @param onItemStateChangedListener {@link OnItemStateChangedListener}.
     */
    public void setOnItemStateChangedListener(OnItemStateChangedListener onItemStateChangedListener) {
        initializeItemTouchHelper();
        this.mDefaultItemTouchHelper.setOnItemStateChangedListener(onItemStateChangedListener);
    }

    /**
     * Set can long press drag.
     *
     * @param canDrag drag true, otherwise is can't.
     */
    public void setLongPressDragEnabled(boolean canDrag) {
        initializeItemTouchHelper();
        this.mDefaultItemTouchHelper.setLongPressDragEnabled(canDrag);
    }

    /**
     * Get can long press drag.
     *
     * @return drag true, otherwise is can't.
     */
    public boolean isLongPressDragEnabled() {
        initializeItemTouchHelper();
        return this.mDefaultItemTouchHelper.isLongPressDragEnabled();
    }


    /**
     * Set can swipe delete.
     *
     * @param canSwipe swipe true, otherwise is can't.
     */
    public void setItemViewSwipeEnabled(boolean canSwipe) {
        initializeItemTouchHelper();
        allowSwipeDelete = canSwipe; // swipe and menu conflict.
        this.mDefaultItemTouchHelper.setItemViewSwipeEnabled(canSwipe);
    }

    /**
     * Get can long press swipe.
     *
     * @return swipe true, otherwise is can't.
     */
    public boolean isItemViewSwipeEnabled() {
        initializeItemTouchHelper();
        return this.mDefaultItemTouchHelper.isItemViewSwipeEnabled();
    }

    /**
     * Start drag a item.
     *
     * @param viewHolder the ViewHolder to start dragging. It must be a direct child of RecyclerView.
     */
    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        initializeItemTouchHelper();
        this.mDefaultItemTouchHelper.startDrag(viewHolder);
    }

    /**
     * Star swipe a item.
     *
     * @param viewHolder the ViewHolder to start swiping. It must be a direct child of RecyclerView.
     */
    public void startSwipe(RecyclerView.ViewHolder viewHolder) {
        initializeItemTouchHelper();
        this.mDefaultItemTouchHelper.startSwipe(viewHolder);
    }

    /**
     * Check the Adapter and throw an exception if it already exists.
     */
    private void checkAdapterExist(String message) {
        if (mAdapterWrapper != null)
            throw new IllegalStateException(message);
    }

    /**
     * Set item click listener.
     */
    public void setSwipeItemClickListener(SwipeItemClickListener itemClickListener) {
        if (itemClickListener == null) return;
        checkAdapterExist("Cannot set item click listener, setAdapter has already been called.");
        this.mSwipeItemClickListener = new ItemClick(this, itemClickListener);
    }

    private static class ItemClick implements SwipeItemClickListener {

        private SwipeMenuRecyclerView mRecyclerView;
        private SwipeItemClickListener mCallback;

        public ItemClick(SwipeMenuRecyclerView recyclerView, SwipeItemClickListener callback) {
            this.mRecyclerView = recyclerView;
            this.mCallback = callback;
        }

        @Override
        public void onItemClick(View itemView, int position) {
            position = position - mRecyclerView.getHeaderItemCount();
            if (position >= 0)
                mCallback.onItemClick(itemView, position);
        }
    }

    /**
     * Set to create menu listener.
     */
    public void setSwipeMenuCreator(SwipeMenuCreator menuCreator) {
        if (menuCreator == null) return;
        checkAdapterExist("Cannot set menu creator, setAdapter has already been called.");
        this.mSwipeMenuCreator = menuCreator;
    }

    /**
     * Set to click menu listener.
     */
    public void setSwipeMenuItemClickListener(SwipeMenuItemClickListener menuItemClickListener) {
        if (menuItemClickListener == null) return;
        checkAdapterExist("Cannot set menu item click listener, setAdapter has already been called.");
        this.mSwipeMenuItemClickListener = new MenuItemClick(this, menuItemClickListener);
    }

    private static class MenuItemClick implements SwipeMenuItemClickListener {

        private SwipeMenuRecyclerView mRecyclerView;
        private SwipeMenuItemClickListener mCallback;

        public MenuItemClick(SwipeMenuRecyclerView recyclerView, SwipeMenuItemClickListener callback) {
            this.mRecyclerView = recyclerView;
            this.mCallback = callback;
        }

        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            int position = menuBridge.getAdapterPosition();
            position = position - mRecyclerView.getHeaderItemCount();
            if (position >= 0) {
                menuBridge.mAdapterPosition = position;
                mCallback.onItemClick(menuBridge);
            }
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookupHolder = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mAdapterWrapper.isHeaderView(position) || mAdapterWrapper.isFooterView(position)) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookupHolder != null)
                        return spanSizeLookupHolder.getSpanSize(position - getHeaderItemCount());
                    return 1;
                }
            });
        }
        super.setLayoutManager(layoutManager);
    }

    /**
     * Get the original adapter.
     */
    public Adapter getOriginAdapter() {
        if (mAdapterWrapper == null) return null;
        return mAdapterWrapper.getOriginAdapter();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapterWrapper != null) {
            mAdapterWrapper.getOriginAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
        }

        if (adapter == null) {
            mAdapterWrapper = null;
        } else {
            adapter.registerAdapterDataObserver(mAdapterDataObserver);

            mAdapterWrapper = new SwipeAdapterWrapper(getContext(), adapter);
            mAdapterWrapper.setSwipeItemClickListener(mSwipeItemClickListener);
            mAdapterWrapper.setSwipeMenuCreator(mSwipeMenuCreator);
            mAdapterWrapper.setSwipeMenuItemClickListener(mSwipeMenuItemClickListener);

            if (mHeaderViewList.size() > 0) {
                for (View view : mHeaderViewList) {
                    mAdapterWrapper.addHeaderView(view);
                }
            }
            if (mFooterViewList.size() > 0) {
                for (View view : mFooterViewList) {
                    mAdapterWrapper.addFooterView(view);
                }
            }
        }
        super.setAdapter(mAdapterWrapper);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mAdapterWrapper != null) {
            mAdapterWrapper.getOriginAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        super.onDetachedFromWindow();
    }

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mAdapterWrapper.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            fromPosition += getHeaderItemCount();
            toPosition += getHeaderItemCount();
            mAdapterWrapper.notifyItemMoved(fromPosition, toPosition);
        }
    };

    private List<View> mHeaderViewList = new ArrayList<>();
    private List<View> mFooterViewList = new ArrayList<>();

    /**
     * Add view at the headers.
     */
    public void addHeaderView(View view) {
        mHeaderViewList.add(view);
        if (mAdapterWrapper != null) {
            mAdapterWrapper.addHeaderViewAndNotify(view);
        }
    }

    /**
     * Remove view from header.
     */
    public void removeHeaderView(View view) {
        mHeaderViewList.remove(view);
        if (mAdapterWrapper != null) {
            mAdapterWrapper.removeHeaderViewAndNotify(view);
        }
    }

    /**
     * Add view at the footer.
     */
    public void addFooterView(View view) {
        mFooterViewList.add(view);
        if (mAdapterWrapper != null) {
            mAdapterWrapper.addFooterViewAndNotify(view);
        }
    }

    public void removeFooterView(View view) {
        mFooterViewList.remove(view);
        if (mAdapterWrapper != null) {
            mAdapterWrapper.removeFooterViewAndNotify(view);
        }
    }

    /**
     * Get size of headers.
     */
    public int getHeaderItemCount() {
        if (mAdapterWrapper == null) return 0;
        return mAdapterWrapper.getHeaderItemCount();
    }

    /**
     * Get size of footer.
     */
    public int getFooterItemCount() {
        if (mAdapterWrapper == null) return 0;
        return mAdapterWrapper.getFooterItemCount();
    }

    /**
     * Get ViewType of item.
     */
    public int getItemViewType(int position) {
        if (mAdapterWrapper == null) return 0;
        return mAdapterWrapper.getItemViewType(position);
    }

    /**
     * open menu on left.
     *
     * @param position position.
     */
    public void smoothOpenLeftMenu(int position) {
        smoothOpenMenu(position, LEFT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION);
    }

    /**
     * open menu on left.
     *
     * @param position position.
     * @param duration time millis.
     */
    public void smoothOpenLeftMenu(int position, int duration) {
        smoothOpenMenu(position, LEFT_DIRECTION, duration);
    }

    /**
     * open menu on right.
     *
     * @param position position.
     */
    public void smoothOpenRightMenu(int position) {
        smoothOpenMenu(position, RIGHT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION);
    }

    /**
     * open menu on right.
     *
     * @param position position.
     * @param duration time millis.
     */
    public void smoothOpenRightMenu(int position, int duration) {
        smoothOpenMenu(position, RIGHT_DIRECTION, duration);
    }

    /**
     * open menu.
     *
     * @param position  position.
     * @param direction use {@link #LEFT_DIRECTION}, {@link #RIGHT_DIRECTION}.
     * @param duration  time millis.
     */
    public void smoothOpenMenu(int position, @DirectionMode int direction, int duration) {
        if (mOldSwipedLayout != null) {
            if (mOldSwipedLayout.isMenuOpen()) {
                mOldSwipedLayout.smoothCloseMenu();
            }
        }
        position += getHeaderItemCount();
        ViewHolder vh = findViewHolderForAdapterPosition(position);
        if (vh != null) {
            View itemView = getSwipeMenuView(vh.itemView);
            if (itemView instanceof SwipeMenuLayout) {
                mOldSwipedLayout = (SwipeMenuLayout) itemView;
                if (direction == RIGHT_DIRECTION) {
                    mOldTouchedPosition = position;
                    mOldSwipedLayout.smoothOpenRightMenu(duration);
                } else if (direction == LEFT_DIRECTION) {
                    mOldTouchedPosition = position;
                    mOldSwipedLayout.smoothOpenLeftMenu(duration);
                }
            }
        }
    }

    /**
     * Close menu.
     */
    public void smoothCloseMenu() {
        if (mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen()) {
            mOldSwipedLayout.smoothCloseMenu();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean isIntercepted = super.onInterceptTouchEvent(e);
        if (allowSwipeDelete)  // swipe and menu conflict.
            return isIntercepted;
        else {
            if (e.getPointerCount() > 1) return true;
            int action = e.getAction();
            int x = (int) e.getX();
            int y = (int) e.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mDownX = x;
                    mDownY = y;
                    isIntercepted = false;

                    int touchingPosition = getChildAdapterPosition(findChildViewUnder(x, y));
                    if (touchingPosition != mOldTouchedPosition && mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen()) {
                        mOldSwipedLayout.smoothCloseMenu();
                        isIntercepted = true;
                    }

                    if (isIntercepted) {
                        mOldSwipedLayout = null;
                        mOldTouchedPosition = INVALID_POSITION;
                    } else {
                        ViewHolder vh = findViewHolderForAdapterPosition(touchingPosition);
                        if (vh != null) {
                            View itemView = getSwipeMenuView(vh.itemView);
                            if (itemView instanceof SwipeMenuLayout) {
                                mOldSwipedLayout = (SwipeMenuLayout) itemView;
                                mOldTouchedPosition = touchingPosition;
                            }
                        }
                    }
                    break;
                }
                // They are sensitive to retain sliding and inertia.
                case MotionEvent.ACTION_MOVE: {
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    if (mOldSwipedLayout == null) break;
                    ViewParent viewParent = getParent();
                    if (viewParent == null) break;

                    int disX = mDownX - x;
                    // 向左滑，显示右侧菜单，或者关闭左侧菜单。
                    boolean showRightCloseLeft = disX > 0 && (mOldSwipedLayout.hasRightMenu() || mOldSwipedLayout.isLeftCompleteOpen());
                    // 向右滑，显示左侧菜单，或者关闭右侧菜单。
                    boolean showLeftCloseRight = disX < 0 && (mOldSwipedLayout.hasLeftMenu() || mOldSwipedLayout.isRightCompleteOpen());
                    viewParent.requestDisallowInterceptTouchEvent(showRightCloseLeft || showLeftCloseRight);
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    break;
                }
            }
        }
        return isIntercepted;
    }

    private boolean handleUnDown(int x, int y, boolean defaultValue) {
        int disX = mDownX - x;
        int disY = mDownY - y;

        // swipe
        if (Math.abs(disX) > mScaleTouchSlop && Math.abs(disX) > Math.abs(disY))
            return false;
        // click
        if (Math.abs(disY) < mScaleTouchSlop && Math.abs(disX) < mScaleTouchSlop)
            return false;
        return defaultValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen()) {
                    mOldSwipedLayout.smoothCloseMenu();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(e);
    }

    private View getSwipeMenuView(View itemView) {
        if (itemView instanceof SwipeMenuLayout) return itemView;
        List<View> unvisited = new ArrayList<>();
        unvisited.add(itemView);
        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            if (!(child instanceof ViewGroup)) { // view
                continue;
            }
            if (child instanceof SwipeMenuLayout) return child;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }
        return itemView;
    }

    private int mScrollState = -1;

    private boolean isLoadMore = false;
    private boolean isAutoLoadMore = true;
    private boolean isLoadError = false;

    private boolean mDataEmpty = true;
    private boolean mHasMore = false;

    private LoadMoreView mLoadMoreView;
    private LoadMoreListener mLoadMoreListener;

    @Override
    public void onScrollStateChanged(int state) {
        this.mScrollState = state;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

            int itemCount = layoutManager.getItemCount();
            if (itemCount <= 0) return;

            int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

            if (itemCount == lastVisiblePosition + 1 &&
                    (mScrollState == SCROLL_STATE_DRAGGING || mScrollState == SCROLL_STATE_SETTLING)) {
                dispatchLoadMore();
            }
        } else if (layoutManager != null && layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;

            int itemCount = layoutManager.getItemCount();
            if (itemCount <= 0) return;

            int[] lastVisiblePositionArray = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
            int lastVisiblePosition = lastVisiblePositionArray[lastVisiblePositionArray.length - 1];

            if (itemCount == lastVisiblePosition + 1 &&
                    (mScrollState == SCROLL_STATE_DRAGGING || mScrollState == SCROLL_STATE_SETTLING)) {
                dispatchLoadMore();
            }
        }
    }

    private void dispatchLoadMore() {
        if (isLoadError) return;

        if (!isAutoLoadMore) {
            if (mLoadMoreView != null)
                mLoadMoreView.onWaitToLoadMore(mLoadMoreListener);
        } else {
            if (isLoadMore || mDataEmpty || !mHasMore) return;

            isLoadMore = true;

            if (mLoadMoreView != null)
                mLoadMoreView.onLoading();

            if (mLoadMoreListener != null)
                mLoadMoreListener.onLoadMore();
        }
    }

    /**
     * Use the default to load more View.
     */
    public void useDefaultLoadMore() {
        DefaultLoadMoreView defaultLoadMoreView = new DefaultLoadMoreView(getContext());
        addFooterView(defaultLoadMoreView);
        setLoadMoreView(defaultLoadMoreView);
    }

    /**
     * Load more view.
     */
    public void setLoadMoreView(LoadMoreView loadMoreView) {
        mLoadMoreView = loadMoreView;
    }

    /**
     * Load more listener.
     */
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    /**
     * Automatically load more automatically.
     * <p>
     * Non-auto-loading mode, you can to click on the item to load.
     * </p>
     *
     * @param autoLoadMore you can use false.
     * @see LoadMoreView#onWaitToLoadMore(LoadMoreListener)
     */
    public void setAutoLoadMore(boolean autoLoadMore) {
        isAutoLoadMore = autoLoadMore;
    }

    /**
     * Load more done.
     *
     * @param dataEmpty data is empty ?
     * @param hasMore   has more data ?
     */
    public final void loadMoreFinish(boolean dataEmpty, boolean hasMore) {
        isLoadMore = false;
        isLoadError = false;

        mDataEmpty = dataEmpty;
        mHasMore = hasMore;

        if (mLoadMoreView != null) {
            mLoadMoreView.onLoadFinish(dataEmpty, hasMore);
        }
    }

    /**
     * Called when data is loaded incorrectly.
     *
     * @param errorCode    Error code, will be passed to the LoadView, you can according to it to customize the prompt information.
     * @param errorMessage Error message.
     */
    public void loadMoreError(int errorCode, String errorMessage) {
        isLoadMore = false;
        isLoadError = true;

        if (mLoadMoreView != null) {
            mLoadMoreView.onLoadError(errorCode, errorMessage);
        }
    }

    public interface LoadMoreView {

        /**
         * Show progress.
         */
        void onLoading();

        /**
         * Load finish, handle result.
         */
        void onLoadFinish(boolean dataEmpty, boolean hasMore);

        /**
         * Non-auto-loading mode, you can to click on the item to load.
         */
        void onWaitToLoadMore(LoadMoreListener loadMoreListener);

        /**
         * Load error.
         */
        void onLoadError(int errorCode, String errorMessage);
    }

    public interface LoadMoreListener {

        /**
         * More data should be requested.
         */
        void onLoadMore();
    }

}
