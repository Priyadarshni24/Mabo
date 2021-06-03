package com.advengers.mabo.Cometchat.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_components.calls.call_manager.CometChatCallActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;

public class CallNotificationAction extends BroadcastReceiver {

    String TAG = "CallNotificationAction";
    @Override
    public void onReceive(Context context, Intent intent) {
        String sessionID = intent.getStringExtra(UIKitConstants.IntentStrings.SESSION_ID);
        int channelID = Integer.parseInt(intent.getStringExtra(UIKitConstants.IntentStrings.CHANNEL_ID));
      /*  if(MyFirebaseMessagingService.r.isPlaying())
        MyFirebaseMessagingService.r.stop();*/
        Log.e(TAG, "onReceive: " + intent.getStringExtra(UIKitConstants.IntentStrings.SESSION_ID));
        if (intent.getAction().equals("Answers"))
        {
            CometChat.acceptCall(sessionID, new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    Intent acceptIntent = new Intent(context, CometChatCallActivity.class);
                    acceptIntent.putExtra(UIKitConstants.IntentStrings.SESSION_ID,call.getSessionId());
                    acceptIntent.putExtra(UIKitConstants.IntentStrings.NOTIFYCALL,"true");
                    acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(acceptIntent);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(channelID);
                }

                @Override
                public void onError(CometChatException e) {
                    Toast.makeText(context,"Error "+e.getMessage(), Toast.LENGTH_LONG).show();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(channelID);
                }
            });
        }
        else {
            CometChat.rejectCall(sessionID, CometChatConstants.CALL_STATUS_REJECTED, new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    Log.e(TAG, "onSuccess: " + call.getCallStatus());
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(channelID);
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: " + e.getCode() + e.getDetails());
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(channelID);
                }
            });
        }
    }
}
