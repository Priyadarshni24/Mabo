package com.advengers.mabo.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.advengers.mabo.Adapter.InterestAdapter;
import com.advengers.mabo.Adapter.RecentLocationAdapter;
import com.advengers.mabo.Cometchat.constants.AppConfig;
import com.advengers.mabo.Database.MyDBHandler;
import com.advengers.mabo.Fragments.ChatFragment;
import com.advengers.mabo.Fragments.MyFragment;
import com.advengers.mabo.Fragments.NotificationFragment;
import com.advengers.mabo.Fragments.PeoplesFragment;
import com.advengers.mabo.Fragments.SettingsFragment;
import com.advengers.mabo.Fragments.TrendingFragment;
import com.advengers.mabo.Interfaces.Keys;
import com.advengers.mabo.Location.LocationTrack;
import com.advengers.mabo.Model.Apiresponse;
import com.advengers.mabo.Model.Interest;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Services.NotificationService;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ActivityDashboardBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.CallUtils;
import com.cometchat.pro.uikit.ui_settings.UISettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.aabhasjindal.otptextview.OtpTextView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import static android.view.View.GONE;
import static com.advengers.mabo.Activity.MainActivity.COMETCHATURL;
import static com.advengers.mabo.Activity.MainActivity.FCM;
import static com.advengers.mabo.Activity.MainActivity.GETINTREST;
import static com.advengers.mabo.Activity.MainActivity.LOCATION;
import static com.advengers.mabo.Activity.MainActivity.LOGOUT;
import static com.advengers.mabo.Activity.MainActivity.PUTINTREST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.USERDETAILS;
import static com.advengers.mabo.Interfaces.Keys.AUTHPASSWORD;
import static com.advengers.mabo.Interfaces.Keys.AUTHUSERNAME;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.INTEREST;
import static com.advengers.mabo.Interfaces.Keys.INTERESTJSON;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.PIN;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USER;

public class DashboardActivity extends MyActivity implements EasyPermissions.PermissionCallbacks,
                                                             LocationListener,
                                                             InterestAdapter.SelectedInterest,MyFragment.ProfileClick{
    FragmentTransaction transaction;
    FragmentManager manager;
  //  ActivityDashboardBinding binding;
    private static final int RC_LOCATION = 1 ;
    LocationTrack locationTrack;
    String SelectedInterest="";
   // private CometChatActivityContract.CometChatActivityPresenter cometChatActivityPresenter;
    Context context;
    int status = 0, attempt =0;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private RelativeLayout onGoingCallView;
    String pin= "";
    BottomNavigationView navView;
    private TextView onGoingCallTxt;
    TextView txtname;
    TextView txtpostcount;
    TextView txtPlace,btnstatus;
    ImageView imgProfile;
    private ImageView onGoingCallClose;
     RelativeLayout onprofileView;
     RecyclerView locationlist;
    LocationManager locationManager;
    Location loc;
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    String token;
    String TAG = "Mabo";
    MyDBHandler db;
    MenuItem mitem;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mitem = item;
            switch (item.getItemId()) {
                case R.id.navigation_trending:
                    manager=getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.container, new TrendingFragment()).commit();
                    return true;
                case R.id.navigation_people:
                    getUser();
                    LogUtils.e(user.getLatitude()+" location "+user.getLongitude());
                    if(!user.getLatitude().isEmpty()&&!user.getLongitude().isEmpty())
                    {
                        manager=getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.container, new PeoplesFragment()).commit();
                    }
                    return true;
                case R.id.navigation_notify:
                    //binding.message.setText(R.string.title_notifications);
