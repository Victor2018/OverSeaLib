package com.victor.util;

/**
 * Created by victor on 2017/9/26.
 */

public class Constant {
    public static final boolean MODEL_DEBUG = true;
    public static final boolean MODEL_ONLINE = true;
    public static final int BUILD_CODE = 0;
    public static final int ON_ACTIVITY_REQUEST_CODE = 2;
    public static final int ON_ACTIVITY_RESULT_CODE = 6;
    public static final String SHARE_TYPE                        = "text/plain";
    public static final String MA_DATA                           = "madata";

    public static final String INTENT_DATA_KEY                  = "INTENT_DATA_KEY";
    public static final String INTENT_ACTION_KEY                = "INTENT_ACTION_KEY";
    public static final String PLAY_ONLY_WIFI_KEY               = "PLAY_ONLY_WIFI_KEY";

    /**
     * 客服电话
     */
    public static final String CUSTOMER_SERVICE_CELL = "400-000-888";


    public static class Action  {
        public static final int EXIT                                                      = 0x101;
        public static final int TOGGLE_MENU                                              = 0x102;
        public static final int PLAY_NEXT                                                = 0x103;
        public static final int PLAY_NEXT_SHORT_VIDEO                                   = 0x104;
        public static final int PLAY_VIDEO                                               = 0x105;
        public static final int PLAY_SHORT_VIDEO                                        = 0x106;
        public static final int DEVICE_REPORT                                            = 0x107;
        public static final int HEART_BEAT                                               = 0x108;
        public static final int CONNECT_STB                                              = 0x109;
        public static final int DISCONNECT_STB                                           = 0x110;
        public static final int AIRPLAY                                                   = 0x111;
    }

    public static class Msg {
        public static final int HIDE_PLAY_TOP_BOTTOM_CTRL_VIEW                         = 0x201;
        public static final int HIDE_PLAY_RIGHT_CTRL_VIEW                               = 0x202;
        public static final int HIDE_PLAY_LEFT_CTRL_VIEW                                = 0x203;
        public static final int HIDE_AIRPLAY_VIEW                                        = 0x204;
        public static final int ENABLE_SCREEN_ORIENTATION                               = 0x205;
        public static final int PREPARE_PLAY                                              = 0x206;
    }


    public static class TB {
        public static final String MOVIE 						    = "movie";
    }


    /**
     * 数据库配置信息
     * @author victor
     * @date 2016-2-24
     */
    public static class DbConfig {
        public static final String DB_NAME 						= "longtv_db";
        public static final String SCHEME 						= "content://";
        public static final String AUTHORITY 					= "content.victor.longtv.content";
        public static final String CALLED_AUTHORITY 			= "content.victor.longtvcontent";
        public static final String URI_PATH 					    = SCHEME + AUTHORITY + "/";
        public static final String CALLED_URI_PATH 				= SCHEME + CALLED_AUTHORITY + "/";
        public static final int DB_VERSION 						= 1;
    }
}
