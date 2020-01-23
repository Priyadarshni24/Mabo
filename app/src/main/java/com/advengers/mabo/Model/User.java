package com.advengers.mabo.Model;

import java.io.Serializable;

public class User implements Serializable {
     String id;
     String first_name;
     String last_name;
     String username;
     String email;
     String password;
     String location;
     String latitude;
     String longitude;
     String created;
     String modified;
     String facebook_id;
     String google_id;
     String phone;
     String device_model;
     String ip_address;
     String fcm_key;
     String device_id;
     String status;
     String login_type;
     String profile_imagename;
     String email_notify;
     String push_notify;
     String scrumble_location;
     String profile_display_status;
     String gender;
     String interests;
     String roomid;
     String request_status;


    public User(String id, String first_name, String last_name, String username, String email, String password, String location, String latitude, String longitude, String created, String modified, String facebook_id, String google_id, String phone, String device_model, String ip_address, String fcm_key, String device_id, String status, String login_type, String profile_imagename, String email_notify, String push_notify, String scrumble_location, String profile_display_status) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created = created;
        this.modified = modified;
        this.facebook_id = facebook_id;
        this.google_id = google_id;
        this.phone = phone;
        this.device_model = device_model;
        this.ip_address = ip_address;
        this.fcm_key = fcm_key;
        this.device_id = device_id;
        this.status = status;
        this.login_type = login_type;
        this.profile_imagename = profile_imagename;
        this.email_notify = email_notify;
        this.push_notify = push_notify;
        this.scrumble_location = scrumble_location;
        this.profile_display_status = profile_display_status;
    }

    private static User mUser;





    public User(String id, String first_name, String last_name, String username, String email, String password, String location, String latitude, String longitude, String created, String modified, String facebook_id, String google_id, String phone, String device_model, String ip_address, String fcm_key, String device_id, String status, String login_type) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created = created;
        this.modified = modified;
        this.facebook_id = facebook_id;
        this.google_id = google_id;
        this.phone = phone;
        this.device_model = device_model;
        this.ip_address = ip_address;
        this.fcm_key = fcm_key;
        this.device_id = device_id;
        this.status = status;
        this.login_type = login_type;

    }
    public User() {

    }

    public static boolean isLogged() {
        return getUser() != null && getUser().getEmail() != null && !getUser().getEmail().isEmpty();
    }
    public String getprofile_imagename() {
        return profile_imagename;
    }

    public void setprofile_imagename(String profile_imagename) {
        this.profile_imagename = profile_imagename;
    }


    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }


    public String getInterests() {
        return interests;
    }

    public void setInterests(String interest) {
        this.interests = interest;
    }



    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public static void logout() {
        mUser = new User();
    }

    public static User getUser() {
        return mUser;
    }

    public static void setUser(User user) {
        mUser = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getFcm_key() {
        return fcm_key;
    }

    public void setFcm_key(String fcm_key) {
        this.fcm_key = fcm_key;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }


    public String getEmail_notify() {
        return email_notify;
    }

    public void setEmail_notify(String email_notify) {
        this.email_notify = email_notify;
    }

    public String getPush_notify() {
        return push_notify;
    }

    public void setPush_notify(String push_notify) {
        this.push_notify = push_notify;
    }

    public String getScrumble_location() {
        return scrumble_location;
    }

    public void setScrumble_location(String scrumble_location) {
        this.scrumble_location = scrumble_location;
    }

    public String getProfile_display_status() {
        return profile_display_status;
    }

    public void setProfile_display_status(String profile_display_status) {
        this.profile_display_status = profile_display_status;
    }


    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

}
