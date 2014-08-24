package net.skysemi.englishwordsdb;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class ViewPagerIndicator extends RadioGroup {

    private int mCount;

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    /**
     * ページ数をセットする
     */
    public void setCount(int count) {
        mCount = count;
        removeAllViews();
        // 指定されたページ数分だけRadioButtonを追加
        for (int i = 0; i < count; i++) {
            // RadioButtonにインディケータの画像をセット
            RadioButton rb = new RadioButton(getContext());
            rb.setFocusable(false);
            rb.setClickable(false);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // 4.2以前は背景画像でpaddingを設定していたため、RadioButtonのサイズを画像サイズセットしなければならない
                Drawable d = getResources().getDrawable(R.drawable.indicator);
                rb.setButtonDrawable(d);

                LinearLayout.LayoutParams params = generateDefaultLayoutParams();
                params.width = d.getIntrinsicWidth();
                params.height = d.getIntrinsicHeight();

                rb.setLayoutParams(params);
            } else {
                rb.setButtonDrawable(R.drawable.indicator);
            }
            addView(rb);
        }
        setCurrentPosition(-1);
    }

    /**
     * 現在の位置をセットする
     */
    public void setCurrentPosition(int position) {
        if (position >= mCount) {
            position = mCount - 1;
        }
        if (position < 0) {
            position = mCount > 0 ? 0 : -1;
        }

        if (position >= 0 && position < mCount) {
            // 現在の位置のRadioButtonをチェック状態にする
            RadioButton rb = (RadioButton) getChildAt(position);
            rb.setChecked(true);
        }
    }
}
