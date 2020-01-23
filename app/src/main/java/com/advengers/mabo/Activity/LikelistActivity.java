package com.advengers.mabo.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Adapter.LikeAdapter;
import com.advengers.mabo.Adapter.PostAdapter;
import com.advengers.mabo.Fragments.TrendingFragment;
import com.advengers.mabo.Model.Likelist;
import com.advengers.mabo.Model.Post;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.advengers.mabo.Activity.MainActivity.LIKELIST;
import static com.advengers.mabo.Activity.MainActivity.LIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;

public class LikelistActivity extends MyActivity implements LikeAdapter.ProfileClick{

    ActivityLikelistBinding binding;
    ArrayList<Likelist> listdata = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(LikelistActivity.this, R.layout.activity_likelist);
        getUser();
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_likes));
        String postid = getIntent().getExtras().getString(POSTID);
        onLikelist(postid);
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
                        binding.listLike.setAdapter(adapter);

                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProfile(int position) {
        Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog,user.getId(),listdata.get(position).getId(),LikelistActivity.this,LikelistActivity.this);
    }
}
