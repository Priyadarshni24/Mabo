package com.advengers.mabo.Cometchat.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.Group;
import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.R;
import com.advengers.mabo.Cometchat.Utils.ColorUtils;
import com.advengers.mabo.Cometchat.Utils.FontUtils;
import com.advengers.mabo.Cometchat.Utils.MediaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupHolder> {

    private Context context;

    private HashMap<String ,Group> groupList;

    public GroupListAdapter(HashMap<String ,Group> groupList, Context context) {

        this.groupList=groupList;
        this.context=context;

    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.group_list_item, viewGroup, false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder groupHolder, int i) {

        Group group = new ArrayList<>(groupList.values()).get(i);


//      groupHolder.usersOnline.setText(String.valueOf(group.));

        groupHolder.itemView.setTag(R.string.group_id, group);
        groupHolder.itemView.setTag(R.string.group_name, group.getName());
        groupHolder.itemView.setTag(R.string.group_owner, group.getOwner());
        groupHolder.itemView.setTag(R.string.groupHolder, groupHolder);
//      groupHolder.itemView.setTag(R.string.group_moderator,group.get);

        groupHolder.groupNameField.setText(group.getName());

        groupHolder.groupNameField.setTypeface(FontUtils.robotoRegular);
        groupHolder.usersOnline.setTypeface(FontUtils.robotoRegular);

        Drawable drawable = context.getResources().getDrawable(R.drawable.cc_ic_group);

        if (group.getIcon() != null && !group.getIcon().isEmpty()) {

            Glide.with(context).load(group.getIcon()).into(groupHolder.imageViewGroupAvatar);

        } else {
                groupHolder.imageViewGroupAvatar.setCircleBackgroundColor(context.getResources().getColor(R.color.secondaryColor));
                groupHolder.imageViewGroupAvatar.setImageBitmap(MediaUtils.getPlaceholderImage(context, drawable));
        }

        if (group.getGroupType().equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
            groupHolder.protectedStatus.setVisibility(View.VISIBLE);
        } else {
            groupHolder.protectedStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (groupList != null) {
            return groupList.size();
        } else {
            return 0;
        }
    }
    public void refreshData(HashMap<String,Group> groupList) {
        this.groupList.putAll(groupList);
        notifyDataSetChanged();
    }

    public void setFilterList(HashMap<String,Group> groups) {
        groupList=groups;
        notifyDataSetChanged();
    }

    public void removeGroup(String guid) {
        groupList.remove(guid);
        notifyDataSetChanged();
    }


    public void resetList() {
        groupList.clear();
    }


    public class GroupHolder extends RecyclerView.ViewHolder {

        public TextView groupNameField, unreadCount, usersOnline, usersOnlineMessage;
        public CircleImageView imageViewGroupAvatar;
        ImageView protectedStatus;
        public RelativeLayout container;

        GroupHolder(@NonNull View itemView) {
            super(itemView);
            groupNameField = itemView.findViewById(R.id.textViewGroupName);
            unreadCount = itemView.findViewById(R.id.textViewGroupUnreadCount);
            usersOnline = itemView.findViewById(R.id.textViewGroupUsersOnline);
            imageViewGroupAvatar = itemView.findViewById(R.id.imageViewGroupAvatar);
            usersOnlineMessage = itemView.findViewById(R.id.textViewUsersOnlineMessage);
            protectedStatus = itemView.findViewById(R.id.imageViewGroupProtected);
            container = (RelativeLayout) itemView;
        }
    }

}