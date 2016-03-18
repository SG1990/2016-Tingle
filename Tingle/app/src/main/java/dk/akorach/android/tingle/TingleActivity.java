package dk.akorach.android.tingle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

public class TingleActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);

        FragmentManager fm = getSupportFragmentManager();

        if(findViewById(R.id.fragment_container) != null) { // vertical mode
            if (savedInstanceState != null) {
                return;
            }

            Fragment tingleFragment = fm.findFragmentById(R.id.fragment_container);
            if(tingleFragment == null) {
                tingleFragment = new TingleFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container, tingleFragment)
                        .commit();
            }
        } else {                                            // horizontal mode

            if (savedInstanceState != null) {
                return;
            }

            Fragment tingleFragment = fm.findFragmentById(R.id.tingle_fragment);
            if(tingleFragment == null) {
                tingleFragment = new TingleFragment();
                fm.beginTransaction()
                        .add(R.id.tingle_fragment, tingleFragment)
                        .commit();
            }

            Fragment listFragment = fm.findFragmentById(R.id.list_fragment);
            if(listFragment == null) {
                listFragment = new ListFragment();
                fm.beginTransaction()
                        .add(R.id.list_fragment, listFragment)
                        .commit();
            }
        }

    }
}
