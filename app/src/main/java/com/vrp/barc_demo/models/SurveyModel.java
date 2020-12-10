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