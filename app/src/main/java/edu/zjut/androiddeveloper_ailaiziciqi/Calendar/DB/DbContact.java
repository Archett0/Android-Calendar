package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DbContact {

    public DbContact() {

    }

    public static final String CONTENT_AUTHORITY = "edu.zjut.androiddeveloper_ailaiziciqi.Calendar";

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SCHEDULES = "schedule";

    public static abstract class ScheduleEntry implements BaseColumns {

        // Table Uri
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_SCHEDULES);

        // Table Columns
        public static final String TABLE_NAME = "schedule";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_EVENT_NAME = "event";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_START_TIME = "starttime";
        public static final String COLUMN_END_TIME = "endtime";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_LUNAR = "lunar";
    }

}
