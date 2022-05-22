package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Objects;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix.MixActivity;

public class Provider extends ContentProvider {

    // Uris
    public static final int SCHEDULES = 100;
    public static final int SCHEDULE_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(DbContact.CONTENT_AUTHORITY, DbContact.PATH_SCHEDULES, SCHEDULES);
        sUriMatcher.addURI(DbContact.CONTENT_AUTHORITY, DbContact.PATH_SCHEDULES + "/#", SCHEDULE_ID);
    }

    // DB Helper
    public DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        Log.i("Provider Executed", "on create");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();  // get readable db
        Cursor cursor; // set a cursor

        // match uris
        int match = sUriMatcher.match(uri);
        switch (match) {
            // Multiple schedules
            case SCHEDULES:
                cursor = database.query(DbContact.ScheduleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            // Single schedule
            case SCHEDULE_ID:
                selection = DbContact.ScheduleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(DbContact.ScheduleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Can Not Query" + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        // add a log
        Log.i("Provider Executed", "query schedule");
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                return insertSchedule(uri, contentValues);

            default:
                throw new IllegalArgumentException("Can Not Insert" + uri);
        }
    }

    private Uri insertSchedule(Uri uri, ContentValues contentValues) {

        // set parameters
        String event = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_EVENT_NAME);
        if (event == null)
            throw new IllegalArgumentException("Event Name is required");

        String date = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_DATE);
        if (date == null)
            throw new IllegalArgumentException("Date is required");

        String startTime = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_START_TIME);
        if (startTime == null)
            throw new IllegalArgumentException("Start Time is required");

        String endTime = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_END_TIME);
        if (endTime == null)
            throw new IllegalArgumentException("End Time is required");

        String week = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_WEEK);
        if (week == null)
            throw new IllegalArgumentException("Week is required");

        String lunar = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_LUNAR);
        if (lunar == null)
            throw new IllegalArgumentException("Lunar is required");

        // get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(DbContact.ScheduleEntry.TABLE_NAME, null, contentValues);

        // judge if the insert fails
        if (id == -1) {
            return null;
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        // add a log
        Log.i("Provider Executed", "insert schedule");
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // set a variable to record thr rows effected
        int rowsDeleted;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                rowsDeleted = database.delete(DbContact.ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SCHEDULE_ID:
                selection = DbContact.ScheduleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(DbContact.ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Can't execute delete" + uri);
        }

        // if the delete is successful
        if (rowsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        // if not, just return 0
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                return updateSchedule(uri, contentValues, selection, selectionArgs);

            case SCHEDULE_ID:
                selection = DbContact.ScheduleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSchedule(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Can not update");
        }
    }

    private int updateSchedule(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // set parameters
        if (contentValues.containsKey(DbContact.ScheduleEntry.COLUMN_EVENT_NAME)) {
            String event = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_EVENT_NAME);
            if (event == null)
                throw new IllegalArgumentException("Event Name is required");
        }

        if (contentValues.containsKey(DbContact.ScheduleEntry.COLUMN_DATE)) {
            String date = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_DATE);
            if (date == null)
                throw new IllegalArgumentException("Date is required");
        }

        if (contentValues.containsKey(DbContact.ScheduleEntry.COLUMN_START_TIME)) {
            String startTime = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_START_TIME);
            if (startTime == null)
                throw new IllegalArgumentException("Start Time is required");
        }

        if (contentValues.containsKey(DbContact.ScheduleEntry.COLUMN_END_TIME)) {
            String endTime = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_END_TIME);
            if (endTime == null)
                throw new IllegalArgumentException("End Time is required");
        }
        if (contentValues.containsKey(DbContact.ScheduleEntry.COLUMN_WEEK)) {
            String week = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_WEEK);
            if (week == null)
                throw new IllegalArgumentException("Week is required");
        }
        if (contentValues.containsKey(DbContact.ScheduleEntry.COLUMN_LUNAR)) {
            String lunar = contentValues.getAsString(DbContact.ScheduleEntry.COLUMN_LUNAR);
            if (lunar == null)
                throw new IllegalArgumentException("Lunar is required");
        }
        // no values to be updated so we don't have to execute sql methods
        if (contentValues.size() == 0) {
            return 0;
        }

        // get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // set a variable to record thr rows effected
        int rowsUpdated = database.update(DbContact.ScheduleEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // if the update is successful
        if (rowsUpdated != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        // add a log
        Log.i("Provider Executed", "update schedule");
        return rowsUpdated;
    }
}
