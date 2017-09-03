package com.example.zacharycolburn.programmingnews;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zachary Colburn on 8/5/2017.
 */

class FetchFeedTask {
    private Context context;
    private Boolean manualRefresh;
    public String intentSource;

    // Constructor
    public FetchFeedTask(Context context, Boolean manualRefresh, String intentSource) {
        this.context = context;
        this.manualRefresh = manualRefresh;
        this.intentSource = intentSource;
    }

    void fetchFeeds(){
        // Create dictionary of feeds
        Map<String, String> urlDict = new HashMap<String, String>();
        urlDict.put("R-bloggers", "http://feeds.feedburner.com/RBloggers");
        urlDict.put("Android", "https://stackoverflow.com/feeds/tag/android");
        urlDict.put("Java", "https://stackoverflow.com/feeds/tag/java");
        urlDict.put("Javascript", "https://stackoverflow.com/feeds/tag/javascript");
        urlDict.put("Javascript", "https://stackoverflow.com/feeds/tag/javascript");
        urlDict.put("Java", "https://stackoverflow.com/feeds/tag/java");
        urlDict.put("Android", "https://stackoverflow.com/feeds/tag/android");
        urlDict.put("JQuery", "https://stackoverflow.com/feeds/tag/jquery");
        urlDict.put("Python", "https://stackoverflow.com/feeds/tag/python");
        urlDict.put("HTML", "https://stackoverflow.com/feeds/tag/html");
        urlDict.put("CSS", "https://stackoverflow.com/feeds/tag/css");
        urlDict.put("C++", "https://stackoverflow.com/feeds/tag/c%2b%2b");
        urlDict.put("SQL", "https://stackoverflow.com/feeds/tag/sql");
        urlDict.put("C", "https://stackoverflow.com/feeds/tag/c");
        urlDict.put("R", "https://stackoverflow.com/feeds/tag/r");
        urlDict.put("XML", "https://stackoverflow.com/feeds/tag/xml");
        urlDict.put("Excel", "https://stackoverflow.com/feeds/tag/excel");
        urlDict.put("HTTP", "https://stackoverflow.com/feeds/tag/http");
        urlDict.put("Android Studio", "https://stackoverflow.com/feeds/tag/android-studio");
        urlDict.put("d3.js", "https://stackoverflow.com/feeds/tag/d3.js");
        urlDict.put("Image processing", "https://stackoverflow.com/feeds/tag/image-processing");
        urlDict.put("SVG", "https://stackoverflow.com/feeds/tag/svg");
        urlDict.put("matplotlib", "https://stackoverflow.com/feeds/tag/matplotlib");
        urlDict.put("ggplot2", "https://stackoverflow.com/feeds/tag/ggplot2");
        urlDict.put("Machine learning", "https://stackoverflow.com/feeds/tag/machine-learning");
        urlDict.put("Chrome extensions", "https://stackoverflow.com/feeds/tag/google-chrome-extension");
        urlDict.put("Planet Python", "http://planetpython.org/rss20.xml");

        // Iterate through dictionary
        Log.d("FetchFeedTask","Attempting to call FeedHandler");
        new FeedHandler(urlDict,context,MainActivity.MAX_RETURNS_PER_SOURCE,intentSource).execute();
    }
}
