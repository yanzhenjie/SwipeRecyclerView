package com.yanzhenjie.recyclerview.sample.activity.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.sample.R;
import com.yanzhenjie.recyclerview.sample.activity.BaseActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by QM-LU on 2022/8/11.
 */
public class ListEmptyViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btn = findViewById(R.id.btn_clearDataSource);
        btn.setOnClickListener(xOnClickListener);
        btn = findViewById(R.id.btn_setDataSource);
        btn.setOnClickListener(xOnClickListener);

        // Let's test this new API :)
        mRecyclerView.setEmptyView(findViewById(R.id.tv_empty));

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_empty_view_test;
    }

    @Override
    protected List<String> createDataList() {
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dataList.add("第" + i + "个Item");
        }
        return dataList;
    }

    @Override
    public void onItemClick(View itemView, int position) {
    }

    /**
     * 一般的点击事件。
     */
    private View.OnClickListener xOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_clearDataSource) {
                mDataList.clear();
                mAdapter.notifyDataSetChanged(mDataList);
            } else if (v.getId() == R.id.btn_setDataSource) {
                mDataList = createDataList();
                mAdapter.notifyDataSetChanged(mDataList);
            }
        }
    };

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(ListEmptyViewActivity.this).setBackground(R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_delete)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(ListEmptyViewActivity.this).setBackground(R.drawable.selector_green)
                    .setText("添加")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };


    // ---------- 开发者只需要关注上面的代码 ---------- //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_open_rv_menu) {
            mRecyclerView.smoothOpenRightMenu(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}