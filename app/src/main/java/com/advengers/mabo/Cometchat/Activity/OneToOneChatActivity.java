package com.advengers.mabo.Cometchat.Activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Utils.LogUtils;
import com.asksira.bsimagepicker.BSImagePicker;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.advengers.mabo.Cometchat.Adapter.OneToOneAdapter;
import com.advengers.mabo.Cometchat.Contracts.OneToOneActivityContract;
import com.advengers.mabo.Cometchat.Contracts.StringContract;
import com.advengers.mabo.Cometchat.CustomView.AttachmentTypeSelector;
import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.Cometchat.CustomView.RecordAudio;
import com.advengers.mabo.Cometchat.CustomView.RecordMicButton;
import com.advengers.mabo.Cometchat.CustomView.StickyHeaderDecoration;
import com.advengers.mabo.Cometchat.Helper.AttachmentHelper;
import com.advengers.mabo.Cometchat.Helper.CCPermissionHelper;
import com.advengers.mabo.Cometchat.Helper.OnTopReachedListener;
import com.advengers.mabo.Cometchat.Helper.RecordListener;
import com.advengers.mabo.Cometchat.Helper.RecyclerTouchListener;
import com.advengers.mabo.Cometchat.Presenters.OneToOneActivityPresenter;
import com.advengers.mabo.R;
import com.advengers.mabo.Cometchat.Utils.AnimUtil;
import com.advengers.mabo.Cometchat.Utils.ColorUtils;
import com.advengers.mabo.Cometchat.Utils.DateUtils;
import com.advengers.mabo.Cometchat.Utils.FileUtils;
import com.advengers.mabo.Cometchat.Utils.FontUtils;
import com.advengers.mabo.Cometchat.Utils.KeyboardVisibilityEvent;
import com.advengers.mabo.Cometchat.Utils.Logger;
import com.advengers.mabo.Cometchat.Utils.MediaUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class OneToOneChatActivity extends AppCompatActivity implements OneToOneActivityContract.OneToOneView, View.OnClickListener, TextWatcher, ActionMode.Callback,  BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate {

    private static final int LIMIT = 30;

    public static final int RECORD_CODE = 22;

    private RecyclerView messageRecyclerView;

    private String ownerUid;

    private Toolbar toolbar;

    private EditText messageField;

    private int messageCount = -1;

    private LinearLayoutManager linearLayoutManager;

    private TextView toolbarTitle, toolbarSubTitle;

    private Button btnScroll;

    private long newMessageCount;

    private ImageButton ivAttchament;

    private Button sendButton;

    private OneToOneAdapter oneToOneAdapter;

    private AttachmentTypeSelector attachmentTypeSelector;

    private CircleImageView contactPic;

    private Animation viewAniamtion, goneAnimation;

    private OneToOneActivityContract.OneToOnePresenter oneToOnePresenter;

    private RelativeLayout rlTitleContainer;

    private String contactUid;

    private MediaRecorder mediaRecorder;

    private static final String TAG = "OneToOneChatActivity";

    private String userStatus;

    private RecordMicButton recordMicButton;

    private RecordAudio recordAudioLayout;

    public static String[] RECORD_PERMISSION = {CCPermissionHelper.REQUEST_PERMISSION_RECORD_AUDIO,
            CCPermissionHelper.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE};

    String[] CAMERA_PERMISSION = {CCPermissionHelper.REQUEST_PERMISSION_CAMERA,
            CCPermissionHelper.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE};

    private String audioFileNamewithPath;

    private String avatar;

    private String contactName;

    public static boolean isReply;

    public static JSONObject metaData;

    private BaseMessage baseMessage;

    private  static RelativeLayout rlMain;

    private static RelativeLayout rlReplyContainer;

    private TextView tvNameReply;

    private TextView tvTextMessage;

    private ImageView ivReplyImage;

    public static  String contactId;

    private User user;

    private Timer timer=new Timer();

    private TextView tvBanner;

    private RelativeLayout rvBanner;

    private boolean isEditMessage;

    private MenuItem searchItem;

    private SearchView searchView;

    private AlertDialog.Builder alertDialog;

    public static final int RC_PHOTO_PICKER_PERM = 123;
    public static final int RC_FILE_PICKER_PERM = 321;
    private static final int CUSTOM_REQUEST_CODE = 532;
    private int MAX_ATTACHMENT_COUNT = 10;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);
        attachmentTypeSelector = null;
        new FontUtils(this);
        oneToOnePresenter = new OneToOneActivityPresenter();
        oneToOnePresenter.attach(this);
        initViewComponent();

    }

    private void initViewComponent() {


        ivAttchament = findViewById(R.id.iv_attchment);
        ivAttchament.setOnClickListener(this);
        sendButton = findViewById(R.id.buttonSendMessage);
        sendButton.setOnClickListener(this);

        recordMicButton = findViewById(R.id.record_button);
        recordAudioLayout = findViewById(R.id.record_audio_view);
        tvBanner=findViewById(R.id.tvBlock);
        rvBanner=findViewById(R.id.blockBanner);

        rvBanner.setOnClickListener(this);

        //set recordAudioview
        recordMicButton.setListenForRecord(true, this);
        recordAudioLayout.setCancelOffset(8);
        recordAudioLayout.setLessThanSecondAllowed(false);
        recordAudioLayout.setSlideToCancelText(getString(R.string.slide_to_cancel));
        recordAudioLayout.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);
        recordMicButton.setRecordAudio(recordAudioLayout);
        setRecordListener();

        messageField = findViewById(R.id.editTextChatMessage);
        messageField.addTextChangedListener(this);
        messageField.setTypeface(FontUtils.openSansRegular);

        messageRecyclerView = findViewById(R.id.rvChatMessages);

        btnScroll = findViewById(R.id.btn_new_message);
        btnScroll.setOnClickListener(this);

        rlReplyContainer = findViewById(R.id.rlReply);
        tvNameReply = findViewById(R.id.tvNameReply);
        tvTextMessage = findViewById(R.id.tvTextMessage);
        ivReplyImage = findViewById(R.id.ivReplyImage);
        rlMain=findViewById(R.id.rl_main);
        ImageView ivClose = findViewById(R.id.ivClose);

        linearLayoutManager = new LinearLayoutManager(this);


        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.getItemAnimator().setChangeDuration(0);

        toolbar = findViewById(R.id.cometchat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        rlTitleContainer = findViewById(R.id.rl_titlecontainer);
        rlTitleContainer.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        contactPic = findViewById(R.id.contact_pic);

        toolbarTitle = findViewById(R.id.title);
        toolbarSubTitle = findViewById(R.id.subTitle);

        toolbarTitle.setTypeface(FontUtils.robotoMedium);
        toolbarSubTitle.setTypeface(FontUtils.robotoRegular);
        toolbarSubTitle.setSelected(true);

        viewAniamtion = AnimationUtils.loadAnimation(this, R.anim.animate);
        goneAnimation = AnimationUtils.loadAnimation(this, R.anim.gone_animation);

        oneToOnePresenter.setContext(this);
        oneToOnePresenter.getOwnerDetail();
        oneToOnePresenter.handleIntent(getIntent());

        new Thread(() -> oneToOnePresenter.fetchPreviousMessage(contactUid, LIMIT)).start();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            setScrollListener();
//
//        } else {
//            messageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//
//                    if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
//                        Logger.error("slow scroll");
//                        oneToOnePresenter.fetchPreviousMessage(contactUid, LIMIT);
//                    }
//
//                    //for toolbar elevation animation i.e stateListAnimator
//                    toolbar.setSelected(messageRecyclerView.canScrollVertically(-1));
//
//
//                }
//            });
//        }



        KeyboardVisibilityEvent.setEventListener(this, var1 -> {
            if (messageCount - linearLayoutManager.findLastVisibleItemPosition() < 5) {

                if (oneToOneAdapter != null) {
                    messageRecyclerView.scrollToPosition(oneToOneAdapter.getItemCount() - 1);
                }
            }
        });

        messageRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                messageRecyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View var1, int var2) {

            }

            @Override
            public void onLongClick(View var1, int var2) {
                baseMessage = (BaseMessage) var1.getTag(R.string.message);
                toolbar.startActionMode(OneToOneChatActivity.this);



            }
        }));

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setScrollListener() {

        messageRecyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            int temp = linearLayoutManager.findFirstVisibleItemPosition();

            if (temp < 5 ) {
                Logger.error("Fast scroll");
                oneToOnePresenter.fetchPreviousMessage(contactUid, LIMIT);

            }

            toolbar.setSelected(messageRecyclerView.canScrollVertically(-1));


            if (messageCount - linearLayoutManager.findLastVisibleItemPosition() < 5) {
                if (btnScroll.getVisibility() == View.VISIBLE) {
                    btnScroll.startAnimation(goneAnimation);
                    btnScroll.setVisibility(View.GONE);

                }

            } else {
                if (btnScroll.getVisibility() == View.GONE) {
                    btnScroll.startAnimation(viewAniamtion);
                    btnScroll.setVisibility(View.VISIBLE);
                }
            }

            if (messageCount - 2 == linearLayoutManager.findLastVisibleItemPosition()) {
                newMessageCount = 0;
                btnScroll.setText(getString(R.string.jump_to_latest));
                btnScroll.getBackground().setColorFilter(Color.parseColor("#8e8e92"), PorterDuff.Mode.SRC_ATOP);
            }
        });

    }


    private void setRecordListener() {
        recordAudioLayout.setOnRecordListener(new RecordListener() {
            @Override
            public void onStart() {

                Logger.error(TAG, "onStart: " + " record start");
                messageField.setHint("");
                startRecording();
            }

            @Override
            public void onCancel() {

                Logger.error(TAG, "onCancel: " + "record cancel");

                messageField.setHint(getString(R.string.message_hint));
                stopRecording(true);
            }

            @Override
            public void onFinish(long time) {
                Logger.error(TAG, "onFinish: " + "record finish");

                messageField.setHint(getString(R.string.message_hint));
                File audioFile;
                if (audioFileNamewithPath != null) {
                    audioFile = new File(audioFileNamewithPath);
                    Logger.error("audioFileNamewithPath", audioFileNamewithPath);
                    oneToOnePresenter.sendMediaMessage(audioFile, contactUid, CometChatConstants.MESSAGE_TYPE_AUDIO);
                }

                stopRecording(false);
            }

            @Override
            public void onLessTime() {
                Logger.error(TAG, "onLessTime: ");
                messageField.setHint(getString(R.string.message_hint));
                stopRecording(true);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.error(TAG, "onDestroy: ");
        oneToOnePresenter.detach();
        timer=null;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_to_one_chat_menu, menu);


        searchItem=menu.findItem(R.id.app_bar_search);

        SearchManager searchManager=((SearchManager)getSystemService(Context.SEARCH_SERVICE));

        if (searchItem!=null){

            searchView=((SearchView)searchItem.getActionView());

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    oneToOnePresenter.searchMessage(s,contactUid);
                    return false;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    oneToOnePresenter.fetchPreviousMessage(contactUid,30);
                    return false;
                }
            });
        }

        if (searchView!=null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_custom_action_video_call:
                if (user.isBlockedByMe()){
                    showDialog("Blocked User","In order to make video call please unblock "+user.getName());
                }else {
                    oneToOnePresenter.sendCallRequest(this, contactUid, CometChatConstants.RECEIVER_TYPE_USER, CometChatConstants.CALL_TYPE_VIDEO);
                }
                break;

            case R.id.menu_custom_action_audio_call:
                if (user.isBlockedByMe()){
                    showDialog("Blocked User","In order to make audio call please unblock "+user.getName());
                }else {
                    oneToOnePresenter.sendCallRequest(this, contactUid, CometChatConstants.RECEIVER_TYPE_USER, CometChatConstants.CALL_TYPE_AUDIO);
                }
                break;

            case R.id.menu_block_user:
                oneToOnePresenter.blockUser(contactId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(String title, String message){
        alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Unblock", (dialog, which) -> {
            oneToOnePresenter.unBlockUser(user.getUid(),OneToOneChatActivity.this);
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.show();
    }

    private void addAttachment(int type) {

        String[] STORAGE_PERMISSION = {CCPermissionHelper.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE};
        String[] CAMERA_PERMISSION = {CCPermissionHelper.REQUEST_PERMISSION_CAMERA, CCPermissionHelper.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE};
        switch (type) {
            case StringContract.RequestCode.ADD_GALLERY:

                if (CCPermissionHelper.hasPermissions(this, CCPermissionHelper.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                 //   AttachmentHelper.selectMedia(this, "*/*", StringContract.IntentStrings.EXTRA_MIME_TYPE, StringContract.RequestCode.ADD_GALLERY);
                    FilePickerBuilder.getInstance().setMaxCount(5)
                            .setSelectedFiles(photoPaths)
                            .enableCameraSupport(false)
                            .setActivityTheme(R.style.LibAppTheme)
                            .pickPhoto(this,StringContract.RequestCode.ADD_GALLERY);
                  /*  BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                            .setMaximumDisplayingImages(Integer.MAX_VALUE)
                            .isMultiSelect()
                            .setMinimumMultiSelectCount(1)
                            .setMaximumMultiSelectCount(3)
                            .build();*/
                  //  pickerDialog.show(getSupportFragmentManager(), "picker");
                } else {
                    CCPermissionHelper.requestPermissions(this, STORAGE_PERMISSION, StringContract.RequestCode.ADD_GALLERY);
                }
                break;
            case StringContract.RequestCode.ADD_DOCUMENT:

                if (CCPermissionHelper.hasPermissions(this, CCPermissionHelper.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                  //  AttachmentHelper.selectMedia(this, StringContract.IntentStrings.DOCUMENT_TYPE,
                    //        StringContract.IntentStrings.EXTRA_MIME_DOC, StringContract.RequestCode.ADD_DOCUMENT);
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setSelectedFiles(docPaths)
                            .setActivityTheme(R.style.LibAppTheme)
                            .pickFile(this, StringContract.RequestCode.ADD_DOCUMENT);
                } else {
                    CCPermissionHelper.requestPermissions(this, STORAGE_PERMISSION, StringContract.RequestCode.ADD_DOCUMENT);
                }
                break;
            case StringContract.RequestCode.ADD_SOUND:
                if (CCPermissionHelper.hasPermissions(this, CCPermissionHelper.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                    AttachmentHelper.selectMedia(this, StringContract.IntentStrings.AUDIO_TYPE, null, StringContract.RequestCode.ADD_SOUND);
                   /* Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, StringContract.RequestCode.ADD_SOUND);*/
                } else {
                    CCPermissionHelper.requestPermissions(this, STORAGE_PERMISSION, StringContract.RequestCode.ADD_SOUND);
                }
                break;

            case StringContract.RequestCode.TAKE_PHOTO:
                if (CCPermissionHelper.hasPermissions(this, CAMERA_PERMISSION)) {
                    AttachmentHelper.captureImage(OneToOneChatActivity.this, StringContract.RequestCode.TAKE_PHOTO);
                } else {
                    CCPermissionHelper.requestPermissions(this, CAMERA_PERMISSION, StringContract.RequestCode.TAKE_PHOTO);
                }

                break;
            case StringContract.RequestCode.TAKE_VIDEO:
                if (CCPermissionHelper.hasPermissions(this, CAMERA_PERMISSION)) {
                    AttachmentHelper.captureVideo(this, StringContract.RequestCode.TAKE_VIDEO);
                } else {
                    CCPermissionHelper.requestPermissions(this, CAMERA_PERMISSION, StringContract.RequestCode.TAKE_VIDEO);
                }
                break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case StringContract.RequestCode.ADD_DOCUMENT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //    AttachmentHelper.selectMedia(this, StringContract.IntentStrings.DOCUMENT_TYPE,
                  //          null, StringContract.RequestCode.ADD_DOCUMENT);
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setSelectedFiles(docPaths)
                            .setActivityTheme(R.style.LibAppTheme)
                            .pickFile(this, StringContract.RequestCode.ADD_DOCUMENT);
                } else {
                    showToast();
                }
                break;

            case StringContract.RequestCode.ADD_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

           //   AttachmentHelper.selectMedia(this, "*/*",
             //               StringContract.IntentStrings.EXTRA_MIME_TYPE, StringContract.RequestCode.ADD_GALLERY);
                    FilePickerBuilder.getInstance().setMaxCount(5)
                            .setSelectedFiles(photoPaths)
                            .enableCameraSupport(false)
                            .setActivityTheme(R.style.LibAppTheme)
                            .pickPhoto(this,StringContract.RequestCode.ADD_GALLERY);
                  /*  BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                            .setMaximumDisplayingImages(Integer.MAX_VALUE)
                            .isMultiSelect()
                            .setMinimumMultiSelectCount(1)
                            .setMaximumMultiSelectCount(3)
                            .build();
                    pickerDialog.show(getSupportFragmentManager(), "picker");*/
                } else {
                    showToast();
                }
                break;

            case StringContract.RequestCode.TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    AttachmentHelper.captureImage(OneToOneChatActivity.this, StringContract.RequestCode.TAKE_PHOTO);
                } else {
                    showToast();
                }
                break;

            case StringContract.RequestCode.TAKE_VIDEO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    AttachmentHelper.captureVideo(this, StringContract.RequestCode.TAKE_VIDEO);
                } else {
                    showToast();
                }
                break;

            case RECORD_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    showToast();
                }

                break;

            case StringContract.RequestCode.ADD_SOUND:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AttachmentHelper.selectMedia(this, StringContract.IntentStrings.AUDIO_TYPE, null, StringContract.RequestCode.ADD_SOUND);
                  /*  Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, StringContract.RequestCode.ADD_SOUND);*/
                } else {
                    showToast();
                }
                break;

            case StringContract.RequestCode.FILE_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    FileUtils.makeDirectory(this, CometChatConstants.MESSAGE_TYPE_AUDIO);
                } else {
                    showToast();
                }
                break;

            case StringContract.RequestCode.READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    showToast();
                }
                break;


        }
    }

    private void startRecording() {
        try {

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
           // mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioFileNamewithPath = FileUtils.getOutputMediaFile(OneToOneChatActivity.this);
            mediaRecorder.setOutputFile(audioFileNamewithPath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioSamplingRate(256);
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording(boolean isCancel) {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                if (isCancel) {
                    new File(audioFileNamewithPath).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.error(TAG, "onResume: ");
        oneToOnePresenter.addPresenceListener(getString(R.string.presenceListener));
        oneToOnePresenter.addMessageReceiveListener(contactUid);
        if (oneToOneAdapter!=null)
            oneToOnePresenter.refreshList(contactUid,LIMIT);
        oneToOnePresenter.addCallEventListener(TAG);
        App.activityResumed(contactUid);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Logger.error(TAG, "onPause: ");
        if (oneToOneAdapter!=null) {
            oneToOneAdapter.stopPlayer();
        }
        timer();
        oneToOnePresenter.removeMessageLisenter(getString(R.string.message_listener));
        oneToOnePresenter.removePresenceListener(getString(R.string.presenceListener));
        oneToOnePresenter.removeCallListener(TAG);
        App.activityPaused(contactUid);
    }


    @Override
    protected void onStop() {
        super.onStop();

        stopRecording(false);
        timer();
        Logger.error(TAG, "onStop:");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LogUtils.e("onActivityResult before"+data);
        if (resultCode == RESULT_OK && data != null && oneToOnePresenter != null) {
          //  LogUtils.e("onActivityResult "+data.getData());
            switch (requestCode) {
                case StringContract.RequestCode.ADD_GALLERY:
                    photoPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    for(int i=0;i<photoPaths.size();i++)
                    AttachmentHelper.handlefileUri(this, "*/*", oneToOnePresenter, Uri.parse(photoPaths.get(i)), contactUid);
                    break;
                case StringContract.RequestCode.TAKE_PHOTO:
                    LogUtils.e("Take Photo "+data);
                    AttachmentHelper.handleCameraImage(this, oneToOnePresenter, data, contactUid);
                    break;
                case StringContract.RequestCode.TAKE_VIDEO:
                    AttachmentHelper.handleCameraVideo(this, oneToOnePresenter, data, contactUid);
                    break;
                case StringContract.RequestCode.ADD_SOUND:
                    AttachmentHelper.handlefile(OneToOneChatActivity.this, CometChatConstants.MESSAGE_TYPE_AUDIO, oneToOnePresenter, data, contactUid);
                    break;
                case StringContract.RequestCode.ADD_DOCUMENT:
                    docPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                  //  LogUtils.e();
                    for(int i=0;i<docPaths.size();i++)
                    {
                        AttachmentHelper.handlefileUri(OneToOneChatActivity.this, CometChatConstants.MESSAGE_TYPE_FILE, oneToOnePresenter, Uri.parse(docPaths.get(i)), contactUid);
                    }
                    break;
            }
        }
    }

    private void showToast() {
        Toast.makeText(this, "PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void setAdapter(List<BaseMessage> messageArrayList) {

        if (oneToOneAdapter != null) {
            messageCount = oneToOneAdapter.getItemCount() - 1;
        } else {
            messageCount = 0;
        }
        if (oneToOneAdapter == null) {
            oneToOneAdapter = new OneToOneAdapter(this, messageArrayList, ownerUid);
            oneToOneAdapter.setHasStableIds(true);
           /* oneToOneAdapter.setTopReachedListener(new OnTopReachedListener() {
                @Override
                public void onTopReached(int pos) {
                    Log.e(TAG, "onTopReached: " + pos);
                    oneToOnePresenter.fetchPreviousMessage(contactUid, LIMIT);
                    messageRecyclerView.scrollToPosition(LIMIT);
                }
            });*/
            messageRecyclerView.setAdapter(oneToOneAdapter);
            StickyHeaderDecoration decor = new StickyHeaderDecoration(oneToOneAdapter);
            messageRecyclerView.addItemDecoration(decor);
            if (oneToOneAdapter.getItemCount() != 0) {
                messageRecyclerView.scrollToPosition(oneToOneAdapter.getItemCount() - 1);
            }
        } else if (messageArrayList != null && messageArrayList.size() != 0) {
            oneToOneAdapter.refreshData(messageArrayList);
        }

    }

    @Override
    public void addMessage(BaseMessage newMessage) {

        if (oneToOneAdapter != null) {
            newMessageCount++;
            oneToOneAdapter.addMessage(newMessage);

            if ((oneToOneAdapter.getItemCount() - 1) - linearLayoutManager.findLastVisibleItemPosition() < 5) {
                messageRecyclerView.scrollToPosition(oneToOneAdapter.getItemCount() - 1);
                newMessageCount = 0;
            } else {
                btnScroll.setVisibility(View.VISIBLE);
                btnScroll.setText(newMessageCount + " " + getString(R.string.new_message));
                AnimUtil.getShakeAnimation(btnScroll);
                btnScroll.getBackground().setColorFilter(getResources().getColor(R.color.secondaryColor), PorterDuff.Mode.SRC_ATOP);

            }
        }

    }

    @Override
    public void setOwnerDetail(User user) {
        ownerUid = user.getUid();
    }

    @Override
    public void setTitle(String name) {
        toolbarTitle.setText(name);
        contactName = name;
    }


    @Override
    public void addSendMessage(BaseMessage baseMessage) {
        if(oneToOneAdapter==null)
        {
            List<BaseMessage> list=new ArrayList<>();
            list.add(baseMessage);
            setAdapter(list);
        }
        if (oneToOneAdapter != null) {
            oneToOneAdapter.addMessage(baseMessage);
            if (btnScroll.getVisibility() == View.GONE) {
                messageRecyclerView.scrollToPosition(oneToOneAdapter.getItemCount() - 1);
            }
        }
        else
        {
            oneToOneAdapter = new OneToOneAdapter(OneToOneChatActivity.this,ownerUid);
            oneToOneAdapter.setHasStableIds(true);
            messageRecyclerView.setAdapter(oneToOneAdapter);
            StickyHeaderDecoration decor = new StickyHeaderDecoration(oneToOneAdapter);
            messageRecyclerView.addItemDecoration(decor);
            oneToOneAdapter.addMessage(baseMessage);
        }
    }

    @Override
    public void setContactUid(String stringExtra) {
        contactUid = stringExtra;
    }

    @Override
    public void setAvatar(String avatar) {
        if (avatar != null) {
            this.avatar = avatar;
            oneToOnePresenter.setContactPic(OneToOneChatActivity.this, avatar, contactPic);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.default_avatar);

            try {
                Bitmap bitmap = MediaUtils.getPlaceholderImage(this, drawable);
                contactPic.setCircleBackgroundColor(ColorUtils.getMaterialColor(this));
                contactPic.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setPresence(User user) {

        if (user != null && user.getUid().equals(contactUid)) {
            this.user=user;
            if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
                userStatus = user.getStatus();
            } else if (user.getStatus().equals(CometChatConstants.USER_STATUS_OFFLINE)) {
                Log.e(TAG, "setPresence: "+user+"\nlastactive:"+DateUtils.getTimeStringFromTimestamp(user.getLastActiveAt(),"dd/MM hh:mm"));
                userStatus = DateUtils.getTimeStringFromTimestamp(user.getLastActiveAt(), "dd/MM/yyyy hh:mm a");
            }

            toolbarSubTitle.setText(userStatus);
        }
        if (user!=null&&user.isBlockedByMe()){
                rvBanner.setVisibility(View.VISIBLE);
                tvBanner.setText("Tab to unblock "+user.getName());
        }
    }

    @Override
    public void setTyping() {

        toolbarSubTitle.setText(getString(R.string.typing));
    }

    @Override
    public void endTyping() {

        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
            userStatus = user.getStatus();
        } else if (user.getStatus().equals(CometChatConstants.USER_STATUS_OFFLINE)) {
            userStatus = DateUtils.getLastSeenDate(user.getLastActiveAt(), this);
        }
        toolbarSubTitle.setText(userStatus);
    }

    @Override
    public void setMessageDelivered(MessageReceipt messageReceipt) {
        if (oneToOneAdapter!=null)
        oneToOneAdapter.Delivered(messageReceipt);
    }

    @Override
    public void onMessageRead(MessageReceipt messageReceipt) {
        if (oneToOneAdapter!=null)
            oneToOneAdapter.setRead(messageReceipt);
    }

    @Override
    public void hideBanner() {
        rvBanner.setVisibility(View.GONE);
        if (alertDialog!=null){
            alertDialog.setOnDismissListener(DialogInterface::dismiss);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_attchment:
                showPopUp();
                break;

            case R.id.buttonSendMessage:

                String message = messageField.getText().toString().trim();

                if (!TextUtils.isEmpty(message)&&!isEditMessage) {
                    oneToOnePresenter.endTypingIndicator(contactUid);
                    oneToOnePresenter.sendMessage(message, contactUid);
                    messageField.setText("");
                }
                else if (isEditMessage&&!TextUtils.isEmpty(message)){
                    isEditMessage=false;
                    oneToOnePresenter.editMessage(baseMessage,message);
                    messageField.setText("");
                }

                break;

            case R.id.blockBanner:
                  oneToOnePresenter.unBlockUser(user.getUid(),OneToOneChatActivity.this);
                break;

            case R.id.rl_titlecontainer:
                Intent intent = new Intent(this, UsersProfileViewActivity.class);
                intent.putExtra(StringContract.IntentStrings.USER_ID, contactUid);
                intent.putExtra(StringContract.IntentStrings.USER_AVATAR, avatar);
                intent.putExtra(StringContract.IntentStrings.USER_NAME, contactName);
                startActivity(intent);
                break;

            case R.id.btn_new_message:
                newMessageCount = 0;
                messageRecyclerView.scrollToPosition(oneToOneAdapter.getItemCount() - 1);
                btnScroll.startAnimation(goneAnimation);
                btnScroll.setVisibility(View.GONE);
                break;

            case R.id.ivClose:
                hideReply();
                break;
        }
    }


    public static void hideReply(){

        metaData=null;
        isReply=false;
        rlReplyContainer.setVisibility(View.GONE);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        int len = messageField.getText().toString().length();

        if (len > 0) {
            sendButton.setTextColor(getResources().getColor(R.color.secondaryDarkColor));
             oneToOnePresenter.sendTypingIndicator(contactUid);

        } else {
            sendButton.setTextColor(getResources().getColor(R.color.secondaryTextColor));
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (timer!=null){
            timer();
        }
        else {
            timer=new Timer();
            timer();
        }

    }

    private void timer(){

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                oneToOnePresenter.endTypingIndicator(contactUid);
            }
        },2000);
    }

    private void showPopUp() {

        if (attachmentTypeSelector == null) {
            attachmentTypeSelector =
                    new AttachmentTypeSelector(OneToOneChatActivity.this,
                            new AttachmentTypeListener());
        }
        attachmentTypeSelector.show(OneToOneChatActivity.this, ivAttchament);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        getMenuInflater().inflate(R.menu.action_mode, menu);

        mode.setTitle(contactName);
        menu.findItem(R.id.info).setVisible(false);
        if (menu!=null&&baseMessage!=null){
            if (!baseMessage.getSender().getUid().equals(ownerUid)) {
                menu.findItem(R.id.delete).setVisible(false);
            }

            if (!baseMessage.getSender().getUid().equals(ownerUid) && baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)){
                 menu.findItem(R.id.edit).setVisible(false);
            }


        }

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void setDeletedMessage(BaseMessage baseMessage) {
         if (oneToOneAdapter!=null){
            oneToOneAdapter.deleteMessage(baseMessage);
         }
    }

    @Override
    public void setEditedMessage(BaseMessage baseMessage) {
        if (oneToOneAdapter!=null){
            oneToOneAdapter.setEditMessage(baseMessage);
        }
    }

    @Override
    public void setFilterList(List<BaseMessage> list) {
          if (oneToOneAdapter!=null){
           oneToOneAdapter.setFilterList(list);
          }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


        mode.finish();

        switch (item.getItemId()) {

            case R.id.delete:
               oneToOnePresenter.deleteMessage(baseMessage);
                break;
            case R.id.edit:
                TextMessage textMessage;
                    isEditMessage=true;
                  if (baseMessage instanceof TextMessage){
                      textMessage=(TextMessage)baseMessage;
                      messageField.setText(textMessage.getText());
                  }
                break;
        }

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mode = null;
    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
        Picasso.get().load(imageFile).into(ivImage);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        LogUtils.e(uriList.toString());
        for(int i=0;i<uriList.size();i++)
        {
            LogUtils.e(uriList.get(i).toString());
            AttachmentHelper.handlefileUri(this, "*/*", oneToOnePresenter, uriList.get(i), contactUid);
        }
    }

    private class AttachmentTypeListener implements AttachmentTypeSelector.AttachmentClickedListener {
        @Override
        public void onClick(int type) {
            addAttachment(type);
        }

    }


}
