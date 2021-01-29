/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.models;

public class ClusterModel {
    private static final String TABLE_NAME="cluster" ;

    public static final String COLUMN_LOCAL_ID="local_id";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_CLUSTER_NO="cluster_no";
    public static final String COLUMN_CENSUS_STATE_CODE="Census_State_Code";
    public static final String COLUMN_STATE_NAME="State_Name";
    public static final String COLUMN_TOWN_VILLAGE_CLASS="Town_Village_Class";
    public static final String COLUMN_CENSUS_DISTRICT_CODE="Census_District_Code";
    public static final String COLUMN_CENSUS_DISTRICT_NAME="Census_District_Name";
    public static final String COLUMN_CENSUS_VILLAGE_TOWN_CODE="Census_Village_Town_Code";
    public static final String COLUMN_CENSUS_VILLAGE_TOWN_NAME="Census_Village_Town_Name";
    public static final String COLUMN_UA_COMPONENT="UA_Component";
    public static final String COLUMN_UA_COMPONENT_CODE="UA_Component_code";
    public static final String COLUMN_BARC_TOWN_CODE="BARC_Town_Code";
    public static final String COLUMN_ORIGINAL_TOWN_VILLAGE="Original_Town_Village";
    public static final String COLUMN_ORIGINAL_TOWN_VILLAGE_CODE="Original_Town_Village_Code";
    public static final String COLUMN_SAMPLING_TOWN_CLASS="Sampling_town_class";
    public static final String COLUMN_SP_NO="SP_No";
    public static final String COLUMN_ORIGINAL_ADDRESS="Original_address";
    public static final String COLUMN_AFTER_VOTER_ADDRESS="After_10_Voter_Address";
    public static final String COLUMN_PREVIOUS_VOTER_ADDRESS="Previous_10_Voter_Address";
    public static final String COLUMN_PINCODE="Pincode";
    public static final String COLUMN_OPERATOR_AGENCY="Operator_Agency";
    public static final String COLUMN_LOCK_STATUS="lock_status";
    public static final String COLUMN_BI_WEIGHTING_TOWN_CLASS="BI_Weighting_town_class";
    public static final String COLUMN_USER_ID="user_id";
    public static final String COLUMN_NCCC_CATAGORY="NCC_catagory";
    public static final String COLUMN_FLAG="flag";
    public static final String COLUMN_STATUS="status";
    public static final String COLUMN_SAMPLE_SIZE="sample_size";
    public static final String COLUMN_NCC_CATAGORY="nccs_category";
    public static final String COLUMN_completed_record="completed_record";
    public static final String COLUMN_SELECTED_VOTER_NO="Selected_Voter_No";
    public static final String COLUMN_EP_ADDRESS="EP_address";
    public static final String COLUMN_REJECTED="tot_rejected";
    public static final String COLUMN_TERMINATED="tot_terminated";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ID + " INTEGER ,"
                    + COLUMN_CLUSTER_NO + " TEXT ,"
                    + COLUMN_CENSUS_STATE_CODE + " TEXT ,"
                    + COLUMN_STATE_NAME + " TEXT ,"
                    + COLUMN_TOWN_VILLAGE_CLASS + " TEXT ,"
                    + COLUMN_CENSUS_DISTRICT_CODE + " TEXT ,"
                    + COLUMN_CENSUS_DISTRICT_NAME + " TEXT ,"
                    + COLUMN_CENSUS_VILLAGE_TOWN_CODE + " TEXT ,"
                    + COLUMN_CENSUS_VILLAGE_TOWN_NAME + " TEXT ,"
                    + COLUMN_UA_COMPONENT + " TEXT ,"
                    + COLUMN_UA_COMPONENT_CODE + " TEXT ,"
                    + COLUMN_BARC_TOWN_CODE + " TEXT ,"
                    + COLUMN_ORIGINAL_TOWN_VILLAGE + " TEXT ,"
                    + COLUMN_ORIGINAL_TOWN_VILLAGE_CODE + " TEXT ,"
                    + COLUMN_SAMPLING_TOWN_CLASS + " TEXT ,"
                    + COLUMN_SP_NO + " TEXT ,"
                    + COLUMN_AFTER_VOTER_ADDRESS + " TEXT ,"
                    + COLUMN_ORIGINAL_ADDRESS + " TEXT ,"
                    + COLUMN_PREVIOUS_VOTER_ADDRESS + " TEXT ,"
                    + COLUMN_PINCODE + " TEXT ,"
                    + COLUMN_OPERATOR_AGENCY + " TEXT ,"
                    + COLUMN_LOCK_STATUS + " TEXT ,"
                    + COLUMN_BI_WEIGHTING_TOWN_CLASS+ " TEXT ,"
                    + COLUMN_USER_ID+ " TEXT ,"
                    + COLUMN_NCCC_CATAGORY+ " TEXT ,"
                    + COLUMN_FLAG + " INTEGER DEFAULT 0 ,"
                    + COLUMN_NCC_CATAGORY + " TEXT ,"
                    + COLUMN_completed_record + " INTEGER DEFAULT 0 ,"
                    + COLUMN_REJECTED + " INTEGER DEFAULT 0 ,"
                    + COLUMN_TERMINATED + " INTEGER DEFAULT 0 ,"
                    + COLUMN_SELECTED_VOTER_NO + " TEXT ,"
                    + COLUMN_EP_ADDRESS + " TEXT ,"
                    + COLUMN_SAMPLE_SIZE + " INTEGER DEFAULT 0 ,"
                    + COLUMN_STATUS + " INTEGER DEFAULT 0 "
                    + ")";

