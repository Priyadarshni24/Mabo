package com.advengers.mabo.Services;
import android.app.IntentService;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.app.Service ;
import android.content.Context;
import android.content.Intent ;
import android.os.Handler ;
import android.os.IBinder ;
import android.util.DebugUtils;
import android.util.Log ;

import androidx.core.app.NotificationCompat;

import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer ;
import java.util.TimerTask ;

public class NotificationService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 1;
    public static final String ACTION_1 = "action_1";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stopTimerTask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, Your_X_SECS * 1000); //
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (CometChat.getActiveCall() != null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId() != null) {
                            try{
                                JsonObject jsonObject = new JsonParser().parse(CometChat.getActiveCall().getSender().toString()).getAsJsonObject();
                                LogUtils.e("Name : "+jsonObject.get("name").getAsString());
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            createNotification("Name","Tap to join ongoing call");

                        } else
                            stopSelf();
                    }
                });
            }
        };
    }

    private void createNotification(String title,String content) {
        Context context = getApplicationContext();
        Intent action1Intent = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_1);

        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
     //   mBuilder.setTicker("Notification Listener Service Example");
        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(action1PendingIntent);
    /*    mBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_launcher_foreground,
                "Action 1", action1PendingIntent));
*/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(Your_X_SECS, mBuilder.build());
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            LogUtils.e("Received notification action: " + action);
          //  if (ACTION_1.equals(action)) {
                // TODO: handle action 1.
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
                utils.Utils.joinOnGoingCall(getApplicationContext());
         //   }
        }
    }
}