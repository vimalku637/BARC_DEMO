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

import com.vrp.barc_demo.models.AnswerModel;
import com.vrp.barc_demo.models.ClusterModel;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "barc.db";
    private static final int DATABASE_VERSION = 1;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        Log.e("version", "outside" + version);

        File dbFile = ctx.getDatabasePath(DATABASE_NAME);
        //  checkDbVersion(dbFile);
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void saveSurveyDataInTable(JSONObject jsonObject, String survey_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();

                values.put("survey_id", survey_id);
                values.put("survey_data", String.valueOf(jsonObject));
                values.put("state", "2");
                values.put("district", "1");
                values.put("city", "4");
                values.put("town", "3");
                values.put("user_id", sharedPrefHelper.getString("user_id", ""));
                values.put("date_time", "");
                values.put("household_name", sharedPrefHelper.getString("interviewer_name", ""));
                values.put("address", "");
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



//    public HashMap<String, Integer> getCity() {
//        HashMap<String, Integer> partner = new HashMap<>();
//        PartnerPojo partnerPojo;
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        try {
//            if (sqLiteDatabase != null && sqLiteDatabase.isOpen() && !sqLiteDatabase.isReadOnly()) {
//                //String query = "Select  partner_id,partner_name from partner";
//                String query = "Select partner_id,partner_name from partner order by partner_name asc";
//                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
//                if (cursor != null && cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    while (!cursor.isAfterLast()) {
//
//                        partnerPojo = new PartnerPojo();
//                        partnerPojo.setPartner_id(cursor.getInt(cursor.getColumnIndex("partner_id")));
//                        partnerPojo.setPartner_name(cursor.getString(cursor.getColumnIndex("partner_name")));
//                        cursor.moveToNext();
//                        partner.put(partnerPojo.getPartner_name().trim(), partnerPojo.getPartner_id());
//
//                    }
//
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            sqLiteDatabase.close();
//        }
//        return partner;
//    }
//
//

    public ArrayList<SurveyModel> getSurveyList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<SurveyModel> arrayList = new ArrayList<>();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select survey_id,household_name,status from survey";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        SurveyModel surveyModel = new SurveyModel();
                        surveyModel.setSurvey_id(cursor.getString(cursor.getColumnIndex("survey_id")));
                        surveyModel.setHousehold_name(cursor.getString(cursor.getColumnIndex("household_name")));
                        surveyModel.setStatus(cursor.getString(cursor.getColumnIndex("status")));

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

    public int getTotalsurvey() {
            int sum = 0;
            SQLiteDatabase db = this.getReadableDatabase();
            String countQuery = "select count(survey_id) from survey";
            Cursor cursor = db.rawQuery(countQuery, null);
            if (cursor.moveToFirst())

                sum = cursor.getInt(cursor.getColumnIndex("count(survey_id)"));
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

    public String getSurveyData(String survey_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String surveyJSON="";
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select survey_data from survey where survey_id='"+survey_id+"' and flag=0 and status=0";
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

                inserted_id = db.update(table, values, "survey_id" + " = " + survey_id + "", null);

                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
    }
}
