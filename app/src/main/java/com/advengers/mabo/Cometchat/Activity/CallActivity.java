package com.advengers.mabo.Cometchat.Activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.advengers.mabo.Services.CallNotificationService;
import com.advengers.mabo.Services.MyNotificationPublisher;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.advengers.mabo.Cometchat.Contracts.CallActivityContract;
import com.advengers.mabo.Cometchat.Contracts.StringContract;
import com.advengers.mabo.Cometchat.Presenters.CallActivityPresenter;
import com.advengers.mabo.R;
import com.advengers.mabo.Cometchat.Utils.Logger;

import static com.advengers.mabo.Services.MyNotificationPublisher.NOTIFICATION_CHANNEL_ID;

public class CallActivity extends AppCompatActivity implements CallActivityContract.CallActivityView {

    private RelativeLayout callView;
    private String sessionId;
    private static final String TAG = "CallActivity";
    private boolean isFirstLaunch;
    private CallActivityContract.CallActivityPresenter callActivityPresenter;
    boolean backpressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        callActivityPresenter = new CallActivityPresenter();
        callActivityPresenter.attach(this);

        callView = findViewById(R.id.call_view);

       /* if (getIntent().hasExtra(StringContract.IntentStrings.SESSION_ID)) {
            sessionId = getIntent().getStringExtra(StringContract.IntentStrings.SESSION_ID);
            Logger.error(TAG, " sessionId hasExtra " + sessionId +"  "+CometChat.getActiveCall().getSessionId());
        }else*/
            sessionId = CometChat.getActiveCall().getSessionId();

        startCall(sessionId);
    }

    private void startCall(String sessionId) {
        Logger.error(TAG, " sessionId " + sessionId);
        //Logger.error(TAG, " sessionId " + sessionId);
        Intent serviceIntent = new Intent(this, CallNotificationService.class);
        if(Tools.isMyServiceRunning(CallNotificationService.class,CallActivity.this))
        stopService(serviceIntent);
        CometChat.startCall(CallActivity.this, sessionId, callView, new CometChat.OngoingCallListener() {
            @Override
            public void onUserJoined(User user) {

                Logger.error(TAG, " Name " + user.getName());
            }

            @Override
            public void onUserLeft(User user) {
                Log.d(TAG, "onUserLeft: "+user.getName());
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: "+e.getMessage());
            }

            @Override
            public void onCallEnded(Call call) {
                Log.d(TAG, "onCallEnded: "+call.toString());
                finish();
            }
        });
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        callActivityPresenter.addCallListener(this,TAG);
        if (isFirstLaunch&&sessionId!=null){
            startCall(sessionId);
        }else {
            isFirstLaunch = true;
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        callActivityPresenter.removeCallListener(TAG);
    }

    @Override
    public void onBackPressed() {
    //    scheduleNotification(getNotification( "1 second delay" ) , 100 ) ;
      //  LogUtils.e("I am coming "+CometChat.getActiveCall().getAction());

        backpressed = true;
     /*   Intent i = new Intent(CallActivity.this, MyNotificationPublisher.class);
        sendBroadcast(i);*/
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if(CometChat.getActiveCall().getAction()!=null)
        {
            if(CometChat.getActiveCall().getAction().equalsIgnoreCase("ongoing"))
            {
                Intent serviceIntent = new Intent(this, CallNotificationService.class);
                serviceIntent.putExtra(StringContract.IntentStrings.SESSION_ID, sessionId);
                ContextCompat.startForegroundService(this, serviceIntent);
            }
        }
        super.onDestroy();
    }

    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }
    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, "default" ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( false ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
}
