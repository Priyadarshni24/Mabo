package com.advengers.mabo.Fragments;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.ChangePasswordActivity;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.GalleryActivity;
import com.advengers.mabo.Activity.LoginActivity;
import com.advengers.mabo.Activity.MainActivity;
import com.advengers.mabo.Adapter.InterestAdapter;
import com.advengers.mabo.Cometchat.Activity.CometChatActivity;
import com.advengers.mabo.Cometchat.Contracts.StringContract;
//import com.advengers.mabo.Database.MyDBHandler;
import com.advengers.mabo.Database.MyDBHandler;
import com.advengers.mabo.Model.Interest;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.NetworkUtil;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.FragmentSettingsBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.advengers.mabo.Activity.MainActivity.COMETCHATURL;
import static com.advengers.mabo.Activity.MainActivity.GETINTREST;
import static com.advengers.mabo.Activity.MainActivity.LOGOUT;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.UPDATEUSER;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.INTEREST;
import static com.advengers.mabo.Interfaces.Keys.INTERESTJSON;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USER;

public class SettingsFragment extends MyFragment implements
                                                            View.OnClickListener,
                                                            CheckBox.OnCheckedChangeListener,
                                                            RadioGroup.OnCheckedChangeListener,
                                                            InterestAdapter.SelectedInterest,
                                                            EasyPermissions.PermissionCallbacks,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    FragmentSettingsBinding binding;
    boolean isEdit = false;
    String gen;
    ArrayList<Interest> listdata;
    String SelectedInterest;
    InterestAdapter adapter;
    String sim_country_code;
    private static final int RC_PHONE = 1 ;
    int RESOLVE_HINT = 0;
    String message = "";
    @Override
    public String getTagManager() {
        return getResources().getString(R.string.app_name);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false);
        getUser();
      /*  binding.txtName.setFocusable(false);
        binding.edtPhone.setFocusable(false);
        binding.edtGender.setFocusable(false);
        binding.tglEmail.setEnabled(false);
        binding.tglPush.setEnabled(false);
        binding.tglScrumble.setEnabled(false);
        binding.tglFavorite.setEnabled(false);
        binding.radioGroup.setEnabled(false);
        binding.radioPrivate.setEnabled(false);
        binding.radioPublic.setEnabled(false);*/
        binding.txtEmail.setText(User.getUser().getEmail());
        binding.txtName.setText(User.getUser().getUsername());
        if(user.getPhone()!=null && user.getPhone().length()>0)
            binding.edtPhone.setText(user.getPhone());
    //    LogUtils.e(gson.toJson(user));

        if(user.getEmail_notify()==null)
            binding.tglEmail.setChecked(false);
        else if(user.getEmail_notify().equals("1"))
            binding.tglEmail.setChecked(true);

        if(user.getPush_notify()==null)
            binding.tglPush.setChecked(false);
        else if(user.getPush_notify().equals("1"))
            binding.tglPush.setChecked(true);

        if(user.getScrumble_location()==null)
            binding.tglScrumble.setChecked(false);
        else if(user.getScrumble_location().equals("1"))
            binding.tglScrumble.setChecked(true);

        if(user.getEmail_notify()==null)
            binding.tglFavorite.setChecked(false);
        else if(user.getEmail_notify().equals("1"))
            binding.tglFavorite.setChecked(true);

        if(user.getProfile_display_status()==null)
            binding.radioPublic.setChecked(true);
        else if(user.getProfile_display_status().equals("1"))
            binding.radioPrivate.setChecked(true);
        if(user.getGender()!=null && user.getGender().length()>0)
            binding.edtGender.setText(user.getGender());

        binding.edtGender.setClickable(false);
        binding.llLogout.setOnClickListener(this);
        binding.edtGender.setOnClickListener(this);
        binding.txtEditprofile.setOnClickListener(this);
        binding.llChangepassword.setOnClickListener(this);
        binding.imgProfile.setOnClickListener(this);
     //   LogUtils.e(user.getprofile_imagename());
        getUser();
        if(user.getprofile_imagename()!=null)
        {
            if(!user.getprofile_imagename().isEmpty())
            Picasso.get().load(user.getprofile_imagename()).into(binding.imgProfile);
        }
        getInterest();
        onUpdateProfile();
        binding.tglEmail.setOnCheckedChangeListener(this);
        binding.tglPush.setOnCheckedChangeListener(this);
        binding.tglScrumble.setOnCheckedChangeListener(this);
        binding.tglFavorite.setOnCheckedChangeListener(this);
        binding.radioGroup.setOnCheckedChangeListener(this);
        onEditProfileDone();
        RequiresPermission();
      /*  try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }*/
        return binding.getRoot();
    }

    // Construct a request for phone numbers and show the picker
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        if(mGoogleApiClient==null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.CREDENTIALS_API)
                    .build();
        }
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mGoogleApiClient, hintRequest);
        getActivity().startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }


    @AfterPermissionGranted(RC_PHONE)
    private void RequiresPermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.GET_ACCOUNTS};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            try {
              /*  TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
                sim_country_code = telephonyManager.getSimCountryIso();
                String getSimSerialNumber = telephonyManager.getSimSerialNumber();
                String getSimNumber =getNoFromWatsApp();// telephonyManager.getVoiceMailNumber();//getLine1Number();
                LogUtils.e("Countrycode " + sim_country_code + " " + getSimSerialNumber + " " + getSimNumber);*/
            sim_country_code =  "+"+ GetCountryZipCode();
                LogUtils.e("Countrycode " + sim_country_code );
            }catch (SecurityException e)
            {
                e.printStackTrace();
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.str_phonepermission),
                    RC_PHONE, perms);
        }
    }

    private String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = getActivity().getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    @Override
    public void onResume() {
      //  LogUtils.e("Resume");
        getUser();
        if(user.getprofile_imagename()!=null)
        {
            if(!user.getprofile_imagename().isEmpty())
            Picasso.get().load(user.getprofile_imagename()).into(binding.imgProfile);
        }else
        {
            Picasso.get().load(R.drawable.ic_privacy).into(binding.imgProfile);
        }
        super.onResume();
    }
   /* public void setUser()
    {
        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(getActivity()).getString(USER),User.class));
        user = User.getUser();
    }*/
   void getInterest()
   {
       HashMap<String, String> map = new HashMap<>();
       map.put("","");
       String URL = SERVER_URL+GETINTREST;
       URL = URL.replaceAll(" ", "%20");
       App.requestQueue.add(MyVolleyRequestManager.getStringRequest(Request.Method.POST,URL,getlister,get_error_listener));
       onLoadProgress(getActivity());
   }
    Response.Listener getlister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            onLoadDismiss();

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        LogUtils.e(response.toString());
                        JSONArray data = login.getJSONArray("data");
                        listdata = new ArrayList<>();
                        String interests = user.getInterests();
                        String[] datas = interests.split(",");
                        List<String> list = new ArrayList<String>(Arrays.asList(datas)); //Arrays.asList(data);

                        for(int i=0;i<data.length();i++) {
                            Interest interest = new Interest();
                            JSONObject check = new JSONObject(data.get(i).toString());
                            interest.setId(check.getString(ID));
                            interest.setInterest(check.getString(INTEREST));
                            interest.setEditable(false);
                            if(list.contains(check.getString(ID)))
                            {
                                interest.setEnable(true);
                            }else
                            {
                                interest.setEnable(false);
                            }
                            listdata.add(interest);
                         //   LogUtils.e(interest.getId()+" "+interest.getInterest()+" "+interest.isEnable());
                        }

                        InterestAdapter adapter = new InterestAdapter(getActivity(),listdata,user.getInterests());
                        adapter.setInterestCallBackListener(SettingsFragment.this);
                        binding.listInterest.setAdapter(adapter);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



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
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ll_logout:
                onLogoutpopup();
                break;
            case R.id.txt_editprofile:
                if(isEdit)
                {
                    callEditProfile();
                }else {
                    isEdit = true;
                    binding.txtEditprofile.setText(getResources().getString(R.string.str_save));
                    onEditProfile();
                }
                break;
            case R.id.img_profile:
                mProfilePhoto();
                break;
            case R.id.edt_gender:
                selectGender();
                break;
            case R.id.ll_changepassword:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;
        }
    }

    private void callEditProfile() {
        onLoadProgress(getActivity());
        user.setUsername(binding.txtName.getText().toString());
        user.setPhone(binding.edtPhone.getText().toString());
        String URL = SERVER_URL+UPDATEUSER+user.getId();
        URL = URL.replaceAll(" ", "%20");
        user.setPhone(binding.edtPhone.getText().toString());
        if(SelectedInterest==null||SelectedInterest.isEmpty())
            user.setInterests(user.getInterests());
        else
            user.setInterests(SelectedInterest);

        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().updateuser(user,sim_country_code)
                , updatelister,update_error_listener));
    }

    private void onEditProfile() {
        binding.txtName.requestFocus();
        binding.txtName.setFocusableInTouchMode(true);
        binding.txtName.setFocusable(true);
        binding.txtName.setEnabled(true);

        binding.edtPhone.requestFocus();
        binding.edtPhone.setFocusableInTouchMode(true);
        binding.edtPhone.setFocusable(true);
        binding.edtPhone.setEnabled(true);

        binding.edtGender.setFocusable(true);
        binding.edtGender.setClickable(true);
        binding.tglEmail.setEnabled(true);
        binding.tglPush.setEnabled(true);
        binding.tglScrumble.setEnabled(true);
        binding.tglFavorite.setEnabled(true);
        binding.radioGroup.setEnabled(true);
        binding.radioPrivate.setEnabled(true);
        binding.radioPublic.setEnabled(true);

        for(int i=0;i<listdata.size();i++)
        {
            Interest setdata = new Interest();
            setdata = listdata.get(i);
            setdata.setEditable(true);
            listdata.set(i,setdata);
        //    LogUtils.e(listdata.get(i).getId()+" "+listdata.get(i).getInterest()+" "+listdata.get(i).isEnable()+" "+listdata.get(i).isEditable());
        }
        adapter = new InterestAdapter(getActivity(),listdata,user.getInterests());
        adapter.setInterestCallBackListener(this);
        binding.listInterest.setAdapter(adapter);
    }

    private void onEditProfileDone() {
        binding.txtEditprofile.setText(getString(R.string.str_editprofile));
        binding.txtName.requestFocus();
        binding.txtName.setFocusableInTouchMode(false);
        binding.txtName.setFocusable(false);
        binding.txtName.setEnabled(false);

        binding.edtPhone.requestFocus();
        binding.edtPhone.setFocusableInTouchMode(false);
        binding.edtPhone.setFocusable(false);
        binding.edtPhone.setEnabled(false);

        binding.edtGender.setFocusable(false);
        binding.edtGender.setClickable(false);
        binding.tglEmail.setEnabled(false);
        binding.tglPush.setEnabled(false);
        binding.tglScrumble.setEnabled(false);
        binding.tglFavorite.setEnabled(false);
        binding.radioGroup.setEnabled(false);
        binding.radioPrivate.setEnabled(false);
        binding.radioPublic.setEnabled(false);
        if (listdata!=null) {
            String interests = user.getInterests();
            String[] datas = interests.split(",");
            List<String> list = new ArrayList<String>(Arrays.asList(datas));
            for (int i = 0; i < listdata.size(); i++) {
                Interest setdata = new Interest();
                setdata = listdata.get(i);
                setdata.setEditable(false);
                if(list.contains(setdata.getId()))
                {
                    setdata.setEnable(true);
                }else
                {
                    setdata.setEnable(false);
                }
                listdata.set(i, setdata);
              //  LogUtils.e(listdata.get(i).getId() + " " + listdata.get(i).getInterest() + " " + listdata.get(i).isEnable() + " " + listdata.get(i).isEditable());
            }
            adapter = new InterestAdapter(getActivity(), listdata, user.getInterests());
            adapter.setInterestCallBackListener(this);
            binding.listInterest.setAdapter(adapter);
             //Arrays.asList(data);


        }
    }
    void mProfilePhoto() {
        Intent galleryint = new Intent(getActivity(), GalleryActivity.class);
         startActivity(galleryint);
    }
    void onUpdateProfile()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder.create();
        builder.setMessage(R.string.str_complete_profile_dialog)
                .setNegativeButton(R.string.later,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utils.getInstance(getMyActivty()).setBoolean(Utils.PREF_PROFILE_FILL,true);
                    /*    if(callBackListener != null)
                            callBackListener.onPeopleCallBack();*/
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.str_gotit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Utils.getInstance(getMyActivty()).setBoolean(Utils.PREF_PROFILE_FILL,true);
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        builder.show();
    }
    void onLogoutpopup()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_warning, null);
        dialogBuilder.setView(dialogView);

        TextView txt_title = (TextView) dialogView.findViewById(R.id.txt_title);
        LinearLayout ll_ok = (LinearLayout) dialogView.findViewById(R.id.ll_ok);
        LinearLayout ll_ok_cancel = (LinearLayout) dialogView.findViewById(R.id.ll_ok_cancel);
        TextView txt_content = (TextView) dialogView.findViewById(R.id.txt_content);
        txt_title.setText(getString(R.string.str_logout));
        txt_content.setText(getString(R.string.str_logout_dialog));
        ll_ok_cancel.setVisibility(View.VISIBLE);
        Button btn_yes = (Button)dialogView.findViewById(R.id.btn_yes);
        Button btn_no = (Button)dialogView.findViewById(R.id.btn_no);
        btn_yes.setText(getString(R.string.yes));
        btn_no.setText(getString(R.string.no));
        final AlertDialog dialog = dialogBuilder.create();
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
              onLoadProgress(getContext());
              new MyDBHandler(getContext()).dropfriends();
                CometChat.logout(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                       /* Intent intent=new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((CometChatActivity)context).finish();*/

                        onLogout();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        LogUtils.e( "onError: "+e.getMessage());
                    }
                });

              /*  if(NetworkUtil.isInternetOn(getApplicationContext()))
                    new ExitTask().execute();
                else
                    showWarning(getResources().getString(R.string.str_check_network));*/

            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ll_ok.setVisibility(View.GONE);
        dialog.show();
    }

    public void selectGender()
    {
        final CharSequence[] gender = {"Male","Female"};
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Select Gender");
        alert.setSingleChoiceItems(gender,-1, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(gender[which]=="Male")
                {
                    gen="male";
                }
                else if (gender[which]=="Female")
                {
                    gen="female";
                }
                binding.edtGender.setText(gender[which]);
                user.setGender(gen);
                LogUtils.e(user.getGender());
              //  dialog.dismiss();
            }
        });
        alert.show();
    }
    private void onLogout() {
        String URL = SERVER_URL+LOGOUT;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().logout(user.getId())
                , lister,error_listener));
    }
    private void CometProfilePicUpdate() {
        String url = "";
         if(user.getprofile_imagename().isEmpty()||user.getprofile_imagename()==null)
        {
            url = MainActivity.LOADAVATAR;
        }
         else
             url = user.getprofile_imagename();
        String URL = COMETCHATURL+"/"+user.getRoomid();
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createCometStringRequest(Request.Method.PUT,
                URL,new ServerParams().UpdateCometUser(user.getUsername(),url)
                , updatelister,update_error_listener));
        //    Picasso.get().load(R.drawable.ic_image_loading).into(img_profile);
    }
    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
        onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(StringContract.AppDetails.AppID_user_UID);
                        Utils.getInstance(getApplicationContext()).clearPref();
                        User.logout();
                       // intent
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
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


    Response.Listener updatelister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        isEdit = false;
                        //onEditProfileDone();
                        setUser();
                        message = login.getString(MESSAGE);
                        CometProfilePicUpdate();
                    }
                }else{
                    showInfo(message);
                    onEditProfileDone();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    Response.ErrorListener update_error_listener = new Response.ErrorListener() {
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId())
        {
            case R.id.tgl_email:
                if(b)
                    user.setEmail_notify("1");
                else
                    user.setEmail_notify("0");
            break;
            case R.id.tgl_favorite:
                if(b)
                    user.setEmail_notify("1");
                else
                    user.setEmail_notify("0");
                break;
            case R.id.tgl_scrumble:
                if(b)
                    user.setScrumble_location("1");
                else
                    user.setScrumble_location("0");
                break;
            case R.id.tgl_push:
                if(b)
                    user.setPush_notify("1");
                else
                    user.setPush_notify("0");
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id)
        {
            case R.id.radioPrivate:
                user.setProfile_display_status("1");
            break;
            case R.id.radioPublic:
                user.setProfile_display_status("0");
            break;
        }
    }

    @Override
    public void onSelect(String interest) {
        SelectedInterest = interest;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        try {
         /*   TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            sim_country_code = telephonyManager.getSimCountryIso();
            String getSimSerialNumber = telephonyManager.getSimSerialNumber();
            String getSimNumber = telephonyManager.getLine1Number();
            LogUtils.e("Countrycode " + sim_country_code + " " + getSimSerialNumber + " " + getSimNumber);*/
       sim_country_code =  "+" +GetCountryZipCode();
       LogUtils.e("countrycode"+sim_country_code);
        }catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
