package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Base.BasePresenter;


public interface LoginActivityContract {

    interface LoginActivityView {

        void startCometChatActivity();
    }

    interface LoginActivityPresenter extends BasePresenter<LoginActivityView> {

        void Login(Context context, String uid);

        void loginCheck();
    }
}
