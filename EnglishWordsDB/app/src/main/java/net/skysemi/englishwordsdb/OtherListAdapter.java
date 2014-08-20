package net.skysemi.englishwordsdb;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by mori on 2014/08/12.
 */
public class OtherListAdapter extends SimpleAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private int lastPosition = -1;
    // コンストラクタ
    public OtherListAdapter(Context context, List<? extends Map<String, ?>> data) {
        super(context, data, R.layout.other_list_item, new String[]{"単語", "登録日"}, new int[]{R.id.itemName, R.id.itemDate});
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //初期アニメなし
    public OtherListAdapter(Context context, List<? extends Map<String, ?>> data,int position) {
        super(context, data, R.layout.other_list_item, new String[]{"単語", "登録日"}, new int[]{R.id.itemName, R.id.itemDate});
        this.context = context;
        this.lastPosition = position;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        Map map = (Map) getItem(position);

        // Viewを再利用している場合は新たにViewを作らない
        if (view == null) {
            view = mInflater.inflate(R.layout.other_list_item, null);
            holder = new ViewHolder();
            holder.wordText = (TextView) view.findViewById(R.id.itemName);
            holder.dateText = (TextView) view.findViewById(R.id.itemDate);
            holder.cardLayout = (RelativeLayout) view.findViewById(R.id.listLayout);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        // 特定の行のデータを取得
        String name = map.get("単語").toString();
        String date = map.get("登録日").toString();

        if (!TextUtils.isEmpty(name)) {
            holder.wordText.setText(name);
            holder.dateText.setText(date);
        }

        // 行毎に背景色を変える
        int color;
        switch (Integer.parseInt(date.split("/")[0])) {
            case 1:
                color = R.color.January2;
                break;
            case 2:
                color = R.color.February2;
                break;
            case 3:
                color = R.color.March2;
                break;
            case 4:
                color = R.color.April2;
                break;
            case 5:
                color = R.color.May2;
                break;
            case 6:
                color = R.color.June2;
                break;
            case 7:
                color = R.color.July2;
                break;
            case 8:
                color = R.color.August2;
                break;
            case 9:
                color = R.color.September2;
                break;
            case 10:
                color = R.color.October2;
                break;
            case 11:
                color = R.color.November2;
                break;
            case 12:
                color = R.color.December2;
                break;
            default:
                color = Color.GRAY;
        }
        holder.cardLayout.setBackgroundColor(context.getResources().getColor(color));

        // XMLで定義したアニメーションを読み込む
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide);
            view.startAnimation(animation);
            lastPosition = position;
        }
        return view;
    }

    static class ViewHolder {
        TextView wordText;
        TextView dateText;
        RelativeLayout cardLayout;
    }
}
