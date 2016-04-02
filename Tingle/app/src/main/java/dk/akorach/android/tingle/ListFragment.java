package dk.akorach.android.tingle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SG on 25.02.2016.
 */
public class ListFragment extends Fragment {

    private RecyclerView mThingRecyclerView;
    private ThingAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mThingRecyclerView = (RecyclerView) v.findViewById(R.id.thing_recycler_view);
        mThingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        ThingsLab thingLab = ThingsLab.getInstance(getActivity());
        List<Thing> things = thingLab.getThings();
        if(mAdapter == null) {
            mAdapter = new ThingAdapter(things);
            mThingRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setThings(things);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void refreshList() {
        updateUI();
    }

    private class ThingHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView mNameTextView;
        private TextView mBarcodeTextView;
        private TextView mWhereTextView;
        private Thing mThing;

        public ThingHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mNameTextView = (TextView)
                    itemView.findViewById(R.id.list_item_what_textview);
            mBarcodeTextView = (TextView)
                    itemView.findViewById(R.id.list_item_barcode_textview);
            mWhereTextView = (TextView)
                    itemView.findViewById(R.id.list_item_where_textview);
        }

        private void bindThing(Thing thing) {
            mThing = thing;
            mNameTextView.setText(thing.getWhat());
            mBarcodeTextView.setText(thing.getBarcode());
            mWhereTextView.setText(thing.getWhere());
        }

        @Override
        public void onClick(View v) {
            ThingsLab thingLab = ThingsLab.getInstance(getActivity());
            thingLab.deleteThing(mThing);
            updateUI();
        }
    }

    private class ThingAdapter extends RecyclerView.Adapter<ThingHolder> {
        private List<Thing> mThings;

        public ThingAdapter(List<Thing> things) {
            mThings = things;
        }

        @Override
        public ThingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_thing, parent, false);
            return new ThingHolder(view);
        }

        @Override
        public void onBindViewHolder(ThingHolder holder, int position) {
            Thing thing = mThings.get(position);
            holder.bindThing(thing);
        }

        @Override
        public int getItemCount() {
            return mThings.size();
        }

        public void setThings(List<Thing> things) {
            mThings = things;
        }
    }

}
