package helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.cometchat.pro.uikit.R;

import constant.StringContract;




public class CallNotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra(StringContract.IntentStrings.SESSION_ID);
       Intent notificationIntent = new Intent(this, CallActivity.class);
       // notificationIntent.putExtra(StringContract.IntentStrings.SESSION_ID, input);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "exampleServiceChannel")
                .setContentTitle(getString(R.string.app_name))
                .setContentText("OnGoing call")
                .setSmallIcon(R.drawable.ic_play_icon)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(getResources().getColor(android.R.color.holo_red_dark))
                .setAutoCancel(false)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
