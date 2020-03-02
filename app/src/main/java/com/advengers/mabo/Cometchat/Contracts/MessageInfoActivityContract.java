package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;
import android.content.Intent;

import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.cometchat.pro.models.MessageReceipt;

import java.util.HashMap;
import java.util.List;

public interface  MessageInfoActivityContract {

    interface MessageInfoActivityView{

        void setType(String url, String type);

        void setSenderUID(String senderUID);

        void receiverUID(String receiverUID);

        void setReceiptsAdapter(List<MessageReceipt> messageReceipts);
    }

    interface MessageInfoActivityPresenter extends BasePresenter<MessageInfoActivityView>{
        void getIntent(Context context,Intent intent);
    }
}
