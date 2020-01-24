package com.advengers.mabo.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.MainActivity;
import com.advengers.mabo.Database.MyDBHandler;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.advengers.mabo.Interfaces.Keys.RECEIVED;
import static com.advengers.mabo.Interfaces.Keys.USER;

public class MyFirebaseMessagingServiceOld extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    private AtomicInteger c = new AtomicInteger(0);
    MyDBHandler db = new MyDBHandler(getBaseContext());
    String id,name,roomid;
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
       //     sendNotification(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
/*        LogUtils.e("Message "+remoteMessage.getNotification().getTitle());
        LogUtils.e("Message "+remoteMessage.getNotification().getBody());*/
        LogUtils.e("Message "+remoteMessage.getData().toString());
        LogUtils.e("Message "+remoteMessage.toString());
        if(remoteMessage.getData()!=null)
        {
            //getImage(remoteMessage);
            Map<String, String> data = remoteMessage.getData();
            Config.title = data.get("title");
            //Config.content = data.get("content");
            Config.imageUrl = data.get("imageUrl");
            Config.gameUrl = data.get("gameUrl");
            Config.message = data.get("message");
            if(data.containsKey("alert"))
                Config.message = data.get("alert");
            if(data.get("message").contains("sender"))
            {
                try {
                    JSONObject obj = new JSONObject(data.get("message"));
                    LogUtils.e("I am coming");
                    name = obj.getString("sender");//data.get("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
           /* String[] arrOfStr = Config.title.split("~", 3);
            int i=0;
            for (String a : arrOfStr)
            {
                if(i==0)
                    id = a.trim();
                else if(i==1)
                    name = a.trim();
                else if(i==2)
                    roomid = a.trim();
                i++;
                LogUtils.e(a.trim());
            }*/

            sendNotification();
           /* Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("message"))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId("0")
                    .build();
            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.notify(*//*notification id*//*0, notification);*/
        }
    }

    private void sendNotification(){
        //Bitmap bitmap

        /*NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.bigPicture(bitmap);*/

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

            //Configure Notification Channel
            notificationChannel.setDescription("Game Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(Config.title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(Config.message)
                .setContentIntent(pendingIntent)
               /* .setStyle(style)
                .setLargeIcon(bitmap)*/
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);
              //  db = new MyDBHandler(getBaseContext());
               // User user = new Gson().fromJson(Utils.getInstance(getBaseContext()).getString(USER),User.class);
           //     db.insertMessages(name,id, user.getId(),Config.message,RECEIVED,roomid);
        LogUtils.e(App.getRoomid()+" "+App.isActivityVisible()+" "+name);
        if(!App.isActivityVisible())
        {
            notificationManager.notify(createID(), notificationBuilder.build());
        }else if(App.isActivityVisible()&&!App.getRoomid().equals(name)){

            notificationManager.notify(createID(), notificationBuilder.build());
        }


    }
    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
    private void getImage(final RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        Config.title = data.get("title");
        Config.content = data.get("content");
        Config.imageUrl = data.get("imageUrl");
        Config.gameUrl = data.get("gameUrl");
        Config.message = data.get("message");
        //Create thread to fetch image from notification
        if(remoteMessage.getData()!=null){

            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Get image from data Notification
                    Picasso.get()
                            .load(Config.imageUrl)
                            .into(target);
                }
            }) ;
        }
    }
}
