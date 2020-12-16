package com.vrp.barc_demo.models;

public class NccsMatrixModel {
    int id;
    int education_id;
    int durables_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEducation_id() {
        return education_id;
    }

    public void setEducation_id(int education_id) {
        this.education_id = education_id;
    }

    public int getDurables_id() {
        return durables_id;
    }

    public void setDurables_id(int durables_id) {
        this.durables_id = durables_id;
    }

    public static final String TABLE_NAME = "nccs_matrix";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EDUCATION = "education_id";
    public static final String COLUMN_CATEGORY = "nccs_category";
    public static final String COLUMN_DURABLES = "durables_id";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EDUCATION + " INTEGER,"
                    + COLUMN_CATEGORY + " TEXT,"
                    + COLUMN_DURABLES + " TEXT"
                    + ")";

}
