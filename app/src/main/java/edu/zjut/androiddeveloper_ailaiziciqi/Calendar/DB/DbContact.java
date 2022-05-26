package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DbContact {

    public DbContact() {

    }

    public static final String CONTENT_AUTHORITY = "edu.zjut.androiddeveloper_ailaiziciqi.Calendar";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SCHEDULES = "schedule";
    public static final String PATH_SMS = "sms";

    public static abstract class ScheduleEntry implements BaseColumns {

        // Schedule Table Uri
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_SCHEDULES);

        // Schedule Table Columns
        public static final String TABLE_NAME = "schedule";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_EVENT_NAME = "event";
        public static final String COLUMN_START_DATE = "startdate";
        public static final String COLUMN_END_DATE = "enddate";
        public static final String COLUMN_START_TIME = "starttime";
        public static final String COLUMN_END_TIME = "endtime";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_LUNAR = "lunar";
    }

    public static abstract class SmsEntry implements BaseColumns {
        // Sms Table Uri
        public static final Uri SMS_CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_SMS);

        // Sms Table Columns
        public static final String TABLE_NAME = "sms";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PHONE_NUMBER = "phone";
        public static final String COLUMN_SMS_DATE = "smsdate";
        public static final String COLUMN_SCHEDULES = "schedulelist";
    }

}
