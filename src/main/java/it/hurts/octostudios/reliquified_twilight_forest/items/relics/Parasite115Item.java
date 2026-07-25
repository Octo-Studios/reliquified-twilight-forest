package it.hurts.octostudios.reliquified_twilight_forest.items.relics;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import net.neoforged.fml.common.EventBusSubscriber;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Parasite115Item extends RTRelicItem {
    public static final String INFECTIONS = ReliquifiedTwilightForest.MOD_ID + ":infections";

    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("infectious_bloom")
                                .stat(AbilityStatTemplate.builder("chance")
                                        .initialValue(0.075, 0.125)
                                        .targetValue(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.36)
                                        .formatValue(MathButCool::percentageAndRoundSingleDigit)
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_attacks")
                                        .initialValue(1, 3).targetValue(RelicsScalingModels.ADDITIVE.get(), 0.5)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(AbilityStatTemplate.builder("damage")
                                        .initialValue(0.5, 1)
                                        .targetValue(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.5)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .stat(AbilityStatTemplate.builder("drops")
                                        .initialValue(1, 3).targetValue(RelicsScalingModels.ADDITIVE.get(), 1)
                                        .formatValue(Math::round)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("enemies_infected")
                                        .build())
                                .initialMaxLevel(5)
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(LootEntries.DARK_TOWER)
                        .build())
                .build();
    }
}
