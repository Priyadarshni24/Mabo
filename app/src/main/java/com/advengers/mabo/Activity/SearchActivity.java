package com.advengers.mabo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Adapter.FriendsAdapter;
import com.advengers.mabo.Chat.web_communication.WebCall;
import com.advengers.mabo.Chat.web_communication.WebConstants;
import com.advengers.mabo.Chat.web_communication.WebResponse;
import com.advengers.mabo.Cometchat.Activity.OneToOneChatActivity;
import com.advengers.mabo.Cometchat.Contracts.StringContract;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.NetworkUtil;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.ActivitySearchBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.USERSEARCH;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class SearchActivity extends MyActivity implements FriendsAdapter.ProfileClick {
ActivitySearchBinding binding;
    ArrayList<User> Userlist = new ArrayList<>();
    FriendsAdapter adapter;
    String name,room_Id,id,type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.mToolbar.setTitle(R.string.str_search);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(binding.edtName.getText().toString().length()>=1)
                {
                    if(NetworkUtil.isInternetOn(SearchActivity.this))
                       makeApiCall(binding.edtName.getText().toString().trim());
                    else
                        Tools.showDialog(true,getString(R.string.str_check_network),SearchActivity.this,SearchActivity.this);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
    private void makeApiCall(String toString) {
        String URL = SERVER_URL+USERSEARCH;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().userSearch(toString)
                , lister,error_listener));
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
                        Userlist.clear();
                        List<String> stringList = new ArrayList<>();
                        try {
                            JSONArray array = login.getJSONArray(DATA);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject row = array.getJSONObject(i);
                                String jsondata = array.get(i).toString();
                                // LogUtils.e(jsondata);
                                User userdata = gson.fromJson(jsondata, User.class);
                                Userlist.add(userdata);
                                stringList.add(row.getString(USERNAME));
                            }
                            adapter = new FriendsAdapter(SearchActivity.this,Userlist);
                            adapter.CallBackListener(SearchActivity.this);
                            binding.listUsers.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                    try {

                        if (obj.has(STATUS_JSON)) {


                            if (obj.getString(STATUS_JSON).equals("true")) {
                                Userlist.clear();
                                List<String> stringList = new ArrayList<>();
                                try {
                                    JSONArray array = obj.getJSONArray(DATA);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject row = array.getJSONObject(i);
                                        String jsondata = array.get(i).toString();
                                        // LogUtils.e(jsondata);
                                        User userdata = gson.fromJson(jsondata, User.class);
                                        Userlist.add(userdata);
                                        stringList.add(row.getString(USERNAME));
                                    }
                                    adapter = new FriendsAdapter(SearchActivity.this,Userlist);
                                    adapter.CallBackListener(SearchActivity.this);
                                    binding.listUsers.setAdapter(adapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
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
        Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog,user.getId(),Userlist.get(position).getId(),this,this);
    }

    @Override
    public void onChatClick(int position) {
        type = "chat";
        room_Id = Userlist.get(position).getRoomid();
        name = Userlist.get(position).getUsername();
        id = Userlist.get(position).getId();
        if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
            Tools.showDialog(true,"This user need to login again to use chat",this,this);
        else
        {
            //  LogUtils.e("AVATAR "+user.getprofile_imagename());
            Intent intent = new Intent(SearchActivity.this, OneToOneChatActivity.class);
            intent.putExtra(StringContract.IntentStrings.USER_ID, room_Id);
            intent.putExtra(StringContract.IntentStrings.USER_AVATAR, Userlist.get(position).getprofile_imagename());
            intent.putExtra(StringContract.IntentStrings.USER_NAME, name);
            startActivity(intent);
        }
    }

    @Override
    public void onCallClick(int position) {
        type = "call";
        room_Id = Userlist.get(position).getRoomid();
        name = Userlist.get(position).getUsername();
        id = Userlist.get(position).getId();
       /* if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2)
            Tools.showDialog(true,"This user need to login again to use chat",this,this);
        else
            new WebCall(SearchActivity.this, SearchActivity.this, null, WebConstants.validateRoomId + room_Id, WebConstants.validateRoomIdCode, true,false).execute();*/
    }

/*    @Override
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
                Toast.makeText(SearchActivity.this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
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
            new WebCall(SearchActivity.this, this, jsonObject, WebConstants.getTokenURL, WebConstants.getTokenURLCode, false,false).execute();
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
                    intent = new Intent(SearchActivity.this, ChatActivity.class);
                else
                    intent = new Intent(SearchActivity.this, VideoConferenceActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("name",name);
                intent.putExtra("id",id);
                intent.putExtra("roomid",room_Id);
                startActivity(intent);*//*
            }
            else
            {
                Toast.makeText(SearchActivity.this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
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
