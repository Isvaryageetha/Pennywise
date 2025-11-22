package com.example.pennywise;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PennywiseProvider extends ContentProvider {

    private static final int BILLS = 100;
    private static final int BILL_ID = 101;

    private static final int SAVINGS = 200;
    private static final int SAVING_ID = 201;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PennywiseContract.AUTHORITY, PennywiseContract.PATH_BILLS, BILLS);
        uriMatcher.addURI(PennywiseContract.AUTHORITY, PennywiseContract.PATH_BILLS + "/#", BILL_ID);

        uriMatcher.addURI(PennywiseContract.AUTHORITY, PennywiseContract.PATH_SAVINGS, SAVINGS);
        uriMatcher.addURI(PennywiseContract.AUTHORITY, PennywiseContract.PATH_SAVINGS + "/#", SAVING_ID);
    }

    private PennywiseDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new PennywiseDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {

            case BILLS:
                cursor = db.query(
                        PennywiseContract.BillEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder
                );
                break;

            case BILL_ID:
                selection = PennywiseContract.BillEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(
                        PennywiseContract.BillEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder
                );
                break;

            case SAVINGS:
                cursor = db.query(
                        PennywiseContract.SavingsEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder
                );
                break;

            case SAVING_ID:
                selection = PennywiseContract.SavingsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(
                        PennywiseContract.SavingsEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;

        switch (uriMatcher.match(uri)) {

            case BILLS:
                id = db.insert(PennywiseContract.BillEntry.TABLE_NAME, null, values);
                break;

            case SAVINGS:
                id = db.insert(PennywiseContract.SavingsEntry.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Insert not supported " + uri);
        }

        if (id == -1)
            throw new SQLException("Insert failed");

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows;

        switch (uriMatcher.match(uri)) {

            case BILLS:
                rows = db.delete(PennywiseContract.BillEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BILL_ID:
                selection = PennywiseContract.BillEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.delete(PennywiseContract.BillEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SAVINGS:
                rows = db.delete(PennywiseContract.SavingsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SAVING_ID:
                selection = PennywiseContract.SavingsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.delete(PennywiseContract.SavingsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Delete not supported " + uri);
        }

        if (rows > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows;

        switch (uriMatcher.match(uri)) {

            case BILL_ID:
                selection = PennywiseContract.BillEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.update(PennywiseContract.BillEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case SAVING_ID:
                selection = PennywiseContract.SavingsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.update(PennywiseContract.SavingsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Update not supported " + uri);
        }

        if (rows > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {

            case BILLS:
                return PennywiseContract.BillEntry.CONTENT_LIST_TYPE;
            case BILL_ID:
                return PennywiseContract.BillEntry.CONTENT_ITEM_TYPE;

            case SAVINGS:
                return PennywiseContract.SavingsEntry.CONTENT_LIST_TYPE;
            case SAVING_ID:
                return PennywiseContract.SavingsEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
