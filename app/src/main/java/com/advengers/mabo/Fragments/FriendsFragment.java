package com.advengers.mabo.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.IntroscreenActivity;
import com.advengers.mabo.Activity.SplashActivity;
import com.advengers.mabo.Adapter.FriendsAdapter;
import com.advengers.mabo.Chat.web_communication.WebCall;
import com.advengers.mabo.Chat.web_communication.WebConstants;
import com.advengers.mabo.Chat.web_communication.WebResponse;
import com.advengers.mabo.Database.MyDBHandler;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.FragmentFriendsBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.advengers.mabo.Activity.MainActivity.FRIENDSLIST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants.IntentStrings.MYUID;

public class FriendsFragment extends MyFragment implements  EasyPermissions.PermissionCallbacks,FriendsAdapter.ProfileClick {
FragmentFriendsBinding binding;
    private static final int RC_READCONTACT = 10010;
    ArrayList<String> phonenumbers = new ArrayList<>();
    ArrayList<String> phonedata = new ArrayList<>();
    int totalcontacts;
    int range=100;
    int start=0;
    int remain=0,checkeddata=0;
    MyDBHandler db;
    FriendsAdapter adapter;
    ArrayList<User> friends = new ArrayList<>();
    String name,room_Id,id,type;
    ProgressDialog dialog;
    @Override
    public String getTagManager() {
        return null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(R.string.str_pleasewait);
        dialog.setCancelable(false);
        db = new MyDBHandler(getContext());
        friends = db.getFriendsList();
        if(db.getFriendsList().size()==0)
        binding.fabRefresh.setVisibility(View.GONE);
        binding.fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoadProgress(getActivity());

                checkeddata = 0;
                remain = 0;
                start = 0;
                range = 100;
                phonedata.clear();
                phonenumbers.clear();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
               getContactList();
                    }},1000);
            }
        });
        binding.btnMyRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.e("I am coming here");
                RequiresPermission();
               /* getUser();
                room_Id = user.getRoomid();
                name =  user.getUsername();
                if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
                    Tools.showDialog(true,"This user need to login again to use chat",getActivity(),getActivity());
                else
                {
                    Intent intent = new Intent(getActivity(), OneToOneChatActivity.class);
                    intent.putExtra(StringContract.IntentStrings.USER_ID, room_Id);
                    intent.putExtra(StringContract.IntentStrings.USER_AVATAR, user.getprofile_imagename());
                    intent.putExtra(StringContract.IntentStrings.USER_NAME, user.getUsername());
                    getActivity().startActivity(intent);
                }*/
              //  new WebCall(getActivity(), FriendsFragment.this, null, WebConstants.validateRoomId + room_Id, WebConstants.validateRoomIdCode, true,false).execute();
            }
        });
        if(friends.size()>0)
        {
            binding.btnMyRoom.setVisibility(View.GONE);
            adapter = new FriendsAdapter(getContext(),friends);
            adapter.CallBackListener(FriendsFragment.this);
            binding.listLike.setAdapter(adapter);
        }
        return binding.getRoot();
    }
    private void getContactList() {
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {

            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                      /*  LogUtils.e("Name: " + name);
                        LogUtils.e("Phone Number: " + phoneNo);*/
                       // phoneNo.replaceAll("[^a-zA-Z0-9]", "");
                        phonenumbers.add(getOnlyDigits(phoneNo));
                    }
                    pCur.close();
                }
            }
        }

        if(cur!=null){
            cur.close();
        }
        LogUtils.e("Total Size "+phonenumbers.size()+"");
        totalcontacts = phonenumbers.size();
        checkFriendlist();
    }
    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }
    void checkFriendlist()
    {
        if(totalcontacts<range)
            range = totalcontacts;
        if(remain!=0&&remain<range)
            range = remain;
        LogUtils.e(range+" Range "+remain);

        for(int i=start;i<(checkeddata+range);i++)
        {
            phonedata.add(phonenumbers.get(i));
        }
        GetFriendsList(phonedata.toString());
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
       /* if(db.getFriendsList().size()==0)
          getContactList();
        else
        {
            adapter = new FriendsAdapter(getContext(),friends);
            adapter.CallBackListener(this);
            binding.listLike.setAdapter(adapter);
        }*/
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_READCONTACT)
    private void RequiresPermission() {
        String[] perms = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            if(db.getFriendsList().size()==0)
            {
                onLoadProgress(getActivity());
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        binding.fabRefresh.setVisibility(View.VISIBLE);
                        getContactList();
                    }},1000);
            }
            else
            {
                adapter = new FriendsAdapter(getContext(),friends);
                adapter.CallBackListener(this);
                binding.listLike.setAdapter(adapter);
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.str_readcontact),
                    RC_READCONTACT, perms);
        }
    }
    void GetFriendsList(String contactlist)
    {
        String URL = SERVER_URL+FRIENDSLIST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,URL,
                new ServerParams().getFriendlist(contactlist),lister,error_listener));
    }
    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

          //  onLoadDismiss();
            Log.e("FBLogin Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        JSONArray data = login.getJSONArray(DATA);
                        for(int i = 0; i<data.length();i++)
                        {
                            JSONArray friend = data.getJSONArray(i);
                            gson = new Gson();
                            User user = gson.fromJson(friend.get(0).toString(),User.class);

                            if(User.getUser().getId().equals(user.getId()))
                            {
                                LogUtils.e("It is myself");
                            }
                            else{
                                db.insertFriends(user);
                            }
                        }

                    }
                    checkeddata = checkeddata + phonedata.size();
                    LogUtils.e("checkeddata "+checkeddata+"");
                    friends = db.getFriendsList();
                    adapter = new FriendsAdapter(getContext(),friends);
                    adapter.CallBackListener(FriendsFragment.this);
                    binding.listLike.setAdapter(adapter);
                    if(checkeddata<totalcontacts)
                    {
                        remain = totalcontacts - checkeddata;
                        if(remain>0)
                        {
                            start = checkeddata;
                            phonedata.clear();
                            checkFriendlist();

                        }
                    }
                    if(checkeddata==totalcontacts)
                    {
                       onLoadDismiss();
                        binding.btnMyRoom.setVisibility(View.GONE);
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
                    JSONObject login = new JSONObject(res);
                    if (login.has(STATUS_JSON)) {


                        if (login.getString(STATUS_JSON).equals("true")) {
                            JSONArray data = login.getJSONArray(DATA);
                            for(int i = 0; i<data.length();i++)
                            {
                                JSONArray friend = data.getJSONArray(i);
                                gson = new Gson();
                                User user = gson.fromJson(friend.get(0).toString(),User.class);
                                if(User.getUser().getId().equals(user.getId()))
                                {
                                    LogUtils.e("It is myself");
                                }
                                else{
                                    db.insertFriends(user);
                                }
                            }
                       }
                        checkeddata = checkeddata + phonedata.size();
                        LogUtils.e(checkeddata+"");
                        if(checkeddata<totalcontacts)
                        {
                            remain = totalcontacts - checkeddata;
                            if(remain>0)
                            {
                                start = checkeddata;
                                phonedata.clear();
                                checkFriendlist();
                            }
                        }
                        if(checkeddata == totalcontacts)
                            onLoadDismiss();

                    }
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
    public void onProfile(int position) {
        getUser();
        //Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog,user.getId(),friends.get(position).getId(),getContext(),getActivity());
        callback.onProfile(friends.get(position).getId());
    }

    @Override
    public void onitemClick(int position) {
        type = "chat";
         room_Id = friends.get(position).getRoomid();
         name = friends.get(position).getUsername();
         id = friends.get(position).getId();
        if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
            Tools.showDialog(true,"This user need to login again to use chat",getActivity(),getActivity());
        else
        {
      /*
            Intent intent = new Intent(getActivity(), OneToOneChatActivity.class);
            intent.putExtra(StringContract.IntentStrings.USER_ID, room_Id);
            intent.putExtra(StringContract.IntentStrings.USER_AVATAR, friends.get(position).getprofile_imagename());
            intent.putExtra(StringContract.IntentStrings.USER_NAME, name);
            getActivity().startActivity(intent);*/
            com.cometchat.pro.models.User user = new com.cometchat.pro.models.User();
            user.setUid(room_Id);
            user.setAvatar(friends.get(position).getprofile_imagename());
            user.setName(name);
            startUserIntent(user);
        }

    }
    private void startUserIntent(com.cometchat.pro.models.User user) {
        Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
        intent.putExtra(MYUID,getUserdetail().getRoomid());
        intent.putExtra(UIKitConstants.IntentStrings.UID, user.getUid());
        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar());
        intent.putExtra(UIKitConstants.IntentStrings.STATUS, user.getStatus());
        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        startActivity(intent);
    }
    @Override
    public void onCallClick(int position) {
        type = "call";
        room_Id = friends.get(position).getRoomid();
        name = friends.get(position).getUsername();
        id = friends.get(position).getId();
       /* if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2)
            Tools.showDialog(true,"This user need to login again to use chat",getContext(),getActivity());
        else
            new WebCall(getActivity(), FriendsFragment.this, null, WebConstants.validateRoomId + room_Id, WebConstants.validateRoomIdCode, true,false).execute();*/
    }

   /* @Override
    public void onWebResponse(String response, int callCode) {
        switch (callCode)
        {

            case WebConstants.getTokenURLCode:
                onGetTokenSuccess(response);
                break;
            case WebConstants.validateRoomIdCode:
                onVaidateRoomIdSuccess(response);
                break;
        }
    }
    private void onVaidateRoomIdSuccess(String response)
    {
        Log.e("responsevalidate", response);
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("result").trim().equalsIgnoreCase("40001"))
            {
                Toast.makeText(getContext(), jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
            }
            else
            {
              //  savePreferences();
                getRoomTokenWebCall();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
    private void getRoomTokenWebCall()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("name", name);
            jsonObject.put("role", "participant");
           // jsonObject.put("role","moderator");
            jsonObject.put("user_ref", "2236");
            jsonObject.put("roomId", room_Id);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (!name.isEmpty() && !room_Id.isEmpty())
        {
            new WebCall(getContext(), this, jsonObject, WebConstants.getTokenURL, WebConstants.getTokenURLCode, false,false).execute();
        }
    }
    private void onGetTokenSuccess(String response)
    {
        Log.e("responseToken", response);

        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("result").equalsIgnoreCase("0"))
            {
               String token = jsonObject.optString("token");
                Log.e("token", token);
                Intent intent;
               *//* if(type.equals("chat"))
                    intent = new Intent(getActivity(), ChatActivity.class);
                else
                    intent = new Intent(getActivity(), VideoConferenceActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("name",name);
                intent.putExtra("id",id);
                intent.putExtra("roomid",room_Id);
                startActivity(intent);*//*
            }
            else
            {
                Toast.makeText(getContext(), jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onWebResponseError(String error, int callCode) {

    }*/
}
