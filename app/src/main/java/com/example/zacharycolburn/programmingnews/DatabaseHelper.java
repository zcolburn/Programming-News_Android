package com.example.zacharycolburn.programmingnews;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "ARTICLES";

    // Table columns
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String SOURCE = "source";
    public static final String ARTICLE_TEXT = "article_text";
    public static final String FAVORITE = "favorite";
    public static final String DATE = "date";
    public static final String SDATE = "sdate";

    // Enums
    public static final Integer UNFAVORITED = 0;
    public static final Integer FAVORITED = 1;

    // Database Information
    static final String DB_NAME = "ARTICLES.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT NOT NULL, " +
            LINK + " TEXT NOT NULL, " +
            SOURCE + " TEXT NOT NULL, " +
            ARTICLE_TEXT + " TEXT NOT NULL, " +
            FAVORITE + " INTEGER, " +
            DATE + " INTEGER, " +
            SDATE + " STRING, " +
            "UNIQUE("+ TITLE +"));";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
