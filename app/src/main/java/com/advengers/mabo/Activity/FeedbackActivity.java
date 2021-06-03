package com.advengers.mabo.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Adapter.CommentAdapter;
import com.advengers.mabo.Model.Commentlist;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.ActivityFeedbackBinding;
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
import java.util.Collections;

import static com.advengers.mabo.Activity.MainActivity.COMMENTLIST;
import static com.advengers.mabo.Activity.MainActivity.FEEDBACK;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;

public class FeedbackActivity extends MyActivity implements View.OnClickListener {
    ActivityFeedbackBinding binding;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUser();
        binding = DataBindingUtil.setContentView(FeedbackActivity.this, R.layout.activity_feedback);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.str_feedback));
        binding.btnSubmit.setOnClickListener(this);
        binding.txtusername.setText(user.getUsername());
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
    private void onComment(String name,String email,String message,String acntid) {
        onLoadProgress(FeedbackActivity.this);
        String URL = SERVER_URL+FEEDBACK;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().SendFeedback(name,email,message,acntid)
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


                    if (login.getString(STATUS_JSON).equals("true"))
                       showInfo(login.getString(MESSAGE),FeedbackActivity.this,FeedbackActivity.this);
                    else
                       showWarning(login.getString(MESSAGE),FeedbackActivity.this,FeedbackActivity.this);
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
        boolean cancel = false;
        View focusView = null;
        if(TextUtils.isEmpty(binding.edtEmail.getText().toString()))
        {
            binding.edtEmail.setError(getString(R.string.enter_email));
            focusView = binding.edtEmail;
            cancel = true;
        }
        else if(!Tools.isValidEmail(binding.edtEmail.getText().toString()))
        {
            binding.edtEmail.setError(getString(R.string.enter_email));
            focusView = binding.edtEmail;
            cancel = true;
        }else if(TextUtils.isEmpty(binding.edtMessage.getText().toString()))
        {
            binding.edtMessage.setError(getString(R.string.enter_feedback));
            focusView = binding.edtMessage;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if(binding.edtEmail.getText().length()>0 && binding.edtMessage.getText().length()>0)
            {
                onComment(user.getUsername(),binding.edtEmail.getText().toString(),binding.edtMessage.getText().toString(),user.getUsername());
            }
        }


    }
}
