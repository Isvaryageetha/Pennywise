package com.example.pennywise;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PennywiseDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pennywise.db";
    private static final int DATABASE_VERSION = 1;

    public PennywiseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_EXPENSES =
                "CREATE TABLE " + PennywiseContract.ExpenseEntry.TABLE_NAME + " ("
                        + PennywiseContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + PennywiseContract.ExpenseEntry.COLUMN_NAME + " TEXT NOT NULL, "
                        + PennywiseContract.ExpenseEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
                        + PennywiseContract.ExpenseEntry.COLUMN_DATE + " TEXT, "
                        + PennywiseContract.ExpenseEntry.COLUMN_NOTE + " TEXT, "
                        + PennywiseContract.ExpenseEntry.COLUMN_CREATED_AT + " TEXT"
                        + ");";

        final String SQL_CREATE_BILLS =
                "CREATE TABLE " + PennywiseContract.BillEntry.TABLE_NAME + " ("
                        + PennywiseContract.BillEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + PennywiseContract.BillEntry.COLUMN_NAME + " TEXT NOT NULL, "
                        + PennywiseContract.BillEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
                        + PennywiseContract.BillEntry.COLUMN_DUE_DATE + " TEXT, "
                        + PennywiseContract.BillEntry.COLUMN_IS_PAID + " INTEGER DEFAULT 0, "
                        + PennywiseContract.BillEntry.COLUMN_CREATED_AT + " TEXT"
                        + ");";

        final String SQL_CREATE_SAVINGS =
                "CREATE TABLE " + PennywiseContract.SavingsEntry.TABLE_NAME + " ("
                        + PennywiseContract.SavingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + PennywiseContract.SavingsEntry.COLUMN_PURPOSE + " TEXT NOT NULL, "
                        + PennywiseContract.SavingsEntry.COLUMN_TARGET + " REAL NOT NULL, "
                        + PennywiseContract.SavingsEntry.COLUMN_SAVED + " REAL DEFAULT 0, "
                        + PennywiseContract.SavingsEntry.COLUMN_DEADLINE + " TEXT, "
                        + PennywiseContract.SavingsEntry.COLUMN_CREATED_AT + " TEXT"
                        + ");";

        db.execSQL(SQL_CREATE_EXPENSES);
        db.execSQL(SQL_CREATE_BILLS);
        db.execSQL(SQL_CREATE_SAVINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple upgrade policy: drop and recreate (replace with migration if you need data)
        db.execSQL("DROP TABLE IF EXISTS " + PennywiseContract.ExpenseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PennywiseContract.BillEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PennywiseContract.SavingsEntry.TABLE_NAME);
        onCreate(db);
    }
}
