package com.yanzhenjie.recyclerview.swipe.sample.activity.nested;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.sample.R;
import com.yanzhenjie.recyclerview.swipe.sample.activity.BaseActivity;
import com.yanzhenjie.recyclerview.swipe.sample.adapter.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Item是CardView时的演示，Item的布局需要特殊处理一下。
 * </p>
 * Created by Yan Zhenjie on 2017/3/12.
 */
public class CardViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
    }

    /**
     * 主要就是这里的这个Adapter，里面的ItemView需要处理一下。
     */
    @Override
    protected BaseAdapter createAdapter() {
        return new MenuCardAdapter(this);
    }

    @Override
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(20, 20, 20, 20);
            }
        };
    }

    /**
     * 就是这个适配器的Item的Layout需要处理，其实就是自定义Menu啦，一模一样。
     */
    private static class MenuCardAdapter extends BaseAdapter<DefaultViewHolder> {

        private List<String> mDataList;

        MenuCardAdapter(Context context) {
            super(context);
        }

        @Override
        public void notifyDataSetChanged(List<String> dataList) {
            this.mDataList = dataList;
            super.notifyDataSetChanged();
        }


        @Override
        public int getItemCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @Override
        public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_card, parent, false));
        }

        @Override
        public void onBindViewHolder(DefaultViewHolder holder, int position) {
            holder.setData(mDataList.get(position));
        }
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);

            ((CardView) itemView).getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "第" + getAdapterPosition() + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setData(String title) {
            this.tvTitle.setText(title);
        }
    }

    @Override
    protected List<String> createDataList() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            strings.add("第" + i + "个Item");
        }
        return strings;
    }
}
