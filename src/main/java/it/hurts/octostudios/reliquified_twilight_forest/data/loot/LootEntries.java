package it.hurts.octostudios.reliquified_twilight_forest.data.loot;

import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootEntry;
import twilightforest.init.TFDimension;

public class LootEntries {
    public static final LootEntry TWILIGHT = LootEntry.builder()
            .dimension(TFDimension.DIMENSION.toString())
            .biome(".*")
            .table("twilightforest:hedge_maze", "twilightforest:tree_cache", "[\\w]+:chests\\/[\\w_\\/]*[\\w]+[\\w_\\/]*")
            .weight(500)
            .build();
}
