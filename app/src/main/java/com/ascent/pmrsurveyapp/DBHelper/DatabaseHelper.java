package com.ascent.pmrsurveyapp.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "pmrdb.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS cities (" +
                "id INTEGER," +
                "name TEXT," +
                "country_id INTEGER,"+
                "state_id INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS state (" +
                "id INTEGER," +
                "name TEXT," +
                "country_id INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS country (" +
                "id INTEGER," +
                "name TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS company (" +
                "id INTEGER," +
                "name TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS account (" +
                "id INTEGER," +
                "name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS state");
        db.execSQL("DROP TABLE IF EXISTS company");
        db.execSQL("DROP TABLE IF EXISTS country");
        db.execSQL("DROP TABLE IF EXISTS cities");
        onCreate(db);
    }

    public List<CountryModel> getAllCountry() {
        List<CountryModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM country", null);

        if (cursor.moveToFirst()) {
            do {
                CountryModel emp = new CountryModel();
                emp.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                emp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                list.add(emp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<CountryModel> getAllCompany() {
        List<CountryModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM company", null);

        if (cursor.moveToFirst()) {
            do {
                CountryModel emp = new CountryModel();
                emp.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                emp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                list.add(emp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<CountryModel> getAllAccount() {
        List<CountryModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account", null);

        if (cursor.moveToFirst()) {
            do {
                CountryModel emp = new CountryModel();
                emp.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                emp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                list.add(emp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<StateModel> getAllStates(int countryId) {
        List<StateModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM state Where country_id ="+countryId, null);
      //101
        if (cursor.moveToFirst()) {
            do {
                StateModel emp = new StateModel();
                emp.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                emp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                emp.country_id = cursor.getString(cursor.getColumnIndexOrThrow("country_id"));
                list.add(emp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<CityModel> getAllCities(int stateId) {
        List<CityModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cities Where state_id ="+stateId, null);
        //101
        if (cursor.moveToFirst()) {
            do {
                CityModel emp = new CityModel();
                emp.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                emp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                emp.country_id = cursor.getString(cursor.getColumnIndexOrThrow("country_id"));
                emp.state_id = cursor.getString(cursor.getColumnIndexOrThrow("state_id"));
                list.add(emp);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public void insertCounty(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        db.insertWithOnConflict("country", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertCompany(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        db.insertWithOnConflict("company", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertAccount(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        db.insertWithOnConflict("account", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertStates(int id, String name , int country_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("country_id", country_id);
        db.insertWithOnConflict("state", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertCities(int id, String name , int country_id, int state_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("country_id", country_id);
        cv.put("state_id", state_id);
        db.insertWithOnConflict("cities", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

}


