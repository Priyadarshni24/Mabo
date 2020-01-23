package com.advengers.mabo.Model;

import com.google.gson.annotations.SerializedName;

public class Apiresponse {
    @SerializedName("status")
    boolean status;
    @SerializedName("message")
    String message;
    @SerializedName("data")
    String data;
  /*  Apiresponse mApiresponse;

    public Apiresponse getmApiresponse() {
        return mApiresponse;
    }

    public void setmApiresponse(Apiresponse mApiresponse) {
        this.mApiresponse = mApiresponse;
    }*/

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
