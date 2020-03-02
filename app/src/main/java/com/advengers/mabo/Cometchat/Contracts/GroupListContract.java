package com.advengers.mabo.Cometchat.Contracts;

import android.app.ProgressDialog;
import android.content.Context;

import com.advengers.mabo.Cometchat.Adapter.GroupListAdapter;
import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.advengers.mabo.Cometchat.Base.BaseView;
import com.cometchat.pro.models.Group;

import java.util.HashMap;

public interface GroupListContract {


    interface GroupView extends BaseView {

        void setGroupAdapter(HashMap<String,Group> groupList);

        void groupjoinCallback(Group group);

        void setFilterGroup(HashMap<String,Group> groups);
    }

    interface GroupPresenter extends BasePresenter<GroupView>
    {
        void initGroupView();

        void joinGroup(Context context, Group group, ProgressDialog progressDialog, GroupListAdapter groupListAdapter);

        void refresh();

        void searchGroup(String s);

        void deleteGroup(Context context,String guid,GroupListAdapter groupListAdapter);
    }
}
