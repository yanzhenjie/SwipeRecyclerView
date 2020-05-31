package com.yanzhenjie.recyclerview.sample.activity.nestscrollview;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.sample.R;

import java.util.ArrayList;
import java.util.List;

public class NestscrollViewActivity extends AppCompatActivity {

    private NestedScrollView mNestedScrollView;

    private SwipeRecyclerView mSwipeRecyclerView;

    private List<Integer> mList ;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_nestscorllview);
        mNestedScrollView = findViewById(R.id.nestedScrollView);
        mSwipeRecyclerView = findViewById(R.id.swipeRecyclerView);

        mList = new ArrayList<>();
        mSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRecyclerView.setAdapter(new Adapter());
        setData();
        mSwipeRecyclerView.getAdapter().notifyDataSetChanged();

        mSwipeRecyclerView.useDefaultLoadMore();
        mSwipeRecyclerView.setLoadMoreListener(new SwipeRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                        mSwipeRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                },2000);
            }
        });

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View onlyChild = v.getChildAt(0);
                if (onlyChild.getHeight() <= scrollY + v.getHeight()) {
                    // 如果满足就是到底部了
                    //该方法需要是public
                    mSwipeRecyclerView.dispatchLoadMore();

                   /* //如果dispatchLoadMore 是private
                    mSwipeRecyclerView.onScrollStateChanged(2);
                    mSwipeRecyclerView.onScrolled(scrollX,scrollY);*/

                }

            }
        });

    }

    private void setData() {
        int baseNo = mList.size() ;
        for(int i = baseNo ; i < baseNo + 20 ; i++ ){
            mList.add(i);
        }
        /*//如果dispatchLoadMore是private
        //去掉RecyclerView内的滚动状态，要不页面滚动会卡顿
        //mSwipeRecyclerView.onScrollStateChanged(-1);*/

        mSwipeRecyclerView.loadMoreFinish(false,true);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nestscorllview_item,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TextView tvItemText = holder.itemView.findViewById(R.id.tvItemText);
            tvItemText.setText("第" + mList.get(position).toString() + "个");
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

}
