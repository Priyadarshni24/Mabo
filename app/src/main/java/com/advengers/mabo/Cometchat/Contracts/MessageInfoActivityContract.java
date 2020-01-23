package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;
import android.content.Intent;

import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.cometchat.pro.models.MessageReceipt;

import java.util.HashMap;

public interface  MessageInfoActivityContract {

    interface MessageInfoActivityView{

        void setType(String url, String type);

        void setSenderUID(String senderUID);

        void receiverUID(String receiverUID);

        void setReceiptsAdapter(HashMap<String, MessageReceipt> messageReceipts);

        void updateReciept(MessageReceipt messageReceipt);

        void setMessageId(int id);
    }

    interface MessageInfoActivityPresenter extends BasePresenter<MessageInfoActivityView> {
        void getIntent(Context context, Intent intent);

        void addmessagelistener(String tag, int id);

        void removemessagelistener(String tag);
    }
}
