package com.advengers.mabo.Model;

import com.advengers.mabo.Utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.googlecode.mp4parser.h264.Debug.trace;

public class Tag implements Serializable {

    String id;
    String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("username", username);

        } catch (JSONException e) {
            trace("DefaultListItem.toString JSONException: "+e.getMessage());
        }
        LogUtils.e(" "+obj.toString());
        return obj;
    }
}
