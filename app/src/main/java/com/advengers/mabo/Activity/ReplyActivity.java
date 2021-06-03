package com.advengers.mabo.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Adapter.AutoSuggestAdapter;
import com.advengers.mabo.Adapter.CommentAdapter;
import com.advengers.mabo.Adapter.FriendsAdapter;
import com.advengers.mabo.Adapter.RecentLocationAdapter;
import com.advengers.mabo.Adapter.ReplyAdapter;
import com.advengers.mabo.Fragments.TrendingFragment;
import com.advengers.mabo.Model.Commentlist;
import com.advengers.mabo.Model.Reply;
import com.advengers.mabo.Model.Tag;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.NetworkUtil;
import com.advengers.mabo.Utils.Tools;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static com.advengers.mabo.Activity.CreatePostActivity.hideKeyboard;
import static com.advengers.mabo.Activity.MainActivity.COMMENTPOST;
import static com.advengers.mabo.Activity.MainActivity.REPLYCOMMENT;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.USERDETAILS;
import static com.advengers.mabo.Activity.MainActivity.USERSEARCH;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class ReplyActivity extends MyActivity implements View.OnClickListener, ReplyAdapter.ProfileClick,FriendsAdapter.ProfileClick{
    Toolbar mToolbar;
    AutoCompleteTextView edtPost;
    Commentlist data;
    ImageView imd_profile,imgProfile;
    TextView txtname,txt_date,txt_replycomment,btn_reply,btn_cancel,taguser;
    AutoCompleteTextView edt_post;
    RecyclerView list_like;
    String content;
    PrettyTime p = new PrettyTime();
    ArrayList<Reply> replylist = new ArrayList<>();
    ReplyAdapter adapter;
    String searchtag,comments;
    private Handler handler;
    ArrayList<Tag> tagpeopledata = new ArrayList<>();
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 100;
    ArrayList<User> taglist = new ArrayList<>();
    ArrayList<User> Userlist = new ArrayList<>();
    AutoSuggestAdapter autoSuggestAdapter;
    RelativeLayout onprofileView;
    RecyclerView locationlist;
    TextView txtpostcount;
    TextView txtPlace,btnstatus;
    int status = 0,replied = 0;
    RecyclerView list_users;
    FriendsAdapter fadapter;
    AlertDialog tagdialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        mToolbar = (Toolbar)findViewById(R.id.mToolbar);
        edtPost =  (AutoCompleteTextView)findViewById(R.id.edt_post);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_Replies));
        getUser();
        data = (Commentlist)getIntent().getSerializableExtra("replydata");
        imd_profile = (ImageView)findViewById(R.id.imd_profile);
        txtname = (TextView)findViewById(R.id.txtname);
        txt_date = (TextView)findViewById(R.id.txt_date);
        txt_replycomment = (TextView)findViewById(R.id.txt_replycomment);
        btn_reply = (TextView)findViewById(R.id.btn_reply);
        btn_cancel = (TextView)findViewById(R.id.btn_cancel);
        btn_reply.setOnClickListener(this);
        edt_post = (AutoCompleteTextView)findViewById(R.id.edt_post);
        list_like = (RecyclerView)findViewById(R.id.list_like);
        View layout = (RelativeLayout)findViewById(R.id.profile_view);
        onprofileView = (RelativeLayout)layout.findViewById(R.id.profile_view);
        taguser = (TextView)findViewById(R.id.taguser);
        taguser.setOnClickListener(this);
        txtname = layout.findViewById(R.id.txt_name);
        txtpostcount = layout.findViewById(R.id.txt_postcount);
        btnstatus = layout.findViewById(R.id.btn_status);
        txtPlace = layout.findViewById(R.id.txt_place);
        imgProfile = layout.findViewById(R.id.img_profile);
        locationlist = layout.findViewById(R.id.list_recentlyvisited);
        if (!data.getProfile_imagename().isEmpty())
            Picasso.get().load(data.getProfile_imagename()).placeholder(R.drawable.ic_avatar).into(imd_profile);
        txtname.setText(data.getUsername());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {

            Date date = formatter.parse(data.getCreated());
            txt_date.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (data.getTagpeopledata()!=null) {

            String tagpeople = "";

            List<Tag> tagpeoples = data.getTagpeopledata();
            for (int i = 0; i < tagpeoples.size(); i++) {
                if (i == 0)
                    tagpeople = tagpeoples.get(0).getUsername();
                else
                    tagpeople = tagpeople + " " + tagpeoples.get(i).getUsername();
            }
            content =data.getComment();
            LogUtils.e(content);
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(content);
            ClickableSpan[] clickableSpans = new ClickableSpan[tagpeoples.size()];
            for (int i = 0; i < tagpeoples.size(); i++) {
                int finalI = i;
                clickableSpans[i] = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        LogUtils.e("" + tagpeoples.get(finalI).getUsername());
                        onProfile(tagpeoples.get(finalI).getId());
                    }
                };
            }
            for (int i = 0; i < tagpeoples.size(); i++)
                ssBuilder.setSpan(
                        clickableSpans[i],
                        content.indexOf(tagpeoples.get(i).getUsername()),
                        content.indexOf(tagpeoples.get(i).getUsername()) + String.valueOf(tagpeoples.get(i).getUsername()).length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            // Display the spannable text to TextView
            txt_replycomment.setText(ssBuilder);

            // Specify the TextView movement method
            txt_replycomment.setMovementMethod(LinkMovementMethod.getInstance());

        }else
            txt_replycomment.setText(data.getComment());
        if(data.getReplydetails()!=null)
            replylist = new ArrayList<Reply>(data.getReplydetails());
        adapter = new ReplyAdapter(ReplyActivity.this,replylist,user.getId());
        adapter.CallBackListener(ReplyActivity.this);
        list_like.setAdapter(adapter);
        if(replylist.size()>0)
            list_like.smoothScrollToPosition(replylist.size()-1);
        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
      /*  edtPost.setThreshold(2);
        edtPost.setAdapter(autoSuggestAdapter);
        edtPost.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        edtPost.setText(comments.replace("@","")+autoSuggestAdapter.getObject(position));
                        hideKeyboard(ReplyActivity.this);
                        if(!taglist.contains(Userlist.get(position)))
                            taglist.add(Userlist.get(position));
                    }
                });
        edtPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LogUtils.e("Before "+charSequence);
                comments = String.valueOf(charSequence).replace("@"+searchtag,"");
                LogUtils.e("Before comments"+comments);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edtPost.getText().toString().length()>0)
                {
                    String comment = edtPost.getText().toString();

                    if(comment.contains(" "))
                    {
                      *//*  comments = comment;
                        LogUtils.e(comments);*//*
                        String[] tags = comment.split(" ");
                        if(tags.length>1)
                        {
                            for(int i=0;i<tags.length;i++)
                            {
                                if(tags[i].contains("@"))
                                {
                                    searchtag = tags[i].replace("@","");
                                    handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                                    handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                                            AUTO_COMPLETE_DELAY);
                                }
                            }
                        }else{
                            if(tags[0].contains("@"))
                            {
                                searchtag = tags[0].replace("@","");
                                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                                        AUTO_COMPLETE_DELAY);
                            }
                        }
                    }
                }
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(edtPost.getText())) {
                        makeApiCall(searchtag);
                        LogUtils.e(""+searchtag);
                    }
                }
                return false;
            }
        });*/

    }
    private void makeApiCall(String toString) {
        String URL = SERVER_URL+USERSEARCH;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().userSearch(toString)
                , taglister,tagerror_listener));

    }
    Response.Listener taglister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            //  LogUtils.e(response.toString());

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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fadapter = new FriendsAdapter(ReplyActivity.this,Userlist);
                        fadapter.CallBackListener(ReplyActivity.this);
                        list_users.setAdapter(fadapter);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    Response.ErrorListener tagerror_listener = new Response.ErrorListener() {
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //IMPORTANT: set data here and notify
                                autoSuggestAdapter.setData(stringList);
                                autoSuggestAdapter.notifyDataSetChanged();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reply:
                if (!edtPost.getText().toString().trim().isEmpty()) {
                    String tagpeopleid = "";

                    if (taglist.size() > 0)
                        for (int i = 0; i < taglist.size(); i++) {
                            LogUtils.e("i " + i + " " + taglist.get(i).getId());
                            if (i == 0) {
                                tagpeopledata.clear();
                                if (edtPost.getText().toString().contains(taglist.get(i).getUsername()))
                                    tagpeopleid = taglist.get(i).getId();
                                Tag tag = new Tag();
                                tag.setId(taglist.get(i).getId());
                                tag.setUsername(taglist.get(i).getUsername());
                                tagpeopledata.add(tag);

                            } else {
                                if (edtPost.getText().toString().contains(taglist.get(i).getUsername()) && !tagpeopleid.contains(taglist.get(i).getId()))
                                    tagpeopleid = tagpeopleid + "," + taglist.get(i).getId();
                                Tag tag = new Tag();
                                tag.setId(taglist.get(i).getId());
                                tag.setUsername(taglist.get(i).getUsername());
                                tagpeopledata.add(tag);
                            }

                        }
                    LogUtils.e("Tag ids " + tagpeopleid);
                    taglist.clear();
                    onReplyComment(user.getId(), StringEscapeUtils.escapeJava(edtPost.getText().toString()), data.getPost_id(), tagpeopleid, data.getId());
                }
                break;
              case R.id.taguser:
                LogUtils.e("Coming here");
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_searchuser, null);
                dialogBuilder.setView(dialogView);

                EditText edt_name = (EditText) dialogView.findViewById(R.id.edt_name);
                list_users = (RecyclerView) dialogView.findViewById(R.id.list_users);
                tagdialog = dialogBuilder.create();
                tagdialog.show();
                edt_name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(edt_name.getText().toString().length()>=1)
                        {
                            if(NetworkUtil.isInternetOn(ReplyActivity.this))
                                makeApiCall(edt_name.getText().toString().trim());
                            else
                                Tools.showDialog(true,getString(R.string.str_check_network),ReplyActivity.this,ReplyActivity.this);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onProfile(String id) {
        UserDetails(id,id);
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
                                txtPlace.setText(Tools.getAddress(ReplyActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                            }
                            if(!user.getId().equals(profile.getId()))
                                btnstatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String room_Id = profile.getRoomid();
                                        String name = profile.getUsername();
                                        String id = profile.getId();
                                        if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
                                            Tools.showDialog(true,"This user need to login again to use chat",ReplyActivity.this,ReplyActivity.this);
                                        else
                                        {
                                            Intent intent = new Intent(ReplyActivity.this, CometChatMessageListActivity.class);
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
                            txtPlace.setText(Tools.getAddress(ReplyActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                        }
                        if(profile.getLocationlist()!=null)
                        {
                            if(profile.getLocationlist().size()>0)
                            {
                                RecentLocationAdapter adapter = new RecentLocationAdapter(ReplyActivity.this,profile.getUsername(),profile.getprofile_imagename(),profile.getLocationlist());
                                locationlist.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));
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
                Toast.makeText(ReplyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
    @Override
    public void onBackPressed() {
        if(onprofileView.getVisibility()==View.VISIBLE)
        {
            onprofileView.setVisibility(GONE);
        }else if(replied==1)
        {
            Intent intent=new Intent();
            setResult(5002,intent);
            finish();
        } else
        {
            finish();
        }
    }

    @Override
    public void onReply(int position) {

    }
    private void onReplyComment(String userid,String comment,String postid,String tagid,String commentid) {
        onLoadProgress(ReplyActivity.this);
        String URL = SERVER_URL+REPLYCOMMENT;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().replycommentpost(userid,comment,postid,tagid,commentid)
                , lister,error_listener));
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

    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        replied = 1;
                        SimpleDateFormat  formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Reply reply = new Reply();
                        reply.setId(login.getString(DATA));
                        reply.setUser_id(user.getId());
                        reply.setPost_id(data.getPost_id());
                        reply.setReply_comment(edtPost.getText().toString());
                        reply.setEmail(user.getEmail());
                        reply.setUsername(user.getUsername());
                        reply.setProfile_imagename(user.getprofile_imagename());
                        String Datetime = formatter.format(new Date());
                        LogUtils.e(Datetime);
                        reply.setCreated_at(Datetime);//gson.fromJson(jsondata, Commentlist.class);
                        reply.setTagpeopledata(tagpeopledata);

                        LogUtils.e(tagpeopledata.size()+"");
                        for(int i=0;i<tagpeopledata.size();i++)
                        {
                            LogUtils.e(tagpeopledata.get(i).getId()+" "+tagpeopledata.get(i).getUsername()+" "+reply.getTagpeopledata().size());
                        }

                        replylist.add(reply);
                        if(replylist.size()==1)
                        {
                          //  Collections.reverse(listdata);
                            adapter = new ReplyAdapter(ReplyActivity.this,replylist,user.getId());
                            adapter.CallBackListener(ReplyActivity.this);
                            list_like.setAdapter(adapter);
                        }
                        else
                        {
                            adapter.notifyDataSetChanged();
                            list_like.smoothScrollToPosition(replylist.size()-1);
                        }
                        edtPost.setText("");
                       // TrendingFragment.onCommented(postid);

                        /// commentcallback.onCommented(postid);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProfile(int position) {

    }

    @Override
    public void onitemClick(int position) {
        comments = edtPost.getText().toString();
        LogUtils.e("OnItem "+comments+" "+Userlist.get(position).getUsername());
        edtPost.setText(comments+" "+Userlist.get(position).getUsername());
        hideKeyboard(ReplyActivity.this);
        if(!taglist.contains(Userlist.get(position)))
            taglist.add(Userlist.get(position));
        tagdialog.dismiss();
    }

    @Override
    public void onCallClick(int position) {

    }
}
