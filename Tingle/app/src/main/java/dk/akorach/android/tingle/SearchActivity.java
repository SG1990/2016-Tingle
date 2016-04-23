package dk.akorach.android.tingle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by SG on 23.04.2016.
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_search_container);

        if(fragment == null) {
            fragment = new SearchFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_search_container, fragment)
                    .commit();
        }
    }
}
