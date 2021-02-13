package screen.messagelist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.uikit.R;

import java.io.File;
import java.util.List;

import adapter.MessageAdapter;
import constant.StringContract;
import listeners.MessageActionCloseListener;
import listeners.OnMessageLongClick;
import com.cometchat.pro.uikit.Settings.UISettings;

/**

* Purpose - CometChatMessageListActivity.class is a Activity used to display messages using CometChatMessageScreen.class. It takes
            parameter like TYPE to differentiate between User MessageScreen & Group MessageScreen.

            It passes parameters like UID (userID) ,AVATAR (userAvatar) ,NAME (userName) ,STATUS (userStatus) to CometChatMessageScreen.class
            if TYPE is CometChatConstant.RECEIVER_TYPE_USER

            It passes parameters like GUID (groupID) ,AVATAR (groupIcon) ,NAME (groupName) ,GROUP_OWNER (groupOwner) to CometChatMessageScreen.class
            if TYPE is CometChatConstant.RECEIVER_TYPE_GROUP

            @see CometChatConstants
            @see CometChatMessageScreen


*/

public class CometChatMessageListActivity extends AppCompatActivity implements MessageAdapter.OnMessageLongClick {

    private static final String TAG = "CometChatMessageListAct";

    private OnMessageLongClick messageLongClick;
    public static String ID = "";

    Fragment fragment = new CometChatMessageScreen();
    public static boolean visibity = false;
    public static File folder_image,folder_videos,folder_videosent,folder_imagesent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cometchat_message_list);
        if (UISettings.getColor() !=null) {
            getWindow().setStatusBarColor(Color.parseColor(UISettings.getColor()));
        }

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);



         if (getIntent()!=null) {
             Bundle bundle = new Bundle();

             bundle.putString(StringContract.IntentStrings.AVATAR, getIntent().getStringExtra(StringContract.IntentStrings.AVATAR));
             bundle.putString(StringContract.IntentStrings.NAME, getIntent().getStringExtra(StringContract.IntentStrings.NAME));
             bundle.putString(StringContract.IntentStrings.TYPE,getIntent().getStringExtra(StringContract.IntentStrings.TYPE));

              if (getIntent().hasExtra(StringContract.IntentStrings.TYPE)&&
                      getIntent().getStringExtra(StringContract.IntentStrings.TYPE).equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                  ID = getIntent().getStringExtra(StringContract.IntentStrings.UID);
                  bundle.putString(StringContract.IntentStrings.UID, getIntent().getStringExtra(StringContract.IntentStrings.UID));
                  bundle.putString(StringContract.IntentStrings.STATUS, getIntent().getStringExtra(StringContract.IntentStrings.STATUS));

              }else {
                  ID = getIntent().getStringExtra(StringContract.IntentStrings.GUID);
                  bundle.putString(StringContract.IntentStrings.GUID, getIntent().getStringExtra(StringContract.IntentStrings.GUID));
                  bundle.putString(StringContract.IntentStrings.GROUP_OWNER,getIntent().getStringExtra(StringContract.IntentStrings.GROUP_OWNER));
                  bundle.putInt(StringContract.IntentStrings.MEMBER_COUNT,getIntent().getIntExtra(StringContract.IntentStrings.MEMBER_COUNT,0));
                  bundle.putString(StringContract.IntentStrings.GROUP_TYPE,getIntent().getStringExtra(StringContract.IntentStrings.GROUP_TYPE));
                  bundle.putString(StringContract.IntentStrings.GROUP_DESC,getIntent().getStringExtra(StringContract.IntentStrings.GROUP_DESC));
                  bundle.putString(StringContract.IntentStrings.GROUP_PASSWORD,getIntent().getStringExtra(StringContract.IntentStrings.GROUP_PASSWORD));
              }
              fragment.setArguments(bundle);
              onMakeDirectory();
             getSupportFragmentManager().beginTransaction().replace(R.id.ChatFragment, fragment).commit();
         }
    }

    void onMakeDirectory()
    {
         folder_image = new File(Environment.getExternalStorageDirectory() +
                File.separator + getString(R.string.app_name)+ File.separator+getString(R.string.app_name)+"_images");
         folder_videos = new File(Environment.getExternalStorageDirectory() +
                File.separator + getString(R.string.app_name)+File.separator+getString(R.string.app_name)+"_videos");
         folder_videosent = new File(Environment.getExternalStorageDirectory() +
                 File.separator + getString(R.string.app_name)+File.separator+getString(R.string.app_name)+"_videos/sent");
         folder_imagesent = new File(Environment.getExternalStorageDirectory() +
            File.separator + getString(R.string.app_name)+File.separator+getString(R.string.app_name)+"_images/sent");
        boolean success = true;
        if (!folder_image.exists()) {
            success = folder_image.mkdirs();
        }

        if (!folder_videos.exists()) {
            success = folder_videos.mkdirs();
        }
        if (!folder_videosent.exists()) {
            success = folder_videosent.mkdirs();
        }
        if (!folder_imagesent.exists()) {
            success = folder_imagesent.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "onActivityResult: ");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLongMessageClick(List<BaseMessage> baseMessage) {
        if (fragment!=null)
        ((OnMessageLongClick)fragment).setLongMessageClick(baseMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        visibity = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        visibity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void handleDialogClose(DialogInterface dialog) {
        ((MessageActionCloseListener)fragment).handleDialogClose(dialog);
        dialog.dismiss();
    }
}