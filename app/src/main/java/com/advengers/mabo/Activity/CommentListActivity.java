package com.advengers.mabo.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Adapter.AutoSuggestAdapter;
import com.advengers.mabo.Adapter.CommentAdapter;
import com.advengers.mabo.Adapter.LikeAdapter;
import com.advengers.mabo.Adapter.TagpeopleAdapter;
import com.advengers.mabo.Fragments.TrendingFragment;
import com.advengers.mabo.Model.Commentlist;
import com.advengers.mabo.Model.Likelist;
import com.advengers.mabo.Model.Tag;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.NetworkUtil;
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
import java.util.List;

import static com.advengers.mabo.Activity.CreatePostActivity.hideKeyboard;
import static com.advengers.mabo.Activity.MainActivity.COMMENTLIST;
import static com.advengers.mabo.Activity.MainActivity.COMMENTPOST;
import static com.advengers.mabo.Activity.MainActivity.LIKELIST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.USERSEARCH;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class CommentListActivity extends MyActivity implements View.OnClickListener, CommentAdapter.ProfileClick{
    ActivityCommentlistBinding binding;
    ArrayList<Commentlist> listdata = new ArrayList<>();
    String postid;
    CommentAdapter adapter;
    private Handler handler;
    OnCommented commentcallback;
    ArrayList<Tag> tagpeopledata = new ArrayList<>();
    ArrayList<User> Userlist = new ArrayList<>();
    ArrayList<User> taglist = new ArrayList<>();
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private AutoSuggestAdapter autoSuggestAdapter;
    String searchtag,comments;
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
        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        binding.edtPost.setThreshold(2);
        binding.edtPost.setAdapter(autoSuggestAdapter);
        binding.btnPost.setOnClickListener(this);
        binding.edtPost.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        LogUtils.e("OnItem "+comments.replace("@","")+" "+autoSuggestAdapter.getObject(position));
                        binding.edtPost.setText(comments.replace("@","")+autoSuggestAdapter.getObject(position));
                        hideKeyboard(CommentListActivity.this);
                        taglist.add(Userlist.get(position));
                    }
                });
        binding.edtPost.addTextChangedListener(new TextWatcher() {
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
                if(binding.edtPost.getText().toString().length()>0)
                {
                    String comment = binding.edtPost.getText().toString();

                    if(comment.contains(" "))
                    {
                      /*  comments = comment;
                        LogUtils.e(comments);*/
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
                    if (!TextUtils.isEmpty(binding.edtPost.getText())) {
                        makeApiCall(searchtag);
                        LogUtils.e(""+searchtag);
                    }
                }
                return false;
            }
        });
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
                        //IMPORTANT: set data here and notify
                        autoSuggestAdapter.setData(stringList);
                        autoSuggestAdapter.notifyDataSetChanged();
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
    private void onComment(String userid,String comment,String postid,String tagid) {
        onLoadProgress(CommentListActivity.this);
        String URL = SERVER_URL+COMMENTPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().commentpost(userid,comment,postid,tagid)
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
                        JSONObject data = login.getJSONObject(DATA);
                        JSONArray listarray = data.getJSONArray("result_posts");
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
                        post.setTagpeopledata(tagpeopledata);

                        LogUtils.e(tagpeopledata.size()+"");
                        for(int i=0;i<tagpeopledata.size();i++)
                        {
                            LogUtils.e(tagpeopledata.get(i).getId()+" "+tagpeopledata.get(i).getUsername());
                        }

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
                         tagpeopledata.clear();
                       /// commentcallback.onCommented(postid);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProfile(String id) {
        Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog,user.getId(),id,CommentListActivity.this,CommentListActivity.this);
    }

    @Override
    public void onClick(View view) {

        if(!binding.edtPost.getText().toString().trim().isEmpty())
        {
            String tagpeopleid="";

            if(taglist.size()>0)
                for(int i=0;i<taglist.size();i++)
                {
                    if(i==0)
                    {
                        if(binding.edtPost.getText().toString().contains(taglist.get(i).getUsername()))
                            tagpeopleid = taglist.get(i).getId();
                            Tag tag = new Tag();
                            tag.setId(taglist.get(i).getId());
                            tag.setUsername(taglist.get(i).getUsername());
                            tagpeopledata.add(tag);

                    }else
                    {
                        if(binding.edtPost.getText().toString().contains(taglist.get(i).getUsername()))
                            tagpeopleid = tagpeopleid+","+taglist.get(i).getId();
                            Tag tag = new Tag();
                            tag.setId(taglist.get(i).getId());
                            tag.setUsername(taglist.get(i).getUsername());
                            tagpeopledata.add(tag);
                    }

                }
            LogUtils.e("Tag ids "+tagpeopleid);
            onComment(user.getId(), StringEscapeUtils.escapeJava(binding.edtPost.getText().toString()),postid,tagpeopleid);
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
