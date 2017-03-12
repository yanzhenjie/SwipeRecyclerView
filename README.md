# SwipeRecyclerView

严振杰的主页：[http://www.yanzhenjie.com](http://www.yanzhenjie.com)  
严振杰的博客：[http://blog.yanzhenjie.com](http://blog.yanzhenjie.com)  
技术交流群：46523908，加群前请务必阅读[群行为规范](https://github.com/yanzhenjie/SkillGroupRule)    

----
# Features
1. 以下功能全部支持：竖向ListView、横向ListView、Grid、StaggeredGrid四种形式。
2. RecyclerView 左右两侧添加侧滑菜单。
3. 菜单可以自适应Item不同高度。
4. 某一个Item显示的不同的菜单（类似QQ）。
5. 菜单横向排布、菜单竖向排布（看下图）。
6. RecyclerView长按拖拽Item，触摸拖拽Item。
7. RecyclerView侧滑删除item，触摸拖拽Item。
8. 指定RecyclerView的某一个Item不能滑动删除或长按拖拽。
9. 用SwipeMenuLayout在任何地方都可以实现你自己的侧滑菜单。
10. 使用SwipeRecyclerView下拉刷新、自动加载更多。
11. 可以和ViewPager、DrawerLayout、CardView嵌套使用。

# 引用方法  
* Gradle
```groovy
compile 'com.yanzhenjie:recyclerview-swipe:1.0.3'
```

* Maven
```xml
<dependency>
  <groupId>com.yanzhenjie</groupId>
  <artifactId>recyclerview-swipe</artifactId>
  <version>1.0.3</version>
  <type>pom</type>
</dependency>
```

* Eclipse ADT请放弃治疗

# Screenshot
gif有一些失真，且网页加载速度慢，建议下载demo运行后查看效果。  

## Item侧滑菜单
1. 左右两侧都有菜单，竖向菜单和横向菜单。
2. 根据`ViewType`某一个`Item`显示的不同的菜单。

<image src="./image/1.gif" width="180px"/> <image src="./image/2.gif" width="180px"/> <image src="./image/3.gif" width="180px"/>

## 和ViewPager、DrawerLayout、CardView嵌套
1. 和`ViewPager`、`DrawerLayout`嵌套使用，兼容了滑动冲突。
2. 把Item放入CardView中，菜单在CardView中出现。

<image src="./image/4.gif" width="180px"/> <image src="./image/5.gif" width="180px"/> <image src="./image/6.gif" width="180px"/>

## 自定义侧滑、下拉刷新、加载更多
1. 利用`SwipeMenuLayout`自定义侧滑`menu`。  
2. 结合`SwipeRefreshLayout`下拉刷新、利用`RecyclerView`自身特性加载更多。

<image src="./image/7.gif" width="250px"/> <image src="./image/8.gif" width="250px"/>

## Item拖拽、侧滑菜单、触摸拖拽、侧滑删除
1. 长按拖拽Item、触摸拖拽Item和侧滑菜单结合。
2. 拖拽Item + 侧滑删除结合。

<image src="./image/9.gif" width="180px"/> <image src="./image/10.gif" width="180px"/> <image src="./image/11.gif" width="180px"/>

# Usage
首先添加再依赖后Sync。

## xml
在xml中引用SwipeRecyclerView：
```xml
<com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

## 只要用到菜单功能就要继承`SwipeMenuAdapter`
`Adapter`要继承`SwipeMenuAdapter`：
```java
public class DragTouchAdapter extends SwipeMenuAdapter<ViewHolder> {

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
    public void onBindViewHolder(DragTouchAdapter.DefaultViewHolder holder, int position) {
    }
}
```
这里要注意，原生的`Adapter`的`onCreateViewHolder()`：
```java
@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = ... // 创建View。
    return new ViewHolder(view); // 创建ViewHolder。
}
```
现在被拆分开两步，其它均无区别：
```java
// 创建View。
@Override
public View onCreateContentView(ViewGroup parent, int viewType) {
    return view;
}

// 创建ViewHolder。
@Override
public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
    return new ViewHolder(realContentView);
}
```

**这段原理，需要的同学阅读即可：**底层在调用`onCreateContentView()`之后拿到一个`Item`的`View`，如果你给这个`Item`添加了菜单，底层会把`View`添加到一个`ViewGroup`中，然后再调用`onCompatCreateViewHolder()`时把`ViewGroup`作为`realContentView`传进来，如果你要在这里拿到你上面创建的View：
```java
@Override
public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
    ViewGroup root = (ViewGroup) realContentView;
    
    View view = root.getChildAt(0); // 这就是你创建的View。
    
    // 但是你返回时必须要把realContentView返回，否则菜单将创建失败。
    return new ViewHolder(realContentView);
}
```

## 添加菜单
> 具体999^999推荐下载demo看代码，demo里面的例子和注释非常全了，还有不明白的请加上面的QQ交流群。

设置菜单创建监听器，然后在监听器中为`Item`添加菜单：
```java
// 设置监听器。
swipeRecyclerView.setSwipeMenuCreator(swipeMenuCreator);

