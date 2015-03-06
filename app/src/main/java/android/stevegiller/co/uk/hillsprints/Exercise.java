package android.stevegiller.co.uk.hillsprints;

public class Exercise {

    private int exerciseImage;
    private String exerciseName;

    public Exercise(String name, int resource) {
        this.exerciseImage = resource;
        this.exerciseName = name;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getExerciseImage() {
        return exerciseImage;
    }
}
