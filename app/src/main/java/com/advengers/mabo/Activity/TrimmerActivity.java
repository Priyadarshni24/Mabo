package com.advengers.mabo.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import com.advengers.mabo.R;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.videoTrimmer.HgLVideoTrimmer;
import com.advengers.mabo.videoTrimmer.interfaces.OnHgLVideoListener;
import com.advengers.mabo.videoTrimmer.interfaces.OnTrimVideoListener;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.tagmanager.PreviewActivity;

import java.io.File;

public class TrimmerActivity extends MyActivity implements OnTrimVideoListener, OnHgLVideoListener {

    private HgLVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    private static final String FILEPATH = "filepath";
    String filePathcrop,filePathcompress;
    private FFmpeg ffmpeg;
    String path = "",method="crop";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();

        int maxDuration = 10;

        if (extraIntent != null) {
            path = extraIntent.getStringExtra(Utils.EXTRA_VIDEO_PATH);
            maxDuration = extraIntent.getIntExtra(Utils.VIDEO_TOTAL_DURATION, 10);
        }

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {


            /**
             * get total duration of video file
             */
            Log.e("tg", "maxDuration = " + maxDuration);
             //mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setOnTrimVideoListener(TrimmerActivity.this);
            mVideoTrimmer.setOnHgLVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
            mVideoTrimmer.setMaxDuration(30000);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }

    @Override
    public void getResult(int Start, int end, String how) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, contentUri.getPath()), Toast.LENGTH_SHORT).show();

            }
        });

      /*  try {


            String path = contentUri.getPath();
            File file = new File(path);
            Log.e("tg", " path1 = " + path + " uri1 = " + Uri.fromFile(file));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
            intent.setDataAndType(Uri.fromFile(file), "video/*");
            startActivity(intent);
            finish();
            Intent output = new Intent();
            output.putExtra(Utils.VIDEOURL, path);
            setResult(RESULT_OK, output);
            this.finish();

        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
      loadFFMpegBinary(Start,end,how);

    }
    /**
     * Load FFmpeg binary
     */
    private void loadFFMpegBinary(final int Start,final int end,final String how) {
        try {
            if (ffmpeg == null) {
                LogUtils.e("ffmpeg : era nulo");
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    LogUtils.e("ffmpeg : correct Loaded");
                    if(how.equals("crop"))
                        executeCutVideoCommand(Start,end,path);
                    else if(how.equals("compress"))
                        executeCompressCommand(path);
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        } catch (Exception e) {
            LogUtils.e( "EXception no controlada : " + e);
        }
    }
    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(TrimmerActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();

    }
    /**
     * Command for cutting video
     */
    private void executeCutVideoCommand(int startMs, int endMs,String selectedVideopath) {
        method = "crop";
        File moviesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"+getString(R.string.app_name));
        moviesDir.mkdirs();
        if (!moviesDir.exists()) {
            moviesDir.mkdirs();
        }/*Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
        );*/
        if(moviesDir.exists()) {
            String filePrefix = "cut_video";
            String fileExtn = ".mp4";
            String yourRealPath = selectedVideopath;//getPath(HgLVideoTrimmer.this, selectedVideoUri);
            File dest = new File(moviesDir, filePrefix + fileExtn);
            int fileNo = 0;
            while (dest.exists()) {
                fileNo++;
                dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
            }

            LogUtils.e("startTrim: src: " + yourRealPath);
            LogUtils.e("startTrim: dest: " + dest.getAbsolutePath());
            LogUtils.e("startTrim: startMs: " + startMs);
            LogUtils.e("startTrim: endMs: " + endMs);
            filePathcrop = dest.getAbsolutePath();
            //String[] complexCommand = {"-i", yourRealPath, "-ss", "" + startMs / 1000, "-t", "" + endMs / 1000, dest.getAbsolutePath()};
            String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePathcrop};

            execFFmpegBinary(complexCommand);
        }else{
        LogUtils.e("FOLDER DOESN'T EXIST");
    }
    }
    /**
     * Command for compressing video
     */
    private void executeCompressCommand(String CroppedVideopath) {
        method = "compress";
        File moviesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"+getString(R.string.app_name));
        if (!moviesDir.exists()) {
            moviesDir.mkdirs();
        }/* Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
        );*/
        if(moviesDir.exists()) {
            String filePrefix = "mabo_video";
            String fileExtn = ".mp4";
            String yourRealPath = CroppedVideopath;//getPath(TrimmerActivity.this, selectedVideoUri);


            File dest = new File(moviesDir, filePrefix + fileExtn);
            int fileNo = 0;
            while (dest.exists()) {
                fileNo++;
                dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
            }

            LogUtils.e("startTrim: src: " + yourRealPath);
            LogUtils.e("startTrim: dest: " + dest.getAbsolutePath());
            filePathcompress = dest.getAbsolutePath();
            String[] complexCommand = {"-y", "-i", yourRealPath, "-s", "160x120", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePathcompress};
            execFFmpegBinary(complexCommand);
        }else{
            LogUtils.e("FOLDER DOESN'T EXIST");
        }
    }
    /**
     * Executing ffmpeg binary
     */
    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    mProgressDialog.dismiss();
                    LogUtils.e( "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    LogUtils.e( "SUCCESS with output : " + s);
                  /*  Intent intent = new Intent(TrimmerActivity.this, PreviewActivity.class);
                    intent.putExtra(FILEPATH, filePath);
                    startActivity(intent);*/
                  if(method.equals("compress")) {
                      if(filePathcrop!=null)
                      {
                          File fdelete = new File(filePathcrop);
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                      Intent output = new Intent();
                                      output.putExtra(Utils.VIDEOURL, filePathcompress);
                                      setResult(RESULT_OK, output);
                                      finish();
                                      System.out.println("file Deleted :" + filePathcrop);
                                  } else {
                                      System.out.println("file not Deleted :" + filePathcrop);
                                  }
                      }
                      }else{
                          Intent output = new Intent();
                          output.putExtra(Utils.VIDEOURL, filePathcompress);
                          setResult(RESULT_OK, output);
                          finish();
                      }
                      mProgressDialog.dismiss();
                  }else if(method.equals("crop"))
                  {
                    executeCompressCommand(filePathcrop);
                  }
                }

                @Override
                public void onProgress(String s) {
                    LogUtils.e( "Started command : ffmpeg " + command);

                  //  mProgressDialog.setMessage("progressing");
                    LogUtils.e( "progress : " + s);
                }

                @Override
                public void onStart() {
                    LogUtils.e( "Started command : ffmpeg " + command);
                   /* if(method.equals("crop"))
                    {*/
                   if(!mProgressDialog.isShowing())
                   {
                       mProgressDialog.setMessage("Processing...");
                       mProgressDialog.show();
                    }

                }

                @Override
                public void onFinish() {
                    LogUtils.e( "Finished command : ffmpeg " + command);
                  /*  if(method.equals("compress"))
                    {
                        mProgressDialog.dismiss();
                    }*/
              }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(TrimmerActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void playUriOnVLC(Uri uri) {

        int vlcRequestCode = 42;
        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
        vlcIntent.setPackage("org.videolan.vlc");
        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
        vlcIntent.putExtra("title", "Kung Fury");
        vlcIntent.putExtra("from_start", false);
        vlcIntent.putExtra("position", 90000l);
        startActivityForResult(vlcIntent, vlcRequestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("tg", "resultCode = " + resultCode + " data " + data);
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
