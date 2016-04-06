package dk.akorach.android.tingle;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TingleActivity extends AppCompatActivity implements TingleFragment.ToActivity {

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
            Fragment tingleFragment = fm.findFragmentById(R.id.tingle_fragment);
            if(tingleFragment == null) {
                tingleFragment = new TingleFragment();
                fm.beginTransaction()
                        .add(R.id.tingle_fragment, tingleFragment)
                        .commit();
            }
        } else {                                            // horizontal mode
            tingleFragmentLandscape = fm.findFragmentById(R.id.tingle_fragment);
            listFragmentLandscape = fm.findFragmentById(R.id.list_fragment);

            if((tingleFragmentLandscape == null) ) {
                tingleFragmentLandscape = new TingleFragment();
                fm.beginTransaction()
                        .add(R.id.tingle_fragment, tingleFragmentLandscape)
                        .commit();
            }

            if((listFragmentLandscape == null) ) {
                listFragmentLandscape = new ListFragment();
                fm.beginTransaction()
                        .add(R.id.list_fragment, listFragmentLandscape)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_tingle_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_settings:
                Intent intent = new Intent();
                intent.setClassName(this, "dk.akorach.android.tingle.TinglePreferenceActivity");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
