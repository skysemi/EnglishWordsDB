package net.skysemi.englishwordsdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AsyncFileWriter extends AsyncTask<String, Integer, String> {
    private boolean isForAdd;
    private String word;
    private String fileName;
    private Context context;

    private int position;
    private int y;

    AsyncFileWriter(Context context, boolean isForAdd, String word, String fileName, int position, int y) {
        this.isForAdd = isForAdd;
        this.word = word;
        this.fileName = fileName;
        this.context = context;

        this.position = position;
        this.y = y;
    }

    AsyncFileWriter(Context context, boolean isForAdd, String word, String fileName) {
        this.isForAdd = isForAdd;
        this.word = word;
        this.fileName = fileName;
        this.context = context;

        this.position = 0;
        this.y = 0;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (isForAdd) {
            add();
        } else {
            delete();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (fileName.equals("markedList")) ((MainActivity) context).showMyList(position, y, "");
    }


    private void add() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd");
        File newFile = new File(context.getFilesDir(), fileName);
        FileWriter output = null;
        try {
            output = new FileWriter(newFile, true);
            output.write(word + "\t" + sdf1.format(new Date()) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        changePref(fileName, 1);
    }


    private void delete() {
        File file = new File(context.getFilesDir(), fileName);
        String writer = "";
        try {
            BufferedReader bufferReader = new BufferedReader(new FileReader(file));
            String StringBuffer;
            while ((StringBuffer = bufferReader.readLine()) != null) {
                if (StringBuffer.split("\t")[0].equals(word))
                    continue;
                writer += StringBuffer + "\n";
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter overWrite = null;
        try {
            overWrite = new FileWriter(file, false);
            overWrite.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (overWrite != null) {
                try {
                    overWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        changePref(fileName, -1);
    }


    private void changePref(String name, int dif) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int newNum = sharedPref.getInt(name, 0) + dif;
        sharedPref.edit().putInt(name, newNum).apply();
    }
}
