package it.hurts.octostudios.reliquified_twilight_forest.util;

import it.hurts.sskirillss.relics.utils.MathUtils;

public class MathButCool {
    public static double roundSingleDigit(double value) {
        return MathUtils.round(value, 1);
    }

    public static int percentage(double value) {
        return (int) Math.round(value*100);
    }

    public static double percentageAndRoundSingleDigit(double value) {
        return roundSingleDigit(value*100);
    }

    public static int secondsToTicks(double value) {
        return (int) Math.round(value*20);
    }

    public static double ticksToSeconds(double value) {
        return Math.round(value) / 20d;
    }

    public static double ticksToSecondsAndRoundSingleDigit(double value) {
        return roundSingleDigit(ticksToSeconds(value));
    }

}
