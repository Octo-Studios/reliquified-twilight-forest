package it.hurts.octostudios.reliquified_twilight_forest.items.relics;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicTemplate;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.Color;

public class InvisibilityCloakItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicData() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("cosmetic_armor")
                                .initialMaxLevel(0)
                                .build())
                        .ability(AbilityTemplate.builder("invisibility")
                                .stat(AbilityStatTemplate.builder("duration")
                                        .initialValue(160, 120).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), -10)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .thresholdValue(1, 9999)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("invisibility")
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams((player, stack) -> {
                            float ticks = player.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                            return BeamsData.builder()
                                    .startColor(new Color(0.5f, 0.5f, 0.5f, Mth.sin(ticks/10f)/2f+0.5f).getRGB())
                                    .endColor(0x00444444)
                                    .build();
                        })
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.HEDGE)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide) {
            return;
        }

        LivingEntity entity = slotContext.entity();
        int idleTicks = stack.getOrDefault(DataComponentRegistry.TIME, 0);
        int maxIdleTicks = this.getMaxIdleTicks(slotContext, stack);
        double lengthSqr = slotContext.entity().getKnownMovement().lengthSqr();
        double movementThreshold = 0.005;

        if (lengthSqr < movementThreshold || entity.isCrouching()) {
            if (idleTicks < maxIdleTicks) {
                idleTicks++;
            } else {
                if (!entity.hasEffect(EffectRegistry.VANISHING)) {
                    this.spreadRelicExperience(entity, stack, 1);
                }
                entity.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 24, 0, true, false));
            }
        } else {
            idleTicks = 0;
            entity.removeEffect(EffectRegistry.VANISHING);
        }

        stack.set(DataComponentRegistry.TIME, idleTicks);
    }

    public int getMaxIdleTicks(SlotContext slotContext, ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "invisibility", "duration"));
    }
}
