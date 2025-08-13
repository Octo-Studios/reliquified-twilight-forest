package it.hurts.octostudios.reliquified_twilight_forest.config;

import it.hurts.octostudios.octolib.module.config.annotation.Prop;
import it.hurts.octostudios.octolib.module.config.impl.OctoConfig;
import lombok.Data;

@Data
public class RTFConfig implements OctoConfig {
    @Prop(comment = "Temporarily disabled due to lag caused by mechanisms that frequently place other blocks. Enable it only if you're sure such mechanisms aren't present in your modpack or won't appear in your field of view too often.")
    private boolean enabledOreScanner = false;
}