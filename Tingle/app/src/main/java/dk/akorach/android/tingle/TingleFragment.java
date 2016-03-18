package dk.akorach.android.tingle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by SG on 19.02.2016.
 */
public class TingleFragment extends Fragment {

    //GUI variables
    private Button mListThings;
    private Button mAddThing;
    private TextView mLastAdded;
    private TextView mNewWhat, mNewWhere;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tingle, container, false);

        //Accessing the GUI elements
        mLastAdded = (TextView) v.findViewById(R.id.last_thing);
        mAddThing = (Button) v.findViewById(R.id.add_button);
        mListThings = (Button) v.findViewById(R.id.list_button);
        mNewWhat = (TextView) v.findViewById(R.id.what_text);
        mNewWhere = (TextView) v.findViewById(R.id.where_text);

        //View products click event
        mAddThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mNewWhat.getText().length() > 0) && (mNewWhere.getText().length() > 0)) {
                    Thing thing = new Thing();
                    thing.setWhat(mNewWhat.getText().toString().trim());
                    thing.setWhere(mNewWhere.getText().toString().trim());
                    ThingsLab.getInstance(getContext()).addThing(thing);

                    mNewWhat.setText("");
                    mNewWhere.setText("");
                    updateUI();
                }
            }
        });

        mListThings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ListActivity.class);
                startActivity(i);

            }
        });

        if(getActivity().findViewById(R.id.fragment_container) == null) { //vertical
            mListThings.setVisibility(View.GONE);
        } else {
            mListThings.setVisibility(View.VISIBLE);
        }

        return v;
    }

    private void updateUI(){
        mLastAdded.setText(
                ThingsLab.getInstance(getContext()).getLastThing().toString());

        if(getActivity().findViewById(R.id.fragment_container) == null) {       //if horizontal
            FragmentManager fm = getFragmentManager();
            ListFragment listFragment = (ListFragment) fm.findFragmentById(R.id.list_fragment);
            if (listFragment != null) {
                listFragment.refreshList();
            }
        }
    }
}
