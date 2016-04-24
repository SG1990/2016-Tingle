package dk.akorach.android.tingle;

import android.preference.PreferenceActivity;

import java.util.List;

/**
 * Created by akor on 06.04.2016.
 */
public class TinglePreferenceActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.tingle_headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return TinglePreferenceFragment.class.getName().equals(fragmentName);
    }
}
