package giftview.co.giftview_android03;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by parkdgun on 2015-07-10.
 */
public class GridViewAdapter extends ArrayAdapter<GItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<GItem> data = new ArrayList<GItem>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<GItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView)row.findViewById(R.id.TextPanel);
            holder.data = (TextView)row.findViewById(R.id.TextData);
            row.setTag(holder);
        }else {
            holder = (ViewHolder)row.getTag();
        }

        GItem item = data.get(position);
        holder.title.setText(item.getTitle());
        holder.data.setText(item.getData());

        return row;
    }

    static class ViewHolder {
        TextView title;
        TextView data;
    }
}
