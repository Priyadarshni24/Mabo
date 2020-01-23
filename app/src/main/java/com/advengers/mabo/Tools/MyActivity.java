package com.advengers.mabo.Tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import com.advengers.mabo.Activity.LoginActivity;
import com.advengers.mabo.Interfaces.Keys;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.google.gson.Gson;

import java.util.Calendar;


import static com.advengers.mabo.Interfaces.Keys.USER;


/**
 * Created by nietzsche on 10/02/15.
 */
public class MyActivity extends AppCompatActivity {

    private static final String LOG_TAG = MyActivity.class.getSimpleName();
    private boolean mIsResumed = false;
   public static Gson gson;
    public static User user;
  //  ProgressDialog progressDialog;
    public void log(String string) {
        if (!Keys.IS_DEBUG) {
            return;
        }
        Log.e(getLogTag(), String.format("Instance:%s - %s", this, string));
    }
    private TransparentProgressDialog progressDialog;
   /* @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    public void getUser()
    {
        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(MyActivity.this).getString(USER),User.class));
        user = User.getUser();
        LogUtils.e("GET USER " +gson.toJson(user));
    }

    public void setUser()
    {
        String jsondata = gson.toJson(user);
        Utils.getInstance(MyActivity.this).setString(USER,jsondata);
    }

   /* public void onLoadProgress(Context context)
    {
        progressDialog = new ProgressDialog(context);
        // Set progressdialog message
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setIndeterminate(true);

        //progressDialog.getWindow().setGravity(Gravity.CENTER);
        //  progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));
   //     progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));
        // Show progressdialog
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      //  progressDialog.getWindow().setGravity(Gravity.CENTER);
    }*/

    public void onLoadProgress(Context context)
    {
        progressDialog = new TransparentProgressDialog(this, R.drawable.ic_loading);
        if(!progressDialog.isShowing())
        progressDialog.show();
    }



    public void onLoadDismiss()
    {
        if(progressDialog!=null)
        if(progressDialog.isShowing())
        progressDialog.dismiss();
    }

    @Override
    protected void onPause() {
        mIsResumed = false;
        super.onPause();
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mIsResumed = true;
    }

    public boolean ismIsResumed() {
        return mIsResumed;
    }

    public String getLogTag() {

        Calendar cal = Calendar.getInstance();

        return String.format("MYLOG: [%d:%d:%d] %s", cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND), getClass().getCanonicalName());
    }

    public void ResetZoom()
    {
        Utils.getInstance(getBaseContext()).setFloat(Utils.PREF_ZOOM_LEVEL,10f);
    }
    public void showInfo(String msg, Context ctx, Activity activity) {
        Tools.showDialog(false, msg,ctx,activity);
    }
    public void showWarning(String msg, Context ctx, Activity activity) {
        Tools.showDialog(true, msg,ctx,activity);
    }

}
