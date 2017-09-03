package com.example.zacharycolburn.programmingnews;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Zachary Colburn on 8/5/2017.
 */

class FeedHandler extends AsyncTask<Void, Void, Boolean> {
    private Map<String, String> urlDict;
    private Context context;
    private int maxReturnsPerSource;
    public static final String UPDATE_UI_FILTER = "zacharycolburn.programmingnews.UPDATE_UI_FILTER";
    public static final String END_REFRESH_ANIMATION = "zacharycolburn.programmingnews.END_REFRESH_ANIMATION";
    public String intentSource;


    public FeedHandler(
            Map<String, String> urlDict,
            Context context,
            int maxReturnsPerSource,
            String intentSource
    ) {
        this.urlDict = urlDict;
        this.context = context;
        this.maxReturnsPerSource = maxReturnsPerSource;
        this.intentSource = intentSource;
    }

    @Override
    protected void onPreExecute() {
        Log.d("FeedHandler","Starting FeedHandler onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        DBManager dbManager;
        dbManager = new DBManager(context);
        dbManager.open();

        Boolean hadSuccess = false;
        for(Map.Entry<String, String> entry : urlDict.entrySet()){
            String source = entry.getKey();
            String url = entry.getValue();
            int numSuccesses = 0;
            List<Article> articleList = new ArrayList<>();

            Log.d("FeedHandler","Starting FeedHandler doInBackground for: " + source);
            try {
                // Generate URL object from url string
                if(!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                URL fullURL= new URL(url);
                Log.d("FeedHandler","Created URL object");

                // Open the input stream
                Log.d("FeedHandler","URL is: " + fullURL.toString());
                InputStream inputStream = fullURL.openConnection().getInputStream();
                Log.d("FeedHandler","Opened input stream");

                // Define variables needed for feed parsing
                String title = null;
                String link = null;
                String articleText = null;
                Boolean isItem = false;
                Long timeMillis = null;
                Log.d("FeedHandler","Defined parsing variables");

                // Parse XML
                XmlPullParser xmlPullParser = Xml.newPullParser();
                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlPullParser.setInput(inputStream, null);
                xmlPullParser.nextTag();
                Log.d("FeedHandler","Created XML parser");

                // Iterate through XML tags
                while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                    // Get current XML event type
                    int eventType = xmlPullParser.getEventType();

                    // Get name of the current XML element
                    String name = xmlPullParser.getName();
                    if(name == null)
                        continue;

                    // Handle start and end tags
                    // If this is an end tag, then skip to the next element
                    if(eventType == XmlPullParser.END_TAG) {
                        if(name.equalsIgnoreCase("item") || name.equalsIgnoreCase("entry")) {
                            isItem = false;
                        }
                        continue;
                    }

                    // If this is a start tag, then skip to the next element and set this as an item
                    if (eventType == XmlPullParser.START_TAG) {
                        if(name.equalsIgnoreCase("item") || name.equalsIgnoreCase("entry")) {
                            isItem = true;
                            continue;
                        }
                    }

                    // Get element text if the next element is text
                    String result = "";
                    if (xmlPullParser.next() == XmlPullParser.TEXT) {
                        result = xmlPullParser.getText();
                        xmlPullParser.nextTag();
                    }

                    // Assign result string to appropriate variable
                    if (name.startsWith("title")) {
                        title = result;
                    } else if (name.startsWith("link")) {
                        String href = xmlPullParser.getAttributeValue(null, "href");
                        if(href != null){
                            link = href;
                        } else {
                            link = result;
                        }
                    } else if (name.startsWith("description") || name.startsWith("summary")) {
                        articleText = result;
                    } else if (name.startsWith("published")) {
                        result = result.replace("T"," ");
                        result = result.replace("Z","");
                        Log.d("FeedHandler","result is "+result);
                        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                        Date time;
                        try{
                            time = ft.parse(result);
                            timeMillis = time.getTime();
                        }catch(ParseException e){
                            Log.d("FeedHandler","Parsing date for "+source+" failed");
                            timeMillis = System.currentTimeMillis();
                        }
                        //2017-08-19T20:22:32Z
                    } else if (name.startsWith("pubDate")){
                        SimpleDateFormat ft = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss +0000");
                        Date time;
                        try{
                            time = ft.parse(result);
                            timeMillis = time.getTime();
                            Log.d("FeedHandler","Article time: "+time);
                        }catch(ParseException e){
                            Log.d("FeedHandler","Parsing date for "+source+" failed");
                            timeMillis = System.currentTimeMillis();
                        }
                        //Sat, 19 Aug 2017 21:12:00 +0000
                    }

                    // If none are null then create an Article object and append it to items
                    if (title != null && link != null && articleText != null) {
                        if(isItem) {
                            numSuccesses++;
                            if(numSuccesses >= maxReturnsPerSource+1){
                                break;
                            }

                            if(timeMillis == null){
                                timeMillis = System.currentTimeMillis();
                            }

                            Date dsdate = new Date(timeMillis);
                            String sdate = dsdate.toString();

                            Article item = new Article(title, link, source, articleText, DatabaseHelper.UNFAVORITED, timeMillis, sdate);
                            articleList.add(item);
                            Log.d("FeedHandler","Inserted article into database");
                            Log.d("FeedHandler",source);

                            hadSuccess = true;


                        }

                        title = null;
                        link = null;
                        articleText = null;
                        timeMillis = null;
                        isItem = false;
                    }
                }

                inputStream.close();
                Log.d("FeedHandler","Closed inputStream");

                for(Article item: articleList){
                    dbManager.insert(
                            item.title,
                            item.link,
                            item.source,
                            item.articleText,
                            item.fav,
                            item.date,
                            item.sdate
                    );
                }

                Log.d("FeedHandler","Preparing to send broadcast");

                // Send broadcast to update UI
                Intent intentBroadcast = new Intent(FeedHandler.UPDATE_UI_FILTER);
                context.sendBroadcast(intentBroadcast);
                Log.d("FeedHandler","Broadcast sent");


            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
        }
        dbManager.close();

        // Send a broadcast instructing the main activity to terminate the refresh animation
        Intent intentBroadcast = new Intent(FeedHandler.END_REFRESH_ANIMATION);
        context.sendBroadcast(intentBroadcast);

        if(hadSuccess){
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.d("FeedHandler","Finished running FeedHandler onPostExecute");
    }

}
