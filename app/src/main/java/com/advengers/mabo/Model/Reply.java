package com.advengers.mabo.Model;

import java.io.Serializable;
import java.util.List;

public class Reply implements Serializable {
    String id;
    String user_id;
    String comment_id;
    String reply_comment;
    String post_id;
    String tag_people;
    String notification_status;
    String created_at;
    String email;
    String username;
    String profile_imagename;
    List<Tag> tagpeopledata;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(String reply_comment) {
        this.reply_comment = reply_comment;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getTag_people() {
        return tag_people;
    }

    public void setTag_people(String tag_people) {
        this.tag_people = tag_people;
    }

    public String getNotification_status() {
        return notification_status;
    }

    public void setNotification_status(String notification_status) {
        this.notification_status = notification_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_imagename() {
        return profile_imagename;
    }

    public void setProfile_imagename(String profile_imagename) {
        this.profile_imagename = profile_imagename;
    }

    public List<Tag> getTagpeopledata() {
        return tagpeopledata;
    }

    public void setTagpeopledata(List<Tag> tagpeopledata) {
        this.tagpeopledata = tagpeopledata;
    }


}