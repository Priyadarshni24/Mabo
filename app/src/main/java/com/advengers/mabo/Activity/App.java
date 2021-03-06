package com.advengers.mabo.Activity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.advengers.mabo.Cometchat.constants.AppConfig;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.VolleySingleTone;
import com.advengers.mabo.Utils.Utils;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.bugfender.android.BuildConfig;
import com.bugfender.sdk.Bugfender;
import com.cloudinary.android.MediaManager;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_components.calls.call_manager.listener.CometChatCallListener;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.downloader.PRDownloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.msg91.sendotpandroid.library.internal.SendOTP;
import com.teliver.sdk.core.TLog;
import com.teliver.sdk.core.Teliver;


import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.HashMap;
import java.util.Map;

/*import com.bugfender.sdk.Bugfender;
import com.bugfender.android.BuildConfig;*/

/*@ReportsCrashes(mailTo = "techie@advengersmedia.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.resToastText)*/
public class App extends Application {


    private VolleySingleTone volleySingleTone;
    public static RequestQueue requestQueue;

    private ImageLoader mImageLoader;

    private static App appInstance;
    public static SharedPreferences app_data_sp;
    public static final String CHANNEL_ID = "exampleServiceChannel";
    public static Gson gson;
    private static boolean activityVisible,callactivityVisible;
    private static String roomid = null;
    private static final String TAG = "UIKitApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Bugfender.init(this, "lL0if3tiK3aSUUNa0jGZgx7ucZLRTijg", BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableLogcatLogging(); // optional, if you want logs automatically collected from logcat
        ACRA.init(this);
        Teliver.init(this,"cde1cb937533fe59df3f1e1af9f6d3e3");
        TLog.setVisible(true);
        SendOTP.initializeApp(this,"335467A4I8Tzy96017ddabP1");//"com.advengers.mabo");


        
        appInstance = this;
        PRDownloader.initialize(getApplicationContext());
        app_data_sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        volleySingleTone = VolleySingleTone.getsInstance();
        requestQueue = volleySingleTone.getMrequestqueue();
        requestQueue.getCache().clear();

      //  Teliver.init(this,"7f5d5d86c194deec1d3ba9ffde30a6a9");
        //mImageLoader = volleySingleTone.getImageLoader();
        FontsOverride.setDefaultFont(this, "DEFAULT", "poppinsregular.ttf");
     //   new FontUtils(this);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
        final Map cloudconfig = new HashMap();
        cloudconfig.put("cloud_name", Utils.cloud_name);
        cloudconfig.put("api_key", Utils.api_key);
        cloudconfig.put("api_secret", Utils.api_secret);
        MediaManager.init(getApplicationContext(), cloudconfig);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
//        Logger.enableLogs("4ddd5d736cf33ca31a0b4c72ae64b6d5");
        AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(AppConfig.AppDetails.REGION).build();
        CometChat.init(getappContext(), AppConfig.AppDetails.APP_ID,appSettings,new CometChat.CallbackListener<String>() {

            @Override
            public void onSuccess(String s) {
             //   Toast.makeText(App.this, "SetUp Complete", Toast.LENGTH_SHORT).show();
                UIKitSettings.setAppID(AppConfig.AppDetails.APP_ID);
                UIKitSettings.setAuthKey(AppConfig.AppDetails.API_KEY);
                CometChat.setSource("ui-kit","android","java");
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(App.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //Timber.d("onError: %s", e.getMessage());
            }

        });
        //createNotificationChannel();
        UIKitSettings uiKitSettings = new UIKitSettings(this);
        uiKitSettings.addConnectionListener(TAG);
        CometChatCallListener.addCallListener(TAG,this);
        createNotificationChannel();
     //   CometChatCallListener.addCallListener(getString(R.string.app_name),this);

    }


    public static App getAppInstance() {

        return appInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    /*public static void setFCMDetails(String fcm) {

        SharedPreferences.Editor editor = app_data_sp.edit();
        editor.putString(FCM, fcm);
        editor.commit();

    }
    public static String getFCMDetails() {
        return app_data_sp.getString(FCM, "");

    }*/
    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static String getRoomid()
    {
        return roomid;
    }

    public static void activityResumed(String room_id) {
        activityVisible = true;
        roomid = room_id;
    }

    public static void activityPaused(String room_id) {
        activityVisible = false;
        roomid = room_id;
    }

    public static void callvisible()
    {
       callactivityVisible = true;
    }

    public static void callinvisible()
    {
        callactivityVisible = false;
    }

    public static boolean isCallactivityVisible()
    {
        return callactivityVisible;
    }
    public static Context getappContext() {

        return appInstance.getApplicationContext();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("2", "App Notification", importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CometChat.removeConnectionListener(TAG);
        CometChatCallListener.removeCallListener(getString(R.string.app_name));
    }
}
