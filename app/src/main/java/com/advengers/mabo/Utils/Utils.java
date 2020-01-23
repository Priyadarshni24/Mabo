package com.advengers.mabo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.cloudinary.Cloudinary;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    public static final String PREF_NAME = "Mabo";
    Context context;
    static Utils utils;
    public static final String PREF_PROFILE_FILL = "fill_profile";
    public static final String SET_USER = "user";
    public static final int GPS_REQUEST = 1010;
    public static final String PREF_ZOOM_LEVEL = "zoomlevel";
    public static final String PROFILEPIC_DELETE_TOKEN = "delete_token";
    public static String  cloud_name="mabo-app";//"divjnpbqd";
    public static  String  api_key="936184482786724";//"416525585885277";
    public static String api_secret="AKZD8ZHAmI1Bic76JWru-wwMJ_4";//"NXpi5_IxBhVK0oEuJcVZrhFBG3A";
    public static DecimalFormat df2 = new DecimalFormat("#.##");
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    public static final String VIDEO_TOTAL_DURATION = "VIDEO_TOTAL_DURATION";
    public static final String VIDEOURL = "videofilelink";
    public Utils(Context context)
    {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        edit = pref.edit();
    }
    public static Cloudinary cloud(Context context)
    {
        final Map config = new HashMap();
        config.put("cloud_name", cloud_name);
        config.put("api_key", api_key);
        config.put("api_secret", api_secret);
        Cloudinary cloudinary  = new Cloudinary(config);
        return  cloudinary;
    }
    public void clearPref() {

        edit = pref.edit();
        edit.clear();
        edit.commit();
    }
    public synchronized static Utils getInstance(Context context)
    {
        if(utils == null)
        {
            utils = new Utils(context);

        }
        return utils;
    }
    public void setBoolean(String key,boolean value)
    {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public boolean getBoolean(String key)
    {
        return pref.getBoolean(key, false);
    }

    public void setString(String key,String value)
    {
        edit.putString(key, value);
        edit.commit();
    }

    public void removeString(String key)
    {
        edit.remove(key);edit.commit();
    }

    public String getString(String key)
    {
        return pref.getString(key,"");
    }

    public void setInt(String key,int value)
    {
        edit.putInt(key, value);
        edit.commit();
    }

    public int getInt(String key)
    {
        return pref.getInt(key, 0);
    }

    public void setDouble(String key,double value)
    {
        edit.putLong(key,  Double.doubleToRawLongBits(value));
        edit.commit();
    }

    public double getDouble(String key)
    {
        return Double.longBitsToDouble(pref.getLong(key, 0));
    }

    public void setFloat(String key,float value)
    {
        edit.putFloat(key, value);
        edit.commit();
    }

    public float getFloat(String key)
    {
        return pref.getFloat(key, 0);
    }

    public void setLong(String key,long value)
    {
        edit.putLong(key, value);
        edit.commit();
    }

    public long getLong(String key)
    {
        return pref.getLong(key,0);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static  void showToast(String message, Context context)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

}
