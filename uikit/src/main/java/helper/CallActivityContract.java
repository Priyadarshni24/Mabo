package helper;

import android.content.Context;

import helper.Base.BasePresenter;


public interface CallActivityContract {

    interface CallActivityView{

    }

    interface CallActivityPresenter extends BasePresenter<CallActivityView> {

        void removeCallListener(String listener);

        void addCallListener(Context context, String listener);

    }
}
