package com.advengers.mabo.Cometchat.Presenters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.advengers.mabo.Cometchat.Activity.OneToOneChatActivity;
import com.advengers.mabo.Cometchat.Base.Presenter;
import com.advengers.mabo.Cometchat.Contracts.OneToOneActivityContract;
import com.advengers.mabo.Cometchat.Contracts.StringContract;
import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.R;
import com.advengers.mabo.Cometchat.Utils.CommonUtils;
import com.advengers.mabo.Cometchat.Utils.Logger;
import com.advengers.mabo.Cometchat.Utils.MediaUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class OneToOneActivityPresenter extends Presenter<OneToOneActivityContract.OneToOneView>
        implements OneToOneActivityContract.OneToOnePresenter {


    private Context context;

    private MessagesRequest messagesRequest;

    private static final String TAG = "OneToOneActivityPresent";

    private Intent intent;

    @Override
    public void sendMessage(String message, String uId) {

       TextMessage textMessage = new TextMessage(uId, message, CometChatConstants.RECEIVER_TYPE_USER);

        if (OneToOneChatActivity.isReply) {
            textMessage.setMetadata(OneToOneChatActivity.metaData);
            OneToOneChatActivity.hideReply();
        }

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
             @Override
            public void onSuccess(TextMessage textMessage1) {
                if (isViewAttached()) {
                    MediaUtils.playSendSound(context, R.raw.send);
                    getBaseView().addSendMessage(textMessage1);
                    Log.d(TAG, "sendMessage onSuccess: "+textMessage1.toString());
                }
            }

             @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "sendMessage onError: " + e.getMessage());
                showToast(e.getMessage());
            }
        });

    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public void handleIntent(Intent intent) {

        this.intent=intent;

        if (intent.hasExtra(StringContract.IntentStrings.USER_ID)) {
            String uid = intent.getStringExtra(StringContract.IntentStrings.USER_ID);
            if (isViewAttached()) {
                getBaseView().setContactUid(uid);
                OneToOneChatActivity.contactId = uid;
            }

            CometChat.getUser(uid, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Timber.d("getUser onSuccess: %s", user.toString());
                        if (isViewAttached())
                        getBaseView().setPresence(user);

                }

                @Override
                public void onError(CometChatException e) {
                    Timber.d("getUser onError: %s", e.getMessage());
                   showToast(e.getMessage());
                }
            });
        }
        if (intent.hasExtra(StringContract.IntentStrings.USER_AVATAR)) {
            if (isViewAttached())
                getBaseView().setAvatar(intent.getStringExtra(StringContract.IntentStrings.USER_AVATAR));
        }
        if (intent.hasExtra(StringContract.IntentStrings.USER_NAME)) {
            if (isViewAttached())
                getBaseView().setTitle(intent.getStringExtra(StringContract.IntentStrings.USER_NAME));
        }
    }

    @Override
    public void addMessageReceiveListener(final String contactUid) {

        CometChat.addMessageListener(context.getString(R.string.message_listener), new CometChat.MessageListener() {

            @Override
            public void onTextMessageReceived(TextMessage message) {
                if (isViewAttached()) {
                    Log.d(TAG, "onTextMessageReceived: "+message.toString());
                    if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)&&message.getSender().getUid().equals(contactUid)) {
                        MediaUtils.playSendSound(context, R.raw.receive);
                        Log.d(TAG, "onTextMessageReceived: "+message.toString());
                        if (message.getReadAt()==0){
                            Log.d(TAG, "onTextMessageReceived: getReadAt "+message.toString());
                            CometChat.markAsRead(message.getId(),message.getSender().getUid(),message.getReceiverType());
                        }
                        getBaseView().addSendMessage(message);
                    }
                }
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                if (isViewAttached()) {
                    if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)
                            && message.getSender().getUid().equals(contactUid)) {
                        if (message.getReadAt()==0){
                            Log.d(TAG, "onMediaMessageReceived: getReadAt "+message.toString());
                            CometChat.markAsRead(message.getId(),message.getSender().getUid(),message.getReceiverType());
                        }
                        MediaUtils.playSendSound(context, R.raw.receive);
                        getBaseView().addSendMessage(message);
                    }
                }
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                if (isViewAttached()) {
                    if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)
                            && message.getSender().getUid().equals(contactUid)) {
                        if (message.getReadAt()==0){
                            Log.d(TAG, "onMediaMessageReceived: getReadAt "+message.toString());
                            CometChat.markAsRead(message.getId(),message.getSender().getUid(),message.getReceiverType());
                        }
                        MediaUtils.playSendSound(context, R.raw.receive);
                        getBaseView().addSendMessage(message);
                    }
                }
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)
                        &&typingIndicator.getSender().getUid().equals(contactUid)) {
                    getBaseView().setTyping();
                    Log.d(TAG, "onTypingStarted: ");
                }
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)
                        &&typingIndicator.getSender().getUid().equals(contactUid)) {
                    getBaseView().endTyping();
                    Log.d(TAG, "onTypingEnded:");
                }
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                if (messageReceipt.getReceivertype().equals(CometChatConstants.RECEIVER_TYPE_USER)
                        &&messageReceipt.getSender().getUid().equals(contactUid)) {
                    getBaseView().setMessageDelivered(messageReceipt);
                    Log.d(TAG, "onMessagesDelivered: " + messageReceipt);
                }
            }

            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                if (messageReceipt.getReceivertype().equals(CometChatConstants.RECEIVER_TYPE_USER)
                        &&messageReceipt.getSender().getUid().equals(contactUid)) {

                    getBaseView().onMessageRead(messageReceipt);
                    Log.d(TAG, "onMessagesRead: " + messageReceipt);

                }
            }


            @Override
            public void onMessageEdited(BaseMessage message) {
                if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)
                        &&message.getSender().getUid().equals(contactUid)) {
                    getBaseView().setEditedMessage(message);
                    Log.d(TAG, "onMessageEdited: " + message.toString());
                }
            }

            @Override
            public void onMessageDeleted(BaseMessage message) {
                if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)
                        && message.getSender().getUid().equals(contactUid)) {
                    getBaseView().setDeletedMessage(message);
                    Log.d(TAG, "onMessageDeleted: " + message.toString());
                }
            }

        });



    }

    @Override
    public void sendMediaMessage(File filepath, String receiverUid, String type) {
        final ProgressDialog dialog = new ProgressDialog(context);
        if(type.equals(CometChatConstants.MESSAGE_TYPE_FILE)||type.equals(CometChatConstants.MESSAGE_TYPE_VIDEO))
        {
            dialog.setCancelable(false);
            dialog.setMessage("Uploading");
            dialog.show();
        }
        final MediaMessage mediaMessage = new MediaMessage(receiverUid, filepath, type,
                CometChatConstants.RECEIVER_TYPE_USER);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("path", filepath.getAbsolutePath());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mediaMessage.setMetadata(jsonObject);

        if (OneToOneChatActivity.isReply) {
            mediaMessage.setMetadata(OneToOneChatActivity.metaData);
            OneToOneChatActivity.hideReply();
        }

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Log.d(TAG, "sendMediaMessage onSuccess: "+mediaMessage.toString());
                MediaUtils.playSendSound(context, R.raw.send);
                getBaseView().addMessage(mediaMessage);
            }

            @Override
            public void onError(CometChatException e) {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Log.d(TAG, "sendMediaMessage onError: "+e.getMessage());
                showToast(e.getMessage());
            }

        });
    }



    @Override
    public void sendLocationMessage(Location location, String receiverUid, String type) {
        final ProgressDialog dialog = new ProgressDialog(context);
        String customType = "LOCATION";
        JSONObject customData = new JSONObject();
        try {
            customData.put("latitude",location.getLatitude());
            customData.put("longitude",location.getLongitude());

            CustomMessage customMessage = new CustomMessage(receiverUid,type,customType, customData);
            customMessage.setSubType("location");
            CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
                @Override
                public void onSuccess(CustomMessage customMessage) {
                    Log.d(TAG, customMessage.toString());
                    MediaUtils.playSendSound(context, R.raw.send);
                    getBaseView().addMessage(customMessage);
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, e.getMessage());
                    showToast(e.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Log.d(TAG, "sendMediaMessage onSuccess: "+mediaMessage.toString());
                MediaUtils.playSendSound(context, R.raw.send);
                getBaseView().addMessage(mediaMessage);
            }

            @Override
            public void onError(CometChatException e) {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                Log.d(TAG, "sendMediaMessage onError: "+e.getMessage());
                showToast(e.getMessage());
            }

        });*/
    }


    @Override
    public void fetchPreviousMessage(String contactUid, int limit) {

        List<BaseMessage> list=new ArrayList<>();
        if (messagesRequest == null) {

            messagesRequest = new MessagesRequest.MessagesRequestBuilder().setUID(contactUid).setLimit(limit).build();
        }

            messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
                @Override
                public void onSuccess(List<BaseMessage> baseMessages) {
                    if (isViewAttached()) {
                        for (BaseMessage baseMessage : baseMessages) {

                            Log.d(TAG, "fetchPreviousMessage onSuccess: delete "+baseMessage.getDeletedAt());

                            Log.d(TAG, "fetchPreviousMessage onSuccess: "+baseMessage.toString());
                            if (!baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)&&baseMessage.getDeletedAt()==0) {

                                list.add(baseMessage);
                            }
                             if (baseMessage.getSender().getUid().equals(contactUid)){
                                 CometChat.markAsRead(baseMessage.getId(), baseMessage.getSender().getUid(), baseMessage.getReceiverType());
                             }
                        }
                        getBaseView().setAdapter(list);
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "fetchPreviousMessage onError: "+e.getMessage());
                    showToast(e.getMessage());
                }

            });
    }

    @Override
    public void getOwnerDetail() {

        User user = CometChat.getLoggedInUser();
        if (user != null) {
            if (isViewAttached())
                getBaseView().setOwnerDetail(user);
        }
    }

    @Override
    public void addPresenceListener(String presenceListener) {
        CometChat.addUserListener(presenceListener, new CometChat.UserListener() {
            @Override
            public void onUserOnline(User user) {
                if (isViewAttached())
                    getBaseView().setPresence(user);
            }

            @Override
            public void onUserOffline(User user) {
                if (isViewAttached())
                    getBaseView().setPresence(user);
            }
        });
    }

    @Override
    public void sendCallRequest(Context context, String contactUid, String receiverTypeUser, String callType) {

        Call call = new Call(contactUid, receiverTypeUser, callType);
        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                CommonUtils.startCallIntent(context, ((User) call.getCallReceiver()), call.getType(), true, call.getSessionId());
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void addCallEventListener(String callEventListener) {

        CometChat.addCallListener(callEventListener, new CometChat.CallListener() {
            @Override
            public void onIncomingCallReceived(Call call) {

                if(call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {

                    CommonUtils.startCallIntent(context, (User) call.getCallInitiator(), call.getType(),
                            false, call.getSessionId());
                }
                else if(call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {

                    CommonUtils.startCallIntent(context, (Group) call.getCallReceiver(), call.getType(),
                            false, call.getSessionId());
                }
            }
            @Override
            public void onOutgoingCallAccepted(Call call) {

            }
            @Override
            public void onOutgoingCallRejected(Call call) {

            }
            @Override
            public void onIncomingCallCancelled(Call call) {

            }
        });
    }

    @Override
    public void removePresenceListener(String listenerId) {
        CometChat.removeUserListener(listenerId);
    }

    @Override
    public void removeCallListener(String listenerId) {
        CometChat.removeCallListener(listenerId);
    }

    @Override
    public void sendTypingIndicator(String receiverId) {
        TypingIndicator typingIndicator = new TypingIndicator(receiverId, CometChatConstants.RECEIVER_TYPE_USER);
        Log.d(TAG, "sendTypingIndicator: ");
        CometChat.startTyping(typingIndicator);
    }

    @Override
    public void endTypingIndicator(String receiverId) {
        Log.d(TAG, "endTypingIndicator: ");
        TypingIndicator typingIndicator = new TypingIndicator(receiverId, CometChatConstants.RECEIVER_TYPE_USER);
        CometChat.endTyping(typingIndicator);
    }

    @Override
    public void blockUser(String contactId) {

        List<String> uids=new ArrayList<>();
          uids.add(contactId);

         CometChat.blockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Toast.makeText(context, "Blocked Successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, " blockUsers onSuccess: "+stringStringHashMap.toString());
                if (intent!=null){
                    handleIntent(intent);
                }
            }

            @Override
            public void onError(CometChatException e) {
                showToast(e.getMessage());
            }
        });
    }

    @Override
    public void unBlockUser(String uid, Context context) {
        List<String> uids = new ArrayList<>();
        uids.add(uid);
        CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Toast.makeText(context, "Unblocked Successfully", Toast.LENGTH_SHORT).show();
                getBaseView().hideBanner();
                if (intent!=null){
                    handleIntent(intent);
                }
            }

            @Override
            public void onError(CometChatException e) {
                showToast(e.getMessage());
            }
        });
    }

    @Override
    public void deleteMessage(BaseMessage baseMessage) {

        CometChat.deleteMessage(baseMessage.getId(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage) {
                Log.d(TAG, "onSuccess: deleteMessage "+baseMessage.toString());
                getBaseView().setDeletedMessage(baseMessage);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: deleteMessage");
                showToast(e.getMessage());
            }
        });
    }

    @Override
    public void editMessage(BaseMessage baseMessage,String newMessage) {
        TextMessage textMessage=new TextMessage(baseMessage.getReceiverUid(),newMessage,baseMessage.getReceiverType());
       textMessage.setId(baseMessage.getId());
      CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
          @Override
          public void onSuccess(BaseMessage baseMessage) {
              Log.d(TAG, "editMessage onSuccess: "+baseMessage.toString());
              getBaseView().setEditedMessage(baseMessage);
          }

          @Override
          public void onError(CometChatException e) {
             showToast(e.getMessage());
          }
      });
    }

    @Override
    public void searchMessage(String s,String UID) {
        List<BaseMessage> list=new ArrayList<>();

        messagesRequest=null;
        MessagesRequest searchMessageRequest=new MessagesRequest.MessagesRequestBuilder()
                .setUID(UID).setSearchKeyword(s).setCategory(CometChatConstants.CATEGORY_MESSAGE).setLimit(30).build();

        searchMessageRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
                @Override
                public void onSuccess(List<BaseMessage> baseMessages) {
                    if (isViewAttached()) {
                        for (BaseMessage baseMessage : baseMessages) {
                            Log.d(TAG, "searchMessage onSuccess: delete "+baseMessage.getDeletedAt());
                            if (!baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)&&baseMessage.getDeletedAt()==0) {
                                list.add(baseMessage);
                            }
                        }
                        getBaseView().setFilterList(list);
                    }
                }

                @Override
                public void onError(CometChatException e) {
                   showToast(e.getMessage());
                }
            });

    }

    @Override
    public void refreshList(String contactUid, int limit) {
        messagesRequest=null;
        fetchPreviousMessage(contactUid,limit);
    }

    @Override
    public void removeMessageLisenter(String listenerId) {
        CometChat.removeMessageListener(listenerId);
    }

    @Override
    public void setContactPic(OneToOneChatActivity oneToOneChatActivity, String avatar, CircleImageView circleImageView) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(oneToOneChatActivity.getResources().getDrawable(R.drawable.ic_broken_image));
        Glide.with(oneToOneChatActivity).load(avatar).apply(requestOptions).into(circleImageView);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
