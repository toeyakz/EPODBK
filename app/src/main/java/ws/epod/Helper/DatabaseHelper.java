package ws.epod.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import ws.epod.ObjectClass.Var;

public class DatabaseHelper extends SQLiteOpenHelper
{

    private static String DATABASE_NAME = Var.dbname;
    private static final int DATABASE_VERSION = 1;


    /*Unlock Exemple*/
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public DatabaseHelper( Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate( SQLiteDatabase sqLiteDatabase)
    {



    }

    @Override
    public void onUpgrade( SQLiteDatabase sqLiteDatabase, int oldversion, int newversion)
    {
        Log.d("NARIS", "onupgrade from ver."+ oldversion + " -> "+newversion );
        String sql = "";
        switch(newversion){
            case 2 :{

            }
        }


    }



    public Cursor selectDB( String SQLCmd)
    {
        Cursor SelectCursor = null;
        SQLiteDatabase readableDatabase = null;
        try
        {
            readableDatabase = getReadableDatabase();
//	        readableDatabase.beginTransaction();
            SelectCursor = readableDatabase.rawQuery(SQLCmd, null);
        }
        catch(Exception e)
        {
            Log.d("SelectDB",e.getMessage());
            w.unlock();
        }
//    	finally {
//    		readableDatabase.endTransaction();
//    	}
        return SelectCursor;
    }

    public boolean rawQuery( String sql)
    {
        SQLiteDatabase writableDatabase = null;
        try
        {
            writableDatabase = getWritableDatabase();

            writableDatabase.rawQuery(sql,null);
            return true;
        }
        catch(Exception e)
        {
            Log.d("SelectDB",e.getMessage());
            w.unlock();
            return false;
        }
//    	finally {
//    		writableDatabase.endTransaction();
//    	}
    }

    public SQLiteDatabase db(){
        SQLiteDatabase writableDatabase = null;
        writableDatabase = getWritableDatabase();
        return writableDatabase;
    }



    public boolean execDB( String sql)
    {
        SQLiteDatabase writableDatabase = null;
        try
        {
            writableDatabase = getWritableDatabase();

            writableDatabase.execSQL(sql);
            return true;
        }
        catch(Exception e)
        {
            Log.d("SelectDB",e.getMessage());
            w.unlock();
            return false;
        }
//    	finally {
//    		writableDatabase.endTransaction();
//    	}
    }


    public void insertDB( String table, String nullColumnHack, ContentValues values)
    {
        final SQLiteDatabase writableDatabase = getWritableDatabase();

        writableDatabase.insert(table, nullColumnHack, values);
    }

    public void updateDB( String table, ContentValues values, String whereClause, String[] whereArgs)
    {
        final SQLiteDatabase writableDatabase = getWritableDatabase();

        writableDatabase.update(table, values, whereClause, whereArgs);
    }

    public Cursor rawDB( String sql, String[] selectionArgs)
    {
        Cursor SelectCursor = null;
        try
        {
            final SQLiteDatabase readableDatabase = getReadableDatabase();
            SelectCursor = readableDatabase.rawQuery(sql, selectionArgs);
        }
        catch(Exception e)
        {
            Log.d("SelectDB",e.getLocalizedMessage());
        }
        return SelectCursor;
    }

    public boolean isopenDB()
    {
        final SQLiteDatabase writableDatabase = getWritableDatabase();

        return writableDatabase.isOpen();
    }

    public SQLiteStatement compileStatementDB( String sql)
    {
        SQLiteStatement insert = null;
        try
        {
            final SQLiteDatabase readableDatabase = getReadableDatabase();
            insert = readableDatabase.compileStatement(sql);
        }
        catch(Exception e)
        {
            Log.d("SelectDB",e.getMessage());
        }
        return insert;
    }

    public int getVersion(){
        SQLiteDatabase db =getWritableDatabase();
        int version =0;
        try {
            version = db.getVersion(); // what value do you get here?
        }catch (SQLiteException e){}
        return version;
    }
    public boolean deleteDB(){
        SQLiteDatabase db =getWritableDatabase();
       String path= db.getPath();
       return SQLiteDatabase.deleteDatabase(new File(path));
    }

}
