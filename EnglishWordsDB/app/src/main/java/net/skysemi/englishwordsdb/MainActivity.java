package net.skysemi.englishwordsdb;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    static final int ANIMATION_DURATION = 500;
    private ListView mListView;
    private MyWordList myList;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private AdView adView;
    private InterstitialAd interstitial;

    public void setMyList() {
        myList = new MyWordList(this);
    }

    /*最初呼ばれる*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        // AdView をリソースとしてルックアップしてリクエストを読み込む
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        // インタースティシャルを作成する。
        loadInterstitial();



        mListView = (ListView) findViewById(R.id.listView);
        myList = new MyWordList(this);

        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
            CharSequence url = getIntent().getExtras().getCharSequence(Intent.EXTRA_TEXT);
            (new AsyncHttpRequest(url.toString(), this)).execute();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = sharedPref.getBoolean("first_time", true);
        if(isFirstTime){
            showFirstTutorial();
            sharedPref.edit().putBoolean("first_time", false).commit();
        }
    }
    public void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-5520972500232876/1168590348");
        interstitial.loadAd(adRequest);
    }
    public void displayInterstitial() {if (interstitial.isLoaded()) {interstitial.show();}}


    private String[] TITLES = {"ようこそ","ブラウザから","アプリから","覚えたリスト"};
    ViewPagerIndicator mViewPagerIndicator;
    private void showFirstTutorial(){

        final Dialog d = new Dialog(this);
        Window window = d.getWindow();
        // 外部タッチで閉じる
        d.setCanceledOnTouchOutside(true);
        // タイトル非表示
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.tutorial_pager_dialog);

        //ViewPagerにPagerAdapterをセット
        CustomPagerAdapter adapter = new CustomPagerAdapter(this);
        final ViewPager myPager = (ViewPager) d.findViewById(R.id.viewPager);
        myPager.setAdapter(adapter);

        // 縦のサイズを80%にする
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogHeight = (int) (metrics.heightPixels * 0.7);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = dialogHeight;
        window.setAttributes(lp);

        mViewPagerIndicator = (ViewPagerIndicator) d.findViewById(R.id.indicator);
        mViewPagerIndicator.setCount(adapter.getCount());

        final Button button = (Button)d.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = myPager.getCurrentItem();
                if(i<3){
                    myPager.setCurrentItem(i + 1);
                }else{
                    d.dismiss();
                }

            }
        });

        final TextView titleText = (TextView) d.findViewById(R.id.tutorialTitle);
        myPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        mViewPagerIndicator.setCurrentPosition(position);
                        titleText.setText(TITLES[position]);
                        if(position<3){
                            button.setText("次へ");
                        }else{
                            button.setText("完了");
                        }
                    }
                });

        d.show();
        //sharedPref.edit().putBoolean("first_time",false).commit();
        updateMyList();
    }
    public void onBackPressed() {
        displayInterstitial();
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    /*リストをデータベースから更新する*/
    public void updateMyList() {
        myList = new MyWordList(this);
        showMyList();
    }

    /*リストを表示させるだけ*/
    public void showMyList() {
        showMyList(0, 0, "");
    }

    /*単語を取り除いてからリストを表示させる（データベース操作なし）*/
    public void showMyList(int position, int y, String newIgnoreWord) {
        final List<Map<String, String>> list = myList.get(newIgnoreWord);
        final MyListAdapter simpleAdapter = new MyListAdapter(this, list);
        mListView.setDividerHeight(2);
        mListView.setAdapter(simpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String clickedWord = list.get(position).get("単語");
                listClickDialog(clickedWord, simpleAdapter.getMarkedWords().contains(clickedWord), view).show();
            }
        });
        mListView.setSelectionFromTop(position, y);
    }

    /*マイ単語帳のアイテムをクリック時に表示するダイアログ*/
    public Dialog listClickDialog(final String clickedWord, final boolean isMarked, final View view) {
        String[] option;
        if (isMarked) {
            option = new String[]{"意味検索", "マーカーを消す", "覚えたリストへ", "無視リストへ", "キャンセル"};
        } else {
            option = new String[]{"意味検索", "マーカー", "覚えたリストへ", "無視リストへ", "キャンセル"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(clickedWord)
                .setItems(option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int position = mListView.getFirstVisiblePosition();
                        int y = mListView.getChildAt(0).getTop();
                        switch (which) {
                            case 0:
                                String url = "http://ejje.weblio.jp/content/" + clickedWord;
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(i);
                                break;
                            case 1:
                                addToFile(clickedWord, "markedList");
                                if (isMarked) deleteWord(clickedWord, "markedList");
                                showMyList(position, y, "");
                                break;
                            case 2:
                                deleteFromMyList(view, clickedWord, isMarked, position, y, "rememberedList");
//                                addToFile(clickedWord,"rememberedList");
//                                if(isMarked)deleteWord(clickedWord,"markedList");
//                                showMyList(position, y, clickedWord);
                                break;
                            case 3:
                                deleteFromMyList(view, clickedWord, isMarked, position, y, "ignoreList");
//                                addToFile(clickedWord,"ignoreList");
//                                if(isMarked)deleteWord(clickedWord,"markedList");
//                                showMyList(position, y, clickedWord);
                                break;
                            case 4:
                                break;
                            default:
                        }
                    }
                });
        return builder.create();
    }

    /*無視する単語をファイルに書き込む*/
    public void addToFile(String word, String fileName) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd");
        File newFile = new File(getFilesDir(), fileName);
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

    private void deleteFromMyList(final View v, final String clickedWord, final boolean isMarked, final int position, final int y, final String fileName) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                addToFile(clickedWord, fileName);
                if (isMarked) deleteWord(clickedWord, "markedList");
                showMyList(position, y, clickedWord);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };
        collapse(v, al, fileName.equals("rememberedList"));
    }

    private void collapse(final View v, Animation.AnimationListener al, final boolean isRememberedList) {
        final int initialHeight = v.getMeasuredHeight();
        final int initialWidth = v.getMeasuredWidth();
        final int multiplier = isRememberedList ? -1 : 1;

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.setTranslationX(multiplier * initialWidth * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }


    /*
     * ドロワーの準備用
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.my_list);
                showMyList();
                break;
            case 2:
                mTitle = getString(R.string.site_list);
                showSiteList(0, 0);
                break;
            case 3:
                mTitle = getString(R.string.remembered_list);
                showIgnoredList(0, 0, "rememberedList", true);
                break;
            case 4:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
        }
    }


    /*サイト一覧を表示する*/
    public void showSiteList(){
        int position = mListView.getFirstVisiblePosition();
        int y = mListView.getChildAt(0).getTop();
        showSiteList(position, y);
    }

    public void showSiteList(int position, int y) {
        final List<Map<String, String>> list = siteMapList();

        SiteListAdapter simpleAdapter = new SiteListAdapter(this, list);
        mListView.setDividerHeight(2);
        mListView.setAdapter(simpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                siteListClickDialog(list.get(position),view).show();
            }
        });
        mListView.setSelectionFromTop(position, y);
    }

    /*マイ単語帳のアイテムをクリック時に表示するダイアログ*/
    public Dialog siteListClickDialog(final Map map,final View view) {
        String[] option = new String[]{"サイトへ移動", "このサイトを削除", "キャンセル"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(map.get("サイト名").toString())
                .setItems(option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Uri uri = Uri.parse(map.get("URL").toString());
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                break;
                            case 1:
                                int position = mListView.getFirstVisiblePosition();
                                int y = mListView.getChildAt(0).getTop();
                                String siteID = map.get("サイトID").toString();
                                deleteWord(siteID, "siteList");
                                deleteSiteFromDB(siteID);
                                deleteAnimFromSiteList(view,position,y);
                                break;
                            case 2:
                                break;
                            default:
                        }
                    }
                });
        return builder.create();
    }

    public void deleteSiteFromDB(String siteID) {
        WordsDBHelper helper = null;
        SQLiteDatabase db = null;
        try {
            helper = new WordsDBHelper(this);
            db = helper.getWritableDatabase();
            db.delete(Word.TB_NAME, Word.SITE_ID + " = " + siteID, null);
            myList = new MyWordList(this);
        } finally {
            if (db != null) db.close();
            if (helper != null) helper.close();
        }
    }

    /*ファイルからサイトリストのためのシンプルアダプタ用にマップのリストを作る*/
    private List<Map<String, String>> siteMapList() {
        File file = new File(getFilesDir(), "siteList");
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            BufferedReader bufferReader = new BufferedReader(new FileReader(file));
            String StringBuffer;
            while ((StringBuffer = bufferReader.readLine()) != null) {
                Map<String, String> map = new HashMap<String, String>();
                String[] strings = StringBuffer.split("\t");
                map.put("サイトID", strings[0]);
                map.put("サイト名", strings[1]);
                map.put("URL", strings[2]);
                map.put("単語数", strings[3]);
                map.put("登録日", strings[4]);
                list.add(map);
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkListSize("siteList", list.size());
        Collections.reverse(list);
        return list;
    }


    private void deleteAnimFromSiteList(final View v, final int position, final int y) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                showSiteList(position, y);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationStart(Animation animation) {
            }
        };
        collapse(v, al, false);
    }





    public void changePref(String name, int dif) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int newNum = sharedPref.getInt(name, 0) + dif;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(name, newNum);
        editor.commit();
    }

    public void checkListSize(String filename, int listSize) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(filename, listSize);
        editor.commit();
    }

    /*覚えたリスト・無視リストを表示*/
    public void showIgnoredList(int position, int y, final String fileName, final boolean isRememberedList) {
        final List<Map<String, String>> list = ignoredMapList(fileName);
        //バグ用確かめ
        checkListSize(fileName, list.size());

        mListView.setDividerHeight(0);

        BaseAdapter simpleAdapter;
        if (isRememberedList) {
            simpleAdapter = new OtherListAdapter(this, list, position+10);
        } else {
            simpleAdapter = new SimpleAdapter(this, list, R.layout.other_list_item, new String[]{"単語", "登録日"}, new int[]{R.id.itemName, R.id.itemDate});
        }
        mListView.setAdapter(simpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,final View view, final int position, long id) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                if (isRememberedList) {
                    alertDialogBuilder.setTitle("覚えたリストから削除して良いですか？");
                } else {
                    alertDialogBuilder.setTitle("無視リストから削除して良いですか？");
                }
                alertDialogBuilder.setPositiveButton("はい",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int firstVisiblePosition = mListView.getFirstVisiblePosition();
                                int y = mListView.getChildAt(0).getTop();
                                deleteWord(list.get(position).get("単語"), fileName);
                                if(isRememberedList){
                                    deleteAnimFromRememberedList(view,firstVisiblePosition,y);
                                }else{
                                    showIgnoredList(firstVisiblePosition, y, fileName, isRememberedList);
                                }
                            }
                        }
                );
                alertDialogBuilder.setNegativeButton("キャンセル",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );
                alertDialogBuilder.setCancelable(true);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        mListView.setSelectionFromTop(position, y);
    }
    private void deleteAnimFromRememberedList(final View v, final int firstVisiblePosition, final int y) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                showIgnoredList(firstVisiblePosition, y, "rememberedList", true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationStart(Animation animation) {
            }
        };
        collapse(v, al, true);
    }


    /*ファイルから覚えたリスト・無視リストのためのシンプルアダプタ用にマップのリストを作る*/
    private List<Map<String, String>> ignoredMapList(String fileName) {
        File file = new File(getFilesDir(), fileName);
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            BufferedReader bufferReader = new BufferedReader(new FileReader(file));
            String StringBuffer;
            while ((StringBuffer = bufferReader.readLine()) != null) {
                Map<String, String> map = new HashMap<String, String>();
                String[] strings = StringBuffer.split("\t");
                map.put("単語", strings[0]);
                map.put("登録日", strings[1]);
                list.add(map);
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(list);
        return list;
    }

    public void deleteWord(String word, String fileName) {
        File file = new File(getFilesDir(), fileName);
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











    /*
     * オプション作成
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
        }
        return true;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_url:
                urlDialog().show();
                break;
            case R.id.action_ignore_list:
                getActionBar().setTitle("無視リスト");
                showIgnoredList(0, 0, "ignoreList", false);
                break;
            case R.id.action_go_wiki:
                Uri uri = Uri.parse("http://en.wikipedia.org/");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;
            case R.id.action_random:
                (new AsyncHttpRequest("http://en.wikipedia.org/wiki/Special:Random", MainActivity.this)).execute();
                Toast.makeText(MainActivity.this, "ランダムページから取得します",
                        Toast.LENGTH_SHORT).show();
                displayInterstitial();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    /*URL入力用ダイアログを取得する*/
    public Dialog urlDialog() {
        final EditText editView = new EditText(MainActivity.this);
        editView.setText("http://");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("URL入力").setView(editView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String s = editView.getText().toString();
                        (new AsyncHttpRequest(s, MainActivity.this)).execute();
                        Toast.makeText(MainActivity.this, s + "から取得します",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        return builder.create();
    }
}
