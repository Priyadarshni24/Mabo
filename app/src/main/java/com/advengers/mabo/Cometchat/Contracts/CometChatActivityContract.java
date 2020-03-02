package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Base.BasePresenter;


public interface CometChatActivityContract {

    interface CometChatActivityView {
    }

    interface CometChatActivityPresenter extends BasePresenter<CometChatActivityView> {

        void addMessageListener(Context context,String listenerId);

        void removeMessageListener(String listenerId);

        void addCallEventListener(Context context,String listenerId);

        void removeCallEventListener(String tag);

        void getBlockedUser(Context context);

        void logOut(Context context);

    }
}
