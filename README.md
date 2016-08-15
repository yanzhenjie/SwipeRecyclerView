# SwipeRecyclerView

严振杰的主页：[http://www.yanzhenjie.com][0]  
严振杰的博客：[http://blog.csdn.net/yanzhenjie1003][1]  
技术交流群：547839514，加群前请务必阅读[群行为规范][2]。  

----
[演示Demo apk下载][3]  

> 1. 需要说明的是，本库没有对RecyclerView做大的修改，只是ItemView的封装。看起来是对RecyclerView的修改，其实仅仅是为RecyclerView添加了使用的方法API而已。
2. 本库已经更新了两个版本了，也将会一直维护下去，根据小伙伴的要求，以后也会添加一些其它功能。

SwipeRecyclerView将完美解决这些问题：
> 0. 以下功能全部支持：竖向ListView、横向ListView、Grid、StaggeredGrid四种形式。
1. RecyclerView侧滑菜单（左右两侧都可以添加）。
2. RecyclerView长按拖拽Item。
3. RecyclerView侧滑删除item。
4. 指定RecyclerView的某一个Item不能滑动删除或长按拖拽。
5. 根据Item的ViewType来决定显示的菜单。
6. 用SwipeMenuLayout在任何地方都可以实现你自己的侧滑菜单。
7. 使用SwipeRefreshLayout下拉刷新。
8. 解决和ViewPager嵌套使用的滑动冲突。。

# 引用方法  
* Eclipse 请自行[下载源码][4]。  
* AndroidStudio使用Gradle构建添加依赖（推荐）  
```groovy
compile 'com.yanzhenjie:recyclerview-swipe:1.0.1'
```

Or Maven:
```xml
<dependency>
  <groupId>com.yanzhenjie</groupId>
  <artifactId>recyclerview-swipe</artifactId>
  <version>1.0.1</version>
</dependency>
```

我在开发SwipeRecyclerView时引用的RecyclerView版本如下：
```groovy
compile 'com.android.support:recyclerview-v7:23.4.0'
```

# 效果图  
gif有一些失真，且网页加载速度慢，可以[下载demo的apk][3]看效果。  

## 侧滑菜单
<image src="./image/1.gif" width="300px"/> <image src="./image/2.gif" width="300px"/>  

## 拖拽、侧滑菜单  
<image src="./image/3.gif" width="300px"/> <image src="./image/4.gif" width="300px"/>  

## 拖拽、侧滑删除  
<image src="./image/5.gif" width="300px"/> <image src="./image/6.gif" width="300px"/> 

## 用户自定义菜单 下拉刷新、上拉加载更多
<image src="./image/7.gif" width="300px"/> <image src="./image/8.gif" width="300px"/> 

# 使用介绍
这里列出关键实现，具体请参考demo，或者加最上面的交流群一起讨论。
更多教程请进入[我的博客][1]查看。

## 启用SwipeReyclerView的长按Item拖拽功能和侧滑删除功能
```java
recyclerView.setLongPressDragEnabled(true);// 开启长按拖拽
recyclerView.setItemViewSwipeEnabled(true);// 开启滑动删除。
recyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽和侧滑删除，更新UI。
```

## 添加Item侧滑菜单
侧滑菜单支持自动打开某个Item的菜单，并可以指定是左边还是右边的：
```java
// 打开第一个Item的左侧菜单。
recyclerView.openLeftMenu(0);

// 打开第一个Item的右侧菜单。
recyclerView.openRightMenu(0);
```

* 第一步，引用自定义View：SwipeMenuReyclerView：  
```xml
<com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:layout_behavior="@string/appbar_scrolling_view_behavior" />
```

* 第二步，设置菜单创建器、菜单点击监听：
```java
SwipeMenuRecyclerView swipeMenuRecyclerView = findViewById(R.id.recycler_view);
// 设置菜单创建器。
swipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
// 设置菜单Item点击监听。
swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
```

* 第三步，菜单创建器创建菜单：
```java
/**
 * 菜单创建器。在Item要创建菜单的时候调用。
 */
private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
    @Override
    public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

        SwipeMenuItem addItem = new SwipeMenuItem(mContext)
            .setBackgroundDrawable(R.drawable.selector_green)// 点击的背景。
            .setImage(R.mipmap.ic_action_add) // 图标。
            .setWidth(size) // 宽度。
            .setHeight(size); // 高度。
        swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。

        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
            .setBackgroundDrawable(R.drawable.selector_red)
            .setImage(R.mipmap.ic_action_delete) // 图标。
            .setText("删除") // 文字。
            .setTextColor(Color.WHITE) // 文字颜色。
            .setTextSize(16) // 文字大小。
            .setWidth(size)
            .setHeight(size);
        swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
    }
};
```

* 第四步，继承SwipeMenuAdapter，和正常的Adapter一样使用：
```java
public class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MenuAdapter.DefaultViewHolder holder, int position) {
    }
}
```

#License
```text
Copyright 2016 Yan Zhenjie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[0]: http://www.yanzhenjie.com
[1]: http://blog.csdn.net/yanzhenjie1003
[2]: https://github.com/yanzhenjie/SkillGroupRule
[3]: https://github.com/yanzhenjie/SwipeRecyclerView/blob/master/sample-release.apk?raw=true
[4]: https://codeload.github.com/yanzhenjie/SwipeRecyclerView/zip/master