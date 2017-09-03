package com.example.zacharycolburn.programmingnews;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {

    public static int retentionDuration = 48 * 60 * 60 * 1000;

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context context) {
        this.context = context;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Insert all values
    public void insert(String title, String link, String source, String desc, Integer fav, Long date, String sdate) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TITLE, title);
        contentValue.put(DatabaseHelper.LINK, link);
        contentValue.put(DatabaseHelper.SOURCE, source);
        contentValue.put(DatabaseHelper.ARTICLE_TEXT, desc);
        contentValue.put(DatabaseHelper.FAVORITE, fav);
        contentValue.put(DatabaseHelper.DATE, date);
        contentValue.put(DatabaseHelper.SDATE, sdate);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    // Fetch all values
    public Cursor fetch() {
        delete();
        String[] columns = new String[] {
                DatabaseHelper._ID,
                DatabaseHelper.TITLE,
                DatabaseHelper.LINK,
                DatabaseHelper.SOURCE,
                DatabaseHelper.ARTICLE_TEXT,
                DatabaseHelper.FAVORITE,
                DatabaseHelper.DATE,
                DatabaseHelper.SDATE
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, DatabaseHelper.DATE + " DESC");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Check if an article (its URL) already exists in the db
    public boolean exists(String link){
        String query = "SELECT " + DatabaseHelper._ID + " FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.LINK + " = \"" + link + "\"";
        Cursor  cursor = database.rawQuery(query,null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    // Update article information
    public int update(long _id, String name, String link, String source, String article_text, String fav, Long date, String sdate) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TITLE, name);
        contentValues.put(DatabaseHelper.LINK, link);
        contentValues.put(DatabaseHelper.SOURCE, source);
        contentValues.put(DatabaseHelper.ARTICLE_TEXT, article_text);
        contentValues.put(DatabaseHelper.FAVORITE, fav);
        contentValues.put(DatabaseHelper.DATE, date);
        contentValues.put(DatabaseHelper.SDATE, sdate);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    // Fetch article information by article ID
    public Cursor fetchById(Integer _id){
        Log.d("DBManager","Fetching by id");
        String fullQuery = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper._ID + " = ?" + " ORDER BY " + DatabaseHelper.DATE + " DESC" + ";";
        Log.d("DBManager","Created full query");
        Cursor cursor = database.rawQuery(fullQuery, new String[]{_id.toString()});
        Log.d("DBManager","Retrieve cursor");
        if (cursor != null) {
            Log.d("DBManager","Checked cursor nullity");
            cursor.moveToFirst();
            Log.d("DBManager","Moved cursor to first position");
        }
        return cursor;
    }

    // Fetch all articles that have been favorited
    public Cursor fetchFavorites(){
        Log.d("DBManager","Fetching by id");
        String fullQuery = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.FAVORITE + " = ?;";
        Log.d("DBManager","Created full query");
        Cursor cursor = database.rawQuery(fullQuery, new String[]{DatabaseHelper.FAVORITED.toString()});
        Log.d("DBManager","Retrieve cursor");
        if (cursor != null) {
            Log.d("DBManager","Checked cursor nullity");
            cursor.moveToFirst();
            Log.d("DBManager","Moved cursor to first position");
        }
        return cursor;
    }

    // Given an article ID number, update its favorited status
    public void setFav(long _id, Integer fav){
        Log.d("DBManager","Starting set fav");
        ContentValues contentValues = new ContentValues();
        Log.d("DBManager","Generated content values object");
        contentValues.put(DatabaseHelper.FAVORITE, fav);
        Log.d("DBManager","Added favorite data to content value");
        database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        Log.d("DBManager","Set fav complete");
    }

    // Delete an article
    public void delete() {
        database.delete(
                DatabaseHelper.TABLE_NAME,
                DatabaseHelper.DATE + " < " + retentionDuration + " AND " +
                DatabaseHelper.FAVORITE + " = " + DatabaseHelper.UNFAVORITED,
                null
        );
    }

}
