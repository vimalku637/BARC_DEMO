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
    public static final String COLUMN_FLAG="flag";
    public static final String COLUMN_STATUS="status";

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
                    + COLUMN_FLAG + " INTEGER DEFAULT 0 ,"
                    + COLUMN_STATUS + " INTEGER DEFAULT 0 "
                    + ")";

    private String local_id;
    private String cluster_id;
    private String cluster_name;
    private String action;
    private String original_address;
    private String next_address;
    private String previous_address;
    private String substitute_address;
    private String user_id;

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

    public String getNext_address() {
        return next_address;
    }

    public void setNext_address(String next_address) {
        this.next_address = next_address;
    }

    public String getPrevious_address() {
        return previous_address;
    }

    public void setPrevious_address(String previous_address) {
        this.previous_address = previous_address;
    }

    public String getSubstitute_address() {
        return substitute_address;
    }

    public void setSubstitute_address(String substitute_address) {
        this.substitute_address = substitute_address;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(String cluster_id) {
        this.cluster_id = cluster_id;
    }

    public String getCluster_name() {
        return cluster_name;
    }

    public void setCluster_name(String cluster_name) {
        this.cluster_name = cluster_name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
