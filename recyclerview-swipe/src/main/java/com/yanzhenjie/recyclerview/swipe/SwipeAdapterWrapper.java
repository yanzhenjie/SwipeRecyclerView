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
package com.yanzhenjie.recyclerview.swipe;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by YanZhenjie on 2017/7/20.
 */
public class SwipeAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mAdapter;
    private LayoutInflater mInflater;

    private SwipeMenuCreator mSwipeMenuCreator;
    private SwipeMenuItemClickListener mSwipeMenuItemClickListener;
    private SwipeItemClickListener mSwipeItemClickListener;

    SwipeAdapterWrapper(Context context, RecyclerView.Adapter adapter) {
        this.mInflater = LayoutInflater.from(context);
        this.mAdapter = adapter;
    }

    public RecyclerView.Adapter getOriginAdapter() {
        return mAdapter;
    }

    /**
     * Set to create menu listener.
     *
     * @param swipeMenuCreator listener.
     */
    void setSwipeMenuCreator(SwipeMenuCreator swipeMenuCreator) {
        this.mSwipeMenuCreator = swipeMenuCreator;
    }

    /**
     * Set to click menu listener.
     *
     * @param swipeMenuItemClickListener listener.
     */
    void setSwipeMenuItemClickListener(SwipeMenuItemClickListener swipeMenuItemClickListener) {
        this.mSwipeMenuItemClickListener = swipeMenuItemClickListener;
    }

    void setSwipeItemClickListener(SwipeItemClickListener swipeItemClickListener) {
        mSwipeItemClickListener = swipeItemClickListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderItemCount() + getContentItemCount() + getFooterItemCount();
    }

    private int getContentItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterView(position)) {
            return mFootViews.keyAt(position - getHeaderItemCount() - getContentItemCount());
        }
        return mAdapter.getItemViewType(position - getHeaderItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return new ViewHolder(mHeaderViews.get(viewType));
        } else if (mFootViews.get(viewType) != null) {
            return new ViewHolder(mFootViews.get(viewType));
        }
        final RecyclerView.ViewHolder viewHolder = mAdapter.onCreateViewHolder(parent, viewType);

        if (mSwipeItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
                }
            });
        }

        if (mSwipeMenuCreator == null) return viewHolder;

        final SwipeMenuLayout swipeMenuLayout = (SwipeMenuLayout) mInflater.inflate(R.layout.recycler_swipe_view_item, parent, false);
        SwipeMenu swipeLeftMenu = new SwipeMenu(swipeMenuLayout, viewType);
        SwipeMenu swipeRightMenu = new SwipeMenu(swipeMenuLayout, viewType);

        mSwipeMenuCreator.onCreateMenu(swipeLeftMenu, swipeRightMenu, viewType);

        int leftMenuCount = swipeLeftMenu.getMenuItems().size();
        if (leftMenuCount > 0) {
            SwipeMenuView swipeLeftMenuView = (SwipeMenuView) swipeMenuLayout.findViewById(R.id.swipe_left);
            // noinspection WrongConstant
            swipeLeftMenuView.setOrientation(swipeLeftMenu.getOrientation());
            swipeLeftMenuView.createMenu(swipeLeftMenu, swipeMenuLayout, mSwipeMenuItemClickListener, SwipeMenuRecyclerView.LEFT_DIRECTION);
        }

        int rightMenuCount = swipeRightMenu.getMenuItems().size();
        if (rightMenuCount > 0) {
            SwipeMenuView swipeRightMenuView = (SwipeMenuView) swipeMenuLayout.findViewById(R.id.swipe_right);
            // noinspection WrongConstant
            swipeRightMenuView.setOrientation(swipeRightMenu.getOrientation());
            swipeRightMenuView.createMenu(swipeRightMenu, swipeMenuLayout, mSwipeMenuItemClickListener, SwipeMenuRecyclerView.RIGHT_DIRECTION);
        }

        ViewGroup viewGroup = (ViewGroup) swipeMenuLayout.findViewById(R.id.swipe_content);
        viewGroup.addView(viewHolder.itemView);

        try {
            Field itemView = getSupperClass(viewHolder.getClass()).getDeclaredField("itemView");
            if (!itemView.isAccessible()) itemView.setAccessible(true);
            itemView.set(viewHolder, swipeMenuLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewHolder;
    }

    private Class<?> getSupperClass(Class<?> aClass) {
        Class<?> supperClass = aClass.getSuperclass();
        if (supperClass != null && !supperClass.equals(Object.class)) {
            return getSupperClass(supperClass);
        }
        return aClass;
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (isHeaderView(position) || isFooterView(position)) {
            return;
        }

        View itemView = holder.itemView;
        if (itemView instanceof SwipeMenuLayout) {
            SwipeMenuLayout swipeMenuLayout = (SwipeMenuLayout) itemView;
            int childCount = swipeMenuLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = swipeMenuLayout.getChildAt(i);
                if (childView instanceof SwipeMenuView) {
                    ((SwipeMenuView) childView).bindViewHolder(holder);
                }
            }
        }

        mAdapter.onBindViewHolder(holder, position - getHeaderItemCount(), payloads);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (isHeaderView(position) || isFooterView(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        } else {
            mAdapter.onViewAttachedToWindow(holder);
        }
    }

    public boolean isHeaderView(int position) {
        return position >= 0 && position < getHeaderItemCount();
    }

    public boolean isFooterView(int position) {
        return position >= getHeaderItemCount() + getContentItemCount();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(getHeaderItemCount() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addHeaderViewAndNotify(View view) {
        mHeaderViews.put(getHeaderItemCount() + BASE_ITEM_TYPE_HEADER, view);
        notifyItemInserted(getHeaderItemCount() - 1);
    }

    public void removeHeaderViewAndNotify(View view) {
        int headerIndex = mHeaderViews.indexOfValue(view);
        mHeaderViews.removeAt(headerIndex);
        notifyItemRemoved(headerIndex);
    }

    public void addFooterView(View view) {
        mFootViews.put(getFooterItemCount() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public void addFooterViewAndNotify(View view) {
        mFootViews.put(getFooterItemCount() + BASE_ITEM_TYPE_FOOTER, view);
        notifyItemInserted(getHeaderItemCount() + getContentItemCount() + getFooterItemCount() - 1);
    }

    public void removeFooterViewAndNotify(View view) {
        int footerIndex = mFootViews.indexOfValue(view);
        mFootViews.removeAt(footerIndex);
        notifyItemRemoved(getHeaderItemCount() + getContentItemCount() + footerIndex);
    }

    public int getHeaderItemCount() {
        return mHeaderViews.size();
    }

    public int getFooterItemCount() {
        return mFootViews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        mAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        if (!isHeaderView(position) && !isFooterView(position)) {
            return mAdapter.getItemId(position);
        }
        return super.getItemId(position);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (!isHeaderView(position) && !isFooterView(position))
            mAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (!isHeaderView(position) && !isFooterView(position))
            return mAdapter.onFailedToRecycleView(holder);
        return false;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (!isHeaderView(position) && !isFooterView(position))
            mAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mAdapter.onDetachedFromRecyclerView(recyclerView);
    }
}