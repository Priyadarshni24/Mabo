package com.advengers.mabo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Adapter.LikeAdapter;
import com.advengers.mabo.Adapter.PostAdapter;
import com.advengers.mabo.Adapter.RecentLocationAdapter;
import com.advengers.mabo.Fragments.TrendingFragment;
import com.advengers.mabo.Model.Likelist;
import com.advengers.mabo.Model.Post;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.ActivityLikelistBinding;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.GONE;
import static com.advengers.mabo.Activity.MainActivity.LIKELIST;
import static com.advengers.mabo.Activity.MainActivity.LIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.USERDETAILS;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;

public class LikelistActivity extends MyActivity implements LikeAdapter.ProfileClick{

    ArrayList<Likelist> listdata = new ArrayList<>();
    TextView txtname;
    TextView txtpostcount;
    TextView txtPlace,btnstatus;
    ImageView imgProfile;
    RelativeLayout onprofileView;
    RecyclerView locationlist;
    int status = 0;
    Toolbar mToolbar;
    RecyclerView listlike;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likelist);
        getUser();
        mToolbar = (Toolbar)findViewById(R.id.mToolbar);
        listlike = (RecyclerView)findViewById(R.id.list_like);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_likes));
        String postid = getIntent().getExtras().getString(POSTID);
        onLikelist(postid);
        View layout = (RelativeLayout)findViewById(R.id.profile_view);
        onprofileView = (RelativeLayout)layout.findViewById(R.id.profile_view);
        txtname = layout.findViewById(R.id.txt_name);
        txtpostcount = layout.findViewById(R.id.txt_postcount);
        btnstatus = layout.findViewById(R.id.btn_status);
        txtPlace = layout.findViewById(R.id.txt_place);
        imgProfile = layout.findViewById(R.id.img_profile);
        locationlist = layout.findViewById(R.id.list_recentlyvisited);
    }
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
    private void onLikelist(String postid) {
        onLoadProgress(LikelistActivity.this);
        String URL = SERVER_URL+LIKELIST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().listlike(postid)
                , likelister,error_listener));
    }

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


    Response.Listener likelister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        JSONArray listarray = login.getJSONArray(DATA);
                        for(int i=0;i<listarray.length();i++) {
                            String jsondata = listarray.get(i).toString();
                            // LogUtils.e(jsondata);
                            Likelist post = gson.fromJson(jsondata, Likelist.class);
                            listdata.add(post);
                        }
                        /* Collections.reverse(postlist);*/
                        LikeAdapter adapter = new LikeAdapter(LikelistActivity.this,listdata);
                        adapter.CallBackListener(LikelistActivity.this);
                        listlike.setAdapter(adapter);

                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProfile(int position) {
    //    Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog,user.getId(),listdata.get(position).getId(),LikelistActivity.this,LikelistActivity.this);
        UserDetails(listdata.get(position).getId(),listdata.get(position).getId());
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
                                txtPlace.setText(Tools.getAddress(LikelistActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                            }
                            if(!user.getId().equals(profile.getId()))
                                btnstatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String room_Id = profile.getRoomid();
                                        String name = profile.getUsername();
                                        String id = profile.getId();
                                        if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
                                            Tools.showDialog(true,"This user need to login again to use chat",LikelistActivity.this,LikelistActivity.this);
                                        else
                                        {
                                            Intent intent = new Intent(LikelistActivity.this, CometChatMessageListActivity.class);
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
                            txtPlace.setText(Tools.getAddress(LikelistActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                        }
                        if(profile.getLocationlist()!=null)
                        {
                            if(profile.getLocationlist().size()>0)
                            {
                                RecentLocationAdapter adapter = new RecentLocationAdapter(LikelistActivity.this,profile.getUsername(),profile.getprofile_imagename(),profile.getLocationlist());
                                locationlist.setLayoutManager(new LinearLayoutManager(LikelistActivity.this));
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
                Toast.makeText(LikelistActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if(onprofileView.getVisibility()==View.VISIBLE)
        {
            onprofileView.setVisibility(GONE);
        }else
            super.onBackPressed();
    }
}
