/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.models;

public class SurveyModel {
    private static final String TABLE_NAME="survey" ;

    public static final String COLUMN_LOCAL_ID="local_id";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_SURVEY_ID="survey_id";
    public static final String COLUMN_SURVEY_DATA="survey_data";
    public static final String COLUMN_STATE="state";
    public static final String COLUMN_DISTRICT="district";
    public static final String COLUMN_CITY="city";
    public static final String COLUMN_TOWN="town";
    public static final String COLUMN_USER_ID="user_id";
    public static final String COLUMN_DATE_TIME="date_time";
    public static final String COLUMN_HOUSEHOLD_NAME="household_name";
    public static final String COLUMN_ADDRESS="address";
    public static final String COLUMN_REASON="reason";
    public static final String COLUMN_TV_DATA="tv_data";
    public static final String COLUMN_FAMILY_DATA="family_data";
    public static final String COLUMN_ADDRESS_TYPE="address_type";
    public static final String COLUMN_FLAG="flag";
    public static final String COLUMN_STATUS="status";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ID + " INTEGER ,"
                    + COLUMN_SURVEY_ID + " TEXT ,"
                    + COLUMN_SURVEY_DATA + " TEXT ,"
                    + COLUMN_STATE + " INTEGER ,"
                    + COLUMN_DISTRICT + " INTEGER ,"
                    + COLUMN_CITY + " INTEGER ,"
                    + COLUMN_TOWN + " INTEGER ,"
                    + COLUMN_USER_ID + " TEXT ,"
                    + COLUMN_DATE_TIME + " TEXT ,"
                    + COLUMN_HOUSEHOLD_NAME + " TEXT ,"
                    + COLUMN_ADDRESS + " TEXT ,"
                    + COLUMN_REASON + " TEXT ,"
                    + COLUMN_TV_DATA + " TEXT ,"
                    + COLUMN_FAMILY_DATA + " TEXT ,"
                    + COLUMN_ADDRESS_TYPE+ " TEXT ,"
                    + COLUMN_FLAG + " INTEGER DEFAULT 0 ,"
                    + COLUMN_STATUS + " INTEGER DEFAULT 0 "
                    + ")";

    private String local_id;
    private String survey_id;
    private String survey_data;
    private String state;
    private String district;
    private String city;
    private String town;
    private String flag;
    private String status;
    private String reason;
    private String user_id;
    private String date_time;
    private String household_name;
    private String address;
    private String tv_data;
    private String family_data;
    private String address_type;

    public String getAddress_type() {
        return address_type;
    }

    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTv_data() {
        return tv_data;
    }

    public void setTv_data(String tv_data) {
        this.tv_data = tv_data;
    }

    public String getFamily_data() {
        return family_data;
    }

    public void setFamily_data(String family_data) {
        this.family_data = family_data;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getHousehold_name() {
        return household_name;
    }

    public void setHousehold_name(String household_name) {
        this.household_name = household_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(String survey_id) {
        this.survey_id = survey_id;
    }

    public String getSurvey_data() {
        return survey_data;
    }

    public void setSurvey_data(String survey_data) {
        this.survey_data = survey_data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
