package com.vrp.barc_demo.login;

public class DataDownloadInput {


    private int city_id;
    private String city_name;
    private int city_code;


    private String user_id;

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getCity_code() {
        return city_code;
    }

    public void setCity_code(int city_code) {
        this.city_code = city_code;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public static final String TABLE_NAME = "sub_districts";
    public static final String COLUMN_CITY_ID = "sub_district_id";
    public static final String COLUMN_CITY_CODE = "sub_district_code";
    public static final String COLUMN_CITY_NAME = "sub_district_name";
    public static final String COLUMN_FLAG = "flag";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_CITY_NAME + " INTEGER,"
                    + COLUMN_CITY_CODE + " TEXT,"
                    + COLUMN_FLAG + " TEXT"
                    + ")";


    private String table_name;
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
}
