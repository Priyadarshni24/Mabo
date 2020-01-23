package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Adapter.GroupMemberListAdapter;
import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.cometchat.pro.models.GroupMember;

import java.util.HashMap;

public interface BannedMemberListContract {

    interface BannedMemberListView{

        void setAdapter(HashMap<String, GroupMember> list);
    }

    interface BannedMemberListPresenter extends BasePresenter<BannedMemberListView>
    {

        void initMemberList(String groupId, int limit, Context context);

        void reinstateUser(String uid, String groupId, GroupMemberListAdapter groupMemberListAdapter);

        void refresh(String GUID, int LIMIT, Context context);

        void addGroupEventListener(String listenerId, String groupId, GroupMemberListAdapter groupMemberListAdapter);
    }


}