package com.advengers.mabo.Utils;

import android.util.Log;

public class LogUtils {
    public static boolean v_debug = true;
    static String TAG = "MABO";
    public static void i(String mesg)
    {
        if(v_debug)
        Log.i(TAG,mesg);
    }
    public static void e(String mesg)
    {
        if(v_debug)
            Log.e(TAG,mesg);
    }
}
