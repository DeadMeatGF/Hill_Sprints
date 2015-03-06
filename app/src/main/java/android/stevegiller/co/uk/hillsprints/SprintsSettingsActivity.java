package android.stevegiller.co.uk.hillsprints;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SprintsSettingsActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "SprintsSettingsActivity";

    ImageButton setCountIncreaseImageButton;
    TextView setCountSelectedTextView;
    ImageButton setCountReduceImageButton;
    ImageButton exerciseCountIncreaseImageButton;
    TextView exerciseCountSelectedTextView;
    ImageButton exerciseCountReduceImageButton;

    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;
    Context context;

    public static final int MIN_SETS = 1;
    public static final int DEFAULT_SETS = 3;
    public static final int MAX_SETS = 5;
    public static final int MIN_EXERCISES = 1;
    public static final int DEFAULT_EXERCISES = 6;
    public static final int MAX_EXERCISES = 12;

    private int sets;
    private int exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_settings);
        Log.d(TAG, "Getting sets and reps from preferences");
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        prefEditor = sharedPref.edit();
        sets = sharedPref.getInt(getResources().getString(R.string.set_total), DEFAULT_SETS);
        exercises = sharedPref.getInt(getResources().getString(R.string.set_length), DEFAULT_EXERCISES);
        setCountIncreaseImageButton = (ImageButton) findViewById(R.id.setCountIncreaseImageButton);
        setCountSelectedTextView = (TextView) findViewById(R.id.setCountSelectedTextView);
        setCountReduceImageButton = (ImageButton) findViewById(R.id.setCountReduceImageButton);
        exerciseCountIncreaseImageButton = (ImageButton) findViewById(R.id.exerciseCountIncreaseImageButton);
        exerciseCountSelectedTextView = (TextView) findViewById(R.id.exerciseCountSelectedTextView);
        exerciseCountReduceImageButton = (ImageButton) findViewById(R.id.exerciseCountReduceImageButton);

        setCountIncreaseImageButton.setOnClickListener(this);
        setCountReduceImageButton.setOnClickListener(this);
        setCountSelectedTextView.setText(String.valueOf(sets));
        exerciseCountIncreaseImageButton.setOnClickListener(this);
        exerciseCountReduceImageButton.setOnClickListener(this);
        exerciseCountSelectedTextView.setText(String.valueOf(exercises));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exerciseCountIncreaseImageButton:
                if(exercises < MAX_EXERCISES) { exercises++; }
                break;
            case R.id.exerciseCountReduceImageButton:
                if(exercises > MIN_EXERCISES) { exercises--; }
                break;
            case R.id.setCountIncreaseImageButton:
                if(sets < MAX_SETS) { sets++; }
                break;
            case R.id.setCountReduceImageButton:
                if(sets > MIN_SETS) { sets--; }
                break;
            default:
                //-- What the hell are you clicking?
        }
        exerciseCountSelectedTextView.setText(String.valueOf(exercises));
        setCountSelectedTextView.setText(String.valueOf(sets));
        prefEditor.putInt(getResources().getString(R.string.set_total), sets);
        prefEditor.putInt(getResources().getString(R.string.set_length), exercises);
        prefEditor.commit();
        SprintsActivity.displaySettings();
    }
}
