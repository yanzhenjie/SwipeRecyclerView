package com.yanzhenjie.recyclerview.sample.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.sample.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MoreTestAdapter extends BaseAdapter<MoreTestAdapter.ViewHolder> {

    private List<String> mDataList;
    private List<ItemStatus> mStatusList;
    private boolean isSelectionMode = false;

    public MoreTestAdapter(Context context) {
        super(context);
    }

    public void notifyDataSetChanged(List<String> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    public void notifyItemStatusChanged(List<ItemStatus> statusList) {
        this.mStatusList = statusList;
    }

    public void switchSelectionMode() {
        isSelectionMode = !isSelectionMode;
        if (!isSelectionMode) {
            for (int i = 0; i < mStatusList.size(); i++) {
                mStatusList.get(i).isChecked = false;
            }
        }
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public void makeItemChecked(int position) {
        ItemStatus status = mStatusList.get(position);
        status.isChecked = !status.isChecked;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_menu_moretests, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
        holder.showSelection(mStatusList.get(position).isChecked);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvHidden;
        TextView tvSelected;
        View itemFrame;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvHidden = itemView.findViewById(R.id.tv_hidden);
            tvSelected = itemView.findViewById(R.id.tv_selected);
            itemFrame = itemView.findViewById(R.id.item_frame);
        }

        public void setData(String title) {
            this.tvTitle.setText(title);
        }

        public void setHiddenView(boolean visible) {
            tvHidden.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public void showSelection(boolean selected) {
            itemFrame.setSelected(selected);
            tvTitle.setSelected(selected);
            tvSelected.setVisibility(selected ?  View.VISIBLE : View.GONE);
        }
    }

    public static class ItemStatus {
        public boolean isChecked;
    }

}