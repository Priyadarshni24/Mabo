package com.advengers.mabo.Fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.CommentListActivity;
import com.advengers.mabo.Activity.CreatePostActivity;
import com.advengers.mabo.Activity.LikelistActivity;
import com.advengers.mabo.Activity.MapDirectionActivity;
import com.advengers.mabo.Adapter.PostAdapter;
import com.advengers.mabo.Model.Post;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.SimpleDividerItemDecoration;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.FragmentTrendingBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.advengers.mabo.Activity.MainActivity.DISLIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.LIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.LOADPOST;
import static com.advengers.mabo.Activity.MainActivity.LOGOUT;
import static com.advengers.mabo.Activity.MainActivity.PROFILEPIC;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.IMAGENAME;
import static com.advengers.mabo.Interfaces.Keys.LATITUDE;
import static com.advengers.mabo.Interfaces.Keys.LONGITUDE;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USER;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class TrendingFragment extends MyFragment implements View.OnClickListener,CreatePostActivity.ClosingCallActivity,
                                                            PostAdapter.LikeCommentCallback{
    FragmentTrendingBinding binding;
    public static ArrayList<Post> postlist = new ArrayList<>();
    CreatePostActivity ClosecallActivity;
    CommentListActivity commentListActivity;
    public static PostAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trending, container, false);
        ClosecallActivity = new CreatePostActivity();
        ClosecallActivity.CallBackListener(this);
       /* commentListActivity = new CommentListActivity();
        commentListActivity.setOnCommentedListener(this);*/
        getUser();

              if(!user.getLatitude().isEmpty()&&!user.getLongitude().isEmpty()) {

                  String address = Tools.getAddress(getActivity(),
                          Double.parseDouble(user.getLatitude()),
                          Double.parseDouble(user.getLongitude()));
                        binding.txtAddress.setText(address);
                    }else
                        binding.txtAddress.setText(getString(R.string.title_home));
                binding.fabAddpost.setOnClickListener(this);
         onLoadPost();
        return binding.getRoot();
    }

    @Override
    public String getTagManager() {
        return null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fab_addpost)
        {
            startActivity(new Intent(getActivity(), CreatePostActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void onLoadPost() {
        onLoadProgress(getActivity());
        String URL = SERVER_URL+LOADPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().loadPost(user.getId(),user.getLatitude(),user.getLongitude())
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
                        postlist.clear();
                        binding.listpost.setVisibility(View.VISIBLE);
                        binding.txtNopost.setVisibility(View.GONE);
                        JSONArray postarray = login.getJSONObject(DATA).getJSONArray("result_posts");
                        for(int i=0;i<postarray.length();i++) {
                            String jsondata = postarray.get(i).toString();
                           // LogUtils.e(jsondata);
                            Post post = gson.fromJson(jsondata, Post.class);
                            postlist.add(post);
                        }
                        LogUtils.e("Length of post "+postlist.size());
                      /* Collections.reverse(postlist);*/
                        adapter = new PostAdapter(getActivity(),getActivity(),postlist,user.getId());
                        adapter.setLikeCommentCallBackListener(TrendingFragment.this);

                        binding.listpost.setAdapter(adapter);

                    }else
                    {
                        binding.txtNopost.setVisibility(View.VISIBLE);
                        binding.listpost.setVisibility(View.GONE);
                        showInfo(login.getString(MESSAGE));
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

    private void onLikepost(String postid) {
        onLoadProgress(getActivity());
        String URL = SERVER_URL+LIKEPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().LikePost(user.getId(),postid)
                , likelister,error_listener));
    }

    private void onDislikepost(String postid) {
        onLoadProgress(getActivity());
        String URL = SERVER_URL+DISLIKEPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().LikePost(user.getId(),postid)
                , likelister,error_listener));
    }

    @Override
    public void onClosecall() {
        onLoadPost();
    }

    @Override
    public void onLike(int position,String postid,String data) {
        if(data.equals("1"))
            onLikepost(postid);
        else if(data.equals("0"))
            onDislikepost(postid);
    }

    @Override
    public void onComment(int position,String postid) {
      //  new CommentListActivity().setOnCommentedListener(this);
        Intent commentintent = new Intent(getActivity(), CommentListActivity.class);
        commentintent.putExtra(POSTID,postid);
        getActivity().startActivity(commentintent);
    }

    @Override
    public void onDirection(int position) {
        Intent mapactivity = new Intent(getActivity(), MapDirectionActivity.class);
        mapactivity.putExtra(ID,postlist.get(position).getUser_id());
        mapactivity.putExtra(USERNAME,postlist.get(position).getUserdetails().getUsername());
        mapactivity.putExtra(IMAGENAME,postlist.get(position).getUserdetails().getProfile_imagename());
        mapactivity.putExtra(LATITUDE,postlist.get(position).getLatitude());
        mapactivity.putExtra(LONGITUDE,postlist.get(position).getLongitude());
        getActivity().startActivity(mapactivity);
    }

    @Override
    public void onShare(int position) {
        String shareBody = "http://maboapp/postdata/"+postlist.get(position).getId();//"http://www.maboapp.com/post?"+postlist.get(position).getId();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    @Override
    public void onProfile(int position) {
       /* User profile = new User();
        profile.setUsername(postlist.get(position).getUserdetails().getUsername());
        profile.setprofile_imagename(postlist.get(position).getUserdetails().getProfile_imagename());
        profile.setEmail(postlist.get(position).getUserdetails().getEmail());
       */
        Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog, user.getId(),postlist.get(position).getUser_id(),getContext(),getActivity());
     //  Tools.UserDetails(postlist.get(position).getUser_id(),getContext(),getActivity());
    }

    @Override
    public void onLikeList(String postid) {
        Intent likeintent = new Intent(getActivity(), LikelistActivity.class);
        likeintent.putExtra(POSTID,postid);
        getActivity().startActivity(likeintent);
    }

    public static void onCommented(String postid) {
        for(int i=0;i<postlist.size();i++)
        {
            if(postlist.get(i).getId().equals(postid))
            {
                int count = Integer.parseInt(postlist.get(i).getCommentcount())+1;
                postlist.get(i).setCommentcount(String.valueOf(count));
               /* adapter = new PostAdapter(getActivity(),getActivity(),postlist,user.getId());
                adapter.setLikeCommentCallBackListener(TrendingFragment.this);     */
               adapter.notifyDataSetChanged();
            }
        }
    }
}
