package com.advengers.mabo.Cometchat.Presenters;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.advengers.mabo.Cometchat.Base.Presenter;
import com.advengers.mabo.Cometchat.Contracts.LoginActivityContract;
import com.advengers.mabo.Cometchat.Contracts.StringContract;

public class LoginAcitivityPresenter extends Presenter<LoginActivityContract.LoginActivityView> implements
LoginActivityContract.LoginActivityPresenter{

    private static final String TAG = "LoginAcitivityPresenter";

    @Override
    public void Login(Context context, String uid) {

        CometChat.login(uid, StringContract.AppDetails.API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "onSuccess: "+user.getUid());
                getBaseView().startCometChatActivity();
            }

            @Override
            public void onError(CometChatException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: "+e.getMessage());
            }

        });
    }
    @Override
    public void loginCheck() {

        try {
            if (CometChat.getLoggedInUser()!=null)
            {    if (isViewAttached())
               getBaseView().startCometChatActivity();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
