package com.pubnub_fcm_example.util;

/**
 * Created by Ramesh on 1/9/17.
 */

public class Constant {
    public static final String ME = "chat";
    public static final String CHAT = "chat";
    public static final class GA {
        public static final String MESSAGE_TAB = "Message Tab";
        public static final String MESSAGE_TAB_GALLARY_OPENED = "Photo Gallary Opened";
        public static final String MESSAGE_TAB_CAMERA_STARTED_PHOTO = "Camera started to capture photo";
        public static final String MESSAGE_TAB_CAMERA_STARTED_VIDEO = "Camera started to capture Video";
    }
    public static final class extra {
        public static final String ERROR = "error";
        public static final String ERROR_MESSAGE = "error_message";
        public static final String RESULTS = "results";
    }
    public static final class value {
        public static final int MAX_MSG_IMAGE_DIMENSION = 500;
    }

    public static String FROM_TO_CAM = "fromToCam";
    public static String FROM_PROFILE = "createProfile";
    public static String FROM_POST = "post";
    public static String URL_NUMBER = "urlNumber";

    public static final class preference {
        public static final String REQUEST_KEY = "request_key";
        public static final String NAME = "name";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String SENDER_ID = "sender_id";
        public static final String SIKKA_SENDER_ID = "sikka_sender_id";
        public static final String OPEN_CHANNEL_ID = "open_channel_id";
        public static final String ACCOUNT_ID = "account_id";
        public static final String FCM_REG_ID = "fcm_reg_id";
    }
    public static final class pubnub {
        public static final String CHANNEL = "my_channel";
        public static final String PUBLISH_KEY = "pub-c-70184576-e839-4baf-b688-d5b63cb05db6";
        public static final String SUBSCRIBE_KEY = "sub-c-72b38940-d700-11e6-978a-02ee2ddab7fe";
        public static final String SECRET_KEY = "sec-c-NDI3NWZlNWQtNTk2OC00MjM2LTkzZGMtZjE0YzVkZjNhZWI5";
    }
}
