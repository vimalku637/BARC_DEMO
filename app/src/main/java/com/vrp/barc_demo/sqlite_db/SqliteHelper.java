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
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "barc.db";
    static final int DATABASE_VERSION = 1;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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

    public ArrayList<SurveyModel> getSurveyList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<SurveyModel> arrayList = new ArrayList<>();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                String query = "select survey_id from survey";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        SurveyModel surveyModel = new SurveyModel();
                        surveyModel.setSurvey_id(cursor.getString(cursor.getColumnIndex("survey_id")));

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

    public long updateFlagInTable(String table, String whr, int local_id, String flag) {
        long inserted_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db != null && db.isOpen() && !db.isReadOnly()) {
                ContentValues values = new ContentValues();
                values.put("flag", flag);

                inserted_id = db.update(table, values, whr + " = " + local_id + "", null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
        return inserted_id;
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
}
