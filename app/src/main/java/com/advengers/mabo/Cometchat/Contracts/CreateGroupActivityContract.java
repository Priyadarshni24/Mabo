package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.advengers.mabo.Cometchat.Base.BaseView;
import com.cometchat.pro.models.Group;


public interface CreateGroupActivityContract {

    interface CreateGroupView extends BaseView {

    }

    interface CreateGroupPresenter extends BasePresenter<CreateGroupView> {

        void createGroup(Context context, Group group);

    }
}
