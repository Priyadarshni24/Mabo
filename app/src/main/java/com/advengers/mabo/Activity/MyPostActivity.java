package com.advengers.mabo.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Adapter.PostAdapter;
import com.advengers.mabo.Fragments.MyFragment;
import com.advengers.mabo.Fragments.TrendingFragment;
import com.advengers.mabo.Model.Post;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Utils;
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

import static com.advengers.mabo.Activity.MainActivity.DELETEPOST;
import static com.advengers.mabo.Activity.MainActivity.DISLIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.LIKEPOST;
import static com.advengers.mabo.Activity.MainActivity.LOADPOST;
import static com.advengers.mabo.Activity.MainActivity.MYPOST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.IMAGENAME;
import static com.advengers.mabo.Interfaces.Keys.LATITUDE;
import static com.advengers.mabo.Interfaces.Keys.LONGITUDE;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.POSTDATA;
import static com.advengers.mabo.Interfaces.Keys.POSTID;
import static com.advengers.mabo.Interfaces.Keys.RANGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class MyPostActivity extends MyActivity implements View.OnClickListener,PostAdapter.LikeCommentCallback {
    Toolbar mToolbar;
    public static ArrayList<Post> postlist = new ArrayList<>();
    RecyclerView listpost;
    TextView txt_nopost,txt_address;
    int movelist = 0;
    public boolean isLoading = false;
    public static PostAdapter adapter;
    boolean postdelete = false;
    Post postdlt;
    MyFragment.ProfileClick callback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trending);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        listpost = (RecyclerView) findViewById(R.id.listpost);
        txt_nopost = (TextView) findViewById(R.id.txt_nopost);
        txt_address = (TextView) findViewById(R.id.txt_address);
        txt_address.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_mypost));
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getUser();
        initScrollListener();
        onLoadPost(0);

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
    private void initScrollListener() {
        listpost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == postlist.size() - 1) {
                        //bottom of list!
                        onLoadPost(postlist.size() + 1);
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void onLoadPost(int start) {
        onLoadProgress(MyPostActivity.this);
        movelist = start;
        if(movelist == 0)
            postlist.clear();
        String URL = SERVER_URL+MYPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().loadMyPost(user.getId(),String.valueOf(start),user.getLatitude(),user.getLongitude())
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

                        listpost.setVisibility(View.VISIBLE);
                        txt_nopost.setVisibility(View.GONE);
                        JSONArray postarray = login.getJSONObject(DATA).getJSONArray("result_posts");
                        //   int movelist = postlist.size();
                        if(postarray.length()>0) {
                            for (int i = 0; i < postarray.length(); i++) {
                                String jsondata = postarray.get(i).toString();
                                // LogUtils.e(jsondata);
                                Post post = gson.fromJson(jsondata, Post.class);
                                postlist.add(post);
                            }
                            LogUtils.e("Length of post " + postlist.size());
                            /* Collections.reverse(postlist);*/
                            if(movelist==0) {
                                adapter = new PostAdapter(MyPostActivity.this, MyPostActivity.this, postlist, user.getId());
                                adapter.setLikeCommentCallBackListener(MyPostActivity.this);
                                listpost.setAdapter(adapter);
                            }else
                                adapter.notifyDataSetChanged();
                            isLoading = false;

                        }else if(postlist.size()==0){
                            txt_nopost.setVisibility(View.VISIBLE);
                            listpost.setVisibility(View.GONE);
                        }

                    }else
                    {
                        txt_nopost.setVisibility(View.VISIBLE);
                        listpost.setVisibility(View.GONE);
                        showInfo(login.getString(MESSAGE),MyPostActivity.this,MyPostActivity.this);
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
    public void onClick(View v) {

    }
    private void onLikepost(String postid) {
        onLoadProgress(MyPostActivity.this);
        String URL = SERVER_URL+LIKEPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().LikePost(user.getId(),postid)
                , likelister,error_listener));
    }

    private void onDislikepost(String postid) {
        onLoadProgress(MyPostActivity.this);
        String URL = SERVER_URL+DISLIKEPOST;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().LikePost(user.getId(),postid)
                , likelister,error_listener));
    }
    @Override
    public void onLike(int position, String postid, String data) {
        if(data.equals("1"))
            onLikepost(postid);
        else if(data.equals("0"))
            onDislikepost(postid);
    }

    @Override
    public void onComment(int position, String postid) {
        Intent commentintent = new Intent(MyPostActivity.this, CommentListActivity.class);
        commentintent.putExtra(POSTID,postid);
        commentintent.putExtra(POSTDATA,postlist.get(position));
        startActivity(commentintent);
    }

    @Override
    public void onDirection(int position) {
        Intent mapactivity = new Intent(MyPostActivity.this, MapDirectionActivity.class);
        mapactivity.putExtra(ID,postlist.get(position).getUser_id());
        mapactivity.putExtra(USERNAME,postlist.get(position).getUserdetails().getUsername());
        mapactivity.putExtra(IMAGENAME,postlist.get(position).getUserdetails().getProfile_imagename());
        mapactivity.putExtra(LATITUDE,postlist.get(position).getLatitude());
        mapactivity.putExtra(LONGITUDE,postlist.get(position).getLongitude());
        startActivity(mapactivity);
    }

    @Override
    public void onShare(int position) {
        String shareBody = "<a href='http://www.mabo.com/postdata/"+postlist.get(position).getId()+"'>"+"http://www.mabo.com/postdata/"+postlist.get(position).getId()+" </a>";//"http://www.maboapp.com/post?"+postlist.get(position).getId();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        LogUtils.e(Html.fromHtml(shareBody).toString());
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Mabo");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(shareBody).toString());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    @Override
    public void onProfile(String postuserid) {
        callback.onProfile(postuserid);
    }

    @Override
    public void onLikeList(String postid) {
        Intent likeintent = new Intent(MyPostActivity.this, LikelistActivity.class);
        likeintent.putExtra(POSTID,postid);
        startActivity(likeintent);
    }

    @Override
    public void onDeletePost(int position) {
        if(user.getId().equals(postlist.get(position).getUser_id())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyPostActivity.this);
            AlertDialog dialog = builder.create();
            builder.setMessage(R.string.str_deletepost)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Utils.getInstance(MyPostActivity.this).setBoolean(Utils.PREF_PROFILE_FILL, true);
                    /*    if(callBackListener != null)
                            callBackListener.onPeopleCallBack();*/
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onLoadProgress(MyPostActivity.this);
                            postdelete = true;
                            String URL = SERVER_URL + DELETEPOST;
                            URL = URL.replaceAll(" ", "%20");
                            App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                                    URL, new ServerParams().LikePost(user.getId(), postlist.get(position).getId())
                                    , likelister, error_listener)); // FIRE ZE MISSILES!
                            postdlt = postlist.get(position);
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.show();
        }
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

                        if(postdelete)
                        {
                            postlist.remove(postdlt);
                            adapter.notifyDataSetChanged();
                            postdelete = false;
                        }
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
