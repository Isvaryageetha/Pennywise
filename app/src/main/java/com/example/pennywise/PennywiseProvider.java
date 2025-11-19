package com.example.pennywise;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

public class PennywiseProvider extends ContentProvider {

    private static final int EXPENSES = 100;
    private static final int EXPENSE_ID = 101;
    private static final int BILLS = 200;
    private static final int BILL_ID = 201;
    private static final int SAVINGS = 300;
    private static final int SAVING_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PennywiseDbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PennywiseContract.AUTHORITY;

        matcher.addURI(authority, PennywiseContract.PATH_EXPENSES, EXPENSES);
        matcher.addURI(authority, PennywiseContract.PATH_EXPENSES + "/#", EXPENSE_ID);

        matcher.addURI(authority, PennywiseContract.PATH_BILLS, BILLS);
        matcher.addURI(authority, PennywiseContract.PATH_BILLS + "/#", BILL_ID);

        matcher.addURI(authority, PennywiseContract.PATH_SAVINGS, SAVINGS);
        matcher.addURI(authority, PennywiseContract.PATH_SAVINGS + "/#", SAVING_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        mDbHelper = new PennywiseDbHelper(ctx);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case EXPENSES:
                cursor = db.query(PennywiseContract.ExpenseEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EXPENSE_ID:
                selection = PennywiseContract.ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(PennywiseContract.ExpenseEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case BILLS:
                cursor = db.query(PennywiseContract.BillEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case BILL_ID:
                selection = PennywiseContract.BillEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(PennywiseContract.BillEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case SAVINGS:
                cursor = db.query(PennywiseContract.SavingsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case SAVING_ID:
                selection = PennywiseContract.SavingsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(PennywiseContract.SavingsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI for query: " + uri);
        }

        // Register for change notifications
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case EXPENSES:
                return PennywiseContract.ExpenseEntry.CONTENT_LIST_TYPE;
            case EXPENSE_ID:
                return PennywiseContract.ExpenseEntry.CONTENT_ITEM_TYPE;
            case BILLS:
                return PennywiseContract.BillEntry.CONTENT_LIST_TYPE;
            case BILL_ID:
                return PennywiseContract.BillEntry.CONTENT_ITEM_TYPE;
            case SAVINGS:
                return PennywiseContract.SavingsEntry.CONTENT_LIST_TYPE;
            case SAVING_ID:
                return PennywiseContract.SavingsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI for getType: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case EXPENSES:
                id = db.insert(PennywiseContract.ExpenseEntry.TABLE_NAME, null, values);
                if (id == -1) throw new SQLException("Failed to insert row into " + uri);
                returnUri = ContentUris.withAppendedId(PennywiseContract.ExpenseEntry.CONTENT_URI, id);
                break;

            case BILLS:
                id = db.insert(PennywiseContract.BillEntry.TABLE_NAME, null, values);
                if (id == -1) throw new SQLException("Failed to insert row into " + uri);
                returnUri = ContentUris.withAppendedId(PennywiseContract.BillEntry.CONTENT_URI, id);
                break;

            case SAVINGS:
                id = db.insert(PennywiseContract.SavingsEntry.TABLE_NAME, null, values);
                if (id == -1) throw new SQLException("Failed to insert row into " + uri);
                returnUri = ContentUris.withAppendedId(PennywiseContract.SavingsEntry.CONTENT_URI, id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI for insert: " + uri);
        }

        // notify listeners
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case EXPENSES:
                rowsDeleted = db.delete(PennywiseContract.ExpenseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EXPENSE_ID:
                selection = PennywiseContract.ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(PennywiseContract.ExpenseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BILLS:
                rowsDeleted = db.delete(PennywiseContract.BillEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BILL_ID:
                selection = PennywiseContract.BillEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(PennywiseContract.BillEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SAVINGS:
                rowsDeleted = db.delete(PennywiseContract.SavingsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SAVING_ID:
                selection = PennywiseContract.SavingsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(PennywiseContract.SavingsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI for delete: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case EXPENSES:
                rowsUpdated = db.update(PennywiseContract.ExpenseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case EXPENSE_ID:
                selection = PennywiseContract.ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated = db.update(PennywiseContract.ExpenseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BILLS:
                rowsUpdated = db.update(PennywiseContract.BillEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BILL_ID:
                selection = PennywiseContract.BillEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated = db.update(PennywiseContract.BillEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SAVINGS:
                rowsUpdated = db.update(PennywiseContract.SavingsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SAVING_ID:
                selection = PennywiseContract.SavingsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated = db.update(PennywiseContract.SavingsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI for update: " + uri);
        }

        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
