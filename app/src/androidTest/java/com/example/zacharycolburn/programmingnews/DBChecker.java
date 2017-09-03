package com.example.zacharycolburn.programmingnews;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DBChecker {
    private Context context;

    @Before
    public void setup(){
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void checkColumns() throws Exception {
        assertEquals(DatabaseHelper.TITLE,"title");
        assertEquals(DatabaseHelper.LINK,"link");
        assertEquals(DatabaseHelper.SOURCE,"source");
        assertEquals(DatabaseHelper.ARTICLE_TEXT,"article_text");
        assertEquals(DatabaseHelper.FAVORITE,"favorite");
        assertEquals(DatabaseHelper.DATE,"date");
        assertEquals(DatabaseHelper.SDATE,"sdate");

    }
}
