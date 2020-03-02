package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Adapter.GroupMemberListAdapter;
import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.cometchat.pro.models.GroupMember;

import java.util.HashMap;

public interface MemberListFragmentContract {


    interface MemberListFragmentView{

        void setAdapter(HashMap<String,GroupMember> groupMemberList);

        void removeMember(String uid);
    }

    interface MemberListFragmentPresenter extends BasePresenter<MemberListFragmentView> {

        void initMemberList(String guid, int LIMIT, Context context);

        void outCastUser(String uid, String groupGuid, GroupMemberListAdapter groupMemberListAdapter);

        void kickUser(String uid, String groupId, GroupMemberListAdapter groupMemberListAdapter);

        void refresh(String GUID,int LIMIT,Context context);

        void updateScope(String uid, String groupId, GroupMemberListAdapter groupMemberListAdapter, String scope);

        void addGroupEventListener(String listenerId,String groupId,GroupMemberListAdapter groupMemberListAdapter);

    }
}