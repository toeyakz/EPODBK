package ws.epod.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;


import android.util.Log;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;
import io.requery.android.database.sqlite.SQLiteStatement;
import ws.epod.ObjectClass.Var;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = Var.dbname;
    private static final int DATABASE_VERSION = 3;


    /*Unlock Exemple*/
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //  Log.d("ASfkasdlfgvno", "onCreate: "+sqLiteDatabase.getVersion());

        String sql = "";

        String plan = "Plan";

        sql = "CREATE TABLE IF NOT EXISTS '" + plan + "' (id TEXT(255,0), delivery_date TEXT(255,0), vehicle_name TEXT(255,0), blackbox TEXT(255,0), delivery_no TEXT(255,0), " +
                "plan_seq TEXT(255,0), station_id TEXT(255,0), station_code TEXT(255,0), station_name TEXT(255,0), station_address TEXT(255,0)," +
                "station_lat Decimal(9,6), station_lon Decimal(8,6), station_area TEXT(255,0), plan_in TEXT(255,0), plan_out TEXT(255,0)" +
                ", consignment_no TEXT(255,0), order_no TEXT(255,0), activity_type TEXT(255,0), box_no TEXT(255,0), waybill_no TEXT(255,0), weight TEXT(255,0)" +
                ", actual_seq TEXT(255,0), actual_lat Decimal(9,6), actual_lon Decimal(8,6), time_actual_in TEXT(255,0), time_actual_out TEXT(255,0)" +
                ", time_begin TEXT(255,0), time_end TEXT(255,0), signature TEXT(255,0), is_scaned TEXT(255,0), is_save TEXT(255,0),status_order_no TEXT(255,0)" +
                ", comment TEXT(255,0), picture1 TEXT(255,0), picture2 TEXT(255,0), picture3 TEXT(255,0), status_upload TEXT(255,0), driver_code TEXT(255,0)" +
                ", driver_name TEXT(255,0), modified_date TEXT(255,0), trash TEXT(255,0), total_box TEXT(255,0), UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS consignment (id TEXT(255,0), item_code item_code(255,0), item_send_time TEXT(255,0), consignment_no TEXT(255,0), subsidiary_cd TEXT(255,0), deli_note_no TEXT(255,0)," +
                "crd TEXT(255,0), ship_plan_date TEXT(255,0), settlement_method TEXT(255,0), cust_shipmode TEXT(255,0), cust_cd TEXT(255,0), ship_to_cd TEXT(255,0), shipto_name TEXT(255,0)," +
                "ship_mode TEXT(255,0), ship_to_postal_cd TEXT(255,0), item_remarks TEXT(255,0), so_voucher_no TEXT(255,0), global_no TEXT(255,0), header_ref TEXT(255,0)" +
                ",  deli_note_amount_price TEXT(255,0), comet_seq TEXT(255,0), warehouse TEXT(255,0), detail_remarks TEXT(255,0), isPlan TEXT(255,0)" +
                ", modified_by TEXT(255,0), modified_date TEXT(255,0), create_by TEXT(255,0), create_date TEXT(255,0), trash TEXT(255,0)" +
                ", inactive TEXT(255,0), status TEXT(255,0), activity_type TEXT(255,0), total_box TEXT(255,0), UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS image (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,name_img TEXT(255,0), status_img TEXT(255,0), UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS pic_sign (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, consignment_no TEXT(255,0), order_no TEXT(255,0), invoice_no TEXT(255,0)" +
                ",pic_sign_load TEXT(255,0),pic_sign_unload TEXT(255,0),comment_load TEXT(255,0), comment_unload TEXT(255,0), date_sign_load TEXT(255,0),date_sign_unload TEXT(255,0), delivery_no TEXT(255,0)" +
                ",status_load TEXT(255,0),status_unload TEXT(255,0),status_upload_invoice TEXT(255,0), status_delete TEXT(255,0), create_date TEXT(255,0)" +
                ", UNIQUE(consignment_no,order_no,invoice_no,delivery_no));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS comment_invoice (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, consignment_no TEXT(255,0), order_no TEXT(255,0), invoice_no TEXT(255,0)" +
                ",comment_load TEXT(255,0), comment_unload TEXT(255,0), status_load TEXT(255,0), status_unload TEXT(255,0), delivery_no TEXT(255,0), status_upload_comment TEXT(255,0)" +
                ", create_date TEXT(255,0), UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS Var (Var TEXT NOT NULL,Value TEXT,Value2 TEXT,MODIFIED_DATE TEXT,PRIMARY KEY(Var));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS  login (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,username TEXT(255,0),pass TEXT(255,0),serial TEXT(255,0),driver_id TEXT(255,0)" +
                ",driver_fname TEXT(255,0),driver_lname TEXT(255,0),vehicle_id TEXT(255,0),vehicle_name TEXT(255,0),status_login TEXT(255,0)" +
                ",driver_brand TEXT(255,0), modified_date TEXT(255,0), UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS reason (id TEXT(255,0), name TEXT(255,0) , UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS image_invoice (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,name_img TEXT(255,0), status_img TEXT(255,0), create_date TEXT(255,0) , UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS header_waybill (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, waybill_no TEXT(255,0), date_scan TEXT(255,0), lat Decimal(9,6), lon Decimal(8,6)," +
                "is_scanned TEXT(255,0), status_complete TEXT(255,0), UNIQUE(id));";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        Log.d("NARIS", "onupgrade from ver." + oldversion + " -> " + newversion);
        String sql = "";
        switch (newversion) {
            case 2: {

            }
        }


    }


    public Cursor selectDB(String SQLCmd) {
        Cursor SelectCursor = null;
        SQLiteDatabase readableDatabase = null;
        try {
            readableDatabase = getReadableDatabase();
//	        readableDatabase.beginTransaction();
            SelectCursor = readableDatabase.rawQuery(SQLCmd, null);
        } catch (Exception e) {
            Log.d("SelectDB", e.getMessage());
            w.unlock();
        }
//    	finally {
//    		readableDatabase.endTransaction();
//    	}
        return SelectCursor;
    }

    public boolean rawQuery(String sql) {
        SQLiteDatabase writableDatabase = null;
        try {
            writableDatabase = getWritableDatabase();

            writableDatabase.rawQuery(sql, null);
            return true;
        } catch (Exception e) {
            Log.d("SelectDB", e.getMessage());
            w.unlock();
            return false;
        }
//    	finally {
//    		writableDatabase.endTransaction();
//    	}
    }

    public SQLiteDatabase db() {
        SQLiteDatabase writableDatabase = null;
        writableDatabase = getWritableDatabase();
        return writableDatabase;
    }


    public boolean execDB(String sql) {
        SQLiteDatabase writableDatabase = null;
        try {
            writableDatabase = getWritableDatabase();

            writableDatabase.execSQL(sql);
            return true;
        } catch (Exception e) {
            Log.d("SelectDB", e.getMessage());
            w.unlock();
            return false;
        }
//    	finally {
//    		writableDatabase.endTransaction();
//    	}
    }


    public void insertDB(String table, String nullColumnHack, ContentValues values) {
        final SQLiteDatabase writableDatabase = getWritableDatabase();

        writableDatabase.insert(table, nullColumnHack, values);
    }

    public void updateDB(String table, ContentValues values, String whereClause, String[] whereArgs) {
        final SQLiteDatabase writableDatabase = getWritableDatabase();

        writableDatabase.update(table, values, whereClause, whereArgs);
    }

    public Cursor rawDB(String sql, String[] selectionArgs) {
        Cursor SelectCursor = null;
        try {
            final SQLiteDatabase readableDatabase = getReadableDatabase();
            SelectCursor = readableDatabase.rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            Log.d("SelectDB", e.getLocalizedMessage());
        }
        return SelectCursor;
    }

    public boolean isopenDB() {
        final SQLiteDatabase writableDatabase = getWritableDatabase();

        return writableDatabase.isOpen();
    }

    public SQLiteStatement compileStatementDB(String sql) {
        SQLiteStatement insert = null;
        try {
            final SQLiteDatabase readableDatabase = getReadableDatabase();
            insert = readableDatabase.compileStatement(sql);
        } catch (Exception e) {
            Log.d("SelectDB", e.getMessage());
        }
        return insert;
    }

    public int getVersion() {
        SQLiteDatabase db = getWritableDatabase();
        int version = 0;
        try {
            version = db.getVersion(); // what value do you get here?
        } catch (SQLiteException e) {
        }
        return version;
    }

    public boolean deleteDB() {
        SQLiteDatabase db = getWritableDatabase();
        String path = db.getPath();
        return SQLiteDatabase.deleteDatabase(new File(path));
    }


//    @Override
//    public void onCreate(io.requery.android.database.sqlite.SQLiteDatabase db) {
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.d("NARIS", "onupgrade from ver."+ oldVersion + " -> "+newVersion );
//        String sql = "";
//        switch(newVersion){
//            case 2 :{
//
//            }
//        }
//    }
}
