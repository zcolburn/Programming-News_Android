package com.example.zacharycolburn.programmingnews;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class ArticleViewer extends AppCompatActivity {
    // Set values
    public static final int RSS = 0;
    public static final int WEB = 1;
    public static int ARTICLE_VIEW = 0;

    public Integer _id;
    public String title;
    public String link;
    public String source;
    public String description;
    public Integer favorite;
    public Long date;

    public String intentSource;

    public MenuItem favoriteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_viewer);

        // Set article view to RSS by default
        ARTICLE_VIEW = RSS;

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Log.d("ArticleViewer","Retrieved intent");
        _id = intent.getIntExtra(MainActivity.ID,0);
        title = intent.getStringExtra(MainActivity.TITLE);
        link = intent.getStringExtra(MainActivity.LINK);
        source = intent.getStringExtra(MainActivity.SOURCE);
        description = intent.getStringExtra(MainActivity.DESCRIPTION);
        favorite = intent.getIntExtra(MainActivity.FAVORITE,0);
        date = intent.getLongExtra(MainActivity.DATE,0);
        Log.d("ArticleViewer","Set variables retrieved from intent");
        Log.d("ArticleViewer","ID: " + _id);
        Log.d("ArticleViewer","Title: " + title);
        Log.d("ArticleViewer","Link: " + link);
        Log.d("ArticleViewer","Source: " + source);
        Log.d("ArticleViewer","Description: " + description);
        Log.d("ArticleViewer","Favorite: " + favorite);
        Log.d("ArticleViewer","Date: " + date);
        setViewOfArticle(ARTICLE_VIEW);

        // Get the Activity that the article view activity was generated from (Main or Favorites)
        intentSource = intent.getStringExtra(MainActivity.INTENT_ACTIVITY_SOURCE);
    }

    // Create view
    public void setViewOfArticle(Integer selection){
        // Display an RSS or web view of the article
        if(selection == RSS){
            setContentView(R.layout.activity_article_viewer);
            Log.d("ArticleViewer","Set content view");
            // Capture the layout's TextView and set the string as its text
            TextView titleTextView = (TextView) findViewById(R.id.article_viewer_title);
            titleTextView.setText(title);
            Log.d("ArticleViewer","Set title text view");
            WebView descriptionWebView = (WebView) findViewById(R.id.article_viewer_description);
            descriptionWebView.loadData(description, "text/html; charset=utf-8", "UTF-8");
        } else if (selection == WEB){
            setContentView(R.layout.article_web_viewer);
            WebView linkWebView = (WebView) findViewById(R.id.article_viewer_link);
            linkWebView.setWebViewClient(new WebViewClient());
            linkWebView.loadUrl(link);
            Log.d("ArticleViewer","Set link view");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.article_viewer, menu);
        favoriteIcon = menu.findItem(R.id.favoriteButton);

        // Set the favorites icon to the proper drawable based on whether the article has been
        // favorited or not.
        setFavoriteIcon();
        return true;
    }

    // Update favorite icon type upon click
    public void setFavoriteIcon(){
        if(favorite.equals(DatabaseHelper.FAVORITED)){
            favoriteIcon.setIcon(R.drawable.ic_favorite_red_24dp);
        } else if(favorite.equals(DatabaseHelper.UNFAVORITED)){
            favoriteIcon.setIcon(R.drawable.ic_unfavorited_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // If the back button is clicked return to the Activity that called this one
        if (id == R.id.articleViwerBackButton){
            if(intentSource.equals("MainActivity")){
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            } else if (intentSource.equals("FavoritesActivity")){
                Intent i = new Intent(this, FavoritesActivity.class);
                startActivity(i);
            }

        // If the favorited button is clicked switch the favorited status in the db for the current
        // article, change the favorite button drawable, and generate a toast.
        } else if (id == R.id.favoriteButton){
            Log.d("ArticleViewer","Favorite button clicked");
            DBManager dbManager;
            dbManager = new DBManager(this);
            dbManager.open();
            Log.d("ArticleViewer","Created and opened connection to dbManager");

            Cursor cursor = dbManager.fetchById(_id);
            Log.d("ArticleViewer","Retrieved cursor after fetching by id");
            int favSetting = cursor.getInt(5);
            Log.d("ArticleViewer","Current favSetting is "+favSetting);
            if(favSetting == DatabaseHelper.FAVORITED){
                Log.d("ArticleViewer","favSetting was originally set to Favorited");
                dbManager.setFav(_id,DatabaseHelper.UNFAVORITED);
                Log.d("ArticleViewer","Set the article with this id ("+_id+") to unfavorited");
                String unfavorited = "Unfavorited";
                Toast.makeText(this, unfavorited, Toast.LENGTH_SHORT).show();
                Log.d("ArticleViewer","Made toast");
                favorite = DatabaseHelper.UNFAVORITED;
            } else {
                Log.d("ArticleViewer","favSetting was originally set to Unfavorited");
                String favorited = "Favorited";
                dbManager.setFav(_id,DatabaseHelper.FAVORITED);
                Log.d("ArticleViewer","Set the article with this id ("+_id+") to favorited");
                Toast.makeText(this, favorited, Toast.LENGTH_SHORT).show();
                Log.d("ArticleViewer","Made toast");
                favorite = DatabaseHelper.FAVORITED;
            }
            setFavoriteIcon();

            dbManager.close();

        // If the switch view button is clicked, then switch between RSS and web views
        } else if (id == R.id.switchViewButton){
            if(ARTICLE_VIEW == RSS){
                ARTICLE_VIEW = WEB;
            } else if (ARTICLE_VIEW == WEB){
                ARTICLE_VIEW = RSS;
            }
            // Update the article view type (RSS or web)
            setViewOfArticle(ARTICLE_VIEW);
        }
        return super.onOptionsItemSelected(item);
    }

}
