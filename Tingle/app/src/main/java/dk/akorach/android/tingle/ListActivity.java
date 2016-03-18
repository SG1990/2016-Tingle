package dk.akorach.android.tingle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_list_container);

        if(findViewById(R.id.fragment_list_container) != null) { // vertical mode

            if (savedInstanceState != null) {
                return;
            }

            if(fragment == null) {
                fragment = new ListFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_list_container, fragment)
                        .commit();
            }
        } else {                                            // horizontal mode

            if(fragment == null) {
                fragment = new ListFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_list_container, fragment)
                        .commit();
            }
        }
    }
}
