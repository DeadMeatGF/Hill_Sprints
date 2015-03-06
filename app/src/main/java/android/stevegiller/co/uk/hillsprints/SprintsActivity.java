package android.stevegiller.co.uk.hillsprints;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class SprintsActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "SprintsActivity";
    static TextView countdownTextView;
    static TextToSpeech tts;
    ImageView imageView;
    TextView exerciseNameTextView;
    static TextView setCountTextView;
    static TextView exerciseCountTextView;
    ArrayList<Exercise> exerciseList = new ArrayList<>();
    ArrayList<Exercise> setList = new ArrayList<>();
    Exercise hillClimb;
    Exercise rest;
    Exercise success;
    Random exercise_selector = new Random(SystemClock.elapsedRealtime());
    String[] exercises;
    TypedArray images;
    Button startSprintsButton;

    static SharedPreferences sharedPref;
    static Context context;

    int current_exercise;
    int current_set;
    int exercise_loader;
    static int set_length;
    static int set_total;

    public static void say(String text) {
        //noinspection deprecation
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public static void setTime(int s) {
        countdownTextView.setText(String.valueOf(s));
    }

    public static void displaySettings() {
        set_total = sharedPref.getInt(context.getString(R.string.set_total), SprintsSettingsActivity.DEFAULT_SETS);
        set_length = sharedPref.getInt(context.getString(R.string.set_length), SprintsSettingsActivity.DEFAULT_EXERCISES);
        setCountTextView.setText(set_total + " sets");
        exerciseCountTextView.setText(set_length + " exercises per set");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprints);
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        countdownTextView = (TextView) findViewById(R.id.countdownTextView);
        exerciseNameTextView = (TextView) findViewById(R.id.exerciseNameTextView);
        setCountTextView = (TextView) findViewById(R.id.setCountMessageTextView);
        exerciseCountTextView = (TextView) findViewById(R.id.exerciseCountMessageTextView);
        imageView = (ImageView) findViewById(R.id.imageView);
        startSprintsButton = (Button) findViewById(R.id.startSprintsButton);
        hillClimb = new Exercise(getResources().getString(R.string.hill_climb), R.drawable.hill_climb);
        rest = new Exercise(getResources().getString(R.string.rest), R.drawable.exhausted);
        success = new Exercise(getResources().getString(R.string.success), R.drawable.success);
        images = getResources().obtainTypedArray(R.array.exercise_images);
        exercises = getResources().getStringArray(R.array.exercise_names);
        startSprintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                current_set = initialiseExercises(0);
                countIn(10);
            }
        });
        tts = new TextToSpeech(this, this);
        displaySettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sprints, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SprintsSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private int initialiseExercises(int s) {
        exerciseList.clear();
        setList.clear();

        for (int exerciseLoop = 0; exerciseLoop < exercises.length; exerciseLoop++) {
            exerciseList.add(new Exercise(exercises[exerciseLoop], images.getResourceId(exerciseLoop, -1)));
        }

        for (exercise_loader = 1; exercise_loader <= set_length; exercise_loader++) {
            current_exercise = exercise_selector.nextInt(exerciseList.size());
            setList.add(exerciseList.get(current_exercise));
            exerciseList.remove(current_exercise);
        }
        s++;
        return s;
    }

    private void countIn(int c) {
        say("Get Ready");
        new ExcerciseCountdownTimer(c + 5, c, false, ExcerciseCountdownTimer.ALERT_COUNTDOWN) {
            @Override
            public void onFinish() {
                hillSprint(0);
            }
        }.start();
    }

    private void hillSprint(int x) {
        final int e = x;
        setCountTextView.setText("Current set: " + current_set);
        exerciseCountTextView.setText("Current rep: " + String.valueOf(e + 1));
        imageView.setImageResource(hillClimb.getExerciseImage());
        exerciseNameTextView.setText(hillClimb.getExerciseName());
        say("Go! Then " + setList.get(e).getExerciseName());
        new ExcerciseCountdownTimer(5, 0, false, ExcerciseCountdownTimer.FULL_COUNTDOWN) {
            @Override
            public void onFinish() {
                newExercise(e);
            }
        }.start();
    }

    private void newExercise(int x) {
        say("Go!");
        //-- I really hate this fudge
        x++;
        final int e = x;
        x--;
        imageView.setImageResource(setList.get(x).getExerciseImage());
        exerciseNameTextView.setText(setList.get(x).getExerciseName());
        new ExcerciseCountdownTimer(30, 5, true, ExcerciseCountdownTimer.FULL_COUNTDOWN) {
            @Override
            public void onFinish() {
                newRest(e);
            }
        }.start();
    }

    private void newRest(int x) {
        final int e = x;
        if (e >= set_length) {
            current_set = initialiseExercises(current_set);
            if (current_set > set_total) {
                finishSet();
            } else {
                relax();
            }
        } else {
            say("Rest. Next exercise will be " + setList.get(e).getExerciseName());
            imageView.setImageResource(rest.getExerciseImage());
            exerciseNameTextView.setText(rest.getExerciseName());
            new ExcerciseCountdownTimer(30, 5, true, ExcerciseCountdownTimer.FULL_COUNTDOWN) {
                @Override
                public void onFinish() {
                    hillSprint(e);
                }
            }.start();
        }
    }

    private void relax() {
        say("Relax before set " + current_set + " ... Next exercise will be " + setList.get(0).getExerciseName());
        imageView.setImageResource(rest.getExerciseImage());
        exerciseNameTextView.setText(rest.getExerciseName());
        new ExcerciseCountdownTimer(60, 10, true, ExcerciseCountdownTimer.FULL_COUNTDOWN) {
            @Override
            public void onFinish() {
                hillSprint(0);
            }
        }.start();
    }

    private void finishSet() {
        say("You're done!");
        setCountTextView.setText("");
        exerciseCountTextView.setText("");
        countdownTextView.setText("");
        imageView.setImageResource(success.getExerciseImage());
        exerciseNameTextView.setText(success.getExerciseName());
        displaySettings();
        startSprintsButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            startSprintsButton.setEnabled(true);
            startSprintsButton.setTextColor(Color.WHITE);
            int result = tts.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG + ":TTS", "This language is not supported");
            }
        } else {
            Log.d(TAG + ":TTS", "Initialisation failed");
        }
    }
}
