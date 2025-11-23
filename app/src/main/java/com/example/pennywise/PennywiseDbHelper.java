package com.example.pennywise;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PennywiseDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pennywise.db";
    public static final int DATABASE_VERSION = 2; // incremented version

    public PennywiseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // --------------------- BILLS TABLE ---------------------
        db.execSQL(
                "CREATE TABLE " + PennywiseContract.BillEntry.TABLE_NAME + " (" +
                        PennywiseContract.BillEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PennywiseContract.BillEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        PennywiseContract.BillEntry.COLUMN_AMOUNT + " REAL NOT NULL, " +
                        PennywiseContract.BillEntry.COLUMN_DUE_DATE + " TEXT NOT NULL, " +
                        PennywiseContract.BillEntry.COLUMN_IS_PAID + " INTEGER NOT NULL DEFAULT 0, " +
                        PennywiseContract.BillEntry.COLUMN_IMAGE_URI + " TEXT, " + // new column for multimedia
                        PennywiseContract.BillEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");"
        );

        // --------------------- SAVINGS TABLE ---------------------
        db.execSQL(
                "CREATE TABLE " + PennywiseContract.SavingsEntry.TABLE_NAME + " (" +
                        PennywiseContract.SavingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PennywiseContract.SavingsEntry.COLUMN_PURPOSE + " TEXT NOT NULL, " +
                        PennywiseContract.SavingsEntry.COLUMN_TARGET + " REAL NOT NULL, " +
                        PennywiseContract.SavingsEntry.COLUMN_SAVED + " REAL NOT NULL, " +
                        PennywiseContract.SavingsEntry.COLUMN_DEADLINE + " TEXT, " +
                        PennywiseContract.SavingsEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PennywiseContract.BillEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PennywiseContract.SavingsEntry.TABLE_NAME);
        onCreate(db);
    }
}
