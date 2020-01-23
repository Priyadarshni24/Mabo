package com.advengers.mabo.Activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Adapter.PosrSliderAdapter;
import com.advengers.mabo.Model.Post;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.advengers.mabo.Activity.MainActivity.DISLIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.LIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.PROFILEPIC;
import static com.advengers.mabo.Activity.MainActivity.PUTINTREST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.SINGLEPOST;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.IMAGENAME;
import static com.advengers.mabo.Interfaces.Keys.LATITUDE;
import static com.advengers.mabo.Interfaces.Keys.LONGITUDE;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class SinglePostActivity extends MyActivity {
    ActivitySinglepostBinding binding;
    String postid;
    SimpleDateFormat formatter1;
    Post post;
    boolean loadpost = false;
    private PosrSliderAdapter sliderAdapter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SinglePostActivity.this, R.layout.activity_singlepost);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_post));
        getUser();
        String URI = getIntent().getStringExtra("URI");
        try{
            LogUtils.e("SinglePost "+URI.split("postdata/")[1]);
            postid = URI.split("postdata/")[1];
            GetPost(postid);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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


                    if (login.getString(STATUS_JSON).equals("true")) {
                        binding.postlayout.setVisibility(View.VISIBLE);
                        binding.txtlayout.setVisibility(View.GONE);
                        JSONObject data = login.getJSONObject(DATA);
                        JSONArray postarray = data.getJSONArray("result_posts");
                        LogUtils.e(postarray.length()+"");
                        for(int i=0;i<postarray.length();i++) {
                            String jsondata = postarray.get(i).toString();
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
                                binding.txtDate.setText(p.format(date));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if(post.getImage_url().isEmpty()||post.getImage_url().equals("[]"))
                            {
                                binding.slideViewpager.setVisibility(View.GONE);
                            }else{

                                binding.slideViewpager.setVisibility(View.VISIBLE);
                                ArrayList<String> listimage = new ArrayList<>();
                                String[] arrOfStr = post.getImage_url().split(",", 3);

                                for (String a : arrOfStr)
                                {
                                    listimage.add(a);
                                }
                                ArrayList<String> Videoimage = new ArrayList<>();
                                String[] arrOfStrs = post.getVideo_url().split(",", 3);

                                for (String a : arrOfStr)
                                {
                                    Videoimage.add(a);
                                }
                                sliderAdapter = new PosrSliderAdapter(SinglePostActivity.this,SinglePostActivity.this,listimage,Videoimage);
                                binding.slideViewpager.setAdapter(sliderAdapter);
                            }
                            binding.txtName.setText(new JSONObject(jsondata).getString(USERNAME));
                            binding.txtAddress.setText(Tools.getAddress(SinglePostActivity.this,Double.parseDouble(post.getLatitude()),Double.parseDouble(post.getLongitude())));
                            if(!userdetails.getString("profile_imagename").isEmpty())
                                Picasso.get().load(userdetails.getString("profile_imagename")).placeholder(R.drawable.ic_avatar).into(binding.imgProfile);
                            binding.txtPostname.setText(post.getPost_title());
                            Location locationA = new Location("point A");
                            locationA.setLatitude(Double.parseDouble(user.getLatitude()));
                            locationA.setLongitude(Double.parseDouble(user.getLongitude()));
                            Location locationB = new Location("point B");
                            locationB.setLatitude(Double.parseDouble(post.getLatitude()));
                            locationB.setLongitude(Double.parseDouble(post.getLongitude()));
                            double distance = locationB.distanceTo(locationA);
                           binding.txtPostdistance.setText(Utils.df2.format(distance*0.001)+" "+getString(R.string.str_kms));
                            if(!post.getTag_people().isEmpty())
                            {
                                binding.txtPostdesc.setText(getString(R.string.str_with)+" "+post.getTag_people());
                            }
                            if(!post.getTag_location().isEmpty())
                            {
                                binding.txtPostdesc.setText(binding.txtPostdesc.getText()+" @"+post.getTag_location());
                            }
                            binding.likecount.setText(post.getLikecount()+" "+getString(R.string.str_likes));
                            if(post.getLikedbyme()==null)
                            {
                                post.setLikedbyme("0");
                            }
                            if(post.getLikedbyme().equals("0"))
                                binding.imdLike.setImageResource(R.drawable.ic_unlike);
                            else
                                binding.imdLike.setImageResource(R.drawable.fav);
                            binding.imdLike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int Count = Integer.parseInt(post.getLikecount());
                                    if(post.getLikedbyme().equals("0"))
                                    {
                                        post.setLikedbyme("1");
                                        binding.imdLike.setImageResource(R.drawable.fav);
                                        Count = Count + 1;
                                        post.setLikecount(String.valueOf(Count));
                                    }else
                                    {
                                        post.setLikedbyme("0");
                                        binding.imdLike.setImageResource(R.drawable.ic_unlike);
                                        Count = Count - 1;
                                        post.setLikecount(String.valueOf(Count));
                                    }
                                    if(post.getLikedbyme().equals("1"))
                                        onLikepost(postid);
                                    else if(post.getLikedbyme().equals("0"))
                                        onDislikepost(postid);
                                    binding.likecount.setText(post.getLikecount()+" "+getString(R.string.str_likes));

                                }
                            });
                            binding.commentcount.setText(post.getCommentcount()+" "+getString(R.string.title_comments));
                            binding.imdComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent commentintent = new Intent(SinglePostActivity.this, CommentListActivity.class);
                                    commentintent.putExtra(POSTID,post.getId());
                                    startActivity(commentintent);
                                }
                            });
                            binding.commentcount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent commentintent = new Intent(SinglePostActivity.this, CommentListActivity.class);
                                    commentintent.putExtra(POSTID,post.getId());
                                    startActivity(commentintent);
                                }
                            });
                            binding.imdForward.setOnClickListener(new View.OnClickListener() {
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
                            binding.imdShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String shareBody = "http://maboapp/postdata/"+post.getId();
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                                }
                            });
                            binding.imgProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog, user.getId(),post.getUser_id(),SinglePostActivity.this,SinglePostActivity.this);
                                }
                            });
                            binding.likecount.setOnClickListener(new View.OnClickListener() {
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
                        binding.pbbar.setVisibility(View.GONE);
                        binding.posttxt.setText(getString(R.string.str_nosinglepost));
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
}
