package com.example.zacharycolburn.programmingnews;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Zachary Colburn on 8/9/2017.
 */
//android.preference.PreferenceActivity
public class PreferenceActivity extends AppCompatPreferenceActivity {

    // Generate preferences interface
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        Log.d("PreferenceActivity","Created getFragmentManager");
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Log.d("PreferenceActivity","Added preferences from resource");
        }
    }

    // Generate menu bar interface
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        Log.d("PreferenceActivity","Inflated options menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("PreferenceActivity","Started onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.settingsBackButton) {
            Log.d("PreferenceActivity","Back button was clicked");
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }
}
