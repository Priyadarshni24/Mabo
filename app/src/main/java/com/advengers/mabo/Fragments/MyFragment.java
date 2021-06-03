package com.advengers.mabo.Fragments;

/**
 * Created by nietzsche on 16/02/15.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.advengers.mabo.Activity.LoginActivity;
import com.advengers.mabo.Adapter.LikeAdapter;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Tools.TransparentProgressDialog;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static com.advengers.mabo.Interfaces.Keys.USER;


/**
 * Created by nietzsche on 31/12/14.
 */
public abstract class MyFragment<T extends AppCompatActivity> extends Fragment {

    private static final String LOG_TAG = MyFragment.class.getSimpleName();
    protected final Object mAttachingActivityLock = new Object();
    protected boolean mSyncVariable = false;
    private T mLastAttachedActivity = null;
    TransparentProgressDialog progressDialog;
    public static ArrayList<Map<String, Object>> lastSearchedUsers = new ArrayList<>();
    Dialog dialog;
    public float zoomlevel = 10f;
    abstract public String getTagManager();
    Gson gson;
    User user;
    ProfileClick callback;
    GoogleApiClient mGoogleApiClient;
    public String getLogTag() {

        Calendar cal = Calendar.getInstance();

        return String.format("MYLOG: [%d:%d:%d] %s", cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND), getClass().getCanonicalName());
    }

    public void setLastSearchedUsers(ArrayList<Map<String, Object>> users)
    {
        this.lastSearchedUsers =users;
    }

    public ArrayList<Map<String, Object>> getLastSearchedUsers()
    {
        return lastSearchedUsers;
    }

  public void onLoadProgress(Context context)
  {
      LogUtils.e("I am coming here");
      progressDialog = new TransparentProgressDialog(context, R.drawable.ic_loading);
   //   if(!progressDialog.isShowing())
      progressDialog.show();
  }
    public void onLoadProgresswithtitle(Context context)
    {
        LogUtils.e("I am coming");
        progressDialog = new TransparentProgressDialog(context, R.drawable.ic_loading);
        progressDialog.setTitle(R.string.str_pleasewait);
        progressDialog.show();
    }
    public void ResetZoom()
    {
        Utils.getInstance(getContext()).setFloat(Utils.PREF_ZOOM_LEVEL,13f);
    }

    public void onLoadDismiss()
    {
        if(progressDialog!=null)
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
    public void showInfo(String msg) {
        Tools.showDialog(false, msg, getActivity(),getActivity());
    }
    public void showWarning(String msg) {
        Tools.showDialog(true, msg, getMyActivty(),getMyActivty());
    }

    public void log(String string) {
        Log.d(getLogTag(), String.format("Instance:%s - %s", this, string));
    }

    public Context getApplicationContext() {
        return getMyActivty().getApplicationContext();
    }

    public T getMyActivty() {
        T result = (T) MyFragment.this.getActivity();
        if (result == null) {
            result = mLastAttachedActivity;
        }
        return result;

    }

  /*  public LayoutInflater getLayoutInflater() {
        return getMyActivty().getLayoutInflater();

    }*/

    @Override
    public void onStart() {
        log("onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        log("onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        log("onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        log((String.format("onSaveInstanceState outState:%s", outState)));
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        log((String.format("getLayoutInflater savedInstanceState:%s", savedInstanceState)));
        return super.getLayoutInflater(savedInstanceState);
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        log((String.format("onInflate savedInstanceState:%s", savedInstanceState)));
        super.onInflate(activity, attrs, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        log((String.format("activityCreated savedInstanceState:%s", savedInstanceState)));
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        log((String.format("onViewStateRestored savedInstanceState:%s", savedInstanceState)));
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        log("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        log("onDetach");
        mLastAttachedActivity = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log((String.format("onCreateView savedInstanceState:%s", savedInstanceState)));
        View view = super.onCreateView(inflater, container, savedInstanceState);
        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(getApplicationContext()).getString(USER),User.class));

        if (view != null) {

            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                }
            });

            view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {

                }
            });
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log(String.format("onCreate savedInstance=%s", savedInstanceState));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        log("onAttach " + activity);

        super.onAttach(activity);
        try {
            callback = (ProfileClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
        synchronized (mAttachingActivityLock) {
            mSyncVariable = true;
            mAttachingActivityLock.notifyAll();
        }
        mLastAttachedActivity = (T) activity;
    }
    public void getUser()
    {
        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(getApplicationContext()).getString(USER),User.class));
        user = User.getUser();
        LogUtils.e(gson.toJson(user));

    }
    public User getUserdetail()
    {
        getUser();
        return user;
    }
    public void setUser()
    {
        String jsondata = gson.toJson(user);
        Utils.getInstance(getApplicationContext()).setString(USER,jsondata);
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        log("onCreateAnimation");
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);
        if (anim != null) {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    log("onAnimationStart");

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    log("onAnimationEnd");

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    log("onAnimationRepeat");
                }
            });
        }
        return anim;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        log("onViewCreated");
        if (view != null) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                }
            });

            view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {

                }
            });
        }
        super.onViewCreated(view, savedInstanceState);

    }
    public void CallBackListener(ProfileClick mCallback) {
        this.callback = mCallback;
    }
    public interface ProfileClick
    {
        public void onProfile(String userid);
    }

}

