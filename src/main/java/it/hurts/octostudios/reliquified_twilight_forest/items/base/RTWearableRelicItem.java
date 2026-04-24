package it.hurts.octostudios.reliquified_twilight_forest.items.base;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.sskirillss.relics.items.relics.base.WearableRelicItem;

public abstract class RTWearableRelicItem extends WearableRelicItem {
    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}