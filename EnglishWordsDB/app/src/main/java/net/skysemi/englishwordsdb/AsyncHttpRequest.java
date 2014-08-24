package net.skysemi.englishwordsdb;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;


public class AsyncHttpRequest extends AsyncTask<String, Integer, String> {
    private final char[] IGNORE_CHARS = {'.', ',', '?'};
    private String myURL;
    private Context context;


    AsyncHttpRequest(String url, Context context) {
        myURL = url;
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        ((Activity) context).setProgressBarIndeterminateVisibility(true);
    }


    @Override
    protected String doInBackground(String... url) {
        Document document = null;
        try {
            document = Jsoup.connect(myURL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!document.getElementsByTag("html").attr("lang").equals("en")) {
            return context.getString(R.string.not_english_error);
        }
        String result = (document.body()).text();


        ArrayList<String> wordList = new ArrayList<String>();
        String word = "";
        int pos = 0;
        html:
        for (; pos < result.length(); pos++) {
            char c = result.charAt(pos);
            for (char ignoreChar : IGNORE_CHARS) {
                if (c == ignoreChar) continue html;
            }
            if (c == ' ') {
                if (word.length() > 2) wordList.add(word);
                word = "";
            } else {
                word += c;
            }
        }


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int siteID = sharedPref.getInt("siteID", 0);

        //DBに解析した単語のリストを入れる
        (new WordsDBHelper(context)).insertData(wordList, siteID);


        //サイトリストに書き込む
        int size = wordList.size();
        String string = siteID + "\t" + document.title() + "\t" + document.baseUri() + "\t" + size;
        (new AsyncFileWriter(context, true, string, "siteList")).execute();


        sharedPref.edit().putInt("siteID", siteID + 1).apply();

        return document.title() + "\n" + context.getString(R.string.from) + " " + size + context.getString(R.string.words_analyzed);
    }


    @Override
    protected void onPostExecute(String result) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setProgressBarIndeterminateVisibility(false);
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        CharSequence title = mainActivity.getActionBar().getTitle();
        mainActivity.updateMyListFromDB();
        if (title.equals(context.getString(R.string.my_list))) {
            mainActivity.showMyList();
        } else if (title.equals(context.getString(R.string.site_list))) {
            mainActivity.showSiteList();
        }
        mainActivity.loadInterstitial();
    }
}