    private String local_id;
    private String user_id;
    private String cluster_no;
    private String Census_State_Code;
    private String State_Name;
    private String Town_Village_Class;
    private String Census_District_Code;
    private String Census_District_Name;
    private String Census_Village_Town_Code;
    private String Census_Village_Town_Name;
    private String UA_Component;
    private String UA_Component_code;
    private String BARC_Town_Code;
    private String Original_Town_Village;
    private String Original_Town_Village_Code;
    private String Sampling_town_class;
    private String SP_No;
    private String original_address;
    private String After_10_Voter_Address;
    private String Previous_10_Voter_Address;
    private String Operator_Agency;
    private String lock_status;
    private int city_id;
    private int city_code;
    private String clu_code;
    private String pincode;
    private String BI_Weighting_town_class;
    private String EP_address;

    public String getEP_address() {
        return EP_address;
    }

    public void setEP_address(String EP_address) {
        this.EP_address = EP_address;
    }

    public String getBI_Weighting_town_class() {
        return BI_Weighting_town_class;
    }

    public void setBI_Weighting_town_class(String BI_Weighting_town_class) {
        this.BI_Weighting_town_class = BI_Weighting_town_class;
    }

    public int getCity_code() {
        return city_code;
    }

    public void setCity_code(int city_code) {
        this.city_code = city_code;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getClu_code() {
        return clu_code;
    }

    public void setClu_code(String clu_code) {
        this.clu_code = clu_code;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCensus_State_Code() {
        return Census_State_Code;
    }

    public void setCensus_State_Code(String census_State_Code) {
        Census_State_Code = census_State_Code;
    }

    public String getCensus_District_Code() {
        return Census_District_Code;
    }

    public void setCensus_District_Code(String census_District_Code) {
        Census_District_Code = census_District_Code;
    }

    public String getOriginal_Town_Village() {
        return Original_Town_Village;
    }

    public void setOriginal_Town_Village(String original_Town_Village) {
        Original_Town_Village = original_Town_Village;
    }

    public String getOriginal_Town_Village_Code() {
        return Original_Town_Village_Code;
    }

    public void setOriginal_Town_Village_Code(String original_Town_Village_Code) {
        Original_Town_Village_Code = original_Town_Village_Code;
    }

    public String getSampling_town_class() {
        return Sampling_town_class;
    }

    public void setSampling_town_class(String sampling_town_class) {
        Sampling_town_class = sampling_town_class;
    }

    public String getSP_No() {
        return SP_No;
    }

    public void setSP_No(String SP_No) {
        this.SP_No = SP_No;
    }

    public String getAfter_10_Voter_Address() {
        return After_10_Voter_Address;
    }

    public void setAfter_10_Voter_Address(String after_10_Voter_Address) {
        After_10_Voter_Address = after_10_Voter_Address;
    }

    public String getPrevious_10_Voter_Address() {
        return Previous_10_Voter_Address;
    }

    public void setPrevious_10_Voter_Address(String previous_10_Voter_Address) {
        Previous_10_Voter_Address = previous_10_Voter_Address;
    }

    public String getOperator_Agency() {
        return Operator_Agency;
    }

    public void setOperator_Agency(String operator_Agency) {
        Operator_Agency = operator_Agency;
    }

    public String getLock_status() {
        return lock_status;
    }

    public void setLock_status(String lock_status) {
        this.lock_status = lock_status;
    }

    public String getCluster_no() {
        return cluster_no;
    }

    public void setCluster_no(String cluster_no) {
        this.cluster_no = cluster_no;
    }

    public String getState_Name() {
        return State_Name;
    }

    public void setState_Name(String state_Name) {
        State_Name = state_Name;
    }

    public String getTown_Village_Class() {
        return Town_Village_Class;
    }

    public void setTown_Village_Class(String town_Village_Class) {
        Town_Village_Class = town_Village_Class;
    }

    public String getCensus_District_Name() {
        return Census_District_Name;
    }

    public void setCensus_District_Name(String census_District_Name) {
        Census_District_Name = census_District_Name;
    }

    public String getCensus_Village_Town_Code() {
        return Census_Village_Town_Code;
    }

    public void setCensus_Village_Town_Code(String census_Village_Town_Code) {
        Census_Village_Town_Code = census_Village_Town_Code;
    }

    public String getCensus_Village_Town_Name() {
        return Census_Village_Town_Name;
    }

    public void setCensus_Village_Town_Name(String census_Village_Town_Name) {
        Census_Village_Town_Name = census_Village_Town_Name;
    }

    public String getUA_Component() {
        return UA_Component;
    }

    public void setUA_Component(String UA_Component) {
        this.UA_Component = UA_Component;
    }

    public String getUA_Component_code() {
        return UA_Component_code;
    }

    public void setUA_Component_code(String UA_Component_code) {
        this.UA_Component_code = UA_Component_code;
    }

    public String getBARC_Town_Code() {
        return BARC_Town_Code;
    }

    public void setBARC_Town_Code(String BARC_Town_Code) {
        this.BARC_Town_Code = BARC_Town_Code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOriginal_address() {
        return original_address;
    }

    public void setOriginal_address(String original_address) {
        this.original_address = original_address;
    }

    private String NCC_catagory,sample_size;
    public String getNCCCatagory() { return NCC_catagory; }
    public void setNCCCatagory(String NCC_catagory) {
        this.NCC_catagory = NCC_catagory;
    }
    public String getsample_size() { return sample_size; }
    public void setsample_size(String sample_size) {
        this.sample_size = sample_size;
    }


}
