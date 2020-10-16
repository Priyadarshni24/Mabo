package com.advengers.mabo.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.advengers.mabo.Adapter.AutoSuggestAdapter;
import com.advengers.mabo.Adapter.ImageUploadSlideAdapter;
import com.advengers.mabo.Adapter.InterestAdapter;
import com.advengers.mabo.Adapter.NewPostPicturesListAdapter;
import com.advengers.mabo.Adapter.TagpeopleAdapter;
import com.advengers.mabo.Adapter.VideoSlideAdapter;
import com.advengers.mabo.Model.Interest;
import com.advengers.mabo.Model.Tag;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.FileUtils;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Tools.NewPostPicture;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ActivityCreatepostBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.asksira.bsimagepicker.BSImagePicker;
import com.cloudinary.Cloudinary;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
//import com.iceteck.silicompressorr.SiliCompressor;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.advengers.mabo.Activity.MainActivity.CLOUDBASEURL;
import static com.advengers.mabo.Activity.MainActivity.CLOUDVIDEOBASEURL;
import static com.advengers.mabo.Activity.MainActivity.CREATEPOST;
import static com.advengers.mabo.Activity.MainActivity.LOGOUT;
import static com.advengers.mabo.Activity.MainActivity.PUTINTREST;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Activity.MainActivity.USERSEARCH;
import static com.advengers.mabo.Interfaces.Keys.DATA;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.INTEREST;
import static com.advengers.mabo.Interfaces.Keys.INTERESTJSON;
import static com.advengers.mabo.Interfaces.Keys.MESSAGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USER;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class CreatePostActivity extends MyActivity implements View.OnClickListener,
                                        BSImagePicker.OnMultiImageSelectedListener,
                                        BSImagePicker.ImageLoaderDelegate,
                                        InterestAdapter.SelectedInterest,
                                        TagpeopleAdapter.TagPeople,
                                        ImageUploadSlideAdapter.TagImages,
                                        VideoSlideAdapter.TagVideo
{
    ActivityCreatepostBinding binding;
    private static final int REQUEST_PLACE_PICKER = 10;
    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int GET_VIDEO_INTENT = 1212;
    boolean placepicked = false;
    private Place lastPlace = null;
    private LatLng latLng = null;
    private String poi = null;
  //  NewPostPicturesListAdapter pAdapter;
    LinearLayoutManager mLayoutManager;
    private ArrayList<NewPostPicture> newPostPictures = new ArrayList<>();
    List<String> suggestions = new ArrayList<>();
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    JSONArray interest;
    ArrayList<Interest> listdata = new ArrayList<>();
    ArrayList<User> taglist = new ArrayList<>();
    ArrayList<User> Userlist = new ArrayList<>();
    TagpeopleAdapter tagAdapter;
    ArrayList<Uri> list;
    ImageUploadSlideAdapter imgAdapter;
    VideoSlideAdapter videoAdapter;

    Cloudinary cloudinary;
    ArrayList<String> imageUrls = new ArrayList<>();
    ArrayList<String> videoUrls = new ArrayList<>();
    int uploadimagesize = 0;
    int uploadvideosize = 0;
    String selectedinterest="";
    String tagplace = "";
    String tagplaceid="";
    String videos = "";
    String images = "";
    ClosingCallActivity callback;
    ArrayList<String> videopath = new ArrayList<>();
    ArrayList<Bitmap> videothumbbnails = new ArrayList<>();
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
  //  String test = "http://res.cloudinary.com/mabo-app/video/upload/v1572590716/aupriyadarshni%40gmail.com_1572590718163.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(CreatePostActivity.this, R.layout.activity_createpost);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle(getString(R.string.str_back));
      //  setUser();
        getUser();
        /*String[] arrOfStr = test.split(CLOUDVIDEOBASEURL);

        for (String a : arrOfStr)
        {edtpost
            //s =a;
            LogUtils.e("path "+a);
        }*/
        binding.txtNextone.setOnClickListener(this);
        binding.pinloction.setOnClickListener(this);
        cloudinary = Utils.cloud(CreatePostActivity.this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.PLACE_API_KEY));
        }

        LogUtils.e(Utils.getInstance(CreatePostActivity.this).getString(INTERESTJSON));
        try {
            interest = new JSONArray(Utils.getInstance(CreatePostActivity.this).getString(INTERESTJSON));
            for(int i = 0;i<interest.length();i++)
            {
                JSONObject check = new JSONObject(interest.get(i).toString());


                    Interest interest = new Interest();
                    interest.setId(check.getString(ID));
                    interest.setInterest(check.getString(INTEREST));
                    interest.setEnable(false);
                    interest.setEditable(true);
                    listdata.add(interest);
            }
            InterestAdapter adapter = new InterestAdapter(CreatePostActivity.this,listdata,"");
            adapter.setInterestCallBackListener(this);
            binding.listInterest.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.btnAddvideo.setOnClickListener(this);
        binding.lytUpload.setOnClickListener(this);
      /*  pAdapter = new NewPostPicturesListAdapter(this);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.newPostImageList.setLayoutManager(mLayoutManager);
        pAdapter = new NewPostPicturesListAdapter(this);
        pAdapter.setItems(newPostPictures);
        binding.newPostImageList.setAdapter(pAdapter);*/



        //Setting up the adapter for AutoSuggest
        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        binding.txttagpeople.setThreshold(2);
        binding.txttagpeople.setAdapter(autoSuggestAdapter);
        binding.txttagpeople.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        LogUtils.e(autoSuggestAdapter.getObject(position));
                        binding.txttagpeople.setText("");
                        hideKeyboard(CreatePostActivity.this);
                        taglist.add(Userlist.get(position));
                        tagAdapter = new TagpeopleAdapter(CreatePostActivity.this,taglist);
                        tagAdapter.tagPeopleCallBackListener(CreatePostActivity.this);
                        binding.listTagpeople.setAdapter(tagAdapter);

                    }
                });
        binding.txttagpeople.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(binding.txttagpeople.getText())) {
                        makeApiCall(binding.txttagpeople.getText().toString());
                    }
                }
                return false;
            }
        });
        binding.btnPost.setOnClickListener(this);
       /* if(callback==null)
        callback.onClosecall();*/
        binding.scrollView.smoothScrollTo(0,0);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void makeApiCall(String toString) {
            String URL = SERVER_URL+USERSEARCH;
            URL = URL.replaceAll(" ", "%20");
            App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                    URL,new ServerParams().userSearch(toString)
                    , lister,error_listener));

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
                            Userlist.clear();
                            List<String> stringList = new ArrayList<>();
                            try {
                               JSONArray array = login.getJSONArray(DATA);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject row = array.getJSONObject(i);
                                    String jsondata = array.get(i).toString();
                                    // LogUtils.e(jsondata);
                                    User userdata = gson.fromJson(jsondata, User.class);
                                    Userlist.add(userdata);
                                    stringList.add(row.getString(USERNAME));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //IMPORTANT: set data here and notify
                            autoSuggestAdapter.setData(stringList);
                            autoSuggestAdapter.notifyDataSetChanged();
                        }
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
                        try {

                            if (obj.has(STATUS_JSON)) {


                                if (obj.getString(STATUS_JSON).equals("true")) {
                                    Userlist.clear();
                                    List<String> stringList = new ArrayList<>();
                                    try {
                                        JSONArray array = obj.getJSONArray(DATA);
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject row = array.getJSONObject(i);
                                            String jsondata = array.get(i).toString();
                                            // LogUtils.e(jsondata);
                                            User userdata = gson.fromJson(jsondata, User.class);
                                            Userlist.add(userdata);
                                            stringList.add(row.getString(USERNAME));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //IMPORTANT: set data here and notify
                                    autoSuggestAdapter.setData(stringList);
                                    autoSuggestAdapter.notifyDataSetChanged();
                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_addvideo:
                if(videopath.size()<2)
                    showDialogForImage();
                else
                    showInfo("Can't add more than 2 videos",CreatePostActivity.this,CreatePostActivity.this);
                break;
            case R.id.lyt_upload:
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(3)
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");
                break;
            case R.id.txt_nextone:
                binding.rlPageone.setVisibility(View.GONE);
                binding.rlPagetwo.setVisibility(View.VISIBLE);
                binding.btnPost.setVisibility(View.VISIBLE);
                break;
            case R.id.pinloction:
                if(!placepicked)
                    newPostPOIPick();
                placepicked = true;
                break;
            case R.id.btn_post:
                if(binding.edtpost.getText().toString().isEmpty())
                {
                    showInfo(getString(R.string.str_postempty),CreatePostActivity.this,CreatePostActivity.this);
                }else if(selectedinterest.isEmpty()){
                    showInfo(getString(R.string.str_postbelong),CreatePostActivity.this,CreatePostActivity.this);
                }
                else
                {
                    if(list!=null)
                        uploadimagesize = list.size();
                    if(videopath!=null)
                        uploadvideosize = videopath.size();
                    onLoadProgress(CreatePostActivity.this);
                    if(uploadimagesize>0)
                    {

                        String path= FileUtils.getPath(CreatePostActivity.this,list.get(uploadimagesize-1));
                        new UploadImage().execute(path);
                    }else if(uploadvideosize>0)
                    {
                        String path= FileUtils.getPath(CreatePostActivity.this,Uri.parse(videopath.get(uploadvideosize-1)));
                        new UploadVideo().execute(videopath.get(uploadvideosize-1));
                    }
                    else
                    {
                       String URL = SERVER_URL+CREATEPOST;
                        URL = URL.replaceAll(" ", "%20");

                        String tagpeople = "";
                        String tagpeopleid="";
                        if(taglist.size()>0)
                            for(int i=0;i<taglist.size();i++)
                            {
                                if(i==0)
                                {
                                    tagpeople = taglist.get(i).getUsername();
                                    tagpeopleid = taglist.get(i).getId();

                                }else
                                {
                                    tagpeople = tagpeople+","+taglist.get(i).getUsername();
                                    tagpeopleid = tagpeopleid+","+taglist.get(i).getId();
                                }

                            }

                        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,URL,
                                new ServerParams().createPost(user.getId(), StringEscapeUtils.escapeJava(binding.edtpost.getText().toString()),user.getLatitude(),user.getLongitude(),
                                        "","",tagplace,selectedinterest,tagpeople,tagpeopleid,tagplaceid),postlister,post_error_listener));
                        LogUtils.e("ALL Image uploaded "+imageUrls.toString());
                    }
                }
                break;

        }
    }
    private void showDialogForImage() {

        new AlertDialog.Builder(CreatePostActivity.this)
                .setTitle("Select video")
                .setMessage("Please choose video selection type")
                .setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openVideoCapture();
                    }
                })
                .setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pickFromGallery();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void openVideoCapture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA, getString(R.string.permission_read_storage_rationale),REQUEST_IMAGE_CAPTURE );
        } else {
            Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
        }
    }
    private void pickFromGallery() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setTypeAndNormalize("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), REQUEST_VIDEO_TRIMMER);
        }
    }

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_TRIMMER) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startTrimActivity(selectedUri);
                } else {
                    Toast.makeText(CreatePostActivity.this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, TrimmerActivity.class);
        intent.putExtra(Utils.EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
        intent.putExtra(Utils.VIDEO_TOTAL_DURATION, getMediaDuration(uri));
        startActivityForResult(intent,GET_VIDEO_INTENT);
    }

    private int  getMediaDuration(Uri uriOfFile)  {
        MediaPlayer mp = MediaPlayer.create(this,uriOfFile);
        int duration = mp.getDuration();
        return  duration;
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permission_title_rationale));
            builder.setMessage(rationale);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(CreatePostActivity.this, PERMISSIONS, requestCode);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, requestCode);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openVideoCapture();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            placepicked = false;
               if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    tagplace = place.getName();
                    tagplaceid = place.getId();
                    LogUtils.e( "Place: " + place.getName() + ", " + place.getId());
                    binding.pinloction.setText(place.getName());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    LogUtils.e(status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
        }else if (requestCode == REQUEST_VIDEO_TRIMMER) {
            LogUtils.e("VideoTrimmer");
            if (resultCode == RESULT_OK) {
                final Uri selectedUri = data.getData();
            if (selectedUri != null) {
                startTrimActivity(selectedUri);
            } else {
                Toast.makeText(CreatePostActivity.this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
            }
        }
        }else if(requestCode == GET_VIDEO_INTENT && resultCode == RESULT_OK && data != null)
        {
           videopath.add(data.getStringExtra(Utils.VIDEOURL));
            try {
             //   videothumbbnails.add(ThumbnailUtils.createVideoThumbnail(videopath.get(0), MediaStore.Video.Thumbnails.MINI_KIND));//retriveVideoFrameFromVideo(videopath.get(0)));
                videoAdapter = new VideoSlideAdapter(CreatePostActivity.this,videopath);
                binding.slideViewpagervideo.setVisibility(View.VISIBLE);
                binding.slideViewpagervideo.setAdapter(videoAdapter);
                videoAdapter.tagVideoCallBackListener(this);
                final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                final String fileName = "MP4_" + timeStamp + ".mp4";
                final String filePath = getDestinationPath() + fileName;

                File file = new File(filePath);
                file.getParentFile().mkdirs();
               // String Compresspath = SiliCompressor.with(CreatePostActivity.this).compressVideo(data.getStringExtra(Utils.VIDEOURL), filePath);
              LogUtils.e(data.getStringExtra(Utils.VIDEOURL));
         //       new UploadVideo().execute(Compresspath);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String getDestinationPath() {
        String mFinalPath;
           File folder = Environment.getExternalStorageDirectory();
            mFinalPath = folder.getPath() + File.separator;
            LogUtils.e("Using default path " + mFinalPath);
        return mFinalPath;
    }
    public static Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
           /* if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);*/
              mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
    private String displayPlace(String attributions) {
        final CharSequence name = lastPlace.getName();
        final CharSequence address = lastPlace.getAddress();

        if (attributions == null) {
            attributions = "";
        }
        String desc = String.format("%s\n(%s) %s", name, address, attributions);

        binding.pinloction.setText(Html.fromHtml(desc).toString());

        poi = String.format("%s (%s)", lastPlace.getName(), lastPlace.getAddress());
        latLng = lastPlace.getLatLng();
        return desc;
    }
    @Override
    public void onBackPressed() {
        if(binding.rlPagetwo.getVisibility()==View.VISIBLE)
        {
            binding.rlPageone.setVisibility(View.VISIBLE);
            binding.rlPagetwo.setVisibility(View.GONE);
            binding.btnPost.setVisibility(View.GONE);

        }else
        {
            super.onBackPressed();
        }

    }
    void newPostPOIPick() {
        // Construct an intent for the place picker
        try {

            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,fields )
                    .build(this);
            startActivityForResult(intent,REQUEST_PLACE_PICKER );


        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        LogUtils.e(uriList.toString());
        for (int i = 0; i < uriList.size(); i++) {
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(uriList.get(i));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            list = new ArrayList<>(uriList.size());
            list.addAll(uriList);
       //     Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
             imgAdapter = new ImageUploadSlideAdapter(CreatePostActivity.this,list);
            imgAdapter.tagImageCallBackListener(CreatePostActivity.this);
            binding.slideViewpager.setVisibility(View.VISIBLE);
            binding.lytUpload.setVisibility(View.GONE);
            binding.slideViewpager.setAdapter(imgAdapter);
           /* if (bitmap != null) {
                final NewPostPicture newPostPicture = new NewPostPicture(Tools.scaleBitmap(bitmap), null, false);
                newPostPicture.onClickDelete = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newPostPictures.remove(newPostPicture);
                        pAdapter.update();
                    }
                };
                newPostPictures.add(newPostPicture);
                binding.newPostImageList.setVisibility(View.VISIBLE);
                pAdapter.update();
            }*/
        }
    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
       // LogUtils.e(imageFile.getAbsolutePath());
        //Glide.with(MainActivity.this).load(imageFile).into(ivImage);
        Picasso.get().load(imageFile).into(ivImage);
    }

    @Override
    public void onSelect(String interest) {
        LogUtils.e(interest);
        selectedinterest = interest;
    }

    @Override
    public void onTag(User userdetail,int position) {
        LogUtils.e("Remove data "+userdetail.getUsername());
        taglist.remove(position);
        /*tagAdapter = new TagpeopleAdapter(CreatePostActivity.this,taglist);
        tagAdapter.tagPeopleCallBackListener(CreatePostActivity.this);*/
        tagAdapter.notifyDataSetChanged();
        //binding.listTagpeople.setAdapter(tagAdapter);
    }

    @Override
    public void onImageTag(int position) {
      /*  LogUtils.e(position+"");
        LogUtils.e(list.get(position).toString());*/
        list.remove(position);
      //  imgAdapter.notifyDataSetChanged();
        imgAdapter = new ImageUploadSlideAdapter(CreatePostActivity.this,list);
        imgAdapter.tagImageCallBackListener(CreatePostActivity.this);
        binding.slideViewpager.setAdapter(imgAdapter);
        LogUtils.e(list.toString());
        if(list.size()==0)
        {
            binding.lytUpload.setVisibility(View.VISIBLE);
            binding.slideViewpager.setVisibility(View.GONE);
        }
    }

    @Override
    public void onVideoTag(int position) {
        if(videopath.size()>0)
        {
            videopath.remove(position);
            videoAdapter = new VideoSlideAdapter(CreatePostActivity.this,videopath);
            binding.slideViewpagervideo.setAdapter(videoAdapter);
            videoAdapter.tagVideoCallBackListener(CreatePostActivity.this);
            if(videopath.size()==0)
            {
                binding.lytVideo.setVisibility(View.GONE);
            }
        }
    }

    public class UploadImage extends AsyncTask<String,Void,String>
    {
        Map<String,String> Cloudresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
        String path = "";
            String s = "";
            try {
                String lastVideoSent = User.getUser().getEmail() + "_" + new Date().getTime() + ".jpg";
                Map<String,String> options = new HashMap<>();
                options.put("public_id",getFileName(lastVideoSent));
                options.put("resource_type", "image");
                options.put("return_delete_token","true");
                LogUtils.e(lastVideoSent);
                Cloudresponse = cloudinary.uploader().upload(strings[0], options);
                LogUtils.e(Cloudresponse.toString());
                path = Cloudresponse.get("url");

                String[] arrOfStr = path.split(CLOUDBASEURL, 2);

                for (String a : arrOfStr)
                {
                    s =a;
                    LogUtils.e(a);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return s;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            imageUrls.add(s);
            uploadimagesize = uploadimagesize -1;
            if(uploadimagesize == 0)
            {
                if(uploadvideosize>0)
                {
                   String path= FileUtils.getPath(CreatePostActivity.this,Uri.parse(videopath.get(uploadvideosize-1)));
                    new UploadVideo().execute(videopath.get(uploadvideosize-1));
                }
                else {
                    for (int i = 0; i < imageUrls.size(); i++) {
                        if (i == 0)
                            images = imageUrls.get(i);
                        else {
                            images = images + "," + imageUrls.get(i);
                        }
                    }
                    String URL = SERVER_URL + CREATEPOST;
                    URL = URL.replaceAll(" ", "%20");
                    JSONArray jsonArray = new JSONArray();
                    String tagpeople = "";
                    String tagpeopleid = "";
                    LogUtils.e("Tag Size "+taglist.size());
                    if (taglist.size() > 0)
                        for (int i = 0; i < taglist.size(); i++) {

                            if (i == 0) {
                                tagpeople = taglist.get(i).getUsername();
                                tagpeopleid = taglist.get(i).getId();

                            } else {
                                tagpeople = tagpeople + "," + taglist.get(i).getUsername();
                                tagpeopleid = tagpeopleid + "," + taglist.get(i).getId();
                            }
                         }
                                  App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST, URL,
                            new ServerParams().createPost(user.getId(),StringEscapeUtils.escapeJava(binding.edtpost.getText().toString()), user.getLatitude(), user.getLongitude(),
                                    images, "", tagplace, selectedinterest, tagpeople, tagpeopleid, tagplaceid), postlister, post_error_listener));
                    LogUtils.e("ALL Image uploaded " + imageUrls.toString());
                }
            }else
            {
                String path= FileUtils.getPath(CreatePostActivity.this,list.get(uploadimagesize-1));
                new UploadImage().execute(path);

            }
          /*  mProfilepicurl = s;
            ProfilePicUpdate(mProfilepicurl);*/
        }
    }
    public class UploadVideo extends AsyncTask<String,Void,String>
    {
        Map<String,String> Cloudresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            String path = "";
            String s = "";
            try {
                String lastVideoSent = User.getUser().getEmail() + "_" + new Date().getTime() + ".mp4";
                Map<String,String> options = new HashMap<>();
                options.put("public_id",getFileName(lastVideoSent));
                options.put("resource_type", "video");
                options.put("return_delete_token","true");
                LogUtils.e(lastVideoSent);
                LogUtils.e(strings[0]);
                Cloudresponse = cloudinary.uploader().upload(strings[0], options);
                LogUtils.e(Cloudresponse.toString());
                path = Cloudresponse.get("url");

                String[] arrOfStr = path.split(CLOUDVIDEOBASEURL);

                for (String a : arrOfStr)
                {
                    s =a;
                    LogUtils.e("path "+a);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return s;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onLoadDismiss();
            LogUtils.e(s);
            videoUrls.add(s);

            File fdelete = new File(videopath.get(uploadvideosize-1));
            if (fdelete.exists()) {
                fdelete.delete();
            }
                    uploadvideosize = uploadvideosize -1;
            if(uploadvideosize == 0)
            {
                for (int i = 0; i < imageUrls.size(); i++) {
                    if (i == 0)
                        images = imageUrls.get(i);
                    else {
                        images = images + "," + imageUrls.get(i);
                    }
                }
                for(int i=0;i<videoUrls.size();i++)
                {
                    if(i==0)
                        videos = videoUrls.get(i);
                    else{
                        videos = videos+","+videoUrls.get(i);
                    }
                }
                String URL = SERVER_URL+CREATEPOST;
                URL = URL.replaceAll(" ", "%20");
                String tagpeople = "";
                String tagpeopleid="";
                if(taglist.size()>0)
                    for(int i=0;i<taglist.size();i++)
                    {
                        if(i==0)
                        {
                            tagpeople = taglist.get(i).getUsername();
                            tagpeopleid = taglist.get(i).getId();

                        }else
                        {
                            tagpeople = tagpeople+","+taglist.get(i).getUsername();
                            tagpeopleid = tagpeopleid+","+taglist.get(i).getId();
                        }

                    }

                App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,URL,
                        new ServerParams().createPost(user.getId(),StringEscapeUtils.escapeJava(binding.edtpost.getText().toString()),user.getLatitude(),user.getLongitude(),
                                images,videos,tagplace,selectedinterest,tagpeople,tagpeopleid,tagplaceid ),postlister,post_error_listener));
                LogUtils.e("ALL Image uploaded "+imageUrls.toString());
            }else
            {
                new UploadVideo().execute(videopath.get(uploadvideosize-1));
            }
          /*  mProfilepicurl = s;
            ProfilePicUpdate(mProfilepicurl);*/
        }
    }
    public static String getFileName(String filename) {
        int pos = filename.lastIndexOf(".");
        String justName = pos > 0 ? filename.substring(0, pos) : filename;
        return justName;
    }
    Response.Listener postlister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {

            onLoadDismiss();
            Log.e("FBLogin Response", "" + response);

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {

                  /*  showInfo(login.getString(MESSAGE),CreatePostActivity.this,CreatePostActivity.this);
                    Utils.showToast(login.getString(MESSAGE),CreatePostActivity.this);*/
                    if (login.getString(STATUS_JSON).equals("true")) {
                        int title = R.string.app_name;

                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CreatePostActivity.this);

                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_warning, null);
                        dialogBuilder.setView(dialogView);

                        TextView txt_title = (TextView) dialogView.findViewById(R.id.txt_title);
                        Button btn_ok = (Button)dialogView.findViewById(R.id.btn_ok);
                        btn_ok.setText(getString(R.string.ok));
                        TextView txt_content = (TextView) dialogView.findViewById(R.id.txt_content);
                        txt_title.setText(getString(title));
                        txt_content.setText(login.getString(MESSAGE));
                        final AlertDialog b = dialogBuilder.create();
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                b.dismiss();

                                finish();
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
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Response.ErrorListener post_error_listener = new Response.ErrorListener() {
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
    public void CallBackListener(ClosingCallActivity mCallback) {
        LogUtils.e("I am coming callback");
        this.callback = mCallback;
        LogUtils.e("Object "+callback);
    }

    public interface ClosingCallActivity
    {
        public void onClosecall();
    }
}
