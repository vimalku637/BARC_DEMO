/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.sqlite_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vrp.barc_demo.login.DataDownloadInput;
import com.vrp.barc_demo.models.ClusterModel;
import com.vrp.barc_demo.models.NccsMatrixModel;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "barc.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "SqLiteHelper";
    String DB_PATH_SUFFIX = "/databases/";
    int version;
    Context ctx;

    SharedPrefHelper sharedPrefHelper;

    public SqliteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
        sharedPrefHelper = new SharedPrefHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SurveyModel.CREATE_TABLE);
        db.execSQL(ClusterModel.CREATE_TABLE);
        db.execSQL(DataDownloadInput.CREATE_TABLE);
        db.execSQL(NccsMatrixModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        if(oldVersion<2){
            db.execSQL("ALTER TABLE cluster ADD completed_record INTEGER DEFAULT '0'");
        }
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        Log.e("version", "outside" + version);

        File dbFile = ctx.getDatabasePath(DATABASE_NAME);
        //  checkDbVersion(dbFile);
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void dropTable(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM'" + tablename + "'");
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
    }

    public void saveSurveyDataInTable(JSONObject jsonObject, String survey_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();

                values.put("survey_id", survey_id);
                values.put("cluster_no", sharedPrefHelper.getString("cluster_no", ""));
                values.put("audio_recording", sharedPrefHelper.getString("AudioSavePathInDevice", ""));
                values.put("survey_data", String.valueOf(jsonObject));
                values.put("state", "2");
                values.put("district", "1");
                values.put("city", "4");
                values.put("town", "3");
                values.put("user_id", sharedPrefHelper.getString("user_id", ""));
                values.put("date_time", "");
                values.put("household_name", sharedPrefHelper.getString("interviewer_name", ""));
                values.put("address", "");
                values.put("address_type", sharedPrefHelper.getString("address_type", ""));
                values.put("reason_of_change", sharedPrefHelper.getString("reason_of_change", ""));
                values.put("flag", "0");

                values.put("status", "0");

                db.insert("survey", null, values);
                db.close(); // closing database connection
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
    }



    public HashMap<String, Integer> getCity() {
        HashMap<String, Integer> city = new HashMap<>();
        DataDownloadInput dataDownloadInput;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            if (sqLiteDatabase != null && sqLiteDatabase.isOpen() && !sqLiteDatabase.isReadOnly()) {
                //String query = "Select  partner_id,partner_name from partner";
                String query = "Select sub_district_id,sub_district_name from sub_districts order by sub_district_name asc";
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        dataDownloadInput = new DataDownloadInput();
                        dataDownloadInput.setCity_name(cursor.getString(cursor.getColumnIndex("sub_district_name")));
                        dataDownloadInput.setCity_code(cursor.getInt(cursor.getColumnIndex("sub_district_id")));
                        cursor.moveToNext();
                        city.put(dataDownloadInput.getCity_name().trim(), dataDownloadInput.getCity_code());
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            sqLiteDatabase.close();
        }
        return city;
    }

    public boolean getNCCMatrix(String education_id,String durables_id,String NCC_catagory) {
        boolean status=false;
        String[]  arraydurables = durables_id.split(",");
        int durableLength=arraydurables.length;
        int nccs_education_id=getNCCSEducationID(education_id);
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select nccs_category from nccs_matrix where nccs_education_id="+nccs_education_id+" AND durables_id in("+durableLength+")";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String st_nccs_category=cursor.getString(cursor.getColumnIndex("nccs_category"));
                        if(NCC_catagory.equals(st_nccs_category)){
                            status=true;
                        }
                        cursor.moveToNext();
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return status;
    }
    public String getNCCMatrix2(String education_id,String durables_id,String NCC_catagory) {
        //boolean status=false;
        String status="";
        String[]  arraydurables = durables_id.split(",");
        int durableLength=arraydurables.length;
        int nccs_education_id=getNCCSEducationID(education_id);
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select nccs_category from nccs_matrix where nccs_education_id="+nccs_education_id+" AND durables_id in("+durableLength+")";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        status=cursor.getString(cursor.getColumnIndex("nccs_category"));
                        /*if(NCC_catagory.equals(st_nccs_category)){
                            status=true;
                        }*/
                        cursor.moveToNext();
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return status;
    }

    public int getNCCSEducationID(String education_id) {
        int nccs_education_id=0;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select nccs_education_id from nccs_matrix where education_id="+education_id+" limit 0,1";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        nccs_education_id=Integer.parseInt(cursor.getString(cursor.getColumnIndex("nccs_education_id")));
                        cursor.moveToNext();
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return nccs_education_id;
    }

    public ArrayList<SurveyModel> getSurveyList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<SurveyModel> arrayList = new ArrayList<>();
        String cluster_no= sharedPrefHelper.getString("cluster_no", "");
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select survey_id,household_name,status,cluster_no from survey where cluster_no='"+cluster_no+"'";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        SurveyModel surveyModel = new SurveyModel();
                        surveyModel.setSurvey_id(cursor.getString(cursor.getColumnIndex("survey_id")));
                        surveyModel.setHousehold_name(cursor.getString(cursor.getColumnIndex("household_name")));
                        surveyModel.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                        surveyModel.setCluster_no(cursor.getString(cursor.getColumnIndex("cluster_no")));

                        cursor.moveToNext();
                        arrayList.add(surveyModel);
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return arrayList;
    }

    public ArrayList<ClusterModel> getClusterList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ClusterModel> arrayList = new ArrayList<>();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select * from cluster";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        ClusterModel clusterModel = new ClusterModel();
                        clusterModel.setCluster_no(cursor.getString(cursor.getColumnIndex("cluster_no")));
                        clusterModel.setCensus_State_Code(cursor.getString(cursor.getColumnIndex("Census_State_Code")));
                        clusterModel.setState_Name(cursor.getString(cursor.getColumnIndex("State_Name")));
                        clusterModel.setTown_Village_Class(cursor.getString(cursor.getColumnIndex("Town_Village_Class")));
                        clusterModel.setCensus_District_Code(cursor.getString(cursor.getColumnIndex("Census_District_Code")));
                        clusterModel.setCensus_District_Name(cursor.getString(cursor.getColumnIndex("Census_District_Name")));
                        clusterModel.setCensus_Village_Town_Code(cursor.getString(cursor.getColumnIndex("Census_Village_Town_Code")));
                        clusterModel.setCensus_Village_Town_Name(cursor.getString(cursor.getColumnIndex("Census_Village_Town_Name")));
                        clusterModel.setUA_Component(cursor.getString(cursor.getColumnIndex("UA_Component")));
                        clusterModel.setUA_Component_code(cursor.getString(cursor.getColumnIndex("UA_Component_code")));
                        clusterModel.setBARC_Town_Code(cursor.getString(cursor.getColumnIndex("BARC_Town_Code")));
                        clusterModel.setOriginal_Town_Village(cursor.getString(cursor.getColumnIndex("Original_Town_Village")));
                        clusterModel.setOriginal_Town_Village_Code(cursor.getString(cursor.getColumnIndex("Original_Town_Village_Code")));
                        clusterModel.setSampling_town_class(cursor.getString(cursor.getColumnIndex("Sampling_town_class")));
                        clusterModel.setSP_No(cursor.getString(cursor.getColumnIndex("SP_No")));
                        clusterModel.setAfter_10_Voter_Address(cursor.getString(cursor.getColumnIndex("After_10_Voter_Address")));
                        clusterModel.setOriginal_address(cursor.getString(cursor.getColumnIndex("Original_address")));
                        clusterModel.setPrevious_10_Voter_Address(cursor.getString(cursor.getColumnIndex("Previous_10_Voter_Address")));
                        clusterModel.setPincode(cursor.getString(cursor.getColumnIndex("Pincode")));
                        clusterModel.setOperator_Agency(cursor.getString(cursor.getColumnIndex("Operator_Agency")));
                        clusterModel.setLock_status(cursor.getString(cursor.getColumnIndex("lock_status")));
                        clusterModel.setBI_Weighting_town_class(cursor.getString(cursor.getColumnIndex("BI_Weighting_town_class")));
                        clusterModel.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
                        clusterModel.setNCCCatagory(cursor.getString(cursor.getColumnIndex("nccs_category")));
                        clusterModel.setsample_size(cursor.getString(cursor.getColumnIndex("sample_size")));
                        clusterModel.setEP_address(cursor.getString(cursor.getColumnIndex("EP_address")));

                        cursor.moveToNext();
                        arrayList.add(clusterModel);
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return arrayList;
    }

    public int getTotallockrd() {
        int sum = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(lock_status) as total from cluster where lock_status = 1", null);
        if (cursor.moveToFirst())
            sum = cursor.getInt(cursor.getColumnIndex("total"));
        return sum;
        }

        public int getTotalsurvey() {
            int sum = 0;
            SQLiteDatabase db = this.getReadableDatabase();
            String countQuery = "select count(id) from survey";
            Cursor cursor = db.rawQuery(countQuery, null);
            if (cursor.moveToFirst())

                sum = cursor.getInt(cursor.getColumnIndex("count(id)"));
            return sum;
        }
 public int getTotalsurveyhousehold(int type) {
            int sum = 0;
     String search="";
     if(type!=0){
         String cluster_no= sharedPrefHelper.getString("cluster_no", "");
         search=" where cluster_no='"+cluster_no+"'";
     }
            SQLiteDatabase db = this.getReadableDatabase();
            String countQuery = "select count(household_name) from survey"+search+"";
            Cursor cursor = db.rawQuery(countQuery, null);
            if (cursor.moveToFirst())

                sum = cursor.getInt(cursor.getColumnIndex("count(household_name)"));
            return sum;
        }



 public int getChartValue(int status,int type) {
        int sum = 0;
        String search="";
     if(type!=0){
         String cluster_no= sharedPrefHelper.getString("cluster_no", "");
         search=" AND cluster_no='"+cluster_no+"'";
     }
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "select count(status) from survey where status ='" + status + "'"+search+"";
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst())

            sum = cursor.getInt(cursor.getColumnIndex("count(status)"));
        return sum;
    }
    public int getTotalchart4(int status,int type) {
        int sum = 0;
        String search="";
        if(type!=0){
            String cluster_no= sharedPrefHelper.getString("cluster_no", "");
            search=" AND cluster_no='"+cluster_no+"'";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "select count(status) from survey where status ='" + status + "'"+search+"";
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst())
            sum = cursor.getInt(cursor.getColumnIndex("count(status)"));
        return sum;
    }

    public int getTotalchartInprogress(int type) {
        String search="";
        if(type!=0){
            String cluster_no= sharedPrefHelper.getString("cluster_no", "");
            search=" AND cluster_no='"+cluster_no+"'";
        }
        int sum = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(status) as total from survey where status IN (2,0) "+search, null);
        if (cursor.moveToFirst())
            sum = cursor.getInt(cursor.getColumnIndex("total"));
        return sum;
    }

    public long updateSurveyDataInTable(String table, String whr, String survey_id, JSONObject jsonObject) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("survey_data", String.valueOf(jsonObject));

                inserted_id = db.update(table, values, whr + " = " + survey_id + "", null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }
    public long updateFamilyDataInTable(String table, String whr, String survey_id, JSONObject jsonObject) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("family_data", String.valueOf(jsonObject));

                inserted_id = db.update(table, values, whr + " = " + survey_id + "", null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }
    public long updateTVDataInTable(String table, String whr, String survey_id, JSONObject jsonObject) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("tv_data", String.valueOf(jsonObject));

                inserted_id = db.update(table, values, whr + " = " + survey_id + "", null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }




    public ArrayList<String> getClusterID() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select cluster_no from cluster";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
