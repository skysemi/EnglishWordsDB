package net.skysemi.englishwordsdb;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by mori on 2014/08/12.
 */
public class SiteListAdapter extends SimpleAdapter {

    private LayoutInflater mInflater;
    private Context context;
    // コンストラクタ
    public SiteListAdapter(Context context, List<? extends Map<String, ?>> data) {
        super(context, data, R.layout.site_list_item, new String[]{"サイト名", "URL","単語数", "登録日"}, new int[]{R.id.siteName, R.id.siteURL,R.id.siteWordsNum, R.id.siteDate});
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        Map map = (Map) getItem(position);

        // Viewを再利用している場合は新たにViewを作らない
        if (view == null) {
            view = mInflater.inflate(R.layout.site_list_item, null);
            holder = new ViewHolder();
            holder.titleText = (TextView) view.findViewById(R.id.siteName);
            holder.urlText = (TextView) view.findViewById(R.id.siteURL);
            holder.wordsNumText = (TextView) view.findViewById(R.id.siteWordsNum);
            holder.dateText = (TextView) view.findViewById(R.id.siteDate);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        // 特定の行のデータを取得
        String name = map.get("サイト名").toString();
        String url = map.get("URL").toString();
        String wordsNum = map.get("単語数").toString();
        String date = map.get("登録日").toString();

        if (!TextUtils.isEmpty(name)) {
            holder.titleText.setText(name);
            holder.urlText.setText(url);
            holder.wordsNumText.setText(wordsNum);
            holder.dateText.setText(date);
        }

        // 行毎に背景色を変える
        int color;
        switch (Integer.parseInt(date.split("/")[0])) {
            case 1:
                color = R.color.January;
                break;
            case 2:
                color = R.color.February;
                break;
            case 3:
                color = R.color.March;
                break;
            case 4:
                color = R.color.April;
                break;
            case 5:
                color = R.color.May;
                break;
            case 6:
                color = R.color.June;
                break;
            case 7:
                color = R.color.July;
                break;
            case 8:
                color = R.color.August;
                break;
            case 9:
                color = R.color.September;
                break;
            case 10:
                color = R.color.October;
                break;
            case 11:
                color = R.color.November;
                break;
            case 12:
                color = R.color.December;
                break;
            default:
                color = Color.LTGRAY;
        }
        view.setBackgroundColor(context.getResources().getColor(color));
        return view;
    }

    static class ViewHolder {
        TextView titleText;
        TextView urlText;
        TextView dateText;
        TextView wordsNumText;
    }
}
