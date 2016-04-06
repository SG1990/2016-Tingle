package dk.akorach.android.tingle;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

public class TingleActivity extends FragmentActivity implements TingleFragment.ToActivity {

    Fragment tingleFragmentLandscape, listFragmentLandscape;

    @Override
    public void stateChange() {
        if ((listFragmentLandscape != null)) ((ListFragment) listFragmentLandscape).refreshList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TingleActivity", "onCreate launched");
        setContentView(R.layout.activity_tingle);

        FragmentManager fm = getSupportFragmentManager();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // vertical mode
            Fragment tingleFragment = fm.findFragmentById(R.id.fragment_container);
            if(tingleFragment == null) {
                tingleFragment = new TingleFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container, tingleFragment)
                        .commit();
            }
        } else {                                            // horizontal mode
            tingleFragmentLandscape = fm.findFragmentById(R.id.tingle_fragment);
            listFragmentLandscape = fm.findFragmentById(R.id.list_fragment);

            if((tingleFragmentLandscape == null) && (listFragmentLandscape == null) ) {
                tingleFragmentLandscape = new TingleFragment();
                listFragmentLandscape = new ListFragment();
                fm.beginTransaction()
                        .add(R.id.tingle_fragment, tingleFragmentLandscape)
                        .add(R.id.list_fragment, listFragmentLandscape)
                        .commit();
            }
        }
    }
}
