package android.stevegiller.co.uk.hillsprints;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class TimePickerPreference extends DialogPreference {
    private String mTimeString;
    private static final String DEFAULT_VALUE = "17:00";

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.timepicker_preference_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {
            persistString(mTimeString);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if(restorePersistedValue) {
            mTimeString = this.getPersistedString(DEFAULT_VALUE);
        } else {
            mTimeString = (String) defaultValue;
            persistString(mTimeString);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) { return a.getString(index); }

    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        String value;

        public SavedState(Parcelable superState) { super(superState); }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readString();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeString(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) { return new SavedState(in); }
            public SavedState[] newArray(int size) { return new SavedState[size]; }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superstate = super.onSaveInstanceState();
        // Check whether this Preference is persistent
        if(isPersistent()) {
            // No need to save instance state as it is persistent;
            return superstate;
        }

        // Create instance of custom BaseSavedState
        final SavedState savedState = new SavedState(superstate);
        // Set the state's value with the class member that holds the setting value
        savedState.value = mTimeString;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if(state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        // Set the Preference's widget to reflect the restored state

    }
}
