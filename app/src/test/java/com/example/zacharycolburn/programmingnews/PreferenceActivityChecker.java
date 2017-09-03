package com.example.zacharycolburn.programmingnews;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PreferenceActivityChecker {
    @Test
    public void viewCorrect() throws Exception {
        assertNotNull(android.preference.PreferenceActivity.class);
    }
}