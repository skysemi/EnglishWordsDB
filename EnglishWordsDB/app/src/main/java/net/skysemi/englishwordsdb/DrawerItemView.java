package net.skysemi.englishwordsdb;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * Created by mori on 2014/08/15.
 */
public class DrawerItemView extends LinearLayout {

    public DrawerItemView(Context context) {
        this(context, null);
    }

    public DrawerItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawerItemView, defStyle, 0);
        Drawable icon;
        String text;
        int count = -1;

        try {
            icon = a.getDrawable(R.styleable.DrawerItemView_src);
            text = a.getString(R.styleable.DrawerItemView_text);
            count = a.getInt(R.styleable.DrawerItemView_count, -1);
        } finally {
            a.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.drawer_list_item, this, true);
        ((ImageView) findViewById(R.id.icon)).setImageDrawable(icon);
        ((TextView) findViewById(android.R.id.text1)).setText(text);
        TextView tv = (TextView) findViewById(R.id.count);
        tv.setVisibility(count < 0 ? View.GONE : View.VISIBLE);
        tv.setText(String.valueOf(count));

    }

}
