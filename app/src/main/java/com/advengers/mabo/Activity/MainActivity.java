package com.advengers.mabo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.advengers.mabo.R;

public class MainActivity extends AppCompatActivity {



    //dev Server url
    public static String SERVER_URL = "https://themabo.com/codeigniter/";


    //Live Server
    //public static String SERVER_URL = "http://themabo.com/codeigniter/";

    //Server Methods

    public static String LOGIN = "Authentication/login";
    public static String REGISTER = "Authentication/registration";
    public static String USERDETAILS = "Authentication/users";
    public static String UPDATEUSER = "Authentication/user/";
    public static String FCM = "Authentication/fcm";
    public static String LOCATION = "Authentication/location/";
    public static String PROFILEPIC = "Authentication/user_profileimage";
    public static String LOGOUT = "Authentication/user_logout";
    public static String GETNEARBY = "Location/nearby";
    public static String GETINTREST = "Interest/viewinterest";
    public static String PUTINTREST = "Interest/update_interest";
    public static String LOADPOST = "Location/listuserdata";
    public static String USERSEARCH = "Authentication/user_search";
    public static String CREATEPOST = "Location/userpostcreate";
    public static String LIKEPOST = "Location/likeuserdata";
    public static String DISLIKEPOST = "Location/dislike";
    public static String COMMENTPOST = "Location/usercomment";
    public static String LIKELIST = "Location/listlike";
    public static String FRIENDSLIST = "Location/friendslist";
    public static String COMMENTLIST = "Location/listcomment";
    public static String REQUESTUSER = "Location/requestuser";
    public static String ACCEPTUSER = "Location/acceptuser";
    public static String SINGLEPOST = "Location/singledetails";
    public static String NOTIFYCHAT = "Location/pushnotifychat";
    public static String NOTIFYCALL = "Location/pushnotifycall";
    public static String ROOMIDUPDATE = "Authentication/roomid_update";
    public static String CLOUDBASEURL="http://res.cloudinary.com/mabo-app/image/upload/";
    public static String CLOUDVIDEOBASEURL = "http://res.cloudinary.com/mabo-app/video/upload/";
    public static String LOADAVATAR = "https://res.cloudinary.com/mabo-app/image/upload/v1568559008/ic_avatar.png";
    public static String CHANGEPASSWORD = "Authentication/change_password/";
    public static String FORGOTPASSWORD = "Authentication/forgot_passwords/";
    //CometChat
    public static String COMETCHATURL = "https://api-us.cometchat.io/v2.0/users";

}
