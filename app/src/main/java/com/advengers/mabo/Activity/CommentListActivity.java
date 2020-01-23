package com.advengers.mabo.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Adapter.CommentAdapter;
import com.advengers.mabo.Adapter.LikeAdapter;
import com.advengers.mabo.Fragments.TrendingFragment;
import com.advengers.mabo.Model.Commentlist;
import com.advengers.mabo.Model.Likelist;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.ActivityCommentlistBinding;
import com.advengers.mabo.databinding.ActivityLikelistBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.advengers.mabo.Activity.MainActivity.COMMENTLIST;
import static com.advengers.mabo.Activity.MainActivity.COMMENTPOST;
import static com.advengers.mabo.Activity.MainActivity.LIKELIST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;

public class CommentListActivity extends MyActivity implements View.OnClickListener, CommentAdapter.ProfileClick{
    ActivityCommentlistBinding binding;
    ArrayList<Commentlist> listdata = new ArrayList<>();
    String postid;
    CommentAdapter adapter;
    OnCommented commentcallback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(CommentListActivity.this, R.layout.activity_commentlist);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_comments));
        getUser();
        postid = getIntent().getExtras().getString(POSTID);
        onCommentlist(postid);
        binding.btnPost.setOnClickListener(this);
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
    private void onCommentlist(String postid) {
        onLoadProgress(CommentListActivity.this);
        String URL = SERVER_URL+COMMENTLIST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().listlike(postid)
                , likelister,error_listener));
    }
    private void onComment(String userid,String comment,String postid) {
        onLoadProgress(CommentListActivity.this);
        String URL = SERVER_URL+COMMENTPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().commentpost(userid,comment,postid)
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
                             LogUtils.e(jsondata);
                            Commentlist post = gson.fromJson(jsondata, Commentlist.class);
                            listdata.add(post);
                        }
                        /* Collections.reverse(postlist);*/
                        Collections.reverse(listdata);
                        adapter = new CommentAdapter(CommentListActivity.this,listdata);
                        adapter.CallBackListener(CommentListActivity.this);
                        binding.listLike.setAdapter(adapter);

                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
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

                        SimpleDateFormat  formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Commentlist post = new Commentlist();
                        post.setId(user.getId());
                        post.setUser_id(user.getId());
                        post.setPost_id(login.getString(DATA));
                        post.setComment(binding.edtPost.getText().toString());
                        post.setEmail(user.getEmail());
                        post.setUsername(user.getUsername());
                        post.setProfile_imagename(user.getprofile_imagename());
                        String Datetime = formatter.format(new Date());
                        LogUtils.e(Datetime);
                        post.setCreated(Datetime);//gson.fromJson(jsondata, Commentlist.class);
                        listdata.add(post);
                        if(listdata.size()==1)
                        {
                            Collections.reverse(listdata);
                            adapter = new CommentAdapter(CommentListActivity.this,listdata);
                            adapter.CallBackListener(CommentListActivity.this);
                            binding.listLike.setAdapter(adapter);
                        }
                        else
                            adapter.notifyDataSetChanged();
                            binding.edtPost.setText("");
                         TrendingFragment.onCommented(postid);
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
        Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog,user.getId(),listdata.get(position).getId(),CommentListActivity.this,CommentListActivity.this);
    }

    @Override
    public void onClick(View view) {

        if(!binding.edtPost.getText().toString().trim().isEmpty())
        {
            onComment(user.getId(), StringEscapeUtils.escapeJava(binding.edtPost.getText().toString()),postid);
        }
    }
    public void setOnCommentedListener(OnCommented mCallback) {
        this.commentcallback = mCallback;
    }

    public interface OnCommented
    {
        public void onCommented(String postid);
    }
}