//                        PhcPojo phcPojo = new PhcPojo();
                        String name = cursor.getString(cursor.getColumnIndex("cluster_no"));

                        cursor.moveToNext();
                        arrayList.add(name);
                    }
                    db.close();
                }
            }
        } catch (Exception e) {


            e.printStackTrace();
            db.close();
        }
        return arrayList;
    }



    public String getSurveyData(String survey_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String surveyJSON="";
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select survey_data from survey where survey_id='"+survey_id+"' and flag=1 and status IN(2,0)";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        surveyJSON=cursor.getString(cursor.getColumnIndex("survey_data"));

                        cursor.moveToNext();
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return surveyJSON;
    }
    public String getSurveyFamilyData(String survey_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String surveyJSON="";
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select family_data from survey where survey_id='"+survey_id+"' and flag=1 and status IN(2,0)";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        surveyJSON=cursor.getString(cursor.getColumnIndex("family_data"));

                        cursor.moveToNext();
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return surveyJSON;
    }
    public String getSurveyTvData(String survey_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String surveyJSON="";
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select tv_data from survey where survey_id='"+survey_id+"' and flag=1 and status IN(2,0)";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        surveyJSON=cursor.getString(cursor.getColumnIndex("tv_data"));

                        cursor.moveToNext();
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return surveyJSON;
    }

    public void saveMasterTable(ContentValues contentValues, String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        long idsds = db.insert(table_name, null, contentValues);
        Log.d("LOG", idsds + " id");
        db.close();
    }

    public long updateServerId(String table, int survey_id, int surveyID) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("id", surveyID);

                inserted_id = db.update(table, values, "survey_id" + " = " + survey_id + "", null);

                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }
    public long updateStatus(String table, String survey_id, int surveyID) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("status", surveyID);

                inserted_id = db.update(table, values, "survey_id" + " = '" + survey_id + "'", null);

                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }

    public long updateAudioFileInTable(String table, int survey_id, String audio_file) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("audio_recording", audio_file);

                inserted_id = db.update(table, values, "survey_id" + " = " + survey_id + "", null);

                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }

    public long updateLocalFlag(String screenType, String table, int survey_id, int flag) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("flag", flag);
                if (screenType.equals("household_survey"))
                    values.put("status", 1);
                else if (screenType.equals("terminate"))
                    values.put("status", 3);
                else if (screenType.equals("halt"))
                    values.put("status", 2);
                else if (screenType.equals("partial"))
                    values.put("status", 0);

                inserted_id = db.update(table, values, "survey_id" + " = " + survey_id + "", null);

                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }

    public ArrayList<SurveyModel> getAllSurveyDataFromTableToSync() {
        ArrayList<SurveyModel> arrayList = new ArrayList<>();
        SurveyModel surveyModel;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select survey_id, survey_data, audio_recording from survey where flag=0 and status=1";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        surveyModel=new SurveyModel();
                        surveyModel.setSurvey_id(cursor.getString(cursor.getColumnIndex("survey_id")));
                        surveyModel.setSurvey_data(cursor.getString(cursor.getColumnIndex("survey_data")));
                        surveyModel.setAudio_recording(cursor.getString(cursor.getColumnIndex("audio_recording")));

                        cursor.moveToNext();
                        arrayList.add(surveyModel);
                    }
                    db.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return arrayList;
    }
    public String getFamilyDataFromTable(String survey_id) {
        String family_data = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select family_data from survey where survey_id= '" + survey_id + "'";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        family_data = cursor.getString(cursor.getColumnIndex("family_data"));
                        cursor.moveToNext();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return family_data;
    }
    public String getTVDataFromTable(String survey_id) {
        String tv_data = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select tv_data from survey where survey_id= '" + survey_id + "'";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        tv_data = cursor.getString(cursor.getColumnIndex("tv_data"));
                        cursor.moveToNext();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return tv_data;
    }
    public int getClusterSampleSizeFromTable(String cluster_no) {
        int sampleSize = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select sample_size from cluster where cluster_no= '" + cluster_no + "'";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        sampleSize = cursor.getInt(cursor.getColumnIndex("sample_size"));
                        cursor.moveToNext();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return sampleSize;
    }
    public int getClusterCompletedFromTable(String cluster_no) {
        int sampleSize = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select completed_record from cluster where cluster_no= '" + cluster_no + "'";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        sampleSize = cursor.getInt(cursor.getColumnIndex("completed_record"));
                        cursor.moveToNext();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return sampleSize;
    }
    public long updateClusterTable(String cluster_no) {
        int totalSurveyForCluster=sharedPrefHelper.getInt("totalSurvey",0);
        totalSurveyForCluster=totalSurveyForCluster+1;

        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("completed_record", totalSurveyForCluster);
                inserted_id = db.update("cluster", values, "cluster_no = '" + cluster_no + "'", null);
                sharedPrefHelper.setInt("totalSurvey",totalSurveyForCluster);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }
    public int getTotalSurveyForCluster(String cluster_no) {
        int sum = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "select count(cluster_no) from survey where cluster_no ='" + cluster_no + "' and status IN(1,0)";
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst())
            sum = cursor.getInt(cursor.getColumnIndex("count(cluster_no)"));
        return sum;
    }
    public String getSurveyIDFromTable(String survey_id) {
        String surveyID = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "Select survey_id from survey where survey_id= '" + survey_id + "'";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        surveyID = cursor.getString(cursor.getColumnIndex("survey_id"));
                        cursor.moveToNext();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return surveyID;
    }
}
