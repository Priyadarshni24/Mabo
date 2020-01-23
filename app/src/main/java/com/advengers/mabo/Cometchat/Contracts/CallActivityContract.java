package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Base.BasePresenter;


public interface CallActivityContract {

    interface CallActivityView{

    }

    interface CallActivityPresenter extends BasePresenter<CallActivityView> {

        void removeCallListener(String listener);

        void addCallListener(Context context, String listener);

    }
}
