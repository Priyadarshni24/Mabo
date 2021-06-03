package com.advengers.mabo.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Interfaces.Keys;
import com.advengers.mabo.R;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.databinding.ActivityVideoPlayerMp4Binding;

import static com.advengers.mabo.Activity.MainActivity.CLOUDVIDEOBASEURL;

public class VideoPlayerMP4Activity extends MyActivity {
    ActivityVideoPlayerMp4Binding binding;
    private MediaController mediacontroller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(VideoPlayerMP4Activity.this,R.layout.activity_video_player_mp4);
        binding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
      /*  mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(binding.videoView);
        mediacontroller.setMediaPlayer(binding.videoView);
        binding.videoView.setMediaController(mediacontroller);*/
        LogUtils.e(getIntent().getStringExtra(Keys.VIDEO_URL));
        binding.videoView.videoUrl(CLOUDVIDEOBASEURL+ getIntent().getStringExtra(Keys.VIDEO_URL));
        binding.pbLoading.setVisibility(View.GONE);
            binding.videoView.enableAutoStart();
        /*binding.videoView.setVideoPath(CLOUDVIDEOBASEURL+ getIntent().getStringExtra(Keys.VIDEO_URL));
        binding.videoView.start();
        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                binding.pbLoading.setVisibility(View.GONE);
            }
        });*/
    }
}
