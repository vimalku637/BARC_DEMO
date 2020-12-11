package com.vrp.barc_demo.models;

public class ClusterModel {
    private static final String TABLE_NAME="cluster" ;

    public static final String COLUMN_LOCAL_ID="local_id";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_CLUSTER_ID="cluster_id";
    public static final String COLUMN_CLUSTER_NAME="cluster_name";
    public static final String COLUMN_ACTION="action";
    public static final String COLUMN_FLAG="flag";
    public static final String COLUMN_STATUS="status";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_LOCAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ID + " INTEGER ,"
                    + COLUMN_CLUSTER_ID + " TEXT ,"
                    + COLUMN_CLUSTER_NAME + " TEXT ,"
                    + COLUMN_ACTION + " TEXT ,"
                    + COLUMN_FLAG + " INTEGER DEFAULT 0 ,"
                    + COLUMN_STATUS + " INTEGER DEFAULT 0 "
                    + ")";

    private String local_id;
    private String cluster_id;
    private String cluster_name;
    private String action;
    private String pincode;

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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
