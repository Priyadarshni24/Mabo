package com.advengers.mabo.Activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.advengers.mabo.Adapter.PosrSliderAdapter;
import com.advengers.mabo.Adapter.RecentLocationAdapter;
import com.advengers.mabo.Model.Post;
import com.advengers.mabo.Model.Tag;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ActivitySinglepostBinding;
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
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static com.advengers.mabo.Activity.MainActivity.DISLIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.LIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.SINGLEPOST;
import static com.advengers.mabo.Activity.MainActivity.USERDETAILS;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.IMAGENAME;
import static com.advengers.mabo.Interfaces.Keys.LATITUDE;
import static com.advengers.mabo.Interfaces.Keys.LONGITUDE;
import static com.advengers.mabo.Interfaces.Keys.POSTDATA;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class SinglePostActivity extends MyActivity {
    String postid;
    SimpleDateFormat formatter1;
    Post post;
    int status = 0;
    ViewPager slide_viewpager;
    PageIndicatorView pageIndicatorView;
    TextView txtname,txtDate,tname,taddress,txt_postdistance,imd_forward,txt_postname,txt_postaddress,txt_postdesc,txt_postedon,txt_date,txt_tag,likecount,commentcount;
    TextView txtpostcount;
    TextView txtPlace,btnstatus;
    ImageView imgProfile,img_tag,imd_like,imd_profile,imd_share,imd_comment;
    RelativeLayout onprofileView,rl_features;
    RecyclerView locationlist;
    boolean loadpost = false;
    Toolbar mToolbar;
    private PosrSliderAdapter sliderAdapter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepost);
        mToolbar = (Toolbar)findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_post));
        getUser();
        if(getIntent().getStringExtra("URI")!=null) {
            String URI = getIntent().getStringExtra("URI");
            try {
                LogUtils.e("SinglePost " + URI.split("postdata/")[1]);
                postid = URI.split("postdata/")[1];
                GetPost(postid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(getIntent().getStringExtra(POSTID)!=null)
        {
            postid = getIntent().getStringExtra(POSTID);
            GetPost(postid);
        }
        slide_viewpager = (ViewPager)findViewById(R.id.slide_viewpager);
        pageIndicatorView = (PageIndicatorView)findViewById(R.id.pageIndicatorView);
        txtDate = (TextView)findViewById(R.id.txt_date);
        tname = (TextView)findViewById(R.id.txtname);
        taddress = (TextView)findViewById(R.id.txt_address);
        imd_forward = (TextView)findViewById(R.id.imd_forward);
        txt_postname = (TextView)findViewById(R.id.txt_postname);
        txt_postaddress = (TextView)findViewById(R.id.txt_postaddress);
        txt_postdesc = (TextView)findViewById(R.id.txt_postdesc);
        txt_postedon = (TextView)findViewById(R.id.txt_postedon);
        txt_date = (TextView)findViewById(R.id.txt_date);
        txt_postdistance = (TextView)findViewById(R.id.txt_postdistance);
        txt_postdistance = (TextView)findViewById(R.id.txt_postdistance);
        commentcount = (TextView)findViewById(R.id.commentcount);
        rl_features = (RelativeLayout)findViewById(R.id.rl_features);
        txt_tag = (TextView)findViewById(R.id.txt_tag);
        likecount = (TextView)findViewById(R.id.likecount);
        img_tag = (ImageView)findViewById(R.id.img_tag);
        imd_like = (ImageView)findViewById(R.id.imd_like);
        imd_profile = (ImageView)findViewById(R.id.imd_profile);
        imd_share = (ImageView)findViewById(R.id.imd_share);
        imd_comment = (ImageView)findViewById(R.id.imd_comment);
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
    void GetPost(String id)
    {
        loadpost = true;
        String URL = SERVER_URL+SINGLEPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,URL,
                new ServerParams().getPost(user.getId(),id,user.getLatitude(),user.getLongitude()),lister,error_listener));
    }

    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            onLoadDismiss();
            Log.e("FBLogin Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {/*
                        binding.postlayout.setVisibility(View.VISIBLE);
                        binding.txtlayout.setVisibility(View.GONE);*/
                        JSONObject data = login.getJSONObject(DATA);
                        JSONArray postarray = data.getJSONArray("result_posts");
                        LogUtils.e(postarray.length()+"");
                        for(int k=0;k<postarray.length();k++) {
                            String jsondata = postarray.get(k).toString();
                            final JSONObject userdetails = new JSONObject(jsondata);
                            // LogUtils.e(jsondata);
                            post = gson.fromJson(jsondata, Post.class);
                            PrettyTime p = new PrettyTime();
                            // formatter = new SimpleDateFormat("dd MMM YY");
                            formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            LogUtils.e(post.getCreated());
                            try {
                                Date date = formatter1.parse(post.getCreated());
                                LogUtils.e(date.toString());
                                //     String strDate = formatter.format(date);
                                txtDate.setText(p.format(date));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if(post.getImage_url().isEmpty()||post.getImage_url().equals("[]"))
                            {
                                slide_viewpager.setVisibility(View.GONE);
                                pageIndicatorView.setVisibility(View.GONE);
                            }else{
                                pageIndicatorView.setVisibility(View.VISIBLE);
                                slide_viewpager.setVisibility(View.VISIBLE);
                                ArrayList<String> listimage = new ArrayList<>();
                                String[] arrOfStr = post.getImage_url().split(",", 3);

                                for (String a : arrOfStr)
                                {
                                    listimage.add(a);
                                }
                                ArrayList<String> Videoimage = new ArrayList<>();
                                String[] arrOfStrs = post.getVideo_url().split(",", 3);

                                for (String a : arrOfStrs)
                                {
                                    if(!a.isEmpty())
                                        Videoimage.add(a);
                                    LogUtils.e(a);
                                }
                                sliderAdapter = new PosrSliderAdapter(SinglePostActivity.this,SinglePostActivity.this,listimage,Videoimage);
                                slide_viewpager.setAdapter(sliderAdapter);
                                pageIndicatorView.setViewPager(slide_viewpager);
                                pageIndicatorView.setSelection(0);
                                pageIndicatorView.setSelectedColor(R.color.apptheme);
                                pageIndicatorView.setUnselectedColor(R.color.grey);
                                pageIndicatorView.setDynamicCount(true);
                            }
                            tname.setText(new JSONObject(jsondata).getString(USERNAME));
                            taddress.setText(Tools.getAddress(SinglePostActivity.this,Double.parseDouble(post.getLatitude()),Double.parseDouble(post.getLongitude())));
                            if(!userdetails.getString("profile_imagename").isEmpty())
                                Picasso.get().load(userdetails.getString("profile_imagename")).placeholder(R.drawable.ic_avatar).into(imd_profile);
                            txt_postname.setText(post.getPost_title());
                            Location locationA = new Location("point A");
                            locationA.setLatitude(Double.parseDouble(user.getLatitude()));
                            locationA.setLongitude(Double.parseDouble(user.getLongitude()));
                            Location locationB = new Location("point B");
                            locationB.setLatitude(Double.parseDouble(post.getLatitude()));
                            locationB.setLongitude(Double.parseDouble(post.getLongitude()));
                            double distance = locationB.distanceTo(locationA);
                           txt_postdistance.setText(Utils.df2.format(distance*0.001)+" "+getString(R.string.str_kms));
                            String content = "";
                            if(!post.getTag_location().isEmpty())
                            {
                                content = "@" + post.getTag_location();
                                txt_postdesc.setText(txt_postdesc.getText()+" @"+post.getTag_location());
                            }

                            if(!post.getTag_people().isEmpty())
                            {

                                    String tagpeople = "";

                                    List<Tag> tagpeoples = post.getTagpeopledata();
                                    for (int i = 0; i < tagpeoples.size(); i++) {
                                        if (i == 0)
                                            tagpeople = tagpeoples.get(0).getUsername();
                                        else
                                            tagpeople = tagpeople + " " + tagpeoples.get(i).getUsername();
                                    }
                                    content = content + " with " + tagpeople;
                                    LogUtils.e(content);
                                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder(content);
                                    ClickableSpan[] clickableSpans = new ClickableSpan[tagpeoples.size()];
                                    for (int i = 0; i < tagpeoples.size(); i++) {
                                        int finalI = i;
                                        clickableSpans[i] = new ClickableSpan() {
                                            @Override
                                            public void onClick(@NonNull View widget) {
                                             //   LogUtils.e("single tag " + tagpeoples.get(finalI).getUsername());
                                               // mLikeCommentcallback.onProfile(tagpeoples.get(finalI).getId());
                                             //  Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog, tagpeoples.get(finalI).getId(),tagpeoples.get(finalI).getId(),SinglePostActivity.this,SinglePostActivity.this);
                                                UserDetails( tagpeoples.get(finalI).getId(),tagpeoples.get(finalI).getId());
                                            }
                                        };
                                    }
                                    for (int i = 0; i < tagpeoples.size(); i++)
                                    {
                                        ssBuilder.setSpan(
                                                clickableSpans[i],
                                                content.indexOf(tagpeoples.get(i).getUsername()),
                                                content.indexOf(tagpeoples.get(i).getUsername()) + String.valueOf(tagpeoples.get(i).getUsername()).length(),
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                        );
                                    }
                                    // Display the spannable text to TextView
                                    txt_postdesc.setText(ssBuilder);

                                    // Specify the TextView movement method
                                    txt_postdesc.setMovementMethod(LinkMovementMethod.getInstance());


                            }


                            likecount.setText(post.getLikecount()+" "+getString(R.string.str_likes));
                            if(post.getLikedbyme()==null)
                            {
                                post.setLikedbyme("0");
                            }
                            if(post.getLikedbyme().equals("0"))
                                imd_like.setImageResource(R.drawable.ic_unlike);
                            else
                                imd_like.setImageResource(R.drawable.ic_like);
                               rl_features.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int Count = Integer.parseInt(post.getLikecount());
                                    if(post.getLikedbyme().equals("0"))
                                    {
                                        post.setLikedbyme("1");
                                        imd_like.setImageResource(R.drawable.ic_like);
                                        Count = Count + 1;
                                        post.setLikecount(String.valueOf(Count));
                                    }else
                                    {
                                        post.setLikedbyme("0");
                                        imd_like.setImageResource(R.drawable.ic_unlike);
                                        Count = Count - 1;
                                        post.setLikecount(String.valueOf(Count));
                                    }
                                    if(post.getLikedbyme().equals("1"))
                                        onLikepost(postid);
                                    else if(post.getLikedbyme().equals("0"))
                                        onDislikepost(postid);
                                    likecount.setText(post.getLikecount()+" "+getString(R.string.str_likes));

                                }
                            });
                            commentcount.setText(post.getCommentcount()+" "+getString(R.string.title_comments));
                           imd_comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent commentintent = new Intent(SinglePostActivity.this, CommentListActivity.class);
                                    commentintent.putExtra(POSTID,post.getId());
                                    commentintent.putExtra(POSTDATA,post);
                                    startActivity(commentintent);
                                }
                            });
                            commentcount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent commentintent = new Intent(SinglePostActivity.this, CommentListActivity.class);
                                    commentintent.putExtra(POSTID,post.getId());
                                    commentintent.putExtra(POSTDATA,post);
                                    startActivity(commentintent);
                                }
                            });
                            if (user.getId().equals(post.getUser_id()))
                               imd_forward.setVisibility(View.GONE);
                            imd_forward.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent mapactivity = new Intent(SinglePostActivity.this, MapDirectionActivity.class);
                                    mapactivity.putExtra(ID,post.getUser_id());
                                    try {
                                        mapactivity.putExtra(USERNAME,userdetails.getString(USERNAME));
                                        mapactivity.putExtra(IMAGENAME,userdetails.getString("profile_imagename"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    mapactivity.putExtra(LATITUDE,post.getLatitude());
                                    mapactivity.putExtra(LONGITUDE,post.getLongitude());
                                    startActivity(mapactivity);
                                }
                            });
                            imd_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String shareBody = "http://maboapp/postdata/"+post.getId();
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                                }
                            });
                            imd_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                //    Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog, user.getId(),post.getUser_id(),SinglePostActivity.this,SinglePostActivity.this);
                                    UserDetails(user.getId(),post.getUser_id());
                                }
                            });
                            likecount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent likeintent = new Intent(SinglePostActivity.this, LikelistActivity.class);
                                    likeintent.putExtra(POSTID,postid);
                                    startActivity(likeintent);
                                }
                            });
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
              //      LogUtils.e(res);
                    if(loadpost)
                    {
                    //    binding.pbbar.setVisibility(View.GONE);
                  //      binding.posttxt.setText(getString(R.string.str_nosinglepost));
                    }
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
    private void onLikepost(String postid) {
        loadpost = false;
        onLoadProgress(this);
        String URL = SERVER_URL+LIKEPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().LikePost(user.getId(),postid)
                , likelister,error_listener));
    }

    private void onDislikepost(String postid) {
        loadpost = false;
        onLoadProgress(this);
        String URL = SERVER_URL+DISLIKEPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().LikePost(user.getId(),postid)
                , likelister,error_listener));
    }
    Response.Listener likelister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {


                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
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
                                txtPlace.setText(Tools.getAddress(SinglePostActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                            }
                            if(!user.getId().equals(profile.getId()))
                            btnstatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String room_Id = profile.getRoomid();
                                    String name = profile.getUsername();
                                    String id = profile.getId();
                                    if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
                                        Tools.showDialog(true,"This user need to login again to use chat",SinglePostActivity.this,SinglePostActivity.this);
                                    else
                                    {
                                        Intent intent = new Intent(SinglePostActivity.this, CometChatMessageListActivity.class);
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
                            txtPlace.setText(Tools.getAddress(SinglePostActivity.this, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                        }
                        if(profile.getLocationlist()!=null)
                        {
                            if(profile.getLocationlist().size()>0)
                            {
                                RecentLocationAdapter adapter = new RecentLocationAdapter(SinglePostActivity.this,profile.getUsername(),profile.getprofile_imagename(),profile.getLocationlist());
                                locationlist.setLayoutManager(new LinearLayoutManager(SinglePostActivity.this));
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
                Toast.makeText(SinglePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
