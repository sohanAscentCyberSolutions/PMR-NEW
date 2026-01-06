package com.ascent.pmrsurveyapp.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ExcelImporter extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "CSVImporter";
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final ImportListener listener;


    public ExcelImporter(Context context, DatabaseHelper dbHelper, ImportListener listener) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            importCSVtoSQLiteCountry(context , dbHelper);
            importCSVtoSQLiteState(context , dbHelper);
            importCSVtoSQLiteCities(context , dbHelper);;
            importCSVtoSQLiteCompany(context , dbHelper);;
            importCSVtoSQLiteAccount(context , dbHelper);;
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (listener != null) {
            listener.onImportComplete(result);
        }
    }

    public static void importCSVtoSQLiteCountry(Context context, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ✅ Check if data already exists
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM country", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            Log.d(TAG, "Data already imported. Skipping import.");
            return;
        }

        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("country.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) { // skip header line
                    headerSkipped = true;
                    continue;
                }

                String[] tokens = line.split(",");
                int id = Integer.parseInt(tokens[0].trim());
                String name = tokens[1].trim();
                dbHelper.insertCounty(id, name);
            }
            reader.close();
            Log.d(TAG, "CSV import completed successfully!");

        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV", e);
        }
    }

    public static void importCSVtoSQLiteCompany(Context context, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ✅ Check if data already exists
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM company", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            Log.d(TAG, "Data already imported. Skipping import.");
            return;
        }

        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("company.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) { // skip header line
                    headerSkipped = true;
                    continue;
                }

                String[] tokens = line.split(",");
                int id = Integer.parseInt(tokens[0].trim());
                String name = tokens[3].trim();
                dbHelper.insertCompany(id, name);
            }
            reader.close();
            Log.d(TAG, "CSV import completed successfully!");

        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV", e);
        }
    }

    public static void importCSVtoSQLiteAccount(Context context, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ✅ Check if data already exists
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM account", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            Log.d(TAG, "Data already imported. Skipping import.");
            return;
        }

        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("account.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) { // skip header line
                    headerSkipped = true;
                    continue;
                }

                String[] tokens = line.split(",");
                int id = Integer.parseInt(tokens[0].trim());
                String name = tokens[2].trim();
                dbHelper.insertAccount(id, name);
            }
            reader.close();
            Log.d(TAG, "CSV account import completed successfully!");

        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV", e);
        }
    }

    public static void importCSVtoSQLiteState(Context context, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ✅ Check if data already exists
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM state", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            Log.d(TAG, "Data already imported. Skipping import.");
            return;
        }

        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("state.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));

            String[] nextLine;
            boolean headerSkipped = false;

            while ((nextLine = reader.readNext()) != null) {
                if (!headerSkipped) { // skip header
                    headerSkipped = true;
                    continue;
                }

                if (nextLine.length >= 3) {
                    int id = Integer.parseInt(nextLine[0].trim());
                    String name = nextLine[1].trim();
                    int countryId = Integer.parseInt(nextLine[2].trim());
                    dbHelper.insertStates(id, name, countryId);
                }
            }

            reader.close();
            Log.d(TAG, "CSV states import completed successfully!");

        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV", e);
        }
    }

    public static void importCSVtoSQLiteCities(Context context, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // ✅ Check if data already exists
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM cities", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            Log.d(TAG, "Data already imported. Skipping import.");
            return;
        }

        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("cityall.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));

            String[] nextLine;
            boolean headerSkipped = false;

            while ((nextLine = reader.readNext()) != null) {
                if (!headerSkipped) { // skip header
                    headerSkipped = true;
                    continue;
                }

                if (nextLine.length >= 4) {
                    int id = Integer.parseInt(nextLine[0].trim());
                    String name = nextLine[1].trim();
                    int countryId = Integer.parseInt(nextLine[2].trim());
                    int stateId = Integer.parseInt(nextLine[3].trim());
                    dbHelper.insertCities(id, name, countryId,stateId);
                }
            }

            reader.close();
            Log.d(TAG, "CSV cities import completed successfully!");

        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV", e);
        }
    }
}


