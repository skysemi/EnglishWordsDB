package net.skysemi.englishwordsdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyListAdapter extends SimpleAdapter {

    private final int oddColor;
    private final int evenColor;
    private final int markerColor;
    private LayoutInflater mInflater;
    private Context context;
    private Typeface[] mTypeface = {Typeface.DEFAULT, Typeface.SANS_SERIF, Typeface.SERIF, Typeface.MONOSPACE};
    private ArrayList<String> markedWords;


    // コンストラクタ
    public MyListAdapter(Context context, List<Map<String, String>> data) {
        super(context, data, R.layout.my_list_item, new String[]{"ランク", "単語", "出現数"}, new int[]{R.id.rank, R.id.word, R.id.number});
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        markedWords = new ArrayList<String>();
        loadMarkedWords();

        oddColor = context.getResources().getColor(R.color.my_list_odd);
        evenColor = context.getResources().getColor(R.color.my_list_even);
        markerColor = context.getResources().getColor(R.color.my_list_marker);
    }

    public ArrayList<String> getMarkedWords() {
        return markedWords;
    }

    private void loadMarkedWords() {
        File file = new File(context.getFilesDir() + "/markedList");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String StringBuffer;
            while ((StringBuffer = bufferedReader.readLine()) != null) {
                String[] strings = StringBuffer.split("\t");
                markedWords.add(strings[0]);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        Map map = (Map) getItem(position);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Viewを再利用している場合は新たにViewを作らない
        if (view == null) {
            view = mInflater.inflate(R.layout.my_list_item, null);
            holder = new ViewHolder();
            holder.wordText = (TextView) view.findViewById(R.id.word);
            holder.rankText = (TextView) view.findViewById(R.id.rank);
            holder.numText = (TextView) view.findViewById(R.id.number);
            holder.wordText2 = (TextView) view.findViewById(R.id.word2);
            holder.wordText3 = (TextView) view.findViewById(R.id.word3);
            holder.wordBG = (LinearLayout) view.findViewById(R.id.wordBG);
            holder.listLayout = (RelativeLayout) view.findViewById(R.id.listLayout);
            view.setTag(holder);

            Typeface tp = mTypeface[Integer.parseInt(sharedPref.getString("words_font", "0"))];
            holder.wordText.setTypeface(tp);
            holder.wordText2.setTypeface(tp);
            holder.wordText3.setTypeface(tp);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        // 特定の行のデータを取得
        String rank = map.get("ランク").toString();
        String word = map.get("単語").toString();
        String num = map.get("出現数").toString();


        if (!TextUtils.isEmpty(word)) {
            holder.rankText.setText(rank);
            holder.wordText.setText(word.substring(0, 2));
            holder.wordText2.setText(word.substring(2, word.length() - 1));
            holder.wordText3.setText(word.substring(word.length() - 1));
            holder.numText.setText(num);
        }

        // 行毎に背景色を変える
        if (position % 2 == 0) {
            holder.listLayout.setBackgroundColor(evenColor);
        } else {
            holder.listLayout.setBackgroundColor(oddColor);
        }

        //マーカー
        if (markedWords.contains(word)) {
            holder.wordBG.setBackgroundColor(markerColor);
        } else {
            holder.wordBG.setBackgroundColor(Color.alpha(0));
        }

        //カードサイズ
        switch (Integer.parseInt(sharedPref.getString("card_size", "1"))) {
            case 0:
                holder.listLayout.setMinimumHeight(0);
                break;
            case 1:
                holder.listLayout.setMinimumHeight(300);
                break;
            case 2:
                holder.listLayout.setMinimumHeight(1000);
                break;
            default:
        }


        if (sharedPref.getBoolean("anim", false)) {
            // XMLで定義したアニメーションを読み込む
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.item_motion);
            switch (Integer.parseInt(sharedPref.getString("anim_length", "1"))) {
                case 0:
                    anim.setStartOffset(200);
                    anim.setDuration(500);
                    break;
                case 1:
                    anim.setStartOffset(1000);
                    anim.setDuration(2000);
                    break;
                case 2:
                    anim.setStartOffset(3000);
                    anim.setDuration(2000);
                    break;
                default:
            }
            // リストアイテムのアニメーションを開始
            holder.wordText2.startAnimation(anim);
        }
        return view;
    }

    static class ViewHolder {

        LinearLayout wordBG;
        TextView wordText;
        TextView wordText2;
        TextView wordText3;
        TextView rankText;
        TextView numText;
        RelativeLayout listLayout;
    }
}
