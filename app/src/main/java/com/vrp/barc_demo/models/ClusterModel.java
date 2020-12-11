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
    private String original_address;
    private String next_address;
    private String previous_address;
    private String substitute_address;

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
