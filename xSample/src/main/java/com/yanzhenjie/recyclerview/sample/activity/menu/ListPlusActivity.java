package com.yanzhenjie.recyclerview.sample.activity.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.OnItemLongClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuStateListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeMenuLayout;
import com.yanzhenjie.recyclerview.SwipeMenuView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.sample.R;
import com.yanzhenjie.recyclerview.sample.activity.BaseActivity;
import com.yanzhenjie.recyclerview.sample.adapter.BaseAdapter;
import com.yanzhenjie.recyclerview.sample.adapter.MoreTestAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ListView形式的侧滑菜单--更多测试！
 * Created by QM-LU on 2022/6/9.
 */
public class ListPlusActivity extends BaseActivity {

    private List<MoreTestAdapter.ItemStatus> mItemStatusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);

        // 有些设置必须在setAdapter之前调用
        beforeSettingAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);

        mRecyclerView.setAutoMarginEnabled(true);
    }

    @Override
    public BaseAdapter createAdapter() {
        MoreTestAdapter adapter = new MoreTestAdapter(this);
        adapter.notifyItemStatusChanged(mItemStatusList);
        return adapter;
    }

    @Override
    protected List<String> createDataList() {
        List<String> dataList = new ArrayList<>();
        mItemStatusList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            dataList.add("第" + i + "个Item");

            MoreTestAdapter.ItemStatus status = new MoreTestAdapter.ItemStatus();
            status.isChecked = false;
            mItemStatusList.add(status);
        }
        return dataList;
    }

    protected void beforeSettingAdapter() {
        mRecyclerView.setOnItemLongClickListener(mItemLongClickListener);

        // 监听侧滑菜单展开/收起的状态变化。
        // 注意：必须在给RecyclerView设置adapter之前设置
        OnItemMenuStateListener listener = new OnItemMenuStateListener() {
            @Override
            public void onMenuState(RecyclerView.ViewHolder viewHolder, int menuState) {
                MoreTestAdapter.ViewHolder vh = (MoreTestAdapter.ViewHolder) viewHolder;
                if (menuState == OPEN) {
                    // 演示：修改Item的状态
                    vh.setHiddenView(true);

                    // 演示：修改菜单项的状态
                    SwipeMenuLayout menuLayout = (SwipeMenuLayout) vh.itemView;
                    if (menuLayout.hasRightMenu()) {
                        SwipeMenuView rightMenuView = (SwipeMenuView) menuLayout.getChildAt(2);
                        View view1 = rightMenuView.getChildAt(0);
                        view1.setBackgroundColor(ContextCompat.getColor(ListPlusActivity.this, R.color.colorPrimary));
                        view1.setEnabled(false);
                    }

                    Toast.makeText(ListPlusActivity.this, "菜单已展开，位置：" + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                } else {
                    vh.setHiddenView(false);
                    Toast.makeText(ListPlusActivity.this, "菜单已收起，位置：" + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        mRecyclerView.setOnItemMenuStateListener(listener);
    }

    private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, int adapterPosition) {
            MoreTestAdapter adapter = (MoreTestAdapter)mAdapter;
            adapter.switchSelectionMode();
            Toast.makeText(ListPlusActivity.this, "选择模式", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onItemClick(View itemView, int position) {
        MoreTestAdapter adapter = (MoreTestAdapter)mAdapter;
        if (adapter.isSelectionMode()) {
            adapter.makeItemChecked(position);
            adapter.notifyItemChanged(position);
            Toast.makeText(ListPlusActivity.this, "单击选择，位置：" + position, Toast.LENGTH_SHORT).show();
        }
    }

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

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(ListPlusActivity.this).setBackground(R.drawable.selector_green)
                    .setImage(R.drawable.ic_action_add)
                    .setWidth(width)
                    .setHeight(height);
                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。

                SwipeMenuItem closeItem = new SwipeMenuItem(ListPlusActivity.this).setBackground(R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_close)
                    .setWidth(width)
                    .setHeight(height);
                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(ListPlusActivity.this).setBackground(R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_delete)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(ListPlusActivity.this).setBackground(R.drawable.selector_green)
                    .setText("添加")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(ListPlusActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(ListPlusActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                    .show();
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