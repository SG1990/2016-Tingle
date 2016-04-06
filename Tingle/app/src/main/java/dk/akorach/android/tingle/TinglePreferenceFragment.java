package dk.akorach.android.tingle;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by SG on 06.04.2016.
 */
public class TinglePreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.tingle_preferences);
    }
}