/*                    CometChat.deleteMessage(788,new CometChat.CallbackListener<BaseMessage>() {
                        @Override
                        public void onSuccess(BaseMessage message) {
                            Log.e(TAG, "Message deleted successfully at : " + message.getDeletedAt());
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.e(TAG, e.getMessage());
                        }

                    });
                    startActivity(new Intent(DashboardActivity.this, CometChatUnified.class));
                    overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );*/
                    manager=getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.container, new NotificationFragment()).commit();
                    return true;
                case R.id.navigation_chat:
                    manager=getSupportFragmentManager();
                    getUser();
                    Fragment fragment = new ChatFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(UIKitConstants.IntentStrings.MYUID, "mabo"+ user.getId());
                    fragment.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.container, fragment).commit();
                    return true;
                case R.id.navigation_settings:
                    manager=getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        navView = (BottomNavigationView)findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_settings);
        context = this;

        manager=getSupportFragmentManager();//create an instance of fragment manager
        transaction=manager.beginTransaction();//create an instance of Fragment-transaction
        transaction.add(R.id.container,new SettingsFragment());
        transaction.commitAllowingStateLoss();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            LogUtils.e("MyApp "+ " Deep link clicked " + uri);
            Intent post = new Intent(DashboardActivity.this,SinglePostActivity.class);
            post.putExtra("URI",uri);
            startActivity(post);
        }
        FirebaseApp.initializeApp(DashboardActivity.this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            LogUtils.e( "getInstanceId failed "+ task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        new UpdateToken().execute();

                    }
                });

        getUser();
        db = new MyDBHandler(this);
      //  db.Createfriends();
        // db.deleteallFriends();
     //   db.insertFriends(user);

       // LogUtils.e("DB "+db.insertFriends(user));
     //   db.getFriendsList();
      //  db.dropfriends();
        String credentials = AUTHUSERNAME+":"+AUTHPASSWORD;
        String auth = "Basic "
                + Base64.encodeToString(credentials.getBytes(),
                Base64.NO_WRAP);
        LogUtils.e(auth);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        RequiresPermission();
        getInterest();
      //  getCometUserList();
        //Check Ongoing Call
        View ongoingview = (RelativeLayout)findViewById(R.id.ongoing_call_view);
        onGoingCallView =(RelativeLayout) ongoingview.findViewById(R.id.ongoing_call_view);
        onGoingCallClose = (ImageView)ongoingview.findViewById(R.id.close_ongoing_view);
        onGoingCallTxt = (TextView)ongoingview.findViewById(R.id.ongoing_call);
        View layout = (RelativeLayout)findViewById(R.id.profile_view);
        onprofileView = (RelativeLayout)layout.findViewById(R.id.profile_view);
         txtname = layout.findViewById(R.id.txt_name);
         txtpostcount = layout.findViewById(R.id.txt_postcount);
         btnstatus = layout.findViewById(R.id.btn_status);
         txtPlace = layout.findViewById(R.id.txt_place);
         imgProfile = layout.findViewById(R.id.img_profile);
         locationlist = layout.findViewById(R.id.list_recentlyvisited);
        checkOnGoingCall();
        onCheckPIN();

    }
    void onCheckPIN()
    {
        attempt = 0;
         final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_pinlock, null);
        dialogBuilder.setView(dialogView);

        TextView txt_title = (TextView) dialogView.findViewById(R.id.txt_title);
        OtpTextView edt_pin = (OtpTextView) dialogView.findViewById(R.id.edt_pin);
        Button Verify = (Button)dialogView.findViewById(R.id.btn_verify);
        if(Utils.getInstance(DashboardActivity.this).getInt(PIN)==0)
        {
            txt_title.setText(getString(R.string.enter_pin));
            Verify.setText(getString(R.string.str_addpin));
        }else{
            txt_title.setText(getString(R.string.str_enterpin));
            Verify.setText(getString(R.string.str_verifytext));
        }
        final AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Verify.getText().equals(getString(R.string.str_addpin)))
                {
                    if(edt_pin.getOTP().equals(""))
                    {
                        Utils.showToast(getString(R.string.enter_pin),context);
                    }else {
                        Verify.setText(getString(R.string.str_confirmpin));
                        pin = edt_pin.getOTP();
                        edt_pin.setOTP("");
                    }
                }else if(Verify.getText().equals(getString(R.string.str_confirmpin)))
                {
                    LogUtils.e("PIN "+edt_pin.getOTP()+" "+pin);
                    if(edt_pin.getOTP().equals(pin))
                    {
                        Utils.getInstance(context).setInt(PIN,Integer.parseInt(edt_pin.getOTP()));
                        dialog.dismiss();
                    }else{
                        edt_pin.setOTP("");
                        Utils.showToast(getString(R.string.str_pinmismatch),context);
                    }
                }else if(Verify.getText().equals(getString(R.string.str_verifytext))) {
                    if(edt_pin.getOTP().equals(""))
                    {
                        Utils.showToast(getString(R.string.enter_pin),context);
                    }else if(edt_pin.getOTP().equals(Utils.getInstance(context).getInt(PIN)+""))
                    {
                        dialog.dismiss();
                    }else{
                        attempt = attempt + 1;
                        if(attempt==3)
                        {
                            dialog.dismiss();
                            onLoadProgress(context);
                            new MyDBHandler(context).dropfriends();
                            CometChat.logout(new CometChat.CallbackListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    onLogout();
                                }

                                @Override
                                public void onError(CometChatException e) {
                                    LogUtils.e( "onError: "+e.getMessage());
                                }
                            });
                        }else
                        {
                            edt_pin.setOTP("");
                            Utils.showToast(" Attempt "+attempt+". More that 3 incorrect attempt makes logout ",context);
                        }
                    }
                }
            }
        });


        dialog.show();
    }
    private void onLogout() {
        String URL = SERVER_URL+LOGOUT;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().logout(user.getId())
                , logoutlister,error_listener));
    }
    Response.Listener logoutlister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        if(login.has(DATA))
                        {
                            showWarning(login.getString(MESSAGE),context,DashboardActivity.this);
                        }else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.AppDetails.AppID_user_UID);
                            Utils.getInstance(getApplicationContext()).clearPref();
                            User.logout();
                            startActivity(new Intent(getApplicationContext(), SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private void checkOnGoingCall() {
        LogUtils.e(CometChat.getActiveCall()+" check ongoing call");
     //   onGoingCallView.setVisibility(View.VISIBLE);
        if(CometChat.getActiveCall()!=null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId()!=null) {
          //  Utils.showToast("Coming in ongoing call",context);

           if(onGoingCallView!=null)
           {
               onGoingCallView.setVisibility(View.VISIBLE);
           }
            if(onGoingCallTxt!=null) {
                onGoingCallTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGoingCallView.setVisibility(View.GONE);
                         CallUtils.joinOnGoingCall(context,CometChat.getActiveCall());
                    }
                });
            }
            if(onGoingCallClose!=null) {
                onGoingCallClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGoingCallView.setVisibility(GONE);
                    }
                });
            }
        } else if (CometChat.getActiveCall()!=null){
            if (onGoingCallView!=null)
                onGoingCallView.setVisibility(GONE);
            Utils.showToast("No ongoing call",context);
            Log.e(TAG, "checkOnGoingCall: "+CometChat.getActiveCall().toString());
        }
        //onGoingCallView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //startService( new Intent( this, NotificationService. class )) ;
    }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
   //     super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    void openLocationSettings()
    {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_warning, null);
        dialogBuilder.setView(dialogView);

        TextView txt_title = (TextView) dialogView.findViewById(R.id.txt_title);
        LinearLayout ll_ok = (LinearLayout) dialogView.findViewById(R.id.ll_ok);
        LinearLayout ll_ok_cancel = (LinearLayout) dialogView.findViewById(R.id.ll_ok_cancel);
        TextView txt_content = (TextView) dialogView.findViewById(R.id.txt_content);
        txt_title.setText(getString(R.string.str_enable_location));
        txt_content.setText(getString(R.string.str_enable_location_content));
        ll_ok_cancel.setVisibility(View.GONE);
        Button btn_yes = (Button)dialogView.findViewById(R.id.btn_yes);
        Button btn_no = (Button)dialogView.findViewById(R.id.btn_no);
        Button btn_ok = (Button)dialogView.findViewById(R.id.btn_ok);
        btn_yes.setText(getString(R.string.cancel));
        btn_no.setText(getString(R.string.str_gosettings));
        btn_ok.setText(getString(R.string.str_gosettings));
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }catch (Exception e)
                {

                }
            }
        });
        ll_ok.setVisibility(View.VISIBLE);
        ll_ok_cancel.setVisibility(View.GONE);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                dialog.dismiss();
            }
        });
        if(!dialog.isShowing())
            dialog.show();
    }

    public void GetLocation()
    {
        locationTrack = new LocationTrack(DashboardActivity.this,DashboardActivity.this);
        LogUtils.e(locationTrack.canGetLocation()+"");

        if (locationTrack.canGetLocation()&&locationTrack.getLatitude()!=0.0&&locationTrack.getLongitude()!=0.0) {


            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            LogUtils.e("Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));//.show();
            Location loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            UpdateLocation(loc);
        } else {

            if(!locationTrack.isCheckGPS())
           {
               if(!locationTrack.isShowAlert())
               locationTrack.showSettingsAlert();
           }
        }

    }


    public void onNewToken(String token) {
        LogUtils.e( "Refreshed token: " + token);

        new UpdateToken().execute();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_LOCATION)
    private void RequiresPermission() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
           // getLocation();
            GetLocation();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.str_location),
                    RC_LOCATION, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        GetLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
     GetLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onProviderEnabled(String s) {
        GetLocation();
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSelect(String interest) {

        SelectedInterest = interest;

    }


   void UserDetails(String userid,String friendid) {
        HashMap<String, String> map = new HashMap<>();
        map.put("","");

        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + USERDETAILS,new ServerParams().UserDetails(userid,friendid)
                , user_lister,error_listener));
    }
     Response.Listener user_lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            Log.e("FBLogin Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {
                   if (login.getString(STATUS_JSON).equals("true")) {
                       JSONObject data = login.getJSONObject("data");
                       LogUtils.e("JSON   "+login.getString("data"));
                       String jsondata = data.getString("users");
                       Gson g = new Gson();
                       final User profile = g.fromJson(jsondata,User.class);


                       onprofileView.setVisibility(View.VISIBLE);

                       txtname.setText(profile.getUsername());
                       getUserStatus(profile.getRoomid());
                       if(status==1)
                       {
                            btnstatus.setText(CometChatConstants.USER_STATUS_ONLINE);
                            btnstatus.setBackgroundColor(getColor(R.color.green_600));
                       }else
                       {
                           btnstatus.setText(CometChatConstants.USER_STATUS_OFFLINE);
                           btnstatus.setBackgroundColor(getColor(R.color.red_600));
                       }
                       try {
                           if (!profile.getprofile_imagename().isEmpty())
                               Picasso.get().load(profile.getprofile_imagename()).placeholder(R.drawable.ic_avatar).into(imgProfile);
                       }catch (Exception e)
                       {
                           e.printStackTrace();
                       }
                       txtpostcount.setText(profile.getPost_count()+" Posts");
                       if(!profile.getProfile_display_status().equals("1")) {
                           if (profile.getLatitude() != null && profile.getLongitude() != null) {
                               txtPlace.setText(Tools.getAddress(DashboardActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                           }
                           if(!user.getId().equals(profile.getId()))
                               btnstatus.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       String room_Id = profile.getRoomid();
                                       String name = profile.getUsername();
                                       String id = profile.getId();
                                       if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
                                           Tools.showDialog(true,"This user need to login again to use chat",DashboardActivity.this,DashboardActivity.this);
                                       else
                                       {
                                           Intent intent = new Intent(DashboardActivity.this, CometChatMessageListActivity.class);
                                           intent.putExtra(UIKitConstants.IntentStrings.UID, room_Id);
                                           intent.putExtra(UIKitConstants.IntentStrings.MYUID, "mabo"+user.getId());
                                           intent.putExtra(UIKitConstants.IntentStrings.AVATAR, profile.getprofile_imagename());
                                           intent.putExtra(UIKitConstants.IntentStrings.STATUS, profile.getStatus());
                                           intent.putExtra(UIKitConstants.IntentStrings.NAME, name);
                                           intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
                                           startActivity(intent);
                                           //  LogUtils.e("AVATAR "+user.getprofile_imagename());
                                     /*   Intent intent = new Intent(activity, OneToOneChatActivity.class);
                                        intent.putExtra(StringContract.IntentStrings.USER_ID, room_Id);
                                        intent.putExtra(StringContract.IntentStrings.USER_AVATAR, profile.getprofile_imagename());
                                        intent.putExtra(StringContract.IntentStrings.USER_NAME, name);
                                        activity.startActivity(intent);*/
                                       }
                                   }
                               });

                          }else if(profile.getProfile_display_status().equals("1")){
                           txtPlace.setText(Tools.getAddress(DashboardActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                       }
                       if(profile.getLocationlist()!=null)
                       {
                           if(profile.getLocationlist().size()>0)
                           {
                               RecentLocationAdapter adapter = new RecentLocationAdapter(context,profile.getUsername(),profile.getprofile_imagename(),profile.getLocationlist());
                               locationlist.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                               locationlist.setAdapter(adapter);
                           }
                       }
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProfile(String userid) {
        UserDetails(userid,userid);
    }

    public class UpdateToken extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings) {
            UpdateFcm(token);
            return null;
        }
    }
    private void UpdateFcm(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put("","");
        String URL = SERVER_URL+FCM;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().putFcm(user.getId(),token)
                , fcm_lister,fcm_error_listener));
    }
    private void UpdateLocation(Location loc) {
        HashMap<String, String> map = new HashMap<>();
        map.put("","");
        String URL = SERVER_URL+LOCATION+user.getId();
        URL = URL.replaceAll(" ", "%20");
        user.setLatitude(String.valueOf(loc.getLatitude()));
        user.setLongitude(String.valueOf(loc.getLongitude()));
        String jsondata = gson.toJson(user);
        Utils.getInstance(DashboardActivity.this).setString(USER,jsondata);
        getUser();
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().putLocation(String.valueOf(loc.getLatitude()),String.valueOf(loc.getLongitude()))
                , fcm_lister,fcm_error_listener));
    }

    void getInterest()
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("","");
        String URL = SERVER_URL+GETINTREST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.getStringRequest(Request.Method.POST,URL,getlister,get_error_listener));
    }
    void putInterest()
    {
        String URL = SERVER_URL+PUTINTREST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,URL,
                new ServerParams().putInterest(user.getId(),SelectedInterest),lister,fcm_error_listener));
    }
    public void getUserStatus(String uid) {
        LogUtils.e("Room id " +uid );
        CometChat.getUser(uid, new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
            @Override
            public void onSuccess(com.cometchat.pro.models.User user) {

                     if(user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE))
                     {
                         status = 1;
                         LogUtils.e("Status "+status);
                         return;
                     }
                     else
                     {
                         status = 0;
                         return;
                     }
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void getCometUserList()
    {
        String URL = COMETCHATURL;
        App.requestQueue.add(MyVolleyRequestManager.getCometStringRequest(Request.Method.GET,URL,lister,fcm_error_listener));
    }
    Response.Listener fcm_lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {


            Log.e("FBLogin Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                     user.setFcm_key(token);
                        CometChat.registerTokenForPushNotification(token, new CometChat.CallbackListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Log.e( "onSuccessPN: ",s );
                            }
                            @Override
                            public void onError(CometChatException e) {
                                Log.e("onErrorPN: ",e.getMessage() );
                            }
                        });
                     setUser();
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

    onLoadDismiss();
            Log.e("FBLogin Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                       user.setInterests(SelectedInterest);

                       setUser();
                       getUser();
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
      Response.ErrorListener error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    LogUtils.e(res);
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
        }
    };

    Response.ErrorListener fcm_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            onLoadDismiss();
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    LogUtils.e(res);
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
        }
    };
    Response.Listener getlister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {



            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        LogUtils.e(response.toString());

                        Utils.getInstance(DashboardActivity.this).setString(INTERESTJSON,login.getString(DATA));
                        if(user.getInterests()==null)
                        {
                            user.setInterests("");
                        ShowInterestAlert(login);

                        }else if(user.getInterests().equals("0")||user.getInterests().isEmpty()){
                            user.setInterests("");
                            ShowInterestAlert(login);
                        }
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

void ShowInterestAlert(JSONObject login)
{
    try {
    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DashboardActivity.this);

    LayoutInflater inflater = getLayoutInflater();
    final View dialogView = inflater.inflate(R.layout.alert_selectinterest, null);
    dialogBuilder.setView(dialogView);
    JSONArray data = null;

        data = login.getJSONArray("data");

    RecyclerView listinterest = (RecyclerView) dialogView.findViewById(R.id.list_interest);
    Button btn_ok = (Button)dialogView.findViewById(R.id.btn_ok);
    ArrayList<Interest> listdata = new ArrayList<>();
    for(int i=0;i<data.length();i++) {
        Interest interest = new Interest();
        JSONObject check = new JSONObject(data.get(i).toString());
        interest.setId(check.getString(ID));
        interest.setInterest(check.getString(INTEREST));
        interest.setEnable(false);
        interest.setEditable(true);
        listdata.add(interest);
    }
    InterestAdapter adapter = new InterestAdapter(DashboardActivity.this,listdata,user.getInterests());
    adapter.setInterestCallBackListener(DashboardActivity.this);
    listinterest.setAdapter(adapter);
    final AlertDialog b = dialogBuilder.create();
    b.setCancelable(false);
    btn_ok.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogUtils.e(SelectedInterest);
            if(SelectedInterest!=null&&!SelectedInterest.isEmpty()) {
                b.dismiss();
                onLoadProgress(DashboardActivity.this);

                putInterest();
            }else
                showInfo(getString(R.string.str_interestinfo),DashboardActivity.this,DashboardActivity.this);

        }
    });
    if(!b.isShowing())
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        b.show();
    }
    } catch (JSONException e) {
        e.printStackTrace();
    }
}

    Response.ErrorListener get_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            onLoadDismiss();
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    LogUtils.e(res);
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkOnGoingCall();
        Log.d(TAG, "onResume: ");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");


    }

    @Override
    public void onBackPressed() {
        if(onprofileView.getVisibility()==View.VISIBLE)
        {
            onprofileView.setVisibility(GONE);
        }else
        {
            if(getCurrentFragment() instanceof SettingsFragment)
            {
                super.onBackPressed();
            }else{
                manager=getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                Menu menu = navView.getMenu();
                menu.findItem(R.id.navigation_settings).setIcon(R.drawable.ic_settings);
                mitem = menu.getItem(0);
                mitem.setIcon(R.drawable.ic_settings);
                mitem.setChecked(true);
                /*switch (mitem.getItemId()) {
                    case R.id.navigation_settings:
                        mitem.setIcon(R.drawable.selector);
                        mitem.setChecked(true);

                }*/
            }

        }
    }
    Fragment getCurrentFragment()
    {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.container);
        return currentFragment;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
