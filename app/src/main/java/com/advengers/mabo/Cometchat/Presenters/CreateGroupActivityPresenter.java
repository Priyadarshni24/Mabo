package com.advengers.mabo.Cometchat.Presenters;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.advengers.mabo.Cometchat.Utils.Logger;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.advengers.mabo.Cometchat.Base.Presenter;
import com.advengers.mabo.Cometchat.Contracts.CreateGroupActivityContract;
import com.advengers.mabo.Cometchat.Utils.CommonUtils;


public class CreateGroupActivityPresenter extends Presenter<CreateGroupActivityContract.CreateGroupView>
        implements CreateGroupActivityContract.CreateGroupPresenter {


    @Override
    public void createGroup(final Context context, Group group) {


        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {

                CommonUtils.startActivityIntent(group, context, true, null);

                Toast.makeText(context, group.getGroupType() + " group created ", Toast.LENGTH_SHORT).show();

                Log.d("createGroup", "onSuccess: "+group);
            }

            @Override
            public void onError(CometChatException e) {
                Logger.error("createGroup", "onError: "+e.getDetails());
                Logger.error("createGroup", "onError: "+e.getMessage());

                Toast.makeText(context, e.getDetails(), Toast.LENGTH_SHORT).show();
            }

        });

    }
}
