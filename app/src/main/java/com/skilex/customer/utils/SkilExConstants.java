package com.skilex.customer.utils;

/**
 * Created by Admin on 23-09-2017.
 */

public class SkilExConstants {

    //URL'S
    //BASE URL
    private static final String BASE_URL = "http://skilex.in/";

    //BUILD URL
    public static final String BUILD_URL = BASE_URL + "development/apicustomer/";

    //LOGIN URL
    public static final String USER_LOGIN = "login/";

    //NUMBER VRIFICATION URL
    public static final String MOBILE_VERIFICATION = "mobile_check/";

    //EMAIL VRIFICATION URLS
    public static final String GET_EMAIL_STATUS = "email_verify_status/";
    public static final String VERIFY_EMAIL = "email_verification/";

    //PROFILE UPDATE URL
    public static final String UPDATE_PROFILE = "profile_update/";

    //UPLOAD URL
    public static final String UPLOAD_IMAGE = "profile_pic_upload/";

    //CATEGORY LIST URL
    public static final String GET_MAIN_CAT_LIST = "view_maincategory/";

    //SUB CATEGORY LIST URL
    public static final String GET_SUB_CAT_LIST = "view_subcategory/";

    //SERVICE URL
    public static final String SERVICE_LIST = "services_list/";

    //    Service Params
    public static String PARAM_MESSAGE = "msg";

    //     Shared preferences file name
    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    //    Shared FCM ID
    public static final String KEY_FCM_ID = "fcm_id";

    //    Shared IMEI No
    public static final String KEY_IMEI = "imei_code";

    //    Shared Phone No
    public static final String KEY_MOBILE_NUMBER = "number";

    //    Shared Lang
    public static final String KEY_LANGUAGE = "language";

    //    USER DATA

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_MASTER_ID = "user_master_id";
    public static final String KEY_USER_NAME = "full_name";
    public static final String KEY_USER_GENDER = "gender";
    public static final String KEY_USER_ADDRESS = "address";
    public static final String KEY_USER_PROFILE_PIC = "profile_pic";
    public static final String KEY_USER_MAIL = "email";
    public static final String KEY_USER_MAIL_STATUS = "email_verify_status";
    public static final String KEY_USER_TYPE = "user_type";


    // Alert Dialog Constants
    public static String ALERT_DIALOG_TITLE = "alertDialogTitle";
    public static String ALERT_DIALOG_MESSAGE = "alertDialogMessage";
    public static String ALERT_DIALOG_TAG = "alertDialogTag";
    public static String ALERT_DIALOG_POS_BUTTON = "alert_dialog_pos_button";
    public static String ALERT_DIALOG_NEG_BUTTON = "alert_dialog_neg_button";

    // Login Parameters
    public static String PHONE_NUMBER = "phone_no";
    public static String OTP = "otp";
    public static String DEVICE_TOKEN = "device_token";
    public static String MOBILE_TYPE = "mobile_type";
    public static String USER_MASTER_ID = "user_master_id";

    // Category Parameters
    public static String MAIN_CATEGORY_ID = "main_cat_id";
    public static String SUB_CATEGORY_ID = "sub_cat_id";
    public static String CAT_COUNT = "count";


}
