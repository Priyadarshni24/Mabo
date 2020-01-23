package com.advengers.mabo.Cometchat.Contracts;

import android.content.Intent;

import com.advengers.mabo.Cometchat.Activity.SelectUserActivity;
import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.cometchat.pro.models.User;

import java.util.HashMap;
import java.util.Set;

public interface SelectUserActivityContract {


    interface SelectUserActivityView{

        void setScope(String scope);

        void setGUID(String guid);

        void setContactAdapter(HashMap<String, User> userHashMap);
    }

    interface SelectUserActivityPresenter extends BasePresenter<SelectUserActivityView> {

        void getIntent(Intent intent);

        void getUserList(int i);


        void addMemberToGroup(String guid, SelectUserActivity selectUserActivity, Set<String> keySet);
    }
}