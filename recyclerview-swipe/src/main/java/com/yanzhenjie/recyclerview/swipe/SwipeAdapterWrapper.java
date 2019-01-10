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

import static com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView.LEFT_DIRECTION;
import static com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView.RIGHT_DIRECTION;

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
    private SwipeItemLongClickListener mSwipeItemLongClickListener;

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
        this.mSwipeItemClickListener = swipeItemClickListener;
    }

    void setSwipeItemLongClickListener(SwipeItemLongClickListener swipeItemLongClickListener) {
        this.mSwipeItemLongClickListener = swipeItemLongClickListener;
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
        if (mSwipeItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mSwipeItemLongClickListener.onItemLongClick(v, viewHolder.getAdapterPosition());
                    return true;
                }
            });
        }

        if (mSwipeMenuCreator == null) return viewHolder;

        final View contentView = mInflater.inflate(R.layout.recycler_swipe_view_item, parent, false);
        ViewGroup viewGroup = contentView.findViewById(R.id.swipe_content);
        viewGroup.addView(viewHolder.itemView);

        try {
            Field itemView = getSupperClass(viewHolder.getClass()).getDeclaredField("itemView");
            if (!itemView.isAccessible()) itemView.setAccessible(true);
            itemView.set(viewHolder, contentView);
        } catch (Exception ignored) {
        }
        return viewHolder;
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (isHeaderOrFooter(holder)) return;

        View itemView = holder.itemView;
        position -= getHeaderItemCount();

        if (itemView instanceof SwipeMenuLayout && mSwipeMenuCreator != null) {
            SwipeMenuLayout menuLayout = (SwipeMenuLayout)itemView;
            SwipeMenu leftMenu = new SwipeMenu(menuLayout);
            SwipeMenu rightMenu = new SwipeMenu(menuLayout);
            mSwipeMenuCreator.onCreateMenu(leftMenu, rightMenu, position);

            SwipeMenuView leftMenuView = (SwipeMenuView)menuLayout.getChildAt(0);
            if (leftMenu.hasMenuItems()) {
                leftMenuView.setOrientation(leftMenu.getOrientation());
                leftMenuView.createMenu(holder, leftMenu, menuLayout, LEFT_DIRECTION, mSwipeMenuItemClickListener);
            } else if (leftMenuView.getChildCount() > 0) {
                leftMenuView.removeAllViews();
            }

            SwipeMenuView rightMenuView = (SwipeMenuView)menuLayout.getChildAt(2);
            if (rightMenu.hasMenuItems()) {
                rightMenuView.setOrientation(rightMenu.getOrientation());
                rightMenuView.createMenu(holder, rightMenu, menuLayout, RIGHT_DIRECTION, mSwipeMenuItemClickListener);
            } else if (rightMenuView.getChildCount() > 0) {
                rightMenuView.removeAllViews();
            }
        }

        mAdapter.onBindViewHolder(holder, position, payloads);
    }

    private Class<?> getSupperClass(Class<?> aClass) {
        Class<?> supperClass = aClass.getSuperclass();
        if (supperClass != null && !supperClass.equals(Object.class)) {
            return getSupperClass(supperClass);
        }
        return aClass;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (isHeaderOrFooter(holder)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams)lp;
                p.setFullSpan(true);
            }
        } else {
            mAdapter.onViewAttachedToWindow(holder);
        }
    }

    public boolean isHeaderOrFooter(RecyclerView.ViewHolder holder) {
        if (holder instanceof ViewHolder) return true;

        return isHeaderOrFooter(holder.getAdapterPosition());
    }

    public boolean isHeaderOrFooter(int position) {
        return isHeaderView(position) || isFooterView(position);
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
        if (headerIndex == -1) return;

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
        if (footerIndex == -1) return;

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
        if (!isHeaderOrFooter(position)) {
            return mAdapter.getItemId(position);
        }
        return super.getItemId(position);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (!isHeaderOrFooter(holder)) mAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        if (!isHeaderOrFooter(holder)) return mAdapter.onFailedToRecycleView(holder);
        return false;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (!isHeaderOrFooter(holder)) mAdapter.onViewDetachedFromWindow(holder);
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
