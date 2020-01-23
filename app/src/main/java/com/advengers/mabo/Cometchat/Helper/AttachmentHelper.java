package com.advengers.mabo.Cometchat.Helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.advengers.mabo.Utils.LogUtils;
import com.cometchat.pro.constants.CometChatConstants;
import com.advengers.mabo.Cometchat.Presenters.GroupChatPresenter;
import com.advengers.mabo.Cometchat.Presenters.OneToOneActivityPresenter;
import com.advengers.mabo.Cometchat.Utils.FileUtils;
import com.advengers.mabo.Cometchat.Utils.Logger;
import com.advengers.mabo.Cometchat.Utils.MediaUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AttachmentHelper {


    private static final String TAG = "AttachmentHelper";

    public static String pictureImagePath = "";

    public static void selectMedia(Activity activity,
                                   String Type, String[] extraMimeType, int requestCode) {

        final Intent intent = new Intent();
        intent.setType(Type);

//        intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeType);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        try {
            activity.startActivityForResult(intent, requestCode);

        } catch (ActivityNotFoundException anfe) {
            Logger.debug("couldn't complete ACTION_OPEN_DOCUMENT, no activity found. falling back.");
        }
    }

    public static <P> void handleGalleryMedia(Context context, P presenter, Intent data, String contactId) {

        String type = null;
        File file = null;
        if (FileUtils.getMimeType(context, data.getData()).toLowerCase().contains("image")) {
            try {
                type = CometChatConstants.MESSAGE_TYPE_IMAGE;
                String filePath = FileUtils.getImageFilePath(data, context);

                if (filePath != null) {
                    file = new File(filePath);
                    Logger.error("handleGalleryMedia", "ACTION_SEND imagefile.exists() ? = " + file.exists() + " filepath : " + filePath);
                    if (file.exists()) {
                        sendMedia(file, contactId, type, presenter);
                    }
                } else {
                    filePath = data.getData().toString()
                            .replace("file://", "").
                                    replace("%20", " ");
                    file = new File(filePath);
                    Logger.error("handleGalleryMedia", "ACTION_SEND imagefile.exists() 2 ? = " + file.exists() + " filepath : " + filePath);
                    if (file.exists()) {
                        sendMedia(file, contactId, type, presenter);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            type = CometChatConstants.MESSAGE_TYPE_VIDEO;
            try {
                String videoPath = FileUtils.getVideoPath(data.getData(), context);

                if (videoPath != null) {
                    file = new File(videoPath);
                    if (file.exists()) {
                        sendMedia(file, contactId, type, presenter);
                    }
                } else {
                    videoPath = data.getData().toString().replace("file://", "").
                            replace("%20", " ");
                    file = new File(videoPath);
                    if (file.exists()) {
                        sendMedia(file, contactId, type, presenter);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static <P> void handlefile(Context context, String type, P presenter, Intent data, String contactUid) {


        String filePath[] = new String[2];

        try {
//            LogUtils.e(data.getExtras().get("data").toString());
            filePath = getPath(context, data.getData());

             if (filePath[0]!=null) {
                 sendMedia(new File(filePath[0]), contactUid, filePath[1], presenter);
             }
        } catch (Exception e) {
               e.printStackTrace();
        }


    }
    public static <P> void handlefileUri(Context context, String type, P presenter, Uri data, String contactUid) {


        String filePath[] = new String[2];

        try {
            LogUtils.e("handlefileUri "+ data.toString());
            filePath = getPath(context, data);

            if (filePath[0]!=null) {
                sendMedia(new File(filePath[0]), contactUid, filePath[1], presenter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private static <P> void sendMedia(File file, String contactId, String type, P presenter) {
        LogUtils.e("Send MEdia "+file.getAbsolutePath()+"  "+type);
        if (presenter instanceof OneToOneActivityPresenter) {

            ((OneToOneActivityPresenter) presenter).sendMediaMessage(file, contactId, type);
        } else if (presenter instanceof GroupChatPresenter) {
            ((GroupChatPresenter) presenter).sendMediaMessage(file, contactId, type);
        }
    }

    public static <P> void handleCameraImage(Context context, P presenter, Intent data, String contactId) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        File file = new File(saveImage(context,thumbnail));
            LogUtils.e(file.getAbsolutePath());
        if (file.exists()) {
              BitmapFactory.decodeFile(file.getAbsolutePath());
            if (presenter instanceof OneToOneActivityPresenter) {
                ((OneToOneActivityPresenter) presenter).sendMediaMessage(file, contactId, CometChatConstants.MESSAGE_TYPE_IMAGE);
            } else if (presenter instanceof GroupChatPresenter) {
                ((GroupChatPresenter) presenter).sendMediaMessage(file, contactId, CometChatConstants.MESSAGE_TYPE_IMAGE);
            }
        }

    }
    public static String saveImage(Context context,Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/mabo");
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    private static File persistImage(Context context, Bitmap bitmap) {
        File filesDir = context.getFilesDir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFile = new File(filesDir, timeStamp + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
            os.flush();
            os.close();
        } catch (Exception e) {

        }
        finally {

            return imageFile;
        }
    }

    public static <P> void handleCameraVideo(Context context, P presenter, Intent data, String contactId) {
        String path = MediaUtils.getVideoPath(data.getData(), context);
        Logger.debug("handleCameraVideo", " Video Path" + path);
        File videoFile = new File(path);

        Logger.error("handleCameraVideo", " videoFile exists ? " + videoFile.exists());
        if (videoFile.exists()) {
            if (videoFile.exists()) {

                if (presenter instanceof OneToOneActivityPresenter) {
                    ((OneToOneActivityPresenter) presenter).sendMediaMessage(videoFile, contactId, CometChatConstants.MESSAGE_TYPE_VIDEO);
                } else if (presenter instanceof GroupChatPresenter) {
                    ((GroupChatPresenter) presenter).sendMediaMessage(videoFile, contactId, CometChatConstants.MESSAGE_TYPE_VIDEO);
                }
            }

        }
    }

    public static String[] getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        String[] ar;
        String type = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        LogUtils.e("Scheme "+uri.getScheme()+ " "+FileUtils.getFileExtension(uri.getPath()));
//        LogUtils.e("Type "+ context.getContentResolver().getType(uri).toLowerCase());
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                ar = new String[]{Environment.getExternalStorageDirectory() + "/" + split[1], CometChatConstants.MESSAGE_TYPE_FILE};
                return ar;
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                type = split[0];
                if ("image".equals(type)) {
                    type = CometChatConstants.MESSAGE_TYPE_IMAGE;
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    type = CometChatConstants.MESSAGE_TYPE_VIDEO;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    type = CometChatConstants.MESSAGE_TYPE_AUDIO;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                type =context.getContentResolver().getType(uri).toLowerCase();
                if (type!=null){
                    if (type.contains("jpeg") || type.contains("jpg") || type.contains("png") || type.contains("image") || type.contains("picture") || type.contains("photo")) {
                        type = CometChatConstants.MESSAGE_TYPE_IMAGE;
                    } else if (type.contains("video") || type.contains("mp4") || type.contains("avi") ||
                            type.contains("flv")) {
                        type = CometChatConstants.MESSAGE_TYPE_VIDEO;
                    } else if (type.contains("mp3"))
                    {
                        type = CometChatConstants.MESSAGE_TYPE_AUDIO;
                    }
                    else {
                        type = CometChatConstants.MESSAGE_TYPE_FILE;
                    }
                }
                if (cursor.moveToFirst()) {
                    ar = new String[]{cursor.getString(column_index), type};
                    return ar;
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            type = FileUtils.getFileExtension(uri.getPath());
            if (type.contains("jpeg") || type.contains("jpg") || type.contains("png") || type.contains("image") || type.contains("picture") || type.contains("photo")) {
                type = CometChatConstants.MESSAGE_TYPE_IMAGE;
            } else if (type.contains("video") || type.contains("mp4") || type.contains("avi") ||
                    type.contains("flv")) {
                type = CometChatConstants.MESSAGE_TYPE_VIDEO;
            }
            else {
                type = CometChatConstants.MESSAGE_TYPE_FILE;
            }
            ar = new String[]{uri.getPath(), type};
            return ar;
        }else{
            type = FileUtils.getFileExtension(uri.getPath());
            if (type.contains("jpeg") || type.contains("jpg") || type.contains("png") || type.contains("image") || type.contains("picture") || type.contains("photo")) {
                type = CometChatConstants.MESSAGE_TYPE_IMAGE;
            } else if (type.contains("video") || type.contains("mp4") || type.contains("avi") ||
                    type.contains("flv")) {
                type = CometChatConstants.MESSAGE_TYPE_VIDEO;
            } else if (type.contains("mp3"))
            {
                type = CometChatConstants.MESSAGE_TYPE_AUDIO;
            }
            else {
                type = CometChatConstants.MESSAGE_TYPE_FILE;
            }
            ar = new String[]{uri.getPath(), type};
            return ar;
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void captureImage(Activity activity, int code) {

       //  try {
        /*     String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri;
        outputFileUri= Uri.fromFile(file);
        LogUtils.e("Capture Image "+outputFileUri);*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    //    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        activity.startActivityForResult(intent, code);
       // Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
     //  activity.startActivityForResult(intent, code);
        /* }catch (Exception e){
             e.printStackTrace();
         }*/
    }

    public static void captureVideo(Activity activity, int code) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 15000000L);
        activity.startActivityForResult(intent, code);
    }

}
