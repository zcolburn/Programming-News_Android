package com.example.zacharycolburn.programmingnews;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FeedFetcher extends IntentService {
    public String intentSource;

    // Constructor
    public FeedFetcher() {
        // Name service thread
        super("FeedFetcher");
        Log.d("FeedFetcher","Constructed FeedFetcher");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("FeedFetcher","Started onHandleIntent");
        try{
            intentSource = intent.getStringExtra("intentSource");
            // Start FetchFeedTask
            Log.d("FeedFetcher","Starting FetchFeedTask");
            new FetchFeedTask(this.getApplicationContext(),true,intentSource).fetchFeeds();
            Log.d("FeedFetcher","Finished FetchFeedTask");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
