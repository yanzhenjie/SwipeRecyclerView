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
package com.yanzhenjie.recyclerview.sample.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.sample.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by QM-LU on 2022/10/20.
 * https://blog.csdn.net/suwenlai/article/details/108276501
 */
public class MainAdapterDiff extends BaseAdapter<MainAdapterDiff.ViewHolder> {

    private AsyncListDiffer<String> mDiffer;
    private DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(String oldItem, String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(String oldItem, String newItem) {
            return oldItem.equals(newItem);
        }
    };

    public MainAdapterDiff(Context context) {
        super(context);
        mDiffer = new AsyncListDiffer<>(this, diffCallback);
    }

    public void notifyDataSetChanged(List<String> dataList) {
        // No longer used!!!
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<String> data) {
        mDiffer.submitList(data);
    }

    public List<String> getList() {
        return mDiffer.getCurrentList();
    }

    public String getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_menu_main, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    ////////////////////////////////////////////////////////////////
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }

        public void setData(String title) {
            this.tvTitle.setText(title);
        }
    }

}