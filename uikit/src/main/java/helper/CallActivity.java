package helper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.Logger;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;


import constant.StringContract;

import static utils.Utils.isMyServiceRunning;

public class CallActivity extends AppCompatActivity implements CallActivityContract.CallActivityView {

    private RelativeLayout callView;
    private String sessionId;
    private static final String TAG = "CallActivity";
    private boolean isFirstLaunch;
    private CallActivityPresenter callActivityPresenter;
    boolean backpressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        callActivityPresenter = new CallActivityPresenter();
        callActivityPresenter.attach(this);

        callView = findViewById(R.id.call_view);

         sessionId = CometChat.getActiveCall().getSessionId();

        startCall(sessionId);
    }

    private void startCall(String sessionId) {
        Logger.error(TAG, " sessionId " + sessionId);
        //Logger.error(TAG, " sessionId " + sessionId);
        Intent serviceIntent = new Intent(this, CallNotificationService.class);
        if(isMyServiceRunning(CallNotificationService.class,CallActivity.this))
        stopService(serviceIntent);
        CometChat.startCall(CallActivity.this, sessionId, callView, new CometChat.OngoingCallListener() {
            @Override
            public void onUserJoined(User user) {

                Logger.error(TAG, " Name " + user.getName());
            }

            @Override
            public void onUserLeft(User user) {
                Logger.error( "onUserLeft: "+user.getName());
            }

            @Override
            public void onError(CometChatException e) {
                Logger.error( "onError: "+e.getMessage());
            }

            @Override
            public void onCallEnded(Call call) {
                Log.e("onCallEnded: ",call.toString());
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        callActivityPresenter.removeCallListener(TAG);
    }

    @Override
    public void onBackPressed() {

        backpressed = true;
       super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try{
        if(CometChat.getActiveCall()!=null)
        {
            if(CometChat.getActiveCall().getAction().equalsIgnoreCase("ongoing"))
            {
                Intent serviceIntent = new Intent(this, CallNotificationService.class);
                serviceIntent.putExtra(StringContract.IntentStrings.SESSION_ID, sessionId);
                ContextCompat.startForegroundService(this, serviceIntent);
            }else{
                finish();
            }
        }else{
            finish();
        }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }


}
