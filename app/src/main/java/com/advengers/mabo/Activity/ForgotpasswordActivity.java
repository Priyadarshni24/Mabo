package com.advengers.mabo.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ActivityChangepasswordBinding;
import com.advengers.mabo.databinding.ActivityForgotpasswordBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.advengers.mabo.Activity.MainActivity.CHANGEPASSWORD;
import static com.advengers.mabo.Activity.MainActivity.FORGOTPASSWORD;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;

public class ForgotpasswordActivity  extends MyActivity implements View.OnClickListener {
    ActivityForgotpasswordBinding binding;
    String newpassword,confirmpassword;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ForgotpasswordActivity.this, R.layout.activity_forgotpassword);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
      //  getSupportActionBar().setTitle(getString(R.string.title_comments));
        getUser();
        binding.btnEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_email:
                if(Tools.isValidEmail(binding.etUsername.getText()))
                {
                    onforgotpassword(binding.etUsername.getText().toString());
                }else
                    Utils.showToast(getString(R.string.err_invalidemail),ForgotpasswordActivity.this);
                break;
        }
    }
    private void onforgotpassword(String email) {
        onLoadProgress(ForgotpasswordActivity.this);
        String URL = SERVER_URL+FORGOTPASSWORD;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().forgotpassword(email)
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
                        Utils.showToast(login.getString(MESSAGE),ForgotpasswordActivity.this);
                        onBackPressed();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
