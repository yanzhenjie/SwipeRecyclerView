/*
 * Copyright 2019 Zhenjie Yan
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

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhenjie Yan on 1/28/19.
 */
public abstract class ExpandableAdapter<VH extends ExpandableAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final int TYPE_PARENT = 10000000;
    private static final int TYPE_CHILD = 20000000;

    private final SparseBooleanArray mExpandItemArray = new SparseBooleanArray();
    private final List<Integer> mParentViewType = new ArrayList<>();

    /**
     * Parent item is expanded.
     *
     * @param parentPosition position of parent item.
     *
     * @return true, otherwise is false.
     */
    public final boolean isExpanded(int parentPosition) {
        return mExpandItemArray.get(parentPosition, false);
    }

    /**
     * Expand parent.
     *
     * @param parentPosition position of parent item.
     */
    public final void expandParent(int parentPosition) {
        if (!isExpanded(parentPosition)) {
            mExpandItemArray.append(parentPosition, true);

            int position = positionFromParentPosition(parentPosition);
            int childCount = childItemCount(parentPosition);
            notifyItemRangeInserted(position + 1, childCount);
        }
    }

    /**
     * Collapse parent.
     *
     * @param parentPosition position of parent item.
     */
    public final void collapseParent(int parentPosition) {
        if (isExpanded(parentPosition)) {
            mExpandItemArray.append(parentPosition, false);

            int position = positionFromParentPosition(parentPosition);
            int childCount = childItemCount(parentPosition);
            notifyItemRangeRemoved(position + 1, childCount);
        }
    }

    /**
     * Notify any registered observers that the item at <code>parentPosition</code> has changed.
     *
     * @param parentPosition position of parent item.
     */
    public final void notifyParentChanged(int parentPosition) {
        int position = positionFromParentPosition(parentPosition);
        notifyItemChanged(position);
    }

    /**
     * Notify any registered observers that the item reflected at <code>parentPosition</code> has been newly inserted.
     *
     * @param parentPosition position of parent item.
     */
    public final void notifyParentInserted(int parentPosition) {
        int position = positionFromParentPosition(parentPosition);
        notifyItemInserted(position);
    }

    /**
     * Notify any registered observers that the item previously located at <code>parentPosition</code> has been removed
     * from the data set.
     *
     * @param parentPosition position of parent item.
     */
    public final void notifyParentRemoved(int parentPosition) {
        int position = positionFromParentPosition(parentPosition);
        notifyItemRemoved(position);
    }

    /**
     * Notify any registered observers that the item at <code>parentPosition, childPosition</code> has changed.
     *
     * @param parentPosition position of parent item.
     * @param childPosition positoin of child item.
     */
    public final void notifyChildChanged(int parentPosition, int childPosition) {
        int position = positionFromChildPosition(parentPosition, childPosition);
        notifyItemChanged(position);
    }

    /**
     * Notify any registered observers that the item reflected at <code>parentPosition, childPosition</code> has been
     * newly inserted.
     *
     * @param parentPosition position of parent item.
     * @param childPosition positoin of child item.
     */
    public final void notifyChildInserted(int parentPosition, int childPosition) {
        int position = positionFromChildPosition(parentPosition, childPosition);
        notifyItemInserted(position);
    }

    /**
     * Notify any registered observers that the item previously located at <code>parentPosition, childPosition</code>
     * has been removed from the data set.
     *
     * @param parentPosition position of parent item.
     * @param childPosition positoin of child item.
     */
    public final void notifyChildRemoved(int parentPosition, int childPosition) {
        int position = positionFromChildPosition(parentPosition, childPosition);
        notifyItemRemoved(position);
    }

    private int positionFromParentPosition(int parentPosition) {
        int itemCount = 0;

        int parentCount = parentItemCount();
        for (int i = 0; i < parentCount; i++) {
            itemCount += 1;

            if (parentPosition == i) {
                return itemCount - 1;
            } else {
                if (isExpanded(i)) {
                    itemCount += childItemCount(i);
                } else {
                    // itemCount += 1;
                }
            }
        }

        throw new IllegalStateException("The parent position is invalid: " + parentPosition);
    }

    private int positionFromChildPosition(int parentPosition, int childPosition) {
        int itemCount = 0;

        int parentCount = parentItemCount();
        for (int i = 0; i < parentCount; i++) {
            itemCount += 1;

            if (parentPosition == i) {
                int childCount = childItemCount(parentPosition);
                if (childPosition < childCount) {
                    itemCount += (childPosition + 1);
                    return itemCount - 1;
                }

                throw new IllegalStateException("The child position is invalid: " + childPosition);
            } else {
                if (isExpanded(i)) {
                    itemCount += childItemCount(i);
                } else {
                    // itemCount += 1;
                }
            }
        }

        throw new IllegalStateException("The parent position is invalid: " + parentPosition);
    }

    @Override
    public final int getItemCount() {
        int parentCount = parentItemCount();
        for (int i = 0; i < parentCount; i++) {
            if (isExpanded(i)) {
                int childCount = childItemCount(i);
                parentCount += childCount;
            } else {
                // parentCount += 0;
            }
        }
        return parentCount;
    }

    /**
     * Get the total number of items in the parent.
     */
    public abstract int parentItemCount();

    /**
     * Get the total number of child items under parent.
     *
     * @param parentPosition position of parent item.
     */
    public abstract int childItemCount(int parentPosition);

    @Override
    public final int getItemViewType(int position) {
        int parentPosition = parentItemPosition(position);
        if (isParentItem(position)) {
            int viewType = parentItemViewType(parentPosition);
            if (!mParentViewType.contains(viewType)) mParentViewType.add(viewType);
            return viewType;
        } else {
            int childPosition = childItemPosition(position);
            return childItemViewType(parentPosition, childPosition);
        }
    }

    /**
     * Get the view type of the parent item.
     *
     * @param parentPosition position of parent item.
     */
    public int parentItemViewType(int parentPosition) {
        return TYPE_PARENT;
    }

    /**
     * Get the view type of the child item.
     *
     * @param parentPosition position of parent item.
     * @param childPosition position of child item.
     */
    public int childItemViewType(int parentPosition, int childPosition) {
        return TYPE_CHILD;
    }

    /**
     * Item is a parent item.
     *
     * @param adapterPosition adapter position.
     *
     * @return true, otherwise is false.
     */
    public final boolean isParentItem(int adapterPosition) {
        int itemCount = 0;

        int parentCount = parentItemCount();
        for (int i = 0; i < parentCount; i++) {
            if (itemCount == adapterPosition) {
                return true;
            }

            itemCount += 1;

            if (isExpanded(i)) {
                itemCount += childItemCount(i);
            } else {
                // itemCount += 1;
            }
        }

        return false;
    }

    /**
     * Get the position of the parent item from the adapter position.
     *
     * @param adapterPosition adapter position of item.
     */
    public final int parentItemPosition(int adapterPosition) {
        int itemCount = 0;
        for (int i = 0; i < parentItemCount(); i++) {
            itemCount += 1;

            if (isExpanded(i)) {
                int childCount = childItemCount(i);
                itemCount += childCount;
            }
            if (adapterPosition < itemCount) {
                return i;
            }
        }

        throw new IllegalStateException("The adapter position is not a parent type: " + adapterPosition);
    }

    /**
     * Get the position of the child item from the adapter position.
     *
     * @param childAdapterPosition adapter position of child item.
     */
    public final int childItemPosition(int childAdapterPosition) {
        int itemCount = 0;

        int parentCount = parentItemCount();
        for (int i = 0; i < parentCount; i++) {
            itemCount += 1;

            if (isExpanded(i)) {
                int childCount = childItemCount(i);
                itemCount += childCount;

                if (childAdapterPosition < itemCount) {
                    return childCount - (itemCount - childAdapterPosition);
                }
            } else {
                // itemCount += 1;
            }
        }

        throw new IllegalStateException("The adapter position is invalid: " + childAdapterPosition);
    }

    @NonNull
    @Override
    public final VH onCreateViewHolder(@NonNull ViewGroup root, int viewType) {
        if (mParentViewType.contains(viewType)) return createParentHolder(root, viewType);
        return createChildHolder(root, viewType);
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an parent item.
     *
     * @param root the ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a new {@link ViewHolder} that holds a View of the given view type.
     */
    public abstract VH createParentHolder(@NonNull ViewGroup root, int viewType);

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an child item.
     *
     * @param root the ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a new {@link ViewHolder} that holds a View of the given view type.
     */
    public abstract VH createChildHolder(@NonNull ViewGroup root, int viewType);

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        int parentPosition = parentItemPosition(position);
        if (isParentItem(position)) {
            bindParentHolder(holder, parentPosition, payloads);
        } else {
            int childPosition = childItemPosition(position);
            bindChildHolder(holder, parentPosition, childPosition, payloads);
        }
    }

    public void bindParentHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        bindParentHolder(holder, position);
    }

    public void bindChildHolder(@NonNull VH holder, int parentPosition, int position, @NonNull List<Object> payloads) {
        bindChildHolder(holder, parentPosition, position);
    }

    /**
     * Called by {@link RecyclerView} to display the data at the specified position. This method should update the
     * contents of the {@link ViewHolder#itemView} to reflect the item at the given position.
     *
     * @param holder parent holder.
     * @param position position of parent item.
     */
    public abstract void bindParentHolder(@NonNull VH holder, int position);

    /**
     * Called by {@link RecyclerView} to display the data at the specified position. This method should update the *
     * contents of the {@link ViewHolder#itemView} to reflect the item at the given position.
     *
     * @param holder child holder.
     * @param parentPosition position of parent item.
     * @param position position of child position.
     */
    public abstract void bindChildHolder(@NonNull VH holder, int parentPosition, int position);

    @Deprecated
    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        private ExpandableAdapter mAdapter;

        public ViewHolder(@NonNull View itemView, ExpandableAdapter adapter) {
            super(itemView);
            this.mAdapter = adapter;
        }

        /**
         * Determine if the current viewholder is a parent item.
         *
         * @return true, otherwise is false.
         */
        public final boolean isParentItem() {
            return mAdapter.isParentItem(getAdapterPosition());
        }

        /**
         * Get the position of parent item.
         */
        public final int parentItemPosition() {
            return mAdapter.parentItemPosition(getAdapterPosition());
        }

        /**
         * Get the position of child item.
         */
        public final int childItemPosition() {
            if (isParentItem()) throw new IllegalStateException("This item is not a child item.");
            return mAdapter.childItemPosition(getAdapterPosition());
        }

        /**
         * Parent item is expanded.
         *
         * @return true, otherwise is false.
         */
        public final boolean isParentExpanded() {
            return mAdapter.isExpanded(parentItemPosition());
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            final GridLayoutManager glm = (GridLayoutManager)lm;
            final GridLayoutManager.SpanSizeLookup originLookup = glm.getSpanSizeLookup();

            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isParentItem(position)) return glm.getSpanCount();
                    if (originLookup != null) return originLookup.getSpanSize(position);
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        if (isParentItem(holder.getAdapterPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams)lp;
                p.setFullSpan(true);
            }
        }
    }
}