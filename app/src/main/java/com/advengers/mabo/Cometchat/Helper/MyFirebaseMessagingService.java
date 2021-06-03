package com.advengers.mabo.Cometchat.Helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.SearchActivity;
import com.advengers.mabo.Activity.SinglePostActivity;
import com.advengers.mabo.Cometchat.constants.AppConfig;
import com.advengers.mabo.R;
import com.advengers.mabo.Services.Config;
import com.advengers.mabo.Services.OnClearFromRecentService;
import com.advengers.mabo.Utils.LogUtils;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.uikit.ui_components.calls.call_manager.CometChatCallActivity;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


import static com.advengers.mabo.Interfaces.Keys.POSTID;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseService";
    private JSONObject json;
    private Intent intent;
    private int channelid;
    private Call call;
    private static final int REQUEST_CODE = 12;
    public static Ringtone r;
    private boolean isCall=false;
    NotificationCompat.Builder builder;
    NotificationChannel channel,channelmesg;
    int call_channelid=100;
    int message_channelid=105;
    int other_channelid = 102;

    public static void subscribeUser(String UID) {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_"+ CometChatConstants.RECEIVER_TYPE_USER +"_" +
                UID);
    }

    public static void unsubscribeUser(String UID) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.APP_ID + "_"+ CometChatConstants.RECEIVER_TYPE_USER +"_" +
                UID);
    }

    public static void subscribeGroup(String GUID) {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.AppDetails.APP_ID + "_"+ CometChatConstants.RECEIVER_TYPE_GROUP +"_" +
                GUID);
    }

    public static void unsubscribeGroup(String GUID) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.APP_ID + "_"+ CometChatConstants.RECEIVER_TYPE_GROUP +"_" +
                GUID);
    }

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "onNewToken: "+s);
    }

   @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            json = new JSONObject(remoteMessage.getData());
            Log.e(TAG, "JSONObject: "+json.toString());
            if(json.has("alert")) {
                BaseMessage baseMessage = CometChatHelper.processMessage(new JSONObject(remoteMessage.getData().get("message")));
                if (baseMessage instanceof Call) {
                    call = (Call) baseMessage;
                    isCall = true;
                } else {
                    isCall = false;
                }
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    if (isCall && call.getCallStatus().equals(CometChatConstants.CALL_STATUS_INITIATED)) {
                        intent = new Intent(getApplicationContext(), CometChatCallActivity.class);
                        intent.putExtra(UIKitConstants.IntentStrings.NAME, baseMessage.getSender().getName());
                        intent.putExtra(UIKitConstants.IntentStrings.UID, baseMessage.getSender().getUid());
                        intent.putExtra(UIKitConstants.IntentStrings.MYUID,baseMessage.getReceiverUid());
                        intent.putExtra(UIKitConstants.IntentStrings.SESSION_ID, call.getSessionId());
                        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
                        intent.setAction(baseMessage.getReceiverType());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setType("incoming");

                    } else {
                        intent = new Intent(getApplicationContext(), CometChatMessageListActivity.class);
                        intent.putExtra(UIKitConstants.IntentStrings.UID, baseMessage.getSender().getUid());
                        intent.putExtra(UIKitConstants.IntentStrings.MYUID,baseMessage.getReceiverUid());
                        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
                        intent.putExtra(UIKitConstants.IntentStrings.STATUS, baseMessage.getSender().getStatus());
                        intent.putExtra(UIKitConstants.IntentStrings.NAME, baseMessage.getSender().getName());
                        intent.putExtra(UIKitConstants.IntentStrings.TYPE, baseMessage.getReceiverType());
                    }
                } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
/*
                intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                intent.putExtra(StringContract.IntentStrings.INTENT_GROUP_ID,baseMessage.getReceiverUid());
                intent.putExtra(StringContract.IntentStrings.INTENT_GROUP_NAME,((Group)baseMessage.getReceiver()).getName());
                intent.putExtra(StringContract.IntentStrings.TYPE,baseMessage.getReceiverType());
*/
                }
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                showNotifcation(pendingIntent, baseMessage);
            }else{
                sendNotification(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void sendNotification(JSONObject json){
        try {

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(getApplicationContext(), SinglePostActivity.class);
        intent.putExtra(POSTID,json.getString("subtitle"));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(other_channelid), "Other Notification", NotificationManager.IMPORTANCE_MAX);

            //Configure Notification Channel
            notificationChannel.setDescription("Game Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = null;
            notificationBuilder = new NotificationCompat.Builder(this, String.valueOf(other_channelid))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentText(json.getString("message"))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(other_channelid, notificationBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showNotifcation(PendingIntent pendingIntent, BaseMessage baseMessage) {

        try {
            int m = (int) ((new Date().getTime()));
            String GROUP_ID = "group_id";

            if(isCall)
            {
                String message = json.getString("alert");
                if(!message.contains("Missed")&&!message.contains("Ended")&&!message.contains("Joined"))
                {
                    LogUtils.e("Coming in call notification");
                    m = call_channelid;
                }
                else
                    m = message_channelid;
            }else {
                m = message_channelid;
            }
            channelid = m;
            String content = json.getString("alert");
           /* if(isCall)
                content = content + " " + call.getCallStatus();*/
             builder = new NotificationCompat.Builder(this,String.valueOf(m))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(json.getString("title"))
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setColor(getResources().getColor(R.color.primaryColor))
                    .setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                    .setGroup(GROUP_ID)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(false);

                    //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

            LogUtils.e("I am coming in notification Mysdk"+Build.VERSION.SDK_INT +" Oreo"+ Build.VERSION_CODES.O);
        //   NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                LogUtils.e("I am coming in notification version");

                Uri sound = Settings.System.DEFAULT_RINGTONE_URI;//Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.ringing);  //Here is FILE_NAME is the name of file that you want to play
                int importance = NotificationManager.IMPORTANCE_HIGH;
                channel = new NotificationChannel(String.valueOf(m), "Call Notification", importance);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setLightColor(Color.BLUE);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                channelmesg = new NotificationChannel(String.valueOf(m), "Message Notification", importance);
                channelmesg.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channelmesg.setLightColor(Color.BLUE);
                channelmesg.enableLights(true);
                channelmesg.enableVibration(true);
                channelmesg.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);
                channelmesg.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


                if(isCall)
                {
                    String message = json.getString("alert");
                    if(!message.contains("Missed")&&!message.contains("Ended")&&!message.contains("Joined"))
                    {
                        LogUtils.e("I am coming in notification version call");

                        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                        switch( audio.getRingerMode() ){
                            case AudioManager.RINGER_MODE_NORMAL:
                                channel.setSound(Settings.System.DEFAULT_RINGTONE_URI, audioAttributes);
                                break;
                            case AudioManager.RINGER_MODE_SILENT:
                                channel.setVibrationPattern(new long[] { 1000L, 1000L });
                                break;
                            case AudioManager.RINGER_MODE_VIBRATE:
                                channel.setVibrationPattern(new long[] {1000L, 1000L });
                                break;
                        }


                        notificationManager.createNotificationChannel(channel);
                    }else {
                        LogUtils.e("I am coming in notification call message");

                        notificationManager.createNotificationChannel(channelmesg);
                    }
                }else{
                    LogUtils.e("I am coming in notification message");
                          notificationManager.createNotificationChannel(channelmesg);
                }


            }

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
            if (!isScreenOn) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
                wl.acquire(10000); //set your time in milliseconds
            }
         if (isCall){
                builder.setGroup(GROUP_ID+"Call");
                LogUtils.e("Coming in call case");
                if (json.getString("alert").equalsIgnoreCase("Incoming audio call") || json.getString("alert").equalsIgnoreCase("Incoming video call")) {
                    LogUtils.e("Coming inside incoming");
                   builder.setSound(Settings.System.DEFAULT_RINGTONE_URI);
                    builder.addAction(0, "Answers", PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, getCallIntent("Answers"), PendingIntent.FLAG_UPDATE_CURRENT));
                    builder.addAction(0, "Decline", PendingIntent.getBroadcast(getApplicationContext(), 1, getCallIntent("Decline"), PendingIntent.FLAG_UPDATE_CURRENT));
                    builder.setAutoCancel(true);
                }

                if(!CometChatCallActivity.visibity)//(!App.isCallactivityVisible())
                {

                    if(!OnClearFromRecentService.apprunning)
                    {

                        notificationManager.notify(channelid,builder.build());
                    }

                }
            }
            else {
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                if(CometChatMessageListActivity.visibity)//if(App.isActivityVisible())
                {
                    LogUtils.e(CometChatMessageListActivity.ID+"  "+baseMessage.getId()+" "+baseMessage.getSender().getUid());
                    if(CometChatMessageListActivity.ID==null)
                    {
                        notificationManager.notify(baseMessage.getId(), builder.build());
                    }else if(!CometChatMessageListActivity.ID.equals(baseMessage.getSender().getUid()))
                    {
                        notificationManager.notify(baseMessage.getId(), builder.build());
                    }
                }else
                    notificationManager.notify(baseMessage.getId(), builder.build());
             //   notificationManager.notify(0, summaryBuilder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void playNotificationSound()
    {
        try
        {

            Uri alarmSound = Settings.System.DEFAULT_RINGTONE_URI;//Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/ringing");
            r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            switch( audio.getRingerMode() ){
                case AudioManager.RINGER_MODE_NORMAL:
                  /*  if(!r.isPlaying())
                {
                    r.play();
                    //  r.setLooping(true);
                }*/
                   builder.setSound(alarmSound,AudioManager.STREAM_NOTIFICATION);
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000});
                break;
            }
        }
        catch (Exception e)
            {
            e.printStackTrace();
        }
    }
    private Intent getCallIntent(String title){
       LogUtils.e(" CH ID "+channelid);

        Intent callIntent = new Intent(getApplicationContext(), CallNotificationAction.class);
        callIntent.putExtra(UIKitConstants.IntentStrings.SESSION_ID,call.getSessionId());
        callIntent.putExtra(UIKitConstants.IntentStrings.CHANNEL_ID,String.valueOf(channelid));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setAction(title);
        return callIntent;
    }

}
