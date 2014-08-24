package net.skysemi.englishwordsdb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.util.Linkify;
import android.widget.TextView;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MyPreferencesFragment()).commit();
    }


    public static class MyPreferencesFragment extends PreferenceFragment {


        public static String getVersionName(Context context) {
            PackageManager pm = context.getPackageManager();
            String versionName = "";
            try {
                PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return versionName;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
            Preference preference = findPreference("about");
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    aboutDialog().show();
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        /*マイ単語帳のアイテムをクリック時に表示するダイアログ*/
        public Dialog aboutDialog() {
            Context context = getActivity();
            TextView tv = new TextView(context);
            tv.setAutoLinkMask(Linkify.WEB_URLS);
            tv.setText("ver." + getVersionName(context) + "\n\n"
                            + context.getString(R.string.author) + "\n\n"
                            + context.getString(R.string.about_skysemi) + "\n"
                            + "        " + "http://skysemi.net"
            );
            tv.setPadding(50, 50, 50, 50);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.app_name)).setView(tv);
            return builder.create();
        }
    }
}

