package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.cometchat.pro.models.User;

import java.util.HashMap;

public interface BlockedUserListActivityContract {

    interface BlockedUserListActivityView{

        void setAdapter(HashMap<String, User> userHashMap);

        void userUnBlocked(String uid);
    }

    interface BlockedUserListActivityPresenter extends BasePresenter<BlockedUserListActivityView> {

        void getBlockedUsers();

        void unBlockUser(Context context, User user);
    }
}
