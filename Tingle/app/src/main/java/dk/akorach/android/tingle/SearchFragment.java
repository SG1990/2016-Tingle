package dk.akorach.android.tingle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by akor on 23.04.2016.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private static final String DIALOG_MATCHES = "DialogMatches";
    private static final int REQUEST_MATCH = 0;

    private static final String KEY_THING_ID = "thingId";

    private Thing mThing;

    private SearchView mSearchField;
    private ImageView mPhotoView;
    private TextView mThingName;
    private TextView mThingPlacement;
    private TextView mThingBarcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            UUID id = (UUID) savedInstanceState.getSerializable(KEY_THING_ID);
            if(id != null) {
                mThing = ThingsLab.getInstance(getActivity()).getThing(id);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        if(mThing != null) {
            savedInstanceState.putSerializable(KEY_THING_ID, mThing.getId());
        }
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
                ArrayList<Thing> things = (ArrayList) ThingsLab.getInstance(getActivity())
                        .findThingsByName(s);
                if(things == null || things.isEmpty()) {
                    mThing = null;
                    Log.d(TAG, "List of things empty.");
                    Toast toast = Toast.makeText(getActivity(), "No matches found.",
                            Toast.LENGTH_LONG);
                    toast.show();
                } else if(things.size() > 1) {
                    FragmentManager fm = getFragmentManager();
                    MatchSelectorFragment dialog = MatchSelectorFragment
                            .newInstance(things);
                    dialog.setTargetFragment(SearchFragment.this, REQUEST_MATCH);
                    dialog.show(fm, DIALOG_MATCHES);
                } else if(things.size() == 1) {
                    mThing = things.get(0);
                    Log.d(TAG, "No of things in the list: " + things.size());
                    updateUI();
                }

                mSearchField.clearFocus();
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

        if(mThing != null) {
            updateUI();
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_MATCH) {
            UUID id = (UUID) data.getSerializableExtra(MatchSelectorFragment.EXTRA_THING);
            mThing = ThingsLab.getInstance(getActivity()).getThing(id);
            updateUI();
        }
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
