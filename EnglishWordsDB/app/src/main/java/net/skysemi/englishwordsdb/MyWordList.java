package net.skysemi.englishwordsdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mori on 2014/08/02.
 */
public class MyWordList {

    private final char[] IGNORE_CHARS_2
            = {':', ';', '^', '\"', '(',')'};
    private Context context;
    private List<Map<String, String>> myWordMapList;
    private ArrayList<String> ignoreWords;
    private int leastNum = 1;
    private int ignoreLevel;
    private int maxWordMapSize;


    MyWordList(Context c) {
        context = c;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        ignoreLevel = Integer.parseInt(sharedPref.getString("words_level", "1200"));
        maxWordMapSize = Integer.parseInt(sharedPref.getString("words_num", "10"));
        ignoreWords = new ArrayList<String>();
        loadIgnoreWords(ignoreLevel);
        loadIgnoreWords("rememberedList");
        loadIgnoreWords("ignoreList");
        readWords();
    }

    List<Map<String, String>> get(String newIgnoreWord) {
        //そのまま渡すパターン
        if (newIgnoreWord.equals("")) return myWordMapList;

        //渡された単語を取り除いて渡すパターン
        for (int i = 0; i < myWordMapList.size(); i++) {
            if (myWordMapList.get(i).get("単語").equals(newIgnoreWord)) {
                myWordMapList.remove(i);
            }
        }
        recountList();
        return myWordMapList;
    }

    private void loadIgnoreWords(int ignoreLevel) {
        try {
            AssetManager as = context.getResources().getAssets();
            InputStream is = as.open("defaultWords");
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is));
            String StringBuffer;
            int i = 0;
            while (i < ignoreLevel && (StringBuffer = bufferReader.readLine()) != null) {
                ignoreWords.add(StringBuffer);
                i++;
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadIgnoreWords(String fileName) {
        File file = new File(context.getFilesDir() + "/" + fileName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String StringBuffer;
            while ((StringBuffer = bufferedReader.readLine()) != null) {
                String[] strings = StringBuffer.split("\t");
                ignoreWords.add(strings[0]);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readWords() {
        WordsDBHelper helper = null;
        SQLiteDatabase db = null;
        try {
            helper = new WordsDBHelper(context);
            db = helper.getWritableDatabase();
            searchData(db);
        } finally {
            if (db != null) db.close();
            if (helper != null) helper.close();
        }
    }


    private void searchData(SQLiteDatabase db) {
        myWordMapList = new ArrayList<Map<String, String>>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + Word.WORD
                    + ", COUNT(*) AS WordCount FROM " + Word.TB_NAME
                    + " GROUP BY " + Word.WORD + " HAVING(Count(*) > "
                    + leastNum + " ) ORDER BY WordCount DESC", new String[]{});
            if (cursor != null) {
                int i = 0;
                while (cursor.moveToNext() && i < maxWordMapSize) {
                    String s = cursor.getString(cursor.getColumnIndex(Word.WORD));

                    if (isIgnoreWord(s)) continue;

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("ランク", String.valueOf(i + 1) + "位");
                    map.put("単語", s);
                    map.put("出現数", cursor.getString(cursor.getColumnIndex("WordCount")));
                    myWordMapList.add(map);
                    i++;
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private boolean isIgnoreWord(String word) {
        if (ignoreWords.contains(word.toLowerCase()) || ignoreWords.contains(word)) return true;
        for (int j = 0; j < word.length(); j++) {
            char c = word.charAt(j);
            if (Character.isDigit(c)) return true;
            for (char ch : IGNORE_CHARS_2) {
                if (c == ch) return true;
            }
        }
        return false;
    }


    private void recountList() {
        for (int i = 0; i < myWordMapList.size(); i++)
            myWordMapList.get(i).put("ランク", String.valueOf(i + 1) + "位");
    }
}
