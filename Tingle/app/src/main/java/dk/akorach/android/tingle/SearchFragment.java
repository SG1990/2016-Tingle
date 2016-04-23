package dk.akorach.android.tingle;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by SG on 23.04.2016.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private Thing mThing;

    private SearchView mSearchField;
    private ImageView mPhotoView;
    private TextView mThingName;
    private TextView mThingPlacement;
    private TextView mThingBarcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchField = (SearchView) v.findViewById(R.id.search_field);
        mSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                List<Thing> things = ThingsLab.getInstance(getActivity())
                        .findThingsByName(s);
                //TODO: if multiple hits, display a dialog

                if (things != null && !things.isEmpty()){
                    mThing = things.get(0);
                    Log.d(TAG, "No of things in the list: " + things.size());
                } else {
                    mThing = null;
                    Log.d(TAG, "List of things empty.");
                    Toast toast = Toast.makeText(getActivity(), "No matches found.",
                            Toast.LENGTH_LONG);
                    toast.show();
                }

                updateUI();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.thing_photo_search);
        mPhotoView.setVisibility(View.GONE);

        mThingName = (TextView) v.findViewById(R.id.thing_name);
        mThingPlacement = (TextView) v.findViewById(R.id.thing_placement);
        mThingBarcode = (TextView) v.findViewById(R.id.thing_barcode);

        return v;
    }

    private void updateUI() {
        if(mThing == null) return;

        mThingName.setText(mThing.getWhat());
        mThingPlacement.setText(mThing.getWhere());
        mThingBarcode.setText(mThing.getBarcode());

        updatePhotoView(mThing);
    }

    private void updatePhotoView(Thing thing) {
        File file = ThingsLab.getInstance(getActivity()).getPhotoFile(thing);
        if(file == null || !file.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            mPhotoView.setVisibility(View.VISIBLE);
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    file.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
