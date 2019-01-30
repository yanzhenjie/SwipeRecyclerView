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
package com.yanzhenjie.recyclerview.sample.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.ExpandableAdapter;
import com.yanzhenjie.recyclerview.sample.R;
import com.yanzhenjie.recyclerview.sample.activity.expanded.entity.Group;
import com.yanzhenjie.recyclerview.sample.activity.expanded.entity.GroupMember;

import java.util.List;

/**
 * Created by Zhenjie Yan on 1/30/19.
 */
public class ExpandedAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Group> mGroupList;

    public ExpandedAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void setGroupList(List<Group> groupList) {
        this.mGroupList = groupList;
    }

    @Override
    public int parentItemCount() {
        return mGroupList == null ? 0 : mGroupList.size();
    }

    @Override
    public int childItemCount(int parentPosition) {
        List<GroupMember> memberList = mGroupList.get(parentPosition).getMemberList();
        return memberList == null ? 0 : memberList.size();
    }

    @Override
    public ViewHolder createParentHolder(@NonNull ViewGroup root, int viewType) {
        View view = mInflater.inflate(R.layout.item_expand_parent, root, false);
        return new ParentHolder(view, this);
    }

    @Override
    public ViewHolder createChildHolder(@NonNull ViewGroup root, int viewType) {
        View view = mInflater.inflate(R.layout.item_expand_child, root, false);
        return new ChildHolder(view, this);
    }

    @Override
    public void bindParentHolder(@NonNull ViewHolder holder, int position) {
        ((ParentHolder)holder).setData(mGroupList.get(position));
    }

    @Override
    public void bindChildHolder(@NonNull ViewHolder holder, int parentPosition, int position) {
        ((ChildHolder)holder).setData(mGroupList.get(parentPosition).getMemberList().get(position));
    }

    static class ParentHolder extends ExpandableAdapter.ViewHolder {

        TextView mTvTitle;
        ImageView mIvStatus;

        public ParentHolder(@NonNull View itemView, ExpandableAdapter adapter) {
            super(itemView, adapter);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mIvStatus = itemView.findViewById(R.id.iv_status);
        }

        public void setData(Group data) {
            mTvTitle.setText(data.getName());
            mIvStatus.setSelected(data.isExpanded());
        }
    }

    static class ChildHolder extends ExpandableAdapter.ViewHolder {

        TextView mTvTitle;

        public ChildHolder(@NonNull View itemView, ExpandableAdapter adapter) {
            super(itemView, adapter);
            mTvTitle = itemView.findViewById(R.id.tv_title);
        }

        public void setData(GroupMember data) {
            mTvTitle.setText(data.getName());
        }
    }
}