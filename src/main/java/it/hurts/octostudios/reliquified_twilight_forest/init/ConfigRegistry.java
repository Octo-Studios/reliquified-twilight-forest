package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.octolib.module.config.ConfigManager;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.config.RTFConfig;

public class ConfigRegistry {
    public static RTFConfig GENERAL = new RTFConfig();

    public static void register() {
        ConfigManager.registerConfig(ReliquifiedTwilightForest.MOD_ID + "/general", GENERAL);
    }
}