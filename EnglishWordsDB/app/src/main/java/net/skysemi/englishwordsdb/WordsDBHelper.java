package net.skysemi.englishwordsdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;


public class WordsDBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "words.db";
    static final int DATABASE_VERSION = 2;
    Context context;

    // コンストラクタ
    public WordsDBHelper(Context context) {
        // 任意のデータベースファイル名と、バージョンを指定する
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    /**
     * このデータベースを初めて使用する時に実行される処理
     * テーブルの作成や初期データの投入を行う
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブルを作成。SQLの文法は通常のSQLiteと同様
        db.execSQL("CREATE TABLE "
                + Word.TB_NAME + "("
                + Word._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Word.WORD + " TINYTEXT NOT NULL, "
                + Word.SITE_ID + " SMALLINT NOT NULL "
                + ");");
        // 必要なら、ここで他のテーブルを作成したり、初期データを挿入したりする
    }


    /**
     * アプリケーションの更新などによって、データベースのバージョンが上がった場合に実行される処理
     * 今回は割愛
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Word.TB_NAME);
        onCreate(db);
    }


    public void insertData(ArrayList<String> arrayList, int siteID) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement sql = db.compileStatement("INSERT INTO "
                    + Word.TB_NAME + "(" + Word.WORD + "," + Word.SITE_ID + ") VALUES(?,?)");
            for (String s : arrayList) {
                sql.bindString(1, s);
                sql.bindLong(2, siteID);
                sql.executeInsert();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();

        }
    }


}
