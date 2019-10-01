package com.skilex.customer.utils;

/**
 * Created by Admin on 23-09-2017.
 */

public class SkilExConstants {

    //URL'S
    //BASE URL
    private static final String BASE_URL = "https://skilex.in/";
//
//    //BUILD URL
    public static final String BUILD_URL = BASE_URL + "apicustomer/";
//    public static final String BUILD_URL = BASE_URL + "development/apicustomer/";
//    public static final String BUILD_URL = BASE_URL + "uat/apicustomer/";

    //LOGIN URL
    public static final String USER_LOGIN = "login/";

    //GUEST LOGIN URL
    public static final String GUEST_LOGIN = "guest_login/";

    //NUMBER VRIFICATION URL
    public static final String MOBILE_VERIFICATION = "mobile_check/";

    //EMAIL VRIFICATION URLS
    public static final String GET_EMAIL_STATUS = "email_verify_status/";
    public static final String VERIFY_EMAIL = "email_verification/";

    //PROFILE UPDATE URL
    public static final String UPDATE_PROFILE = "profile_update/";

    //PROFILE UPDATE URL
    public static final String PROFILE_INFO = "user_info/";

    //UPLOAD URL
    public static final String UPLOAD_IMAGE = "profile_pic_upload/";

    //BANNER LIST URL
    public static final String GET_BANNER_IMAGES = "view_banner_list/";

    //CATEGORY LIST URL
    public static final String GET_MAIN_CAT_LIST = "view_maincategory/";

    //SUB CATEGORY LIST URL
    public static final String GET_SUB_CAT_LIST = "view_subcategory/";

    //SERVICE URL
    public static final String SERVICE_LIST = "services_list/";

    //SEARCH SERVICE URL
    public static final String SEARCH_SERVICE_LIST = "search_service/";

    //SERVICE DETAIL URL
    public static final String GET_SERVICE_DETAIL = "service_details/";

    //ADD SERVICE CART URL
    public static final String ADD_TO_CART = "add_service_to_cart/";

    //CART SUMMARY URL
    public static final String CART_LIST = "view_cart_summary/";

    //REMOVE FROM CART URL
    public static final String REMOVE_FROM_CART = "remove_service_to_cart/";

    //CLEAR CART URL
    public static final String CLEAR_CART = "clear_cart/";

    //SERVICE TIME SLOT URL
    public static final String GET_TIME_SLOT = "view_time_slot/";

    //PROCEED TO BOOK SERVICE URL
    public static final String PROCEED_TO_BOOK = "proceed_to_book_order/";

    //ONGOING SERVICE URL
    public static final String ONGOING_SERVICES = "ongoing_services/";

    //REQUESTED SERVICE URL
    public static final String REQUESTED_SERVICES = "requested_services/";

    //CANCEL SERVICE BOOKING URL
    public static final String CANCEL_SERVICE = "cancel_service_order/";

    //CANCEL SERVICE REASON URL
    public static final String CANCEL_REASON = "list_reason_for_cancel/";

    //ADVANCE PAYMENT URL
    public static final String ADVANCE_PAYMENT = "skilex.in/ccavenue_app/customer_advance.php/";

    //SERVICE HISTORY URL
    public static final String HISTORY_SERVICES = "service_history/";

    //SERVICE ORDER DETAIL URL
    public static final String ONGOING_SERVICE_DETAILS = "service_order_details/";

    //SERVICE PROVER ALLOCATION URL
    public static final String SERVICE_ALLOCATION = "service_provider_allocation/";

    //SERVICE PERSON TARACK URL
    public static final String SERVICE_PERSON_LOCATION = "service_person_tracking/";

    //SERVICE ORDER SUMMARY URL
    public static final String SERVICE_ORDER_SUMMARY = "service_order_summary/";

    //ADDITIONAL SERVICE URL
    public static final String ADDITIONAL_SERVICE = "view_addtional_service/";

    //VIEW BILLS URL
    public static final String VIEW_BILLS = "service_order_bills/";

    //SERVICE ORDER STATUS URL
    public static final String SERVICE_ORDER_STATUS = "service_order_status/";

    //SERVICE COUPON URL
    public static final String COUPON_LIST = "service_coupon_list/";

    //APPLY COUPON URL
    public static final String APPLY_COUPON = "apply_coupon_to_order/";

    //APPLY COUPON URL
    public static final String REMOVE_COUPON = "remove_coupon_from_order/";

    //APPLY COUPON URL
    public static final String PROCEED_TO_PAY = "proceed_for_payment/";


    //APPLY COUPON URL
    public static final String REVIEW = "service_reviews_add/";

//
//    //APPLY COUPON URL
//    public static final String PROCEED_TO_PAY = "proceed_for_payment/";




    //////    Service Params    ///////



    public static String PARAM_MESSAGE = "msg";
    public static String PARAM_MESSAGE_ENG = "msg_en";
    public static String PARAM_MESSAGE_TAMIL = "msg_ta";

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
    public static String UNIQUE_NUMBER = "unique_number";
    public static String MOBILE_KEY = "mobile_key";
    public static String USER_STATUS = "user_stat";

    // Category Parameters
    public static String MAIN_CATEGORY_ID = "main_cat_id";
    public static String CATEGORY_ID = "category_id";
    public static String SUB_CATEGORY_ID = "sub_cat_id";
    public static String SUB_CAT_ID = "sub_category_id";
    public static String CAT_COUNT = "count";

    // Service Parameters
    public static String SERVICE_ID = "service_id";
    public static String SERVICE_ORDER_ID = "service_order_id";
    public static String CANCEL_ID = "cancel_id";
    public static String CANCEL_COMMENTS = "comments";
    public static String SERVICE_RATE = "service_rate";
    public static String SERVICE_COUNT = "service_count";
    public static String SERVICE_DATE = "service_date";
    public static String SERVICE_STATUS = "sat";
    public static String CART_STATUS = "car_sat";

    // Service Parameters
    public static String CART_ID = "cart_id";
    public static String SERVICE_PERSON_ID = "person_id";

    // Order Parameters
    public static String ORDER_ID = "order_id";
    public static String TIME_INTERVAL = "display_minute";

    // Service Parameters
    public static String SEARCH_STATUS = "search_status";
    public static String SEARCH_TEXT = "service_txt";
    public static String SEARCH_TEXT_TA = "search_txt_ta";

    // Booking Parameters
    public static String CONTACT_PERSON = "contact_person_name";
    public static String CONTACT_PERSON_NUMBER = "contact_person_number";
    public static String SERVICE_LATLNG = "service_latlon";
    public static String SERVICE_LOCATION = "service_location";
    public static String SERVICE_ADDRESS = "service_address";
    public static String ORDER_DATE = "order_date";
    public static String ORDER_TIMESLOT = "order_timeslot_id";


    // Advance Payment
    public static String ADVANCE_AMOUNT = "advance_amount";
    public static String ADVANCE_STATUS = "advance_payment_status";
    public static String COUPON_ID = "coupon_id";
    public static String COUPON_TEXT = "coupon_text";

    // Advance Payment
    public static String KEY_RATINGS = "ratings";
    public static String KEY_COMMENTS = "reviews";

}
