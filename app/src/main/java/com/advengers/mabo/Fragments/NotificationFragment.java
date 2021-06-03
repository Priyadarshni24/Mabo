package com.advengers.mabo.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.LikelistActivity;
import com.advengers.mabo.Activity.SinglePostActivity;
import com.advengers.mabo.Adapter.NotificationAdapter;
import com.advengers.mabo.Adapter.PostAdapter;
import com.advengers.mabo.Model.Notification;
import com.advengers.mabo.Model.Post;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.FragmentNotificationBinding;
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
import java.util.Collections;

import static com.advengers.mabo.Activity.MainActivity.LOADNOTIFICATION;
import static com.advengers.mabo.Activity.MainActivity.LOADPOST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;

public class NotificationFragment extends MyFragment implements NotificationAdapter.ProfileClick {
    public static ArrayList<Notification> postlist = new ArrayList<>();
    FragmentNotificationBinding binding;
    NotificationAdapter adapter;
    @Override
    public String getTagManager() {
        return null;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        getUser();
        onLoadNotification();
        return binding.getRoot();
    }

    private void onLoadNotification() {
        onLoadProgress(getActivity());
        String URL = SERVER_URL+LOADNOTIFICATION;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().loadNotification(user.getId())
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
                        JSONArray postarray = login.getJSONArray(DATA);
                        for(int i=0;i<postarray.length();i++) {
                            String jsondata = postarray.get(i).toString();
                            // LogUtils.e(jsondata);
                            if(jsondata.contains("details")) {
                                Notification post = gson.fromJson(jsondata, Notification.class);
                                postlist.add(post);
                            }
                        }
                        LogUtils.e("Length of post "+postlist.size());
                       ///  Collections.reverse(postlist);
                        adapter = new NotificationAdapter(getActivity(),postlist);
                        adapter.CallBackListener(NotificationFragment.this);

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


    @Override
    public void onItem(int position) {
        Intent post = new Intent(getApplicationContext(), SinglePostActivity.class);
        post.putExtra(POSTID,postlist.get(position).getPost_id());
        startActivity(post);
    }

    @Override
    public void onProfile(int position) {
       // Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog,user.getId(),postlist.get(position).getUser_id(), getContext(),getActivity());
        callback.onProfile(postlist.get(position).getDetails().getUser_id());
    }
}
