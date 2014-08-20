package net.skysemi.englishwordsdb;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MyPreferencesFragment()).commit();
    }

    public static class MyPreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }

        @Override
        public void onResume() {
            super.onResume();

        }
    }
}
