package com.advengers.mabo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.advengers.mabo.Chat.web_communication.WebCall;
import com.advengers.mabo.Chat.web_communication.WebConstants;
import com.advengers.mabo.Chat.web_communication.WebResponse;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.NetworkUtil;
import com.advengers.mabo.Utils.Tools;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.advengers.mabo.Activity.MainActivity.LOGIN;
import static com.advengers.mabo.Activity.MainActivity.REGISTER;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;

public class SignupActivity extends MyActivity {
    public TextView forgetten, title, subtitle, phonenosignup, emailidsignup;
    public Button login, googlelogin, facebooklogin, signup;
    public ImageView profile;
    public Spinner citysignup;
    Dialog emailsignupdialog;
    String token;
    ImageView pwdview,cpwdview;

    EditText etusername, etpassword,etname,etcnfpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //googlesignup
        googlelogin = (Button) findViewById(R.id.btn_google);
        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ShowGmailpopup();
            }
        });
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("Package name:", getPackageName());
                Log.e("MY KEY HASH:", sign);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //facebooksignup
        facebooklogin = (Button) findViewById(R.id.btn_facebook);
        facebooklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ShowFacebookpopup();

            }
        });


        //forgetten function
         etname = (EditText) findViewById(R.id.et_name);
         etusername = (EditText) findViewById(R.id.et_username);
         etpassword = (EditText) findViewById(R.id.et_password);
         etcnfpassword = (EditText) findViewById(R.id.et_cnfpassword);
         pwdview = (ImageView) findViewById(R.id.pwdview);
         cpwdview = (ImageView) findViewById(R.id.cpwdview);
        pwdview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.e(etpassword.getInputType()+"");
                if(etpassword.getInputType()== 129)
                    etpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                else if(etpassword.getInputType()== InputType.TYPE_CLASS_TEXT)

                    etpassword.setInputType(129);
            }
        });
        cpwdview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.e(etcnfpassword.getInputType()+"");
                if(etcnfpassword.getInputType()== 129)
                    etcnfpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                else if(etcnfpassword.getInputType()== InputType.TYPE_CLASS_TEXT)

                    etcnfpassword.setInputType(129);
            }
        });
        //signup function
        signup = (Button) findViewById(R.id.btn_signup);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


  boolean cancel = false;
                    View focusView = null;
                if (TextUtils.isEmpty(etname.getText().toString())) {
                    etname.setError("Please Enter Your Name");
                    focusView = etname;
                    cancel = true;
                }else if (TextUtils.isEmpty(etusername.getText().toString())) {
                        etusername.setError("Please Enter Your Email-id");
                        focusView = etusername;
                        cancel = true;
                    } else if (TextUtils.isEmpty(etpassword.getText().toString())) {
                        etpassword.setError("Please Enter Password");
                        focusView = etpassword;
                        cancel = true;
                    }

                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    } else {
                   if(!etpassword.getText().toString().equals("") && !etcnfpassword.getText().toString().equals("")){
                       if(etcnfpassword.getText().toString().equals(etpassword.getText().toString())){
                           if(NetworkUtil.isInternetOn(SignupActivity.this)) {
                           {
                               registration(etname.getText().toString(), etusername.getText().toString(), etcnfpassword.getText().toString(),"");
                           }
                           }else
                           {
                               Tools.showDialog(true,getResources().getString(R.string.str_check_network),SignupActivity.this,SignupActivity.this);
                           }

                       }else {
                           Toast.makeText(SignupActivity.this,"Enter password & confirm password correctly",Toast.LENGTH_SHORT).show();
                       }
                   }else{
                       Toast.makeText(SignupActivity.this,"Enter password or confirm password",Toast.LENGTH_SHORT).show();
                   }



                  }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            LogUtils.e( "getInstanceId failed "+ task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                      /*  // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/
                    }
                });
    }
    public void onNewToken(String token) {
        LogUtils.e( "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
      //  sendRegistrationToServer(token);
    }
    private void registration(String username,String email, String password, String room_Id) {

        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + REGISTER,
                new ServerParams().getSignup(username,email,password,token,room_Id), register_lister, register_error_listener));
                onLoadProgress(SignupActivity.this);

    }

    Response.Listener register_lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            Log.e("register Response", "" + response);

            try {
                JSONObject register = new JSONObject(response.toString());

                if (register.has(STATUS_JSON)) {


                    if (register.getString(STATUS_JSON).equals("true")) {
                        Toast.makeText(SignupActivity.this,register.getString(MESSAGE),Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }else if(register.getString(STATUS_JSON).equals("false"))
                    {
                        Tools.showDialog(true,register.getString("message"),SignupActivity.this,SignupActivity.this);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    Response.ErrorListener register_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            onLoadDismiss();
            Log.e("register error", "" + error.getLocalizedMessage().toString());
        }
    };


}
