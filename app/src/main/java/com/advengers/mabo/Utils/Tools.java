package com.advengers.mabo.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.LoginActivity;
import com.advengers.mabo.Activity.SearchActivity;
import com.advengers.mabo.Model.Post;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.databinding.ActivityProfileBinding;
import com.andexert.library.RippleView;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.protocol.ResponseContent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import static com.advengers.mabo.Activity.MainActivity.REQUESTUSER;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.USERDETAILS;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USER;


/**
 * Created by nietzsche on 13/02/15.
 */
public class Tools {

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
    public static final SimpleDateFormat dateFormatterComplete =
            new SimpleDateFormat("dd MMM yyyy hh:mm a");
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final int NOTIFICATION_ID = 1;
    private static final int RIPPLE_DURATION = 150;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static Context context;
    public static Activity activity;
    public static ActivityProfileBinding binding;
    public static String myid;
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getThumbnailFromVideo(String videoPath, Context context) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        return thumb;
    }


    public static Bitmap scaleBitmap(Bitmap bitmap) {
        //Scale Bitmap
       int PICTURE_SIZE = 640;
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(0, 0, PICTURE_SIZE, PICTURE_SIZE),
                Matrix.ScaleToFit.CENTER);
        Bitmap scaledBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                        true);

        return scaledBitmap;
    }
    public static boolean isMyServiceRunning(Class<?> serviceClass,Activity act) {
        ActivityManager manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static String getAddress(Context context,double latitude,double longtitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {

                addresses = geocoder.getFromLocation(latitude, longtitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getSubLocality();//.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                LogUtils.e(addresses.toString());
                //address + "," +
                return (city);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getfullAddress(Context context,double latitude,double longtitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(latitude, longtitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            LogUtils.e(addresses.toString());
            //address + "," +
            return (address);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String writeFileToInternalStorage(Context context, Bitmap outputImage) {
        String fileName = "zoom_image";

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        outputImage.compress(Bitmap.CompressFormat.JPEG, 70, fos);

        try {
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }



    public static Double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else {

            Double result = 0.0;
            try {
                result = Double.valueOf(value.toString());
            } catch (Exception e) {
                result = 0.0;
            }
            return result;
        }
    }



    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void hideSoftInput(Context context, View view) {

        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


/*
    public static void showDialog(Boolean isWarning, String stringRes, Context context, Activity activity) {
        int title = R.string.app_name;
        if (isWarning) {
            title = R.string.warning;
        }
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_warning, null);
        dialogBuilder.setView(dialogView);

        TextView txt_title = (TextView) dialogView.findViewById(R.id.txt_title);
        Button btn_ok = (Button)dialogView.findViewById(R.id.btn_ok);
        btn_ok.setText(context.getString(R.string.ok));
        TextView txt_content = (TextView) dialogView.findViewById(R.id.txt_content);
        txt_title.setText(activity.getString(title));
        txt_content.setText(stringRes);
        final AlertDialog b = dialogBuilder.create();
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               b.dismiss();
            }
        });
        if(!b.isShowing())
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            b.show();
        }
        
    }*/

    public static void showDialog(Boolean isWarning, String stringRes, Context context, Activity activity) {
        int title = R.string.app_name;
        if (isWarning) {
            title = R.string.warning;
        }
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_warning, null);
        dialogBuilder.setView(dialogView);

        TextView txt_title = (TextView) dialogView.findViewById(R.id.txt_title);
        Button btn_ok = (Button)dialogView.findViewById(R.id.btn_ok);
        btn_ok.setText(context.getString(R.string.ok));
        TextView txt_content = (TextView) dialogView.findViewById(R.id.txt_content);
        txt_title.setText(activity.getString(title));
        txt_content.setText(stringRes);
        final AlertDialog b = dialogBuilder.create();
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWarning)
                    b.dismiss();
                else
                {
                    activity.finish();
                }
            }
        });
        if(!b.isShowing())
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            b.show();
        }
    }
  public static User findUser(String id,ArrayList<User> listCarnet) {
        for(User carnet : listCarnet) {
            if(carnet.getId().equals(id)) {
                return carnet;
            }
        }
        return null;
    }
    public static Post findPost(String id,ArrayList<Post> listCarnet) {
        for(Post carnet : listCarnet) {
            if(carnet.getId().equals(id)) {
                return carnet;
            }
        }
        return null;
    }
    public static String hashPassword(String salt, String plainPassword) {

        String hash = "";
        try {
            String salted = null;
            if (salt == null || "".equals(salt)) {
                salted = plainPassword;
            } else {
                salted = plainPassword + "{" + salt + "}";
            }
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte sha[] = md.digest(salted.getBytes());
            for (int i = 1; i < 5000; i++) {
                byte c[] = new byte[sha.length + salted.getBytes().length];
                System.arraycopy(sha, 0, c, 0, sha.length);
                System.arraycopy(salted.getBytes(), 0, c, sha.length, salted.getBytes().length);
                sha = md.digest(c);
            }
            hash = new String(Base64.encode(sha, Base64.NO_WRAP));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //do something with this exception
        }
        return hash;
    }





    public static boolean notBlank(String string) {
        Log.d("MYLOG", "NOTBLANK: " + string);
        return string != null && !string.isEmpty() && !string.contentEquals("null") &&
                !string.contentEquals("none") && string.lastIndexOf("/") != (string.length() - 1);
    }

    public static String escapeFirebaseKey(String key) {
        String result = null;
        try {
            result = URLEncoder.encode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result.replaceAll("\\.", "%2E");
    }


    // convert video file to base64
    public static String convertFileToBase64(String videoPath) {

        File videoFile = new File(videoPath);
        try {
            InputStream inputStream = new FileInputStream(videoFile);//Get input stream from file
            byte[] bytes;
            byte[] buffer = new byte[3000];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void showUserProfile(int animationSource,String userid,String friendid, Context ctx, Activity act) {
        context = ctx;
        activity = act;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        //    LayoutInflater inflater = activity.getLayoutInflater();
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.activity_profile, null, false);
        dialogBuilder.setView(binding.getRoot());
        binding.pbLoading.setVisibility(View.VISIBLE);
        UserDetails(userid,friendid);
        final AlertDialog b = dialogBuilder.create();
        b.getWindow().getAttributes().windowAnimations = animationSource;
        if(!b.isShowing())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            b.show();
        }
    }
    public static void UserDetails(String userid,String friendid) {
        myid = userid;
       HashMap<String, String> map = new HashMap<>();
        map.put("","");

        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                SERVER_URL + USERDETAILS,new ServerParams().UserDetails(userid,friendid)
                , user_lister,user_error_listener));


    }
    static Response.Listener user_lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

           LogUtils.e(response.toString());

            //  JSONObject obj = new JSONObject(res);
            try {
                JSONObject login = new JSONObject(response.toString());
                if (login.has(STATUS_JSON)) {
                    if (login.getString(STATUS_JSON).equals("true")) {
                        JSONObject data = login.getJSONObject("data");
                        LogUtils.e("JSON   "+login.getString("data"));
                        String jsondata = data.getString("users");
                        Gson g = new Gson();
                        final User profile = g.fromJson(jsondata,User.class);
                        binding.txtName.setText(profile.getUsername());
                        try {
                            if (!profile.getprofile_imagename().isEmpty())
                                Picasso.get().load(profile.getprofile_imagename()).placeholder(R.drawable.ic_avatar).into(binding.imgProfile);
                        }catch (Exception e)
                        {

                        }
                        LogUtils.e("profile_display_status "+profile.getProfile_display_status());
                        if(!profile.getProfile_display_status().equals("1")) {
                            binding.txtLocation.setVisibility(View.VISIBLE);
                            binding.txtEmail.setVisibility(View.VISIBLE);
                            binding.txtEmail.setText(profile.getEmail());
                            if (profile.getLatitude() != null && profile.getLongitude() != null) {
                                binding.txtLocation.setText(Tools.getAddress(context, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                            }
                            android.view.ViewGroup.LayoutParams layoutParams = binding.imgStatus.getLayoutParams();
                            layoutParams.width =150;
                            layoutParams.height =150;
                            binding.imgStatus.setLayoutParams(layoutParams);
                            if(myid.equals(profile.getId()))
                                binding.imgStatus.setVisibility(View.GONE);
                            binding.imgStatus.setImageResource(R.drawable.ic_makechat);
                            /*  binding.imgStatus.setLayoutParams(new RelativeLayout.LayoutParams(50,50)); */
                            binding.imgStatus.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            binding.imgStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String room_Id = profile.getRoomid();
                                    String name = profile.getUsername();
                                    String id = profile.getId();
                                    if(room_Id.isEmpty()||room_Id==null||room_Id.length()<2||!room_Id.contains("mabo"))
                                        showDialog(true,"This user need to login again to use chat",context,activity);
                                    else
                                    {
                                        Intent intent = new Intent(activity, CometChatMessageListActivity.class);
                                        intent.putExtra(UIKitConstants.IntentStrings.UID, room_Id);
                                        intent.putExtra(UIKitConstants.IntentStrings.MYUID, "mabo"+myid);
                                        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, profile.getprofile_imagename());
                                        intent.putExtra(UIKitConstants.IntentStrings.STATUS, profile.getStatus());
                                        intent.putExtra(UIKitConstants.IntentStrings.NAME, name);
                                        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
                                        activity.startActivity(intent);
                                        //  LogUtils.e("AVATAR "+user.getprofile_imagename());
                                     /*   Intent intent = new Intent(activity, OneToOneChatActivity.class);
                                        intent.putExtra(StringContract.IntentStrings.USER_ID, room_Id);
                                        intent.putExtra(StringContract.IntentStrings.USER_AVATAR, profile.getprofile_imagename());
                                        intent.putExtra(StringContract.IntentStrings.USER_NAME, name);
                                        activity.startActivity(intent);*/
                                    }
                                }
                            });
                        }else if(profile.getProfile_display_status().equals("1")){
                            if(myid.equals(profile.getId()))
                            {
                                binding.imgStatus.setVisibility(View.GONE);
                            }else
                            {
                                if(profile.getRequest_status().equals("0"))
                                   binding.imgStatus.setImageResource(R.drawable.ic_requested);
                                else if(profile.getRequest_status().equals("1"))
                                {
                                    binding.txtLocation.setVisibility(View.VISIBLE);
                                    binding.txtEmail.setVisibility(View.VISIBLE);
                                    binding.txtEmail.setText(profile.getEmail());
                                    if (profile.getLatitude() != null && profile.getLongitude() != null) {
                                        binding.txtLocation.setText(Tools.getAddress(context, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                                    }
                                    android.view.ViewGroup.LayoutParams layoutParams = binding.imgStatus.getLayoutParams();
                                    layoutParams.width =150;
                                    layoutParams.height =150;
                                    binding.imgStatus.setLayoutParams(layoutParams);
                                    binding.imgStatus.setImageResource(R.drawable.ic_makechat);
                                    /*  binding.imgStatus.setLayoutParams(new RelativeLayout.LayoutParams(50,50)); */
                                    binding.imgStatus.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                }
                                else
                                    binding.imgStatus.setImageResource(R.drawable.ic_request);
                            }
                            binding.imgStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Request(myid,profile.getId());
                                }
                            });
                        }
                        binding.pbLoading.setVisibility(View.GONE);

                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

  static Response.ErrorListener user_error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;
            if (response != null && response.statusCode == 404) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                //    LogUtils.e(res);
                    //  JSONObject obj = new JSONObject(res);
                    try {
                        JSONObject login = new JSONObject(res.toString());
                        if (login.has(STATUS_JSON)) {
                            if (login.getString(STATUS_JSON).equals("true")) {
                                JSONObject data = login.getJSONObject("data");
                                LogUtils.e("JSON   "+login.getString("data"));
                                String jsondata = login.getString("data");
                                Gson g = new Gson();
                                final User profile = g.fromJson(jsondata,User.class);
                                binding.txtName.setText(profile.getUsername());
                                try {
                                    if (!profile.getprofile_imagename().isEmpty())
                                        Picasso.get().load(profile.getprofile_imagename()).placeholder(R.drawable.ic_avatar).into(binding.imgProfile);
                                }catch (Exception e)
                                {

                                }
                                if(!profile.getProfile_display_status().equals("1")) {
                                    binding.txtLocation.setVisibility(View.VISIBLE);
                                    binding.txtEmail.setVisibility(View.VISIBLE);
                                    binding.txtEmail.setText(profile.getEmail());
                                    if (profile.getLatitude() != null && profile.getLongitude() != null) {
                                        binding.txtLocation.setText(Tools.getAddress(context, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                                    }
                                    android.view.ViewGroup.LayoutParams layoutParams = binding.imgStatus.getLayoutParams();
                                    layoutParams.width =150;
                                    layoutParams.height =150;
                                    binding.imgStatus.setLayoutParams(layoutParams);
                                    binding.imgStatus.setImageResource(R.drawable.ic_makechat);
                                   /*  binding.imgStatus.setLayoutParams(new RelativeLayout.LayoutParams(50,50)); */
                                    binding.imgStatus.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                }else if(profile.getProfile_display_status().equals("1")){

                                    if(profile.getRequest_status().equals("0"))
                                        binding.imgStatus.setImageResource(R.drawable.ic_requested);
                                    else if(profile.getRequest_status().equals("1"))
                                    {
                                        binding.txtLocation.setVisibility(View.VISIBLE);
                                        binding.txtEmail.setVisibility(View.VISIBLE);
                                        binding.txtEmail.setText(profile.getEmail());
                                        if (profile.getLatitude() != null && profile.getLongitude() != null) {
                                            binding.txtLocation.setText(Tools.getAddress(context, Double.parseDouble(profile.getLatitude()), Double.parseDouble(profile.getLongitude())));
                                        }
                                        android.view.ViewGroup.LayoutParams layoutParams = binding.imgStatus.getLayoutParams();
                                        layoutParams.width =150;
                                        layoutParams.height =150;
                                        binding.imgStatus.setLayoutParams(layoutParams);
                                        binding.imgStatus.setImageResource(R.drawable.ic_makechat);
                                        /*  binding.imgStatus.setLayoutParams(new RelativeLayout.LayoutParams(50,50)); */
                                        binding.imgStatus.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    }
                                    else
                                        binding.imgStatus.setImageResource(R.drawable.ic_request);
                                }
                                binding.pbLoading.setVisibility(View.GONE);

                             }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //use this json as you want
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                }
            }
//            LogUtils.e("" + error.getLocalizedMessage().toString());
        }
    };
    public static void Request(String id,String request_id) {
        String URL = SERVER_URL+REQUESTUSER;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,URL,
                new ServerParams().sendRequest(id,request_id),lister,error_listener));
        }
    static Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());
                if (login.has(STATUS_JSON)) {
                    binding.imgStatus.setImageResource(R.drawable.ic_requested);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    static Response.ErrorListener error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;
            if (response != null && response.statusCode == 404) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    JSONObject login = new JSONObject(res.toString());
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            LogUtils.e("" + error.getLocalizedMessage().toString());
        }
    };

    public static void setAfterRippleClick(RippleView rippleView,
                                           final View.OnClickListener onClickListener) {

        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onClickListener.onClick(v);
                    }
                }, RIPPLE_DURATION * 2);
            }
        });
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
          //  LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }
}

