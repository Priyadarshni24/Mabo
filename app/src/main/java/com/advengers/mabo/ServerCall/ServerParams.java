package com.advengers.mabo.ServerCall;

import android.content.Context;
import android.location.Location;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.advengers.mabo.Activity.MainActivity;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.Utils.LogUtils;

import java.util.HashMap;

import static com.advengers.mabo.Interfaces.Keys.*;
import static com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants.IntentStrings.NAME;


public class ServerParams {


    public HashMap<String, String> getLogin(String email, String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put(EMAIL, email);
        map.put(PASSWORD, password);
        //Log.d("LOGIN", "" + map);
        return map;

    }


    public HashMap<String, String> putRoomID(String userid, String roomid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID, userid);
        map.put(ROOMID, roomid);
        //Log.d("LOGIN", "" + map);
        return map;

    }
    public HashMap<String, String> changePassword(String userid, String newpassword,String confirmpassword,String email) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USER_ID, userid);
        map.put(EMAIL, email);
        map.put(NEWPASSWORD,newpassword);
        map.put(CONFIRMPASSWORD,confirmpassword);
        //Log.d("LOGIN", "" + map);
        return map;

    }
    public HashMap<String, String> UserDetails(String userid, String freindid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID, freindid);
        map.put(FRIENDID, userid);
        Log.d("LOGIN", "" + map);
        return map;

    }

    public HashMap<String, String> SendOtp(String authkey, String tempid, String mobile) {
        HashMap<String, String> map = new HashMap<>();
        map.put(AUTHKEY, authkey);
        map.put(TEMPID, tempid);
        map.put(MOBILE,mobile);
        Log.d("LOGIN", "" + map);
        return map;
    }
    public HashMap<String, String> LoginPhoneNumber(String username,String phonenumber) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERNAME,username);
        map.put(PHONENUMBER, phonenumber);
        LogUtils.e(map.toString());
        return map;

    }
    public HashMap<String, String> putFcm(String id,String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ID,id);
        map.put(FCM, token);
        LogUtils.e(map.toString());
        return map;

    }

    public HashMap<String, String> userSearch(String search) {
        HashMap<String, String> map = new HashMap<>();
        map.put(SEARCHTEXT,search);
        LogUtils.e(map.toString());
        return map;

    }

    public HashMap<String, String> putProfilePic(String id,String url,String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ID,id);
        map.put(TYPE,type);
        if(type.equals("delete"))
            map.put(IMAGENAME, "");
        else
            map.put(IMAGENAME, url);
        LogUtils.e(map.toString());
        return map;

    }
    public HashMap<String, String> forgotpassword(String email) {
        HashMap<String, String> map = new HashMap<>();
        map.put(EMAIL,email);
        LogUtils.e(map.toString());
        return map;

    }
    public HashMap<String, String> logout(String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ID,id);
        LogUtils.e(map.toString());
        return map;

    }
    public HashMap<String, String> loadPost(String id,String latitude,String longitude,String range,String start) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ID,id);
        map.put(LATITUDE,latitude);
        map.put(LONGITUDE,longitude);
        map.put(RANGE,range);
        map.put(START,start);
        LogUtils.e(map.toString());
        return map;
    }
    public HashMap<String, String> loadMyPost(String id,String start,String lat,String lng) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USER_ID,id);
        map.put(START,start);
        map.put(LATITUDE,lat);
        map.put(LONGITUDE,lng);
        LogUtils.e(map.toString());
        return map;
    }


    public HashMap<String, String> loadNotification(String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USER_ID,id);
        LogUtils.e(map.toString());
        return map;

    }
    public HashMap<String, String> listlike(String postid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(POSTID,postid);
        LogUtils.e(map.toString());
        return map;

    }
    public HashMap<String, String> SendFeedback(String name,String email,String message,String accountid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(NAME,name);
        map.put(EMAIL,email);
        map.put(MESSAGE,message);
        map.put(ACCOUNTID,accountid);
        LogUtils.e(map.toString());
        return map;

    }
    public HashMap<String, String> getFriendlist(String contactlist) {
        HashMap<String, String> map = new HashMap<>();
        map.put(CONTACTS,contactlist);
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> commentpost(String userid,String comment,String postid,String tagid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(POSTID,postid);
        map.put(USERID,userid);
        map.put(COMMENT,comment);
        map.put(TAGPEOPLEID,tagid);
        LogUtils.e(map.toString());
        return map;
     }
    public HashMap<String, String> replycommentpost(String userid,String comment,String postid,String tagid,String commentid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(POSTID,postid);
        map.put(USERID,userid);
        map.put(REPLY,comment);
        map.put(TAGPEOPLEID,tagid);
        map.put(COMMENTID,commentid);
        LogUtils.e(map.toString());
        return map;
    }
    public HashMap<String, String> UpdateLocation(String lat,String lng,String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put(LOCATION,"latlng("+lat+","+lng+")");
        map.put(LATITUDE, lat);
        map.put(LONGITUDE,lng);
        map.put(USERID,id);
        LogUtils.e(map.toString());
        return map;
    }
    public HashMap<String, String> putLocation(String lat,String lng) {
        HashMap<String, String> map = new HashMap<>();
        map.put(LOCATION,"latlng("+lat+","+lng+")");
        map.put(LATITUDE, lat);
        map.put(LONGITUDE,lng);
        LogUtils.e(map.toString());
        return map;
    }
    public HashMap<String, String> putInterest(String id,String interest) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID,id);
        map.put(INTEREST, interest);
        LogUtils.e(map.toString());
        return map;
    }
    public HashMap<String, String> getPost(String id,String postid,String latitude,String longitude) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ID,id);
        map.put(POSTID, postid);
        map.put(LATITUDE,latitude);
        map.put(LONGITUDE,longitude);
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> LikePost(String userid,String postid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID,userid);
        map.put(POSTID, postid);
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> UpdatePhonenumber(String userid,String phone) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID,userid);
        map.put(PHONE, phone);
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> createPost(String id,String title,String latitude,String longitude,String imageurl,String videourl,String taglocation,String taginterest,String tagpeople,String tagpeopleid,String tagplaceid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID,id);
        map.put(TITLE, title);
        map.put(LATITUDE,latitude);
        map.put(LONGITUDE,longitude);
        map.put(IMAGE_URL,imageurl);
        map.put(VIDEO_URL,videourl);
        map.put(TAGINTEREST,taginterest);
        map.put(TAGLOCATION,taglocation);
        map.put(TAGPEOPLE,tagpeopleid);
       /* map.put(TAGPEOPLEID,tagpeopleid);*/
        map.put(TAGPLACEID,tagplaceid);
        map.put(VIDEO_THUMBNAIL,"");
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> getNearby(String id,String lat,String lng,int range) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ID,id);
        map.put(LATITUDE, lat);
        map.put(LONGITUDE,lng);
        map.put(RANGE,String.valueOf(range));
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> notifychat(String id,String name,String friendid,String roomid,String message) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID,id);
        map.put(USERNAME, name);
        map.put(FRIENDID,friendid);
        map.put(ROOMID,roomid);
        map.put(MESSAGE,message);
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> notifycall(String id,String name,String friendid,String roomid,String message) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID,id);
        map.put(USERNAME, name);
        map.put(FRIENDID,friendid);
        map.put(ROOMID,roomid);
        map.put(MESSAGE,message);
        LogUtils.e(map.toString());
        return map;
    }
    public HashMap<String,String> createCometUser(String id,String name,String avatar)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(COMET_ID,id);
        map.put(COMET_NAME,name);
        if(!avatar.isEmpty())
            map.put(AVATAR,avatar);
        else
            map.put(AVATAR, MainActivity.LOADAVATAR);
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String,String> UpdateCometUser(String name,String avatar)
    {
        HashMap<String, String> map = new HashMap<>();
    //   map.put(COMET_ID,id);
        map.put(COMET_NAME,name);
        if(!avatar.isEmpty())
            map.put(AVATAR,avatar);
        else
            map.put(AVATAR, MainActivity.LOADAVATAR);
        LogUtils.e(map.toString());
        return map;
    }

    public HashMap<String, String> sendRequest(String id, String request_id) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERID,id);
        map.put(REQUESTID, request_id);
        LogUtils.e(map.toString());
        return map;
    }
    public HashMap<String, String> getSignup(String username,String email, String password,String token,String roomid) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERNAME, username);
        map.put(EMAIL, email);
        map.put(PASSWORD, password);
        map.put(FIRSTNAME,"");
        map.put(LASTNAME,"");
        map.put(CREATED,"");
        map.put(MODIFIED,"");
        map.put(IPADDRESS,"");
        map.put(DEVICEMODEL,"Android");
        map.put(FCMKEY,"");
        map.put(DEVICEID,"");
        map.put(GOOGLEID,"");
        map.put(FACEBOOKID,"");
        map.put(LOCATION,"");
        map.put(LATITUDE,"");
        map.put(LONGITUDE,"");
        map.put(STATUS,"");
        map.put(ROOMID,roomid);
        map.put(GENDER,"");
        map.put(LOGIN_TYPE,"manual");
        Log.d("LOGIN", "" + map);
        return map;

    }

    public HashMap<String, String> getSignupfacebook(String username,String email, String fbid,String token,String gender) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERNAME, username);
        map.put(EMAIL, email);
        map.put(PASSWORD, "");
        map.put(FIRSTNAME,"");
        map.put(LASTNAME,"");
        map.put(CREATED,"");
        map.put(GENDER,gender);
        map.put(MODIFIED,"");
        map.put(IPADDRESS,"");
        map.put(DEVICEMODEL,"Android");
        map.put(FCMKEY,"");
        map.put(DEVICEID,"");
        map.put(GOOGLEID,"");
        map.put(FACEBOOKID,fbid);
        map.put(LOCATION,"");
        map.put(LATITUDE,"");
        map.put(LONGITUDE,"");
        map.put(ROOMID,"");
        map.put(STATUS,"");
        map.put(LOGIN_TYPE,"facebook");
        Log.d("LOGIN", "" + map);
        return map;

    }

    public HashMap<String, String> getSignupgoogle(String username,String email, String googleid, String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERNAME, username);
        map.put(EMAIL, email);
        map.put(PASSWORD, "");
        map.put(FIRSTNAME,"");
        map.put(LASTNAME,"");
        map.put(CREATED,"");
        map.put(GENDER,"");
        map.put(MODIFIED,"");
        map.put(IPADDRESS,"");
        map.put(DEVICEMODEL,"Android");
        map.put(FCMKEY,"");
        map.put(DEVICEID,"");
        map.put(GOOGLEID,googleid);
        map.put(FACEBOOKID,"");
        map.put(LOCATION,"");
        map.put(LATITUDE,"");
        map.put(LONGITUDE,"");
        map.put(ROOMID,"");
        map.put(STATUS,"");
        map.put(LOGIN_TYPE,"gmail");
        Log.d("LOGIN", "" + map);
        return map;

    }

    public HashMap<String, String> updateuser(User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put(USERNAME, user.getUsername());
        map.put(FIRSTNAME,"");
        map.put(LASTNAME,"");
     //   if(user.getPhone().contains(code))
      //  map.put(PHONE,"+919790449694");
    /*    else
            map.put(PHONE,code+user.getPhone());*/
        map.put(IPADDRESS,"");
        map.put(DEVICEMODEL,"Android");
        map.put(FCMKEY,user.getFcm_key());
        map.put(DEVICEID,"");
        map.put(GOOGLEID,"");
        map.put(FACEBOOKID,"");
        map.put(LOCATION,"");
        map.put(LATITUDE,"");
        map.put(LONGITUDE,"");
        map.put(GENDER,user.getGender());
        if(user.getEmail_notify()==null)
            map.put(EMAILNOTIFY,"");
        else
        map.put(EMAILNOTIFY,user.getEmail_notify());
        if(user.getPush_notify()==null)
            map.put(PUSHNOTIFY,"");
        else
            map.put(PUSHNOTIFY,user.getPush_notify());
        if(user.getScrumble_location()==null)
            map.put(SCRUMLELOCATION,"");
        else
            map.put(SCRUMLELOCATION,user.getScrumble_location());
        if(user.getProfile_display_status()==null)
            map.put(PRIVACY,"0");
        else
            map.put(PRIVACY,user.getProfile_display_status());
        map.put(INTERESTS,user.getInterests());
        Log.d("PROFILE UPDATE", "" + map);
        return map;
    }



}
