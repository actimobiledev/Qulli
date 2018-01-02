package com.actiknow.qulli.utils;

public class AppConfigURL {
    public static String version = "v1.0";
    //    public static String BASE_URL = "https://project-qulli-cammy92.c9users.io/api/" + version + "/";
    public static String BASE_URL = "http://actipatient.com/qulli/api/" + version + "/";
    public static  String Login= BASE_URL+"driver/login";
    public static  String FORGOT_PASSWORD= BASE_URL+"driver/forgot-password";
    public static  String FORGOT_CHANGE_PASSWORD= BASE_URL+"driver/change-password";
    public static  String BOOKING= BASE_URL+"driver/bookings";
    public static String SCAN_PRODUCT= BASE_URL+"driver/scan";
    public static String ADD_ASSET= BASE_URL+"driver/add/asset";
    public static String UPDATE_STATUS= BASE_URL+"driver/booking/update";
    

}