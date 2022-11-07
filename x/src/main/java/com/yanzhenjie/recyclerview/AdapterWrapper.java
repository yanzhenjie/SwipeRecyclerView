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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yanzhenjie.recyclerview.x.R;

import java.lang.reflect.Field;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static com.yanzhenjie.recyclerview.SwipeRecyclerView.LEFT_DIRECTION;
import static com.yanzhenjie.recyclerview.SwipeRecyclerView.RIGHT_DIRECTION;

/**
 * Created by Yan Zhenjie on 2017/7/20.
 */
class AdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mAdapter;
    private LayoutInflater mInflater;
    private boolean autoMarginEnable = false;

    private SwipeMenuCreator mSwipeMenuCreator;
    private OnItemMenuStateListener mItemMenuStateListener;
    private OnItemMenuClickListener mOnItemMenuClickListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    AdapterWrapper(Context context, RecyclerView.Adapter adapter) {
        this.mInflater = LayoutInflater.from(context);
        this.mAdapter = adapter;
    }

    public RecyclerView.Adapter getOriginAdapter() {
        return mAdapter;
    }

    public void setAutoMarginEnabled(boolean enabled) {
        this.autoMarginEnable = enabled;
    }

    /**
     * Set to create menu listener.
     *
     * @param swipeMenuCreator listener.
     */
    void setSwipeMenuCreator(SwipeMenuCreator swipeMenuCreator) {
        this.mSwipeMenuCreator = swipeMenuCreator;
    }

    void setOnItemMenuStateListener(OnItemMenuStateListener listener) {
        this.mItemMenuStateListener = listener;
    }

    /**
     * Set to click menu listener.
     *
     * @param onItemMenuClickListener listener.
     */
    void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
        this.mOnItemMenuClickListener = onItemMenuClickListener;
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getContentItemCount() + getFooterCount();
    }

    private int getContentItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooter(position)) {
            return mFootViews.keyAt(position - getHeaderCount() - getContentItemCount());
        }
        return mAdapter.getItemViewType(position - getHeaderCount());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = mHeaderViews.get(viewType);
        if (contentView != null) {
            return new ViewHolder(contentView);
        }

        contentView = mFootViews.get(viewType);
        if (contentView != null) {
            return new ViewHolder(contentView);
        }

        final RecyclerView.ViewHolder viewHolder = mAdapter.onCreateViewHolder(parent, viewType);
        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(v, viewHolder.getAdapterPosition());
                    return true;
                }
            });
        }

        if (mSwipeMenuCreator == null) return viewHolder;

        contentView = mInflater.inflate(R.layout.x_recycler_view_item, parent, false);
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

    private Class<?> getSupperClass(Class<?> aClass) {
        Class<?> supperClass = aClass.getSuperclass();
        if (supperClass != null && !supperClass.equals(Object.class)) {
            return getSupperClass(supperClass);
        }
        return aClass;
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    @SuppressLint("WrongConstant")
    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
        @NonNull List<Object> payloads) {
        if (isHeaderOrFooter(holder)) return;

        View itemView = holder.itemView;
        position -= getHeaderCount();

        if (itemView instanceof SwipeMenuLayout && mSwipeMenuCreator != null) {
            SwipeMenuLayout menuLayout = (SwipeMenuLayout)itemView;
            menuLayout.setOnItemMenuStateListener(mItemMenuStateListener);
            if (autoMarginEnable) {
                FrameLayout contentFrame = (FrameLayout) menuLayout.findViewById(R.id.swipe_content);
                ViewGroup nestedContent = (ViewGroup) contentFrame.getChildAt(0);
                ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) nestedContent.getLayoutParams();
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) menuLayout.getLayoutParams();
                // Make sure we only adjust margins once for each item
                if (params1.leftMargin != 0 || params1.rightMargin != 0) {
                    params2.leftMargin = params1.leftMargin ;
                    params2.rightMargin = params1.rightMargin;
                    params1.leftMargin = params1.rightMargin = 0;
                }
            }

            SwipeMenu leftMenu = new SwipeMenu(menuLayout);
            SwipeMenu rightMenu = new SwipeMenu(menuLayout);
            mSwipeMenuCreator.onCreateMenu(leftMenu, rightMenu, position);

            SwipeMenuView leftMenuView = (SwipeMenuView)menuLayout.getChildAt(0);
            if (leftMenu.hasMenuItems()) {
                leftMenuView.setOrientation(leftMenu.getOrientation());
                leftMenuView.createMenu(holder, leftMenu, menuLayout, LEFT_DIRECTION, mOnItemMenuClickListener);
            } else if (leftMenuView.getChildCount() > 0) {
                leftMenuView.removeAllViews();
            }

            SwipeMenuView rightMenuView = (SwipeMenuView)menuLayout.getChildAt(2);
            if (rightMenu.hasMenuItems()) {
                rightMenuView.setOrientation(rightMenu.getOrientation());
                rightMenuView.createMenu(holder, rightMenu, menuLayout, RIGHT_DIRECTION, mOnItemMenuClickListener);
            } else if (rightMenuView.getChildCount() > 0) {
                rightMenuView.removeAllViews();
            }
        }

        mAdapter.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            final GridLayoutManager glm = (GridLayoutManager)lm;
            final GridLayoutManager.SpanSizeLookup originLookup = glm.getSpanSizeLookup();

            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeaderOrFooter(position)) return glm.getSpanCount();
                    if (originLookup != null) return originLookup.getSpanSize(position);
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
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
        return isHeader(position) || isFooter(position);
    }

    public boolean isHeader(int position) {
        return position >= 0 && position < getHeaderCount();
    }

    public boolean isFooter(int position) {
        return position >= getHeaderCount() + getContentItemCount();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(getHeaderCount() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addHeaderViewAndNotify(View view) {
        addHeaderView(view);
        notifyItemInserted(getHeaderCount() - 1);
    }

    public void removeHeaderViewAndNotify(View view) {
        int headerIndex = mHeaderViews.indexOfValue(view);
        if (headerIndex == -1) return;

        mHeaderViews.removeAt(headerIndex);
        notifyItemRemoved(headerIndex);
    }

    public void addFooterView(View view) {
        mFootViews.put(getFooterCount() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public void addFooterViewAndNotify(View view) {
        addFooterView(view);
        notifyItemInserted(getHeaderCount() + getContentItemCount() + getFooterCount() - 1);
    }

    public void removeFooterViewAndNotify(View view) {
        int footerIndex = mFootViews.indexOfValue(view);
        if (footerIndex == -1) return;

        mFootViews.removeAt(footerIndex);
        notifyItemRemoved(getHeaderCount() + getContentItemCount() + footerIndex);
    }

    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    public int getFooterCount() {
        return mFootViews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public final void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        if (isHeaderOrFooter(position)) {
            return -position - 1;
        }

        position -= getHeaderCount();
        return mAdapter.getItemId(position);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (!isHeaderOrFooter(holder)) mAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        if (!isHeaderOrFooter(holder)) return mAdapter.onFailedToRecycleView(holder);
        return false;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (!isHeaderOrFooter(holder)) mAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter.onDetachedFromRecyclerView(recyclerView);
    }
}