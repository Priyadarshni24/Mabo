package com.advengers.mabo.Cometchat.Presenters;

import android.content.Context;

import com.advengers.mabo.Utils.LogUtils;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.advengers.mabo.Cometchat.Base.Presenter;
import com.advengers.mabo.Cometchat.Contracts.RecentsContract;
import com.advengers.mabo.Cometchat.Utils.Logger;

import java.util.List;

import timber.log.Timber;


public class RecentsListPresenter extends Presenter<RecentsContract.RecentsView>
        implements RecentsContract.RecentsPresenter {

    private ConversationsRequest conversationRequest;

    private static final String TAG = "ContactsListPresenter";

    @Override
    public void fetchConversations(Context context) {
       LogUtils.e("I am coming");
        conversationRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(30).build();
        conversationRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Logger.error(TAG, " " + conversations.size());
                LogUtils.e("I am coming"+ conversations.size());
                getBaseView().setRecentAdapter(conversations);
            }

            @Override
            public void onError(CometChatException e) {
                Timber.d("fetchNext onError: %s", e.getMessage());
                LogUtils.e("I am coming"+e.getMessage());
            }
        });
    }

    @Override
    public void addMessageListener(String messageListener) {
        CometChat.addMessageListener(messageListener, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                getBaseView().refreshConversation(message);

            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                getBaseView().refreshConversation(message);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                getBaseView().refreshConversation(message);
            }

        });
    }

    @Override
    public void removeMessageListener(String messageListener) {
        CometChat.removeMessageListener(messageListener);
    }


    @Override
    public void updateConversation() {

    }

    @Override
    public void refreshConversations(Context context) {
        conversationRequest=null;
        getBaseView().clearConversations();
        fetchConversations(context);
    }
}