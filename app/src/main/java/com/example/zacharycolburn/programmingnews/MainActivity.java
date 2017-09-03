package com.example.zacharycolburn.programmingnews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
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
    private BroadcastReceiver endFeedRefreshReceiver;
    private BroadcastReceiver feedFetcherReceiver;
    public Context mainActivityContext = this;
    public static final String ID = "zacharycolburn.programmingnews.ID";
    public static final String TITLE = "zacharycolburn.programmingnews.TITLE";
    public static final String LINK = "zacharycolburn.programmingnews.LINK";
    public static final String DESCRIPTION = "zacharycolburn.programmingnews.DESCRIPTION";
    public static final String SOURCE = "zacharycolburn.programmingnews.SOURCE";
    public static final String FAVORITE = "zacharycolburn.programmingnews.FAVORITE";
    public static final String DATE = "zacharycolburn.programmingnews.DATE";
    public static final String SDATE = "zacharycolburn.programmingnews.SDATE";
    public static final int MAX_RETURNS_PER_SOURCE = 10;
    public static final String INTENT_ACTIVITY_SOURCE = "INTENT_ACTIVITY_SOURCE";
    public static final String intentSource = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","Set content view");

        // Create a connection to the database
        dbManager = new DBManager(mainActivityContext);
        dbManager.open();
        Log.d("MainActivity","Opened database");

        feedFetcherHandler = new Handler();
        feedFetcherReceiver = new FeedFetcherReceiver();
        endFeedRefreshHandler = new Handler();
        endFeedRefreshReceiver = new FinishedRefresh();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long lastTime = preferences.getLong("time", (long) 0.0);
        Log.d("MainActivity","Get lastTime");
        long currentTime = System.currentTimeMillis();
        Log.d("MainActivity","Get currentTime");
        long critUpdateTime = (long) (1000.0 * 60 * 60);
        if((currentTime - lastTime) > critUpdateTime){
            Log.d("MainActivity","Time since last update is greater than the critical update time");
            Intent feedFetcherIntent = new Intent(this,FeedFetcher.class);
            feedFetcherIntent.putExtra("intentSource",MainActivity.intentSource);
            startService(feedFetcherIntent);
            Log.d("MainActivity","Started feed fetching service");
            preferences.edit().putLong("time", System.currentTimeMillis()).apply();
            Log.d("MainActivity","Updated time of last update to right now");
        }
        Boolean enableNotification = preferences.getBoolean("allowNotifications", false);
        Log.d("MainActivity","Got enableNotification preference");
        if(enableNotification){
            Log.d("MainActivity","Notifications are enabled");
            ReadReminderReceiver.setupAlarm(this);
        } else if (!enableNotification){
            Log.d("MainActivity","Notifications are not enabled");
        }

        updateUI();
    }



    public void updateUI(){
        // Fetch the data
        Cursor cursor = dbManager.fetch();
        Log.d("MainActivity","Fetched data");

        // Create adapter for putting the data into a visualization
        adapter = new SimpleCursorAdapter(
                mainActivityContext,
                R.layout.article_item,
                cursor,
                from, to,
                0
        );

        adapter.notifyDataSetChanged();
        Log.d("MainActivity","Created article list view adapter");

        // Assign a default view before articles have loaded and assign the above
        // adapter to the view
        articleList = (ListView) findViewById(R.id.article_list);
        Log.d("MainActivity","Finished updateUI doInBackground");

        articleList.setAdapter(adapter);
        Log.d("MainActivity","Set article list view adapter");

        adapter.notifyDataSetChanged();

        articleList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    Cursor listCursor = (Cursor) parent.getItemAtPosition(position);
                    Intent startIntent = new Intent(view.getContext(), ArticleViewer.class);
                    startIntent.putExtra(ID,listCursor.getInt(0));
                    startIntent.putExtra(TITLE,listCursor.getString(1));
                    startIntent.putExtra(LINK,listCursor.getString(2));
                    startIntent.putExtra(SOURCE,listCursor.getString(3));
                    startIntent.putExtra(DESCRIPTION,listCursor.getString(4));
                    startIntent.putExtra(FAVORITE,listCursor.getInt(5));
                    startIntent.putExtra(DATE,listCursor.getInt(6));
                    startIntent.putExtra(SDATE,listCursor.getInt(7));
                    startIntent.putExtra(MainActivity.INTENT_ACTIVITY_SOURCE,"MainActivity");
                    Log.d("MainActivity","Item click - id = " + listCursor.getString(0));
                    Log.d("MainActivity","Item click - title = " + listCursor.getString(1));
                    Log.d("MainActivity","Item click - link = " + listCursor.getString(2));
                    Log.d("MainActivity","Item click - source = " + listCursor.getString(3));
                    Log.d("MainActivity","Item click - description = " + listCursor.getString(4));
                    Log.d("MainActivity","Item click - favorite = " + listCursor.getString(5));
                    Log.d("MainActivity","Item click - date = " + listCursor.getString(6));
                    Log.d("MainActivity","Item click - sdate = " + listCursor.getString(7));
                    startActivity(startIntent);
                }
            });
    }

    private Handler feedFetcherHandler;
    public class FeedFetcherReceiver extends BroadcastReceiver {
        public FeedFetcherReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            feedFetcherHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
        }
    }

    private Handler endFeedRefreshHandler;
    public class FinishedRefresh extends BroadcastReceiver {
        public FinishedRefresh(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            endFeedRefreshHandler.post(new Runnable() {
                @Override
                public void run() {
                    endRefresh();
                }
            });
        }
    }

    private void endRefresh(){
        (findViewById(R.id.refresh_records)).clearAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         int id = item.getItemId();
         if (id == R.id.settings) {
             Intent i = new Intent(this, PreferenceActivity.class);
             startActivity(i);
         } else if (id == R.id.refresh_records) {
             Animation animation;
             animation = AnimationUtils.loadAnimation(getApplicationContext(),
                     R.anim.refresh);
             (findViewById(R.id.refresh_records)).startAnimation(animation);
             Intent feedFetcherIntent = new Intent(this,FeedFetcher.class);
             feedFetcherIntent.putExtra("intentSource",MainActivity.intentSource);
             startService(feedFetcherIntent);
         } else if (id == R.id.favorites){
             Intent favoritesIntent = new Intent(this,FavoritesActivity.class);
             startActivity(favoritesIntent);
         }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(feedFetcherReceiver,new IntentFilter(FeedHandler.UPDATE_UI_FILTER));
        this.registerReceiver(endFeedRefreshReceiver,new IntentFilter(FeedHandler.END_REFRESH_ANIMATION));


        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(feedFetcherReceiver);
        this.unregisterReceiver(endFeedRefreshReceiver);
        //unregisterReceiver(feedFetcherReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
        Log.d("MainActivity","Destroying activity");
    }
}
