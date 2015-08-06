package giftview.co.giftview_android03;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by parkdgun on 2015-07-06.
 */
public class AirFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private Button Mbutton;

    public void onEvent(DataEvent e) {
        gridAdapter.getItem(0).setData(e.getTem());
        gridAdapter.getItem(1).setData(e.getBattery());
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hello, container, false);
        EventBus.getDefault().register(this);

        Mbutton = (Button) rootView.findViewById(R.id.Mbutton);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);
        Mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MeasurementEvent());
            }
        });

        return rootView;
    }

    private ArrayList<GItem> getData() {
        final ArrayList<GItem> GItem = new ArrayList<>();
        TypedArray item = getResources().obtainTypedArray(R.array.item_ids);
        for (int i = 0; i < item.length(); i++) {
            String titleText = item.getString(i);
            GItem.add(new GItem(titleText, "a"));
        }
        return GItem;
    }
}
