package com.example.zacharycolburn.programmingnews;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Zachary Colburn on 8/10/2017.
 */

public class FavoritesActivity extends AppCompatActivity {

    // Values
    private DBManager dbManager;
    private SimpleCursorAdapter adapter;
    final String[] from = new String[] {
            DatabaseHelper.TITLE,
            DatabaseHelper.SOURCE,
            DatabaseHelper.SDATE };
    final int[] to = new int[] {
            R.id.title,
            R.id.source,
            R.id.article_date_time
    };
    private ListView articleList;
    private Context favoritesActivityContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("FavoritesActivity","Set content view");

        // Create a connection to the database
        dbManager = new DBManager(favoritesActivityContext);
        dbManager.open();
        Log.d("FavoritesActivity","Opened database");

        updateUI();
    }

    public void updateUI(){
        // Fetch the data for favorited articles
        Cursor cursor = dbManager.fetchFavorites();
        Log.d("FavoritesActivity","Fetched data");

        // Create adapter for putting the data into a visualization
        adapter = new SimpleCursorAdapter(
                favoritesActivityContext,
                R.layout.article_item,
                cursor,
                from, to,
                0
        );

        adapter.notifyDataSetChanged();
        Log.d("FavoritesActivity","Created article list view adapter");

        // Assign a default view before articles have loaded and assign the above
        // adapter to the view
        articleList = (ListView) findViewById(R.id.article_list);
        Log.d("FavoritesActivity","Finished updateUI doInBackground");

        articleList.setAdapter(adapter);
        Log.d("FavoritesActivity","Set article list view adapter");

        adapter.notifyDataSetChanged();

        articleList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    Cursor listCursor = (Cursor) parent.getItemAtPosition(position);

                    Intent startIntent = new Intent(view.getContext(), ArticleViewer.class);
                    startIntent.putExtra(MainActivity.ID,listCursor.getInt(0));
                    startIntent.putExtra(MainActivity.TITLE,listCursor.getString(1));
                    startIntent.putExtra(MainActivity.LINK,listCursor.getString(2));
                    startIntent.putExtra(MainActivity.SOURCE,listCursor.getString(3));
                    startIntent.putExtra(MainActivity.DESCRIPTION,listCursor.getString(4));
                    startIntent.putExtra(MainActivity.FAVORITE,listCursor.getInt(5));
                    startIntent.putExtra(MainActivity.DATE,listCursor.getInt(6));
                    startIntent.putExtra(MainActivity.SDATE,listCursor.getInt(7));
                    startIntent.putExtra(MainActivity.INTENT_ACTIVITY_SOURCE,"FavoritesActivity");
                    Log.d("MainActivity","Item click - id = " + listCursor.getString(0));
                    Log.d("MainActivity","Item click - title = " + listCursor.getString(1));
                    Log.d("MainActivity","Item click - link = " + listCursor.getString(2));
                    Log.d("MainActivity","Item click - description = " + listCursor.getString(3));
                    Log.d("MainActivity","Item click - source = " + listCursor.getString(4));
                    Log.d("MainActivity","Item click - favorite = " + listCursor.getString(5));
                    Log.d("MainActivity","Item click - date = " + listCursor.getString(6));
                    Log.d("MainActivity","Item click - sdate = " + listCursor.getString(7));
                    startActivity(startIntent);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.settingsBackButton) {
            // Go back to the main activity if the back arrow is pressed
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}
