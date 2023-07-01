package impel.imhealthy.adminapp.Excel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import impel.imhealthy.adminapp.Model.DaySortingModel;
import impel.imhealthy.adminapp.Model.ExpenseModel;
import impel.imhealthy.adminapp.Model.MonthSortingModel;
import impel.imhealthy.adminapp.Model.WeekSortingModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "naturesafari.db";
    public static final String TABLE_NAME = "naturesafari_table";
    public static final String COL_1 = "UserId";
    public static final String COL_2 = "User_Name";
    public static final String COL_3 = "Temperature_Celcius";
    public static final String COL_4 = "Temperature_Fahrenheit";
    public static final String COL_5 = "Spo2";
    public static final String COL_6 = "BPM";
    public static final String COL_7 = "Address";
    public static final String COL_8 = "Time";
    public static final String COL_9 = "Date";

    private Context mContext;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
        } catch (Exception e) {
        }

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (UserId TEXT, User_Name TEXT , Temperature_Celcius TEXT,Temperature_Fahrenheit TEXT , Spo2 TEXT,BPM TEXT , Address TEXT, Time TEXT,Date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean inertData(String UserId, String User_Name, String Temperature_Celcius, String Temperature_Fahrenheit, String Spo2, String BPM, String Address, String Time, String Date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, UserId);
        contentValues.put(COL_2, User_Name);
        contentValues.put(COL_3, Temperature_Celcius);
        contentValues.put(COL_4, Temperature_Fahrenheit);
        contentValues.put(COL_5, Spo2);
        contentValues.put(COL_6, BPM);
        contentValues.put(COL_7, Address);
        contentValues.put(COL_8, Time);
        contentValues.put(COL_9, Date);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public void delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public java.util.List<DaySortingModel> getAllDataHistory(String date) {
        java.util.List<DaySortingModel> model = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;// +" WHERE "+COL_2+"="+name;
        SQLiteDatabase db = this.getWritableDatabase();
        android.database.Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DaySortingModel emodel = new DaySortingModel();

                if (cursor.getString(8).equals(date)) {
                    emodel.setId(cursor.getString(1));
                    emodel.setMember_name(cursor.getString(0));
                    emodel.setFever_c(cursor.getString(2));
                    emodel.setFever_f(cursor.getString(3));
                    emodel.setOxymetry_spo2(cursor.getString(4));
                    emodel.setOxymetry_bpm(cursor.getString(5));
                    emodel.setAddress(cursor.getString(6));
                    emodel.setDate(cursor.getString(7));
                    emodel.setTime(cursor.getString(8));
                    model.add(emodel);
                }
            } while (cursor.moveToNext());
        }
        db.close();
        return model;
    }

    public java.util.List<WeekSortingModel> getAllDataHistoryWeekly(String date, String lastWeekDate) {


        java.util.List<WeekSortingModel> model = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;// + " WHERE " + COL_9 + " BETWEEN '" + date + "' AND '" + lastWeekDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        android.database.Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WeekSortingModel emodel = new WeekSortingModel();

                emodel.setId(cursor.getString(1));
                emodel.setMember_name(cursor.getString(0));
                emodel.setFever_c(cursor.getString(2));
                emodel.setFever_f(cursor.getString(3));
                emodel.setOxymetry_spo2(cursor.getString(4));
                emodel.setOxymetry_bpm(cursor.getString(5));
                emodel.setAddress(cursor.getString(6));
                emodel.setDate(cursor.getString(7));
                emodel.setTime(cursor.getString(8));
                model.add(emodel);

            } while (cursor.moveToNext());
        }
        db.close();
        return model;
    }

    public java.util.List<MonthSortingModel> getAllDataHistoryMonthly(String date, String lastWeekDate) {

        java.util.List<MonthSortingModel> model = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;// + " WHERE " + COL_9 + " BETWEEN '" + date + "' AND '" + lastWeekDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        android.database.Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MonthSortingModel emodel = new MonthSortingModel();


                emodel.setId(cursor.getString(1));
                emodel.setMember_name(cursor.getString(0));
                emodel.setFever_c(cursor.getString(2));
                emodel.setFever_f(cursor.getString(3));
                emodel.setOxymetry_spo2(cursor.getString(4));
                emodel.setOxymetry_bpm(cursor.getString(5));
                emodel.setAddress(cursor.getString(6));
                emodel.setDate(cursor.getString(7));
                emodel.setTime(cursor.getString(8));
                model.add(emodel);

            } while (cursor.moveToNext());
        }
        db.close();
        return model;
    }

    public JSONArray getResults() {
        Context context = mContext;

        String myPath = String.valueOf(context.getDatabasePath("naturesafari.db"));// Set path to your database

        String myTable = DatabaseHelper.TABLE_NAME;//Set name of your table

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {

//                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        //   Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
//                SelectItemListActivity.tltitem.setText(""+i);
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public long getProfilesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }
}
