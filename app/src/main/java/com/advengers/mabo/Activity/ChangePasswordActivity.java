package com.advengers.mabo.Activity;

import android.os.Bundle;
import android.text.InputType;
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
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ActivityChangepasswordBinding;
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

import static com.advengers.mabo.Activity.MainActivity.CHANGEPASSWORD;
import static com.advengers.mabo.Activity.MainActivity.COMMENTPOST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;


public class ChangePasswordActivity extends MyActivity implements View.OnClickListener {
    ActivityChangepasswordBinding binding;
    String newpassword,confirmpassword;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ChangePasswordActivity.this, R.layout.activity_changepassword);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_comments));
        getUser();
        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.npwdview.setOnClickListener(this);
        binding.cpwdview.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                newpassword = binding.edtNewpassword.getText().toString();
                confirmpassword = binding.edtConfirmpassword.getText().toString();
                LogUtils.e("confirm "+newpassword+" "+confirmpassword);
                if (!newpassword.isEmpty() && !confirmpassword.isEmpty())
                    onChangepassword(user.getId(), newpassword, confirmpassword, user.getEmail());
                break;
            case R.id.npwdview:
                if(binding.edtNewpassword.getInputType()== 129)
                    binding.edtNewpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                else if(binding.edtNewpassword.getInputType()== InputType.TYPE_CLASS_TEXT)
                    binding.edtNewpassword.setInputType(129);
                break;
            case R.id.cpwdview:
                if(binding.edtConfirmpassword.getInputType()== 129)
                    binding.edtConfirmpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                else if(binding.edtConfirmpassword.getInputType()== InputType.TYPE_CLASS_TEXT)
                    binding.edtConfirmpassword.setInputType(129);
                break;
        }
    }
    private void onChangepassword(String userid,String newpwd,String confirmpwd,String email) {
        onLoadProgress(ChangePasswordActivity.this);
        String URL = SERVER_URL+CHANGEPASSWORD;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().changePassword(userid,newpassword,confirmpassword,email)
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
                            Utils.showToast(login.getString(MESSAGE),ChangePasswordActivity.this);
                           onBackPressed();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
