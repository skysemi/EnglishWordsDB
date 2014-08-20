package net.skysemi.englishwordsdb;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mori on 2014/08/17.
 */
public class CustomPagerAdapter  extends PagerAdapter {

    private Context mContext;
    public static final int PAGE_NUM = 4;
    private int[] images = {R.drawable.tutorial00,R.drawable.tutorial01,R.drawable.tutorial02,R.drawable.tutorial03};
    private String[] explanations = {
            " 英単語コレクター\n英語のサイトから英単語を集めて、あなただけの単語帳を自動で作ることができます。",
    "お使いのブラウザの共有ボタンから、サイトの全単語をコレクトすることができます。",
    "アプリのオプションボタンから、ランダムにWikipediaの記事をコレクトしたり、URLからのコレクトもできます。",
    "覚えた単語はどんどん「覚えたリスト」に追加していきましょう。"};


    public CustomPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // View を生成

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView iv = new ImageView(mContext);
        iv.setAdjustViewBounds(true);
        iv.setMaxHeight(300);
        iv.setImageDrawable(mContext.getResources().getDrawable(images[position]));
        linearLayout.addView(iv);

        TextView textView = new TextView(mContext);
        textView.setText(explanations[position]);
        textView.setTextSize(16);
        textView.setPadding(24,12,12,24);
        linearLayout.addView(textView);

        // コンテナに追加
        container.addView(linearLayout);

        return linearLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // コンテナから View を削除
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        // リストのアイテム数を返す
        return PAGE_NUM;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // Object 内に View が存在するか判定する
        return arg0 == arg1;
    }
}


