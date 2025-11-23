package com.example.pennywise;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PennywiseContract {

    private PennywiseContract() {}

    public static final String AUTHORITY = "com.example.pennywise.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_BILLS = "bills";
    public static final String PATH_SAVINGS = "savings";

    // ----------------------- BILLS -----------------------
    // ----------------------- BILLS -----------------------
    public static final class BillEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BILLS);

        public static final String TABLE_NAME = "bills";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_IS_PAID = "is_paid";
        public static final String COLUMN_IMAGE_URI = "image_uri"; // new column for multimedia
        public static final String COLUMN_CREATED_AT = "created_at";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + ".bills";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + ".bills";
    }


    // ----------------------- SAVINGS -----------------------
    public static final class SavingsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SAVINGS);

        public static final String TABLE_NAME = "savings";

        public static final String COLUMN_PURPOSE = "purpose";
        public static final String COLUMN_TARGET = "target_amount";
        public static final String COLUMN_SAVED = "saved_amount";
        public static final String COLUMN_DEADLINE = "deadline";
        public static final String COLUMN_CREATED_AT = "created_at";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + ".savings";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + ".savings";
    }
}
