package com.advengers.mabo.Model;

import java.util.ArrayList;
import java.util.List;

public class Post {
   public class userdetails {

        String first_name;
        String last_name;
        String username;
        String profile_imagename;
        String email;

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

        public String getProfile_imagename() {
            return profile_imagename;
        }

        public void setProfile_imagename(String profile_imagename) {
            this.profile_imagename = profile_imagename;
        }

       public String getEmail() {
           return email;
       }

       public void setEmail(String email) {
           this.email = email;
       }



   }
    String id;
    String user_id;
    String post_title;
    String latitude;
    String longitude;
    String image_url;
    String video_url;
    String tag_location;
    String tag_interest;
    String tag_people;
    String created;
    String distance;
    String commentcount;
    String likecount;
    String likedbyme;
    List<Tag> tagpeopledata;

    userdetails userdetails;


    public List<Tag> getTagpeopledata() {
        return tagpeopledata;
    }

    public void setTagpeopledata(List<Tag> tagpeopledata) {
        this.tagpeopledata = tagpeopledata;
    }



    public String getLikedbyme() {
        return likedbyme;
    }

    public void setLikedbyme(String likedbyme) {
        this.likedbyme = likedbyme;
    }

    public String getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(String commentcount) {
        this.commentcount = commentcount;
    }

    public String getLikecount() {
        return likecount;
    }

    public void setLikecount(String likecount) {
        this.likecount = likecount;
    }

    public Post.userdetails getUserdetails() {
        return userdetails;
    }

    public void setUserdetails(Post.userdetails userdetails) {
        this.userdetails = userdetails;
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

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getTag_location() {
        return tag_location;
    }

    public void setTag_location(String tag_location) {
        this.tag_location = tag_location;
    }

    public String getTag_interest() {
        return tag_interest;
    }

    public void setTag_interest(String tag_interest) {
        this.tag_interest = tag_interest;
    }

    public String getTag_people() {
        return tag_people;
    }

    public void setTag_people(String tag_people) {
        this.tag_people = tag_people;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


}
