package impel.imhealthy.adminapp;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.ArrayList;

import impel.imhealthy.adminapp.Model.ExpenseModel;
import impel.imhealthy.adminapp.Model.ToyotaModel;

public class DatabaseHelperNew extends SQLiteOpenHelper {
    @Override
    public void onOpen(SQLiteDatabase database) {
        super.onOpen(database);
        if(Build.VERSION.SDK_INT >= 28)
        {
            database.disableWriteAheadLogging();
        }
    }
    public static final String DATABASE_NAME = "Student1.db";
    public static final String TABLE_NAME = "SmsTable";
    public static final String COL_1 = "MODEL";
    public static final String COL_2 = "SUFFIX";
    public static final String COL_3 = "EXSHOWROOM";
    public static final String COL_4 = "individual";
    public static final String COL_5 = "taxicompany";
    public static final String COL_6 = "insurance";
    public static final String COL_7 = "tcs";
    public static final String COL_8 = "DepoCharges";
    public static final String COL_9 = "NumberPlate";
    public static final String COL_10 = "fastag";
    public static final String COL_11 = "accessories";
    public static final String COL_12 = "carkit";
    public static final String COL_13 = "gearslkm";
    public static final String COL_14 = "tyotasmiles";
    public static final String COL_15 = "onroadindividual";
    public static final String COL_16 = "onroadcompany";



    private Activity mContext;

    public DatabaseHelperNew(@androidx.annotation.Nullable Activity context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (MODEL TEXT , SUFFIX TEXT , EXSHOWROOM TEXT , individual TEXT" +
                ", taxicompany TEXT , insurance TEXT , tcs TEXT , DepoCharges TEXT" +
                ", NumberPlate TEXT , fastag TEXT , accessories TEXT , carkit TEXT" +
                ", gearslkm TEXT , tyotasmiles TEXT , onroadindividual TEXT , onroadcompany TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean inertData(String MODEL, String SUFFIX, String EXSHOWROOM, String individual, String taxicompany, String insurance,
                                 String tcs, String DepoCharges, String NumberPlate, String fastag, String accessories, String carkit,
                                 String gearslkm, String tyotasmiles, String onroadindividual, String onroadcompany) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,MODEL);
        contentValues.put(COL_2,SUFFIX);
        contentValues.put(COL_3,EXSHOWROOM);
        contentValues.put(COL_4,individual);
        contentValues.put(COL_5,taxicompany);
        contentValues.put(COL_6,insurance);
        contentValues.put(COL_7,tcs);
        contentValues.put(COL_8,DepoCharges);
        contentValues.put(COL_9,NumberPlate);
        contentValues.put(COL_10,fastag);
        contentValues.put(COL_11,accessories);
        contentValues.put(COL_12,carkit);
        contentValues.put(COL_13,gearslkm);
        contentValues.put(COL_14,tyotasmiles);
        contentValues.put(COL_15,onroadindividual);
        contentValues.put(COL_16,onroadcompany);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public java.util.List<ToyotaModel> homedeletedata() {
        java.util.List<ToyotaModel> model = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        android.database.Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                    ToyotaModel emodel = new ToyotaModel();
                    emodel.setMODEL(cursor.getString(0));
                    emodel.setSUFFIX(cursor.getString(1));
                    emodel.setEXSHOWROOM(cursor.getString(2));
                    emodel.setIndividual(cursor.getString(3));
                    emodel.setTaxicompany(cursor.getString(4));
                    emodel.setInsurance(cursor.getString(5));
                    emodel.setTcs(cursor.getString(6));
                    emodel.setDepoCharges(cursor.getString(7));
                    emodel.setNumberPlate(cursor.getString(8));
                    emodel.setFastag(cursor.getString(9));
                    emodel.setAccessories(cursor.getString(10));
                    emodel.setCarkit(cursor.getString(11));
                    emodel.setGearslkm(cursor.getString(12));
                    emodel.setTyotasmiles(cursor.getString(13));
                    emodel.setOnroadindividual(cursor.getString(14));
                    emodel.setOnroadcompany(cursor.getString(15));
                    model.add(emodel);
            } while (cursor.moveToNext());
        }
        db.close();
        return model;
    }

    public java.util.List<ExpenseModel> homedeletedata(String name) {
        java.util.List<ExpenseModel> model = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;// +" WHERE "+COL_2+"="+name;
        SQLiteDatabase db = this.getWritableDatabase();
        android.database.Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ExpenseModel emodel = new ExpenseModel();
                if ((cursor.getString(4).equals(name))) {

                    emodel.setId(cursor.getString(0));
                    emodel.setGroupname(cursor.getString(1));
                    emodel.setCustomername(cursor.getString(2));
                    emodel.setCustomernumber(cursor.getString(3));
                    emodel.setDummy(cursor.getString(4));
                    model.add(emodel);
                }
            } while (cursor.moveToNext());
        }
        db.close();
        return model;
    }
    ////////////HISTORY DATA


    public boolean updateDatas(String id, String groupname, String customername, String customernumber, String dummy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, groupname);
        contentValues.put(COL_3, customername);
        contentValues.put(COL_4, customernumber);
        contentValues.put(COL_5, dummy);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});

        return true;
    }
    //Group Name List Count
    public String getTaskCount(String tasklist_id){
        SQLiteDatabase db = this.getWritableDatabase();
        android.database.Cursor cursor= db.rawQuery("SELECT COUNT (*) FROM SmsTable WHERE groupname="+ "'"+tasklist_id+"'" , null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        String s= String.valueOf(count);
        return s;
    }


}
