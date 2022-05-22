package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact.ScheduleEntry;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Calendar.db";
    public static final int DATABASE_VERSION = 1;

    // Constructor
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create Table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_TABLE = "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " ("
                + ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ScheduleEntry.COLUMN_EVENT_NAME + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_START_TIME + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_END_TIME + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_WEEK + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_LUNAR + " TEXT);";
        sqLiteDatabase.execSQL(SQL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
