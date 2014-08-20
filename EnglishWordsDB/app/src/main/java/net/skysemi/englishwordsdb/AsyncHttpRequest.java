package net.skysemi.englishwordsdb;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mori on 2014/08/02.
 */
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
            return "英語のサイトではありません";
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


        int size = wordList.size();
        ((MainActivity) context).addToFile(siteID + "\t" + document.title() + "\t" + document.baseUri() + "\t" + size, "siteList");


        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("siteID", siteID + 1);
        editor.commit();
        
        return document.title() + "\nから " + size + "単語を解析しました";
    }


    @Override
    protected void onPostExecute(String result) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setProgressBarIndeterminateVisibility(false);
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        CharSequence title = mainActivity.getActionBar().getTitle();
        if (title.equals(context.getString(R.string.my_list))) {
            mainActivity.updateMyList();
        } else if(title.equals(context.getString(R.string.site_list))){
            mainActivity.showSiteList();
            mainActivity.setMyList();
        }else {
            mainActivity.setMyList();
        }
        mainActivity.loadInterstitial();
    }
}

