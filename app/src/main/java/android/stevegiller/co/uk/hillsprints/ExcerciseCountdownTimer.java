package android.stevegiller.co.uk.hillsprints;

import android.view.View;

/**
 * Created by Giller_S on 17/02/2015.
 */
abstract class ExcerciseCountdownTimer extends LastTickCountDownTimer {

    public static final int FULL_COUNTDOWN = 0;
    public static final int ALERT_COUNTDOWN = 1;
    public static final int NO_COUNTDOWN = 99;

    int countdownDisplay;
    long countDownLength;
    long alertTime;
    boolean voiceCountdown;
    boolean hasNotified;
    View v;

    ExcerciseCountdownTimer(int time, int alert, boolean halfway, int display) {
        super(time * 1000, 1000);
        this.countDownLength = time * 1000;
        this.alertTime = alert * 1000;
        if (alertTime > 1000) {
            this.voiceCountdown = true;
        } else {
            this.voiceCountdown = false;
        }
        if (halfway) {
            this.hasNotified = false;
        } else {
            this.hasNotified = true;
        }
        this.countdownDisplay = display;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        int s = (int) ((millisUntilFinished + 499) / 1000);
        if(countdownDisplay == FULL_COUNTDOWN) { SprintsActivity.setTime(s); }
        if(countdownDisplay == ALERT_COUNTDOWN && millisUntilFinished <= alertTime) { SprintsActivity.setTime(s); }
        if (!hasNotified && millisUntilFinished < countDownLength / 2) {
            SprintsActivity.say("Halfway Point");
            hasNotified = true;
        }
        if (voiceCountdown && millisUntilFinished <= alertTime) { SprintsActivity.say(String.valueOf(s)); }
    }

    @Override
    abstract public void onFinish();

}