// 创建菜单：
SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
    @Override
    public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
            .setBackgroundDrawable(R.drawable.selector_red)
            .setImage(R.mipmap.ic_action_delete)
            .setText("删除") // 文字。
            .setTextColor(Color.WHITE) // 文字的颜色。
            .setWidth(width) // 菜单宽度。
            .setHeight(height); // 菜单高度。
        leftMenu.addMenuItem(deleteItem); // 在左侧添加一个菜单。

        SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
            .setBackgroundDrawable(R.drawable.selector_purple)
            .setImage(R.mipmap.ic_action_close) // 图片。
            .setWidth(width) // 菜单宽度。
            .setHeight(height); // 菜单高度。
        rightMenu.addMenuItem(closeItem); // 在右侧添加一个菜单。

        // 还有添加，这里各添加一个吧...
    }
};
```

**关于菜单高度的特别说明：**  
使用`MATCH_PARENT`将和Item高度一致；使用`WRAP_CONTENT`将和设置的文字或者图片一样高；也可以指定菜单具体高度，比如：100，这里的100是px，如果要传入dp，请先把dp转为px后再传入。

## 使用拖拽和侧滑删除功能
拖拽和侧滑删除的功能默认关闭的，所以先要打开功能：
```java
swipeRecyclerView.setLongPressDragEnabled(true); // 开启拖拽。
swipeRecyclerView.setItemViewSwipeEnabled(true); // 开启滑动删除。
```

然后用户就可以长按拖拽`Item`和侧滑删除`Item`了，我们可以监听用户的操作：
```java
// 设置操作监听。
swipeRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。

OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // Item被拖拽时，交换数据，并更新adapter。
        Collections.swap(mDataList, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        // Item被侧滑删除时，删除数据，并更新adapter。
        mDataList.remove(position);
        adapter.notifyItemRemoved(position);
    }
};
```

**使用`Grid`形式的`RecyclerView`拖拽`Item`时特别注意，因为`Grid`的`Item`可以跨`position`拖拽，所以`onItemMove()`方法体有所不同：**
```java
@Override
public boolean onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition)
        for (int i = fromPosition; i < toPosition; i++)
            Collections.swap(mDataList, i, i + 1);
    else
        for (int i = fromPosition; i > toPosition; i--)
            Collections.swap(mDataList, i, i - 1);

    mMenuAdapter.notifyItemMoved(fromPosition, toPosition);
    return true;
}
```

我们还可以监听用户的侧滑删除和拖拽Item时的手指状态：
```java
/**
 * Item的拖拽/侧滑删除时，手指状态发生变化监听。
 */
private OnItemStateChangedListener stateChangedListener = (viewHolder, actionState) -> {
    if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
        // 状态：正在拖拽。
    } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
        // 状态：滑动删除。
    } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
        // 状态：手指松开。
    }
};
```

## 触摸拖拽 & 触摸侧滑删除
想用户触摸到某个`Item`的`View`时就开始拖拽或者侧滑删除实现也很简单。  

* 触摸拖拽
```java
swipeRecyclerView.startDrag(ViewHolder);
```
这里只要传入当前触摸`Item`对应的`ViewHolder`即可立即开始拖拽。

* 触摸侧滑删除
```java
swipeRecyclerView.startSwipe(ViewHolder);
```
这里只要传入当前触摸`Item`对应的`ViewHolder`即可立即开始侧滑删除。

# Thanks
* [SwipeMenu](https://github.com/TUBB/SwipeMenu/)

# License
```text
Copyright 2017 Yan Zhenjie

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