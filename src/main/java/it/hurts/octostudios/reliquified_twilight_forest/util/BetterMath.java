package it.hurts.octostudios.reliquified_twilight_forest.util;

import it.hurts.sskirillss.relics.utils.MathUtils;

public class BetterMath {
    public static double roundSingleDigit(double value) {
        return MathUtils.round(value, 1);
    }

    public static int percentage(double value) {
        return (int) Math.round(value*100);
    }
}
