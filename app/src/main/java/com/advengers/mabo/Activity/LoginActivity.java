package com.advengers.mabo.Activity;

import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.app.Dialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.text.InputType;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.advengers.mabo.Chat.web_communication.WebCall;
        import com.advengers.mabo.Chat.web_communication.WebConstants;
        import com.advengers.mabo.Chat.web_communication.WebResponse;
import com.advengers.mabo.Cometchat.Activity.CometChatActivity;
import com.advengers.mabo.Cometchat.Contracts.StringContract;
import com.advengers.mabo.Model.User;
        import com.advengers.mabo.Tools.MyActivity;
        import com.advengers.mabo.Utils.LogUtils;
        import com.advengers.mabo.Utils.NetworkUtil;
        import com.advengers.mabo.Utils.Tools;
        import com.advengers.mabo.Utils.Utils;
        import com.android.volley.NetworkResponse;
        import com.android.volley.toolbox.HttpHeaderParser;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.facebook.FacebookSdk;
        import com.facebook.appevents.AppEventsLogger;

        import com.advengers.mabo.R;
        import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
        import com.advengers.mabo.ServerCall.ServerParams;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.facebook.AccessToken;
        import com.facebook.CallbackManager;
        import com.facebook.FacebookCallback;
        import com.facebook.FacebookException;
        import com.facebook.GraphRequest;
        import com.facebook.GraphResponse;
        import com.facebook.login.LoginManager;
        import com.facebook.login.LoginResult;
        import com.facebook.login.widget.LoginButton;
        import com.google.android.gms.auth.api.signin.GoogleSignIn;
        import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
        import com.google.android.gms.auth.api.signin.GoogleSignInClient;
        import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
        import com.google.android.gms.common.SignInButton;
        import com.google.android.gms.common.api.ApiException;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.FieldNamingPolicy;
        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.JsonObject;

        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONTokener;

        import java.io.UnsupportedEncodingException;
        import java.util.Arrays;
        import java.util.HashMap;

import static com.advengers.mabo.Activity.MainActivity.COMETCHATURL;
import static com.advengers.mabo.Activity.MainActivity.LOGIN;
        import static com.advengers.mabo.Activity.MainActivity.REGISTER;
        import static com.advengers.mabo.Activity.MainActivity.ROOMIDUPDATE;
        import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
        import static com.advengers.mabo.Activity.MainActivity.USERDETAILS;
