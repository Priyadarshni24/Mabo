package com.advengers.mabo.Model;

import java.io.Serializable;
import java.util.List;

public class Notification implements Serializable {

    String post_id;
    String category;
    String count;
    NotifyDetails details;
    public NotifyDetails getDetails() {
        return details;
    }

    public void setDetails(NotifyDetails details) {
        this.details = details;
    }



    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public class NotifyDetails implements Serializable {

        String id;
        String user_id;
        String profile_imagename;
        String post_id;
        String notification_msg;
        String status;
        String sender_user_id;
        String created_at;
        String category;
        String username;


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }


        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
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

        public String getNotification_msg() {
            return notification_msg;
        }

        public void setNotification_msg(String notification_msg) {
            this.notification_msg = notification_msg;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSender_user_id() {
            return sender_user_id;
        }

        public void setSender_user_id(String sender_user_id) {
            this.sender_user_id = sender_user_id;
        }


        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getProfile_imagename() {
            return profile_imagename;
        }

        public void setProfile_imagename(String profile_imagename) {
            this.profile_imagename = profile_imagename;
        }
    }
}