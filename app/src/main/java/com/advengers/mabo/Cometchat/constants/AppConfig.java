package com.advengers.mabo.Cometchat.constants;

import com.advengers.mabo.Model.User;

public class AppConfig {

    public static class AppDetails {

        public static final String APP_ID = "27527ff8f21b086";//"25642270b37e56d";//"24261ee26c53fa8";//"22428a3cbc4da21";//"213551a8f3adc5d";//"19947f26282ea61";//"17656fdb7bec9fa";//"16753412f8c0584";

        public static final String API_KEY ="6aff0c1e8192cafcc3e3a023f0ac7bd22eae9455";//"6aff0c1e8192cafcc3e3a023f0ac7bd22eae9455";//"8a60a2eafa45a5458062567703810988d844b9c9";//"eece205c287894baac3d2caee2c3ff7e7179786e";// "c05e2aacdad2c13d5ad32aa62993d4071b63c423";//"38e47957a9dea0f15aef1bce0cd6bf5b32d9e5b4";// "8e80ca8dac20f4f28c5a3c989a485b51c182ff86";//"2717c5c95bf831722969aa83637c5968290b9260";//"df87c80148759803a60c9e25a61d38420caa8f42";

        public static final String REGION = "us";

        public static final String AppID_user_UID = APP_ID+"_user_mabo"+ User.getUser().getId();
    }
}