import static com.advengers.mabo.Interfaces.Keys.COMET_ID;
import static com.advengers.mabo.Interfaces.Keys.CREATED;
        import static com.advengers.mabo.Interfaces.Keys.DATA;
        import static com.advengers.mabo.Interfaces.Keys.DEVICEID;
        import static com.advengers.mabo.Interfaces.Keys.DEVICEMODEL;
        import static com.advengers.mabo.Interfaces.Keys.EMAIL;
        import static com.advengers.mabo.Interfaces.Keys.FACEBOOKID;
        import static com.advengers.mabo.Interfaces.Keys.FCMKEY;
        import static com.advengers.mabo.Interfaces.Keys.FIRSTNAME;
        import static com.advengers.mabo.Interfaces.Keys.GOOGLEID;
        import static com.advengers.mabo.Interfaces.Keys.ID;
        import static com.advengers.mabo.Interfaces.Keys.IPADDRESS;
        import static com.advengers.mabo.Interfaces.Keys.LASTNAME;
        import static com.advengers.mabo.Interfaces.Keys.LATITUDE;
        import static com.advengers.mabo.Interfaces.Keys.LOCATION;
        import static com.advengers.mabo.Interfaces.Keys.LOGIN_TYPE;
        import static com.advengers.mabo.Interfaces.Keys.LONGITUDE;
        import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
        import static com.advengers.mabo.Interfaces.Keys.MODIFIED;
        import static com.advengers.mabo.Interfaces.Keys.PASSWORD;
        import static com.advengers.mabo.Interfaces.Keys.PHONE;
        import static com.advengers.mabo.Interfaces.Keys.RANGE;
        import static com.advengers.mabo.Interfaces.Keys.STATUS;
        import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
        import static com.advengers.mabo.Interfaces.Keys.USER;
        import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class LoginActivity extends MyActivity {
    public Button login, googlelogin, facebooklogin, signup;
    Dialog emailsignupdialog;
    LoginButton loginButton;
    CallbackManager callbackManager;
    Gson gson;
    User user;
    String token;
    EditText etusername, etpassword;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    ImageView pwdview;
    GoogleSignInOptions gso;
    int RC_SIGN_IN = 1022;
    String CODE = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(LoginActivity.this).getString(USER),User.class));

        if(User.getUser().isLogged())
        {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            //startActivity(new Intent(LoginActivity.this, CometChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        emailsignupdialog = new Dialog(this);


       googlelogin = (Button) findViewById(R.id.btn_google);
        pwdview = (ImageView) findViewById(R.id.pwdview);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("Login","I am coming inside");
                if(NetworkUtil.isInternetOn(LoginActivity.this)) {
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("SignOut", "Done");
                            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                            startActivityForResult(signInIntent, RC_SIGN_IN);


                        }
                    });
                }else
                {
                    Tools.showDialog(true,getResources().getString(R.string.str_check_network),LoginActivity.this,LoginActivity.this);
                }
            }
        });

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Login","Google I am coming inside");
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("SignOut","Done");
                        //On Succesfull signout we navigate the user back to LoginActivity
                    /*    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);*/
                    }
                });

            }
        });
        //facebooksignup
        facebooklogin = (Button) findViewById(R.id.btn_facebook);
        loginButton = findViewById(R.id.login_button);
        facebooklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ShowFacebookpopup();
                if(NetworkUtil.isInternetOn(LoginActivity.this)) {
                    LoginManager.getInstance().logOut();
                    loginButton.performClick();
                }else
                {
                    Tools.showDialog(true,getResources().getString(R.string.str_check_network),LoginActivity.this,LoginActivity.this);
                }
            }
        });



        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //forgetten function

        etusername = (EditText) findViewById(R.id.et_username);
        etpassword = (EditText) findViewById(R.id.et_password);

        //signup function
        signup = (Button) findViewById(R.id.btn_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                Intent intent = new Intent(LoginActivity.this,
                        SignupActivity.class);
                startActivity(intent);


            }
        });

        //login signup
        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(etusername.getText().toString())) {
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
                    Log.e("Login Response", "" + etusername.getText().toString()+etpassword.getText().toString());
                    if(NetworkUtil.isInternetOn(LoginActivity.this)) {
                        Login(etusername.getText().toString(),etpassword.getText().toString());
                    }else
                    {
                        Tools.showDialog(true,getResources().getString(R.string.str_check_network),LoginActivity.this,LoginActivity.this);
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
        //   UserDetails("32");
     //  CreateComet("mabo_test","test","");
    }
    public void onNewToken(String token) {
        LogUtils.e( "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //  sendRegistrationToServer(token);
    }
    private void Login(String username, String password) {
        CODE = "Login";
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + LOGIN,
                new ServerParams().getLogin(""+username,""+password), login_lister, login_error_listener));
        onLoadProgress(LoginActivity.this);

    }

    private void Facebooklogin(String username,String email, String fbid,String gender) {
        CODE = "Facebooklogin";
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + REGISTER,
                new ServerParams().getSignupfacebook(username,email,fbid,token,gender), login_lister,login_error_listener));
        onLoadProgress(LoginActivity.this);

    }

    private void UserDetails(String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("","");
        CODE = "UserDetails";
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + USERDETAILS,new ServerParams().UserDetails(id,id)
                , login_lister,user_error_listener));
    }

    private void CreateComet(String id,String name,String avatar) {
        CODE = "CreateComet";
       // url = url.replaceAll(" ", "%20");
        String UID = "mabo"+id;
        App.requestQueue.add(MyVolleyRequestManager.createCometStringRequest(Request.Method.POST,
                COMETCHATURL.replaceAll(" ", "%20"),new ServerParams().createCometUser(UID, name, avatar)
                , login_lister,user_error_listener));
    }

    private void Googlelogin(String username,String email, String googleid) {
        CODE = "Googlelogin";
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + REGISTER,
                new ServerParams().getSignupgoogle(username,email,googleid,token), login_lister,login_error_listener));
        onLoadProgress(LoginActivity.this);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
             Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e("Google Account", account.getDisplayName()+" "+account.getEmail()+" "+account.getId());
            Googlelogin(account.getDisplayName().toString(),account.getEmail().toString(),account.getId().toString());
        } catch (ApiException e) {
           Log.w("Google Account", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object, GraphResponse response) {
                        try {
                            String   fbname = json_object.get("name").toString();
                            String   fbid = json_object.get("id").toString();
                            Log.e("Facebook details",response.getRawResponse().toString()+"  "+fbname+"  "+fbid+"  "+json_object.get("email").toString());
                         //   String  fbgender = json_object.get("gender").toString();
                            JSONObject profile_pic_data = new JSONObject(json_object.get("picture").toString());
                            JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                            Facebooklogin(fbname,json_object.get("email").toString(),fbid,"");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields","id,name,email,gender,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    Response.Listener login_lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {


            Log.e("Login Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                    switch (CODE) {
                        case "Login":
                        setRange();
                        if (login.getString(STATUS_JSON).equals("true")) {
                            JSONObject data = login.getJSONObject("data");
                            String jsondata = login.getString(DATA);//gson.toJson(data);
                            Utils.getInstance(LoginActivity.this).setString(USER, jsondata);
                            User.setUser(gson.fromJson(jsondata, User.class));
                              CreateComet(User.getUser().getId(),User.getUser().getUsername().trim(),User.getUser().getprofile_imagename());

                        } else if (login.getString(STATUS_JSON).equals("false")) {
                            Tools.showDialog(true, login.getString("message"), LoginActivity.this, LoginActivity.this);
                        }
                        break;
                        case "Facebooklogin":
                            socialLogin(login);
                            break;
                        case "UserDetails":
                            if (login.has(STATUS_JSON)) {
                                if (login.getString(STATUS_JSON).equals("true")) {
                                    JSONObject data = login.getJSONObject("data");
                                    String jsondata = data.getString("users");//gson.toJson(data);
                                    LogUtils.e("JSON   "+jsondata);
                                    Utils.getInstance(LoginActivity.this).setString(USER,jsondata);
                                    User.setUser(gson.fromJson(Utils.getInstance(LoginActivity.this).getString(USER),User.class));
                                    setRange();
                                   /* if(User.getUser().getRoomid().isEmpty()||User.getUser().getRoomid().equals("0"))
                                    {
                                        //  new WebCall(LoginActivity.this, LoginActivity.this, null, WebConstants.getRoomId, WebConstants.getRoomIdCode, false, true).execute();
                                    }else{
                                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    }*/
                                    CreateComet(User.getUser().getId(),User.getUser().getUsername().trim(),User.getUser().getprofile_imagename());

                                }
                            }
                            break;
                        case "CreateComet":
                            LogUtils.e("Coming here");
                            String UID="";
                            if(login.has("data")) {
                                JSONObject data = login.getJSONObject("data");
                                LogUtils.e(data.toString());
                                 UID = data.getString(COMET_ID);
                                LogUtils.e(UID);
                            }else if(login.has("error"))
                            {
                                JSONObject data = login.getJSONObject("error");
                                 UID = data.getJSONObject("details").getString(COMET_ID);
                                LogUtils.e(UID);
                            }

                                UpdateRoomId("mabo"+User.getUser().getId(),User.getUser().getId());


                            break;
                        case "Googlelogin":
                            socialLogin(login);
                            break;
                        case "":
                            break;
                    }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    void socialLogin(JSONObject login)
    {
        setRange();
        try {
            if (login.getString(STATUS_JSON).equals("true")) {
                Object json = new JSONTokener(login.getString("data")).nextValue();
                if (json instanceof JSONObject)
                {
                    UserDetails(((JSONObject) json).getString(ID));
                }else {
                    UserDetails(login.getString("data"));
                }
            }else if(login.getString(STATUS_JSON).equals("false"))
            {
                if(login.getString(MESSAGE).equals("The given gmailid already exists.")||login.getString(MESSAGE).equals("The given facebookid already exists."))
                {
                    Object json = new JSONTokener(login.getString("data")).nextValue();
                    if (json instanceof JSONObject)
                    {
                        UserDetails(((JSONObject) json).getString(ID));
                    }else {
                        UserDetails(login.getString("data"));
                    }
                }else
                {
                    onLoadDismiss();
                    showInfo(login.getString(MESSAGE),LoginActivity.this,LoginActivity.this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Response.ErrorListener login_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            onLoadDismiss();

            LogUtils.e("login error "+  error.getLocalizedMessage());
        }
    };




    Response.Listener user_lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            onLoadDismiss();
            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());
                if (login.has(STATUS_JSON)) {
                    if (login.getString(STATUS_JSON).equals("true")) {
                        JSONObject data = login.getJSONObject("data");
                        String jsondata = gson.toJson(data);
                        LogUtils.e("JSON   "+jsondata);
                        Utils.getInstance(LoginActivity.this).setString(USER,jsondata);
                        User.setUser(gson.fromJson(Utils.getInstance(LoginActivity.this).getString(USER),User.class));
                        setRange();
                        /*if(User.getUser().getRoomid().isEmpty()||User.getUser().getRoomid().equals("0"))
                        {
                          //  new WebCall(LoginActivity.this, LoginActivity.this, null, WebConstants.getRoomId, WebConstants.getRoomIdCode, false, true).execute();
                        }else{
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }*/
                        CreateComet(User.getUser().getId(),User.getUser().getUsername().trim(),User.getUser().getprofile_imagename());
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    Response.ErrorListener user_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            NetworkResponse response = error.networkResponse;
            if (response != null ) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                      try {
                        JSONObject login = new JSONObject(res.toString());
                        LogUtils.e(res);
                        if (login.has(STATUS_JSON)) {

                            if (login.getString(STATUS_JSON).equals("true")) {
                                JSONObject data = login.getJSONObject("data");
                                String jsondata = login.getString("data");
                                LogUtils.e("JSON   "+login.getString("data"));
                                Utils.getInstance(LoginActivity.this).setString(USER,jsondata);
                                User.setUser(gson.fromJson(Utils.getInstance(LoginActivity.this).getString(USER),User.class));
                                setRange();
                               /* if(User.getUser().getRoomid().isEmpty()||User.getUser().getRoomid().equals("0"))
                                {
                                   // new WebCall(LoginActivity.this, LoginActivity.this, null, WebConstants.getRoomId, WebConstants.getRoomIdCode, false, true).execute();
                                }else{
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                }*/
                                CreateComet(User.getUser().getId(),User.getUser().getUsername().trim(),User.getUser().getprofile_imagename());
                            }
                        }else if(CODE.equals("CreateComet")) {
                            LogUtils.e("Coming here");
                            String UID="";
                            if (login.has("data")) {
                                JSONObject data = login.getJSONObject("data");
                                LogUtils.e(data.toString());
                                 UID = data.getString(COMET_ID);
                                LogUtils.e(UID);
                            } else if (login.has("error")) {
                                JSONObject data = login.getJSONObject("error");
                                 UID = data.getJSONObject("details").getString(COMET_ID);
                                LogUtils.e(UID);
                            }
                                UpdateRoomId("mabo"+User.getUser().getId(),User.getUser().getId());

                        }


                    } catch (JSONException e) {
                          onLoadDismiss();
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e1) {
                    onLoadDismiss();
                    e1.printStackTrace();
                }
            }
        }
    };
    void setRange()
    {
        Utils.getInstance(LoginActivity.this).setInt(RANGE,1000);
    }



    private void UpdateRoomId(String roomid, String userid) {

        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + ROOMIDUPDATE,
                new ServerParams().putRoomID(userid,roomid), lister, login_error_listener));
      //  onLoadProgress(LoginActivity.this);

    }
    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

           // onLoadDismiss();
            Log.e("Login Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {
                     if (login.getString(STATUS_JSON).equals("true")) {
                         String uid = "mabo"+User.getUser().getId();
                         CometChat.login(uid, StringContract.AppDetails.API_KEY, new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
                             @Override
                             public void onSuccess(com.cometchat.pro.models.User user) {
                                 onLoadDismiss();
                            //     Log.d(TAG, "onSuccess: "+user.getUid());
                                 FirebaseMessaging.getInstance().subscribeToTopic(StringContract.AppDetails.AppID_user_UID);
                                 startActivity(new Intent(LoginActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                             }

                             @Override
                             public void onError(CometChatException e) {
                                 onLoadDismiss();
                                 e.printStackTrace();
                                 Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                              //   Log.d(TAG, "onError: "+e.getMessage());
                             }

                         });
                    }else if(login.getString(STATUS_JSON).equals("false"))
                    {
                        onLoadDismiss();
                        Tools.showDialog(true,login.getString("message"),LoginActivity.this,LoginActivity.this);
                    }

                }



            } catch (JSONException e) {
                onLoadDismiss();
                e.printStackTrace();
            }
        }
    };

}
