package com.advengers.mabo.Sendotp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.LoginActivity;
import com.advengers.mabo.Activity.SplashActivity;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.msg91.sendotpandroid.library.internal.SendOTP;
import com.msg91.sendotpandroid.library.listners.VerificationListener;
import com.msg91.sendotpandroid.library.roots.RetryType;
import com.msg91.sendotpandroid.library.roots.SendOTPConfigBuilder;
import com.msg91.sendotpandroid.library.roots.SendOTPResponseCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.advengers.mabo.Activity.MainActivity.LOGIN;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.UPDATEPHONENUMBER;
import static com.advengers.mabo.Activity.MainActivity.UPDATEUSER;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;


public class VerificationActivity extends MyActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, VerificationListener {
    private static final String TAG = "VerificationActivity";
    private static final int OTP_LNGTH = 4;
    TextView resend_timer;
    int countryCode;
    String phoneNumber;
    private OtpEditText mOtpEditText;

    String URL = "https://api.msg91.com/api/v5/otp";
    String authkey = "335467A4I8Tzy96017ddabP1";
    String tempid = "6017d207aadf7653df3d3304";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        resend_timer = (TextView) findViewById(R.id.resend_timer);
        resend_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendCode();
            }
        });
        startTimer();
        mOtpEditText = findViewById(R.id.inputCode);
        mOtpEditText.setMaxLength(OTP_LNGTH);
        enableInputField(true);
        initiateVerification();
    }

    void createVerification(String phoneNumber, int countryCode) {
        new SendOTPConfigBuilder()
                .setCountryCode(countryCode)
                .setMobileNumber(phoneNumber)
                //////////////////direct verification while connect with mobile network/////////////////////////
                .setVerifyWithoutOtp(true)
                //////////////////Auto read otp from Sms And Verify///////////////////////////
                .setAutoVerification(VerificationActivity.this)
                .setOtpExpireInMinute(5)//default value is one day
                .setOtpHits(3) //number of otp request per number
                .setOtpHitsTimeOut(0L)//number of otp request time out reset in milliseconds default is 24 hours
                .setSenderId("MABOVERIFY")
                .setMessage("##OTP## is Your verification digits.")
                .setOtpLength(OTP_LNGTH)
                .setVerificationCallBack(this).build();

      //  SendOTP.getInstance().getTrigger().initiate();


    }

    private void sendotp(String mobile) {

        String finalurl = URL+"?authkey="+authkey+"&template_id="+tempid+"&mobile="+mobile;
        App.requestQueue.add(MyVolleyRequestManager.getmsgStringRequest(Request.Method.GET,
                finalurl,fcm_lister,fcm_error_listener));
      }

    Response.Listener fcm_lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {


            Log.e("FBLogin Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());
                if(login.getString("type").equals("success"))
                {
                    DataManager.getInstance().hideProgressMessage();
                    if(login.getString("message").equalsIgnoreCase("OTP verified success"))
                    {

                    enableInputField(false);
                    hideKeypad();
                    TextView textView2 = (TextView) findViewById(R.id.textView2);
                    TextView textView1 = (TextView) findViewById(R.id.textView1);
                    TextView messageText = (TextView) findViewById(R.id.textView);
                    ImageView topImg = (ImageView) findViewById(R.id.topImg);
                    TextView phoneText = (TextView) findViewById(R.id.numberText);
                    RelativeLayout topLayout = findViewById(R.id.topLayout);
                    if (android.os.Build.VERSION.SDK_INT > 16)
                        topLayout.setBackgroundDrawable(ContextCompat.getDrawable(VerificationActivity.this, R.drawable.gradient_bg_white));
                    else
                        topLayout.setBackgroundResource(R.drawable.gradient_bg_white);
                    messageText.setVisibility(View.GONE);
                    phoneText.setVisibility(View.GONE);
                    topImg.setVisibility(View.INVISIBLE);
                    textView1.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                        showCompleted(false);
                    hideProgressBarAndShowMessage(R.string.verified);

                    getUser();

                    user.setPhone("+"+countryCode+phoneNumber);
                    setUser();
                    String URL = SERVER_URL+UPDATEPHONENUMBER;
                    App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                            URL,new ServerParams().UpdatePhonenumber(user.getId(),user.getPhone())
                            , updatelister,update_error_listener));
                    }else if(login.getString("message").equalsIgnoreCase("retry send successfully"))
                    {

                    }
                }else if(login.getString("type").equals("error"))
                {
                    DataManager.getInstance().hideProgressMessage();
                    hideKeypad();
                    hideProgressBarAndShowMessage(R.string.failed);
                    Toast.makeText(VerificationActivity.this,login.getString("message"),Toast.LENGTH_LONG).show();
                    enableInputField(true);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Response.ErrorListener fcm_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

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
    void initiateVerification() {
        Intent intent = getIntent();
        if (intent != null) {
            DataManager.getInstance().showProgressMessage(this, "");
            phoneNumber = intent.getStringExtra(SendotpActivity.INTENT_PHONENUMBER);
            countryCode = intent.getIntExtra(SendotpActivity.INTENT_COUNTRY_CODE, 0);
            TextView phoneText = (TextView) findViewById(R.id.numberText);
            phoneText.setText(phoneNumber);
            sendotp(countryCode+phoneNumber);
         //   createVerification(phoneNumber, countryCode);
        }
    }

    public void ResendCode() {
        startTimer();
        String verifyurl = URL+"/retry?mobile="+countryCode+phoneNumber+"&authkey="+authkey;
        App.requestQueue.add(MyVolleyRequestManager.getmsgStringRequest(Request.Method.POST,
                verifyurl,fcm_lister,fcm_error_listener));
    //    SendOTP.getInstance().getTrigger().resend(RetryType.VOICE);

    }

    public void onSubmitClicked(View view) {
        String code = mOtpEditText.getText().toString();
        if (!code.isEmpty()) {
            hideKeypad();
            verifyOtp(code);
            DataManager.getInstance().showProgressMessage(this, "");
            TextView messageText = (TextView) findViewById(R.id.textView);
            messageText.setText("Verification in progress");
            enableInputField(false);

        }

    }


    void enableInputField(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View container = findViewById(R.id.inputContainer);
                if (enable) {
                    container.setVisibility(View.VISIBLE);
                    mOtpEditText.requestFocus();
                } else {
                    container.setVisibility(View.GONE);
                }
                TextView resend_timer = (TextView) findViewById(R.id.resend_timer);
                resend_timer.setClickable(false);
            }
        });

    }

    void hideProgressBarAndShowMessage(int message) {
        hideProgressBar();
        TextView messageText = (TextView) findViewById(R.id.textView);
        messageText.setText(message);
    }

    void hideProgressBar() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.INVISIBLE);
        TextView progressText = (TextView) findViewById(R.id.progressText);
        progressText.setVisibility(View.INVISIBLE);
    }

    void showProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.VISIBLE);
    }

    void showCompleted(boolean isDirect) {
        ImageView checkMark = (ImageView) findViewById(R.id.checkmarkImage);
        if (isDirect) {
            checkMark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_magic));
        } else {
            checkMark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark));
        }
        checkMark.setVisibility(View.VISIBLE);
    }

    public void verifyOtp(String otp) {

        String verifyurl = URL+"/verify?mobile="+countryCode+phoneNumber+"&authkey="+authkey+"&otp="+otp;
        App.requestQueue.add(MyVolleyRequestManager.getmsgStringRequest(Request.Method.POST,
                verifyurl,fcm_lister,fcm_error_listener));
      //  SendOTP.getInstance().getTrigger().verify(otp);
    }


    @Override
    public void onSendOtpResponse(final SendOTPResponseCode responseCode, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "onSendOtpResponse: " + responseCode.getCode() + "=======" + message);
                if (responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER || responseCode == SendOTPResponseCode.OTP_VERIFIED) {
                    DataManager.getInstance().hideProgressMessage();
                    enableInputField(false);
                    hideKeypad();
                    TextView textView2 = (TextView) findViewById(R.id.textView2);
                    TextView textView1 = (TextView) findViewById(R.id.textView1);
                    TextView messageText = (TextView) findViewById(R.id.textView);
                    ImageView topImg = (ImageView) findViewById(R.id.topImg);
                    TextView phoneText = (TextView) findViewById(R.id.numberText);
                    RelativeLayout topLayout = findViewById(R.id.topLayout);
                    if (android.os.Build.VERSION.SDK_INT > 16)
                        topLayout.setBackgroundDrawable(ContextCompat.getDrawable(VerificationActivity.this, R.drawable.gradient_bg_white));
                    else
                        topLayout.setBackgroundResource(R.drawable.gradient_bg_white);
                    messageText.setVisibility(View.GONE);
                    phoneText.setVisibility(View.GONE);
                    topImg.setVisibility(View.INVISIBLE);
                    textView1.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    if (responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER)
                        textView2.setText("Mobile verified using Invisible OTP.");
                    else textView2.setText("Your Mobile number has been successfully verified.");

                    hideProgressBarAndShowMessage(R.string.verified);
                    showCompleted(responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER);
                    getUser();

                    user.setPhone("+"+countryCode+phoneNumber);
                    setUser();
                    String URL = SERVER_URL+UPDATEPHONENUMBER;
                    App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                            URL,new ServerParams().UpdatePhonenumber(user.getId(),user.getPhone())
                            , updatelister,update_error_listener));

                } else if (responseCode == SendOTPResponseCode.READ_OTP_SUCCESS) {
                    DataManager.getInstance().hideProgressMessage();
                    mOtpEditText.setText(message);
                    LogUtils.e(message);
                   /* getUser();
                    user.setVerify(true);
                    setUser();
                    startActivity(new Intent(VerificationActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));*/
                } else if (responseCode == SendOTPResponseCode.SMS_SUCCESSFUL_SEND_TO_NUMBER || responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_FAILED_SMS_SUCCESSFUL_SEND_TO_NUMBER) {
                    DataManager.getInstance().hideProgressMessage();
                } else if (responseCode == SendOTPResponseCode.NO_INTERNET_CONNECTED) {
                    DataManager.getInstance().hideProgressMessage();
                } else {
                    DataManager.getInstance().hideProgressMessage();
                    hideKeypad();
                    hideProgressBarAndShowMessage(R.string.failed);
                    enableInputField(true);
                }
            }
        });
    }
    Response.Listener updatelister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
          //  onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {
                    if (login.getString(STATUS_JSON).equals("true")) {
                        user.setVerify(true);
                        setUser();
                        startActivity(new Intent(VerificationActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }else{
                        //setUser();
                        showWarning(login.getString(MESSAGE),VerificationActivity.this,VerificationActivity.this);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    Response.ErrorListener update_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        //    onLoadDismiss();
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
    private void startTimer() {
        resend_timer.setClickable(false);
        resend_timer.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.white));
        new CountDownTimer(30000, 1000) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) ms / 1000.0f);
                    resend_timer.setText("Resend via call ( " + secondsLeft + " )");
                }
            }

            public void onFinish() {
                resend_timer.setClickable(true);
                resend_timer.setText("Resend via call");
                resend_timer.setTextColor(ContextCompat.getColor(VerificationActivity.this, R.color.white));
            }
        }.start();
    }

    private void hideKeypad() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // SendOTP.getInstance().getTrigger().stop();
    }
}
