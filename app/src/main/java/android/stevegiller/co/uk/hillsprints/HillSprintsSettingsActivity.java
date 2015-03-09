package android.stevegiller.co.uk.hillsprints;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HillSprintsSettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
