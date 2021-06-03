package com.advengers.mabo.Model;


import java.io.Serializable;
import java.util.List;

public class Commentlist implements Serializable {
    String id;
    String user_id;
    String post_id;
    String comment;
    String created;
    String email;
    String username;
    String profile_imagename;
    List<Tag> tagpeopledata;

    List<Reply> replydetails;

    public List<Reply> getReplydetails() {
        return replydetails;
    }

    public void setReplydetails(List<Reply> replydetails) {
        this.replydetails = replydetails;
    }


    public List<Tag> getTagpeopledata() {
        return tagpeopledata;
    }

    public void setTagpeopledata(List<Tag> tagpeopledata) {
        this.tagpeopledata = tagpeopledata;
    }

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

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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
}
