package com.example.zacharycolburn.programmingnews;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MainVarChecker {
    @Test
    public void viewCorrect() throws Exception {
        assertEquals(MainActivity.DESCRIPTION, "com.example.zacharycolburn.programmingnews.DESCRIPTION");
    }
}