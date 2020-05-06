package com.advengers.mabo.Cometchat.constants;

import com.advengers.mabo.Model.User;

public class AppConfig {

    public static class AppDetails {

        public static final String APP_ID = "17656fdb7bec9fa";//"16753412f8c0584";

        public static final String API_KEY = "2717c5c95bf831722969aa83637c5968290b9260";//"df87c80148759803a60c9e25a61d38420caa8f42";

        public static final String REGION = "us";

        public static final String AppID_user_UID = APP_ID+"_user_mabo"+ User.getUser().getId();
    }
}

