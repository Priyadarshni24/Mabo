package com.advengers.mabo.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.FileUtils;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Utils;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.advengers.mabo.Activity.MainActivity.COMETCHATURL;
import static com.advengers.mabo.Activity.MainActivity.LOGOUT;
import static com.advengers.mabo.Activity.MainActivity.PROFILEPIC;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USER;

public class GalleryActivity extends MyActivity implements EasyPermissions.PermissionCallbacks {


    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int RC_STORAGE = 1 ;
    boolean mIsEditMode = false;
    String mProfilepicurl;
    Toolbar mToolbar;
    ImageView img_profile;
    MenuItem deleteimage,selecteditem;
    private ArrayList<HashMap<String, String>> mData;
    Uri resultUri=null;
    private static final int SELECT_PHOTO = 100;
    Cloudinary cloudinary;
    Gson gson;
    User user;
    String type = "";
    String message = "Updated successfully";

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_STORAGE)
    private void RequiresPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if(selecteditem.getItemId()== R.id.action_upload_image)
            //   uploadImage();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle(getString(R.string.activity_crop))
                        .start(this);

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.storage_rationale),
                    RC_STORAGE, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
           // uploadImage();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mToolbar = (Toolbar)findViewById(R.id.mToolbar);
        img_profile = (ImageView) findViewById(R.id.img_profile);
           setup();
    }
    void setup() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_gallery));

        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(getApplicationContext()).getString(USER),User.class));
        user = User.getUser();
        cloudinary = Utils.cloud(GalleryActivity.this);
        if(user.getprofile_imagename()==null||user.getprofile_imagename().isEmpty())
            Picasso.get().load(R.drawable.no_image).into(img_profile);
        else
            Picasso.get().load(user.getprofile_imagename()).error(R.drawable.no_image).into(img_profile);
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        deleteimage = menu.findItem(R.id.action_image_delete);
        if(user.getprofile_imagename()==null)
            deleteimage.setVisible(false);
        else
            deleteimage.setVisible(true);
        return true;
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        selecteditem = item;
        switch(item.getItemId())
        {
            case android.R.id.home:
                menuHome();
                break;
            case R.id.action_upload_image:
                RequiresPermission();
                return true;
            case R.id.action_image_delete:
                onDeletepopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
    private void onDeletepopup() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GalleryActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_warning, null);
        dialogBuilder.setView(dialogView);

        TextView txt_title = (TextView) dialogView.findViewById(R.id.txt_title);
        LinearLayout ll_ok = (LinearLayout) dialogView.findViewById(R.id.ll_ok);
        LinearLayout ll_ok_cancel = (LinearLayout) dialogView.findViewById(R.id.ll_ok_cancel);
        TextView txt_content = (TextView) dialogView.findViewById(R.id.txt_content);
        txt_title.setText(getString(R.string.str_delete));
        txt_content.setText(getString(R.string.str_delete_image));
        ll_ok_cancel.setVisibility(View.VISIBLE);
        Button btn_yes = (Button)dialogView.findViewById(R.id.btn_yes);
        Button btn_no = (Button)dialogView.findViewById(R.id.btn_no);
        btn_yes.setText(getString(R.string.yes));
        btn_no.setText(getString(R.string.no));
        final AlertDialog dialog = dialogBuilder.create();
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


                mProfilepicurl="";
               new DeleteImage().execute();


              /*  if(NetworkUtil.isInternetOn(GalleryActivity.this))
                    onImageDelete();
                else
                    Tools.showDialog(true,getString(R.string.str_check_network),GalleryActivity.this,GalleryActivity.this);*/
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ll_ok.setVisibility(View.GONE);
        dialog.show();
    }

    void menuHome() {
        onBackPressed();
    }




    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
      //  onLoadDismiss();
        LogUtils.e("I am coming");
       if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                LogUtils.e(resultUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (resultCode == RESULT_OK) {
             LogUtils.e("Video Path "+ FileUtils.getPath(GalleryActivity.this,resultUri));
            String path=FileUtils.getPath(GalleryActivity.this,resultUri);
            new UploadImage().execute(path);
        }
    }
    public class UploadImage extends AsyncTask<String,Void,String>
    {
        Map<String,String> Cloudresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onLoadProgress(GalleryActivity.this);
            Picasso.get().load(R.drawable.ic_image_loading).into(img_profile);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String lastVideoSent = User.getUser().getEmail() + "_" + new Date().getTime() + ".jpg";
                Map<String,String> options = new HashMap<>();
                options.put("public_id",getFileName(lastVideoSent));
                options.put("resource_type", "image");
                options.put("return_delete_token","true");
                LogUtils.e(lastVideoSent);
              //  if(!Utils.getInstance(GalleryActivity.this).getString(Utils.PROFILEPIC_DELETE_TOKEN).isEmpty())
                   // cloudinary.uploader().deleteByToken(Utils.getInstance(GalleryActivity.this).getString(Utils.PROFILEPIC_DELETE_TOKEN));
                Cloudresponse = cloudinary.uploader().upload(strings[0], options);
                Utils.getInstance(GalleryActivity.this).setString(Utils.PROFILEPIC_DELETE_TOKEN,Cloudresponse.get("delete_token"));
                LogUtils.e(Cloudresponse.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Cloudresponse.get("url");
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            mProfilepicurl = s;
          ProfilePicUpdate(mProfilepicurl);
        }
    }
    public class DeleteImage extends AsyncTask<String,Void,String>
    {
        Map<String,String> Cloudresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onLoadProgress(GalleryActivity.this);
            Picasso.get().load(R.drawable.ic_image_loading).into(img_profile);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                Cloudresponse = cloudinary.uploader().deleteByToken(Utils.getInstance(GalleryActivity.this).getString(Utils.PROFILEPIC_DELETE_TOKEN));
                LogUtils.e(Cloudresponse.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            Utils.getInstance(GalleryActivity.this).setString(Utils.PROFILEPIC_DELETE_TOKEN,"");
            mProfilepicurl = "";
            ProfilePicUpdate(mProfilepicurl);
        }
    }

    public static String getFileName(String filename) {
        int pos = filename.lastIndexOf(".");
        String justName = pos > 0 ? filename.substring(0, pos) : filename;
        return justName;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }
    private void ProfilePicUpdate(String url) {
   // LogUtils.e(user.getprofile_imagename());
        if(user.getprofile_imagename()==null||user.getprofile_imagename().isEmpty())
            type = "insert";
        else if(user.getprofile_imagename()!=null&&user.getprofile_imagename().length()>0&&url.length()>0)
            type = "update";
        else if(url=="")
             type = "delete";
        String URL = SERVER_URL+PROFILEPIC;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().putProfilePic(user.getId(),url,type)
                , lister,error_listener));
        Picasso.get().load(R.drawable.ic_image_loading).into(img_profile);
    }
    private void CometProfilePicUpdate(String url) {
        // LogUtils.e(user.getprofile_imagename());
        if(user.getprofile_imagename()==null||user.getprofile_imagename().isEmpty())
            type = "insert";
        else if(user.getprofile_imagename()!=null&&user.getprofile_imagename().length()>0&&url.length()>0)
            type = "update";
        else if(url=="")
        {
            type = "delete";
            url = MainActivity.LOADAVATAR;
        }

        String roomid = "mabo"+user.getId();

        String URL = COMETCHATURL+"/"+roomid;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createCometStringRequest(Request.Method.PUT,
                URL,new ServerParams().UpdateCometUser(user.getUsername(),url)
                , lister,error_listener));
    //    Picasso.get().load(R.drawable.ic_image_loading).into(img_profile);
    }

    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {
                        if(type.equals("insert"))
                        {
                            user.setprofile_imagename(mProfilepicurl);
                            Picasso.get().load(mProfilepicurl).into(img_profile);
                            deleteimage.setVisible(true);
                        }else if(type.equals("update"))
                        {
                            user.setprofile_imagename(mProfilepicurl);
                            Picasso.get().load(mProfilepicurl).into(img_profile);
                        }else if(type.equals("delete"))
                        {
                            user.setprofile_imagename(null);
                            Picasso.get().load(R.drawable.no_image).into(img_profile);
                            deleteimage.setVisible(false);
                            String jsondata = gson.toJson(user);
                            LogUtils.e(jsondata);
                        }

                        String jsondata = gson.toJson(user);
                        Utils.getInstance(GalleryActivity.this).setString(USER,jsondata);
                        getUser();

                        message = login.getString(MESSAGE);
                        CometProfilePicUpdate(mProfilepicurl);
                    }

                }else{
                    LogUtils.e("Coming here");
                    showInfo(message,GalleryActivity.this,GalleryActivity.this);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    Response.ErrorListener error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            onLoadDismiss();
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
}
