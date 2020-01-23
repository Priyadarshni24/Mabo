package com.advengers.mabo.Cometchat.Contracts;

import android.content.Context;

import com.advengers.mabo.Cometchat.Base.BasePresenter;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.advengers.mabo.Cometchat.Base.BasePresenter;

import java.util.List;


public interface RecentsContract {

    interface RecentsView{

        void setRecentAdapter(List<Conversation> conversationList);

        void updateUnreadCount(Conversation conversation);

        void setLastMessage(Conversation conversation);

        void setFilterList(List<Conversation> hashMap);

        void refreshConversation(BaseMessage message);
    }

    interface RecentsPresenter extends BasePresenter<RecentsView> {

          void fetchConversations(Context context);

          void addMessageListener(String presenceListener);

          void removeMessageListener(String string);

//          void searchConversation(String s);

          void updateConversation();
    }
}
