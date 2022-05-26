package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact.ScheduleEntry;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact.SmsEntry;

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

        // Table of simple schedules
        String SQL_TABLE = "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " ("
                + ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ScheduleEntry.COLUMN_EVENT_NAME + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_START_DATE + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_END_DATE + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_START_TIME + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_END_TIME + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_WEEK + " TEXT NOT NULL, "
                + ScheduleEntry.COLUMN_LUNAR + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_TABLE);

        // Table of sms
        String SQL_SMS_TABLE = "CREATE TABLE " + SmsEntry.TABLE_NAME + " ("
                + SmsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SmsEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
                + SmsEntry.COLUMN_SMS_DATE + " TEXT NOT NULL, "
                + SmsEntry.COLUMN_SCHEDULES + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_SMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
