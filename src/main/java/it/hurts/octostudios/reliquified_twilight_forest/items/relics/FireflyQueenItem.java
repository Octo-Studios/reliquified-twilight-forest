package it.hurts.octostudios.reliquified_twilight_forest.items.relics;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.IRelicItem;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFParticleType;

public class FireflyQueenItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("glowkeeper")
                                .stat(AbilityStatTemplate.builder("cooldown")
                                        .initialValue(200, 140).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), -20)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_charges")
                                        .initialValue(1, 3).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 1)
                                        .formatValue(Math::round)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("fireflies_spawned")
                                        .build())
                                .initialMaxLevel(5)
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(LootEntries.TWILIGHT)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        Level level = entity.level();

        if (!(stack.getItem() instanceof IRelicItem relic)) {
            return;
        }

        if (level.isClientSide) {
            if (slotContext.visible()
                    && entity.tickCount % 10 == 0
                    && entity.getRandom().nextFloat() < 0.6f
            ) {
                level.addParticle(TFParticleType.FIREFLY.get(), entity.getRandomX(0.5f), entity.getRandomY(), entity.getRandomZ(0.5f), 0, 0, 0);
            }
            return;
        }

        int maxTime = (int) Math.round(relic.getStatValue(stack, "glowkeeper", "cooldown"));
        int maxCharges = (int) Math.round(relic.getStatValue(stack, "glowkeeper", "max_charges"));

        int time = stack.getOrDefault(DataComponentRegistry.TIME, maxTime);
        int charge = stack.getOrDefault(DataComponentRegistry.CHARGE, 0);

        BlockPos pos = entity.blockPosition();
        BlockState state = TFBlocks.FIREFLY.get().defaultBlockState();
        BlockState posState = level.getBlockState(pos);

        if (charge > 0
                && entity.onGround()
                && !entity.isInLiquid()
                && (!level.isDay() || level.getBrightness(LightLayer.SKY, pos) == 0)
                && level.getBrightness(LightLayer.BLOCK, pos) == 0
                && state.canSurvive(level, pos)
                && posState.canBeReplaced()
        ) {
            level.setBlock(pos, state, 0b00000011);
            relic.spreadRelicExperience(entity, stack, 1);
            charge--;
        }

        if (time <= 0) {
            if (charge < maxCharges) {
                charge++;
                time = maxTime;
            }
        } else {
            time--;
        }

        stack.set(DataComponentRegistry.TIME, time);
        stack.set(DataComponentRegistry.CHARGE, charge);
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
