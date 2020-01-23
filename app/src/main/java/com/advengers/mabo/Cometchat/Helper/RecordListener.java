package com.advengers.mabo.Cometchat.Helper;

public interface RecordListener {
    void onStart();
    void onCancel();
    void onFinish(long time);
    void onLessTime();
}