package it.hurts.octostudios.reliquified_twilight_forest.items.relics.back;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTDataComponent;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsMobEffects;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class InvisibilityCloakItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
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
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("going_invisible")
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
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
        int idleTicks = stack.getOrDefault(RTDataComponent.INVISIBILITY_CLOAK_TIME, 0);
        int maxIdleTicks = this.getMaxIdleTicks(slotContext, stack);
        double lengthSqr = slotContext.entity().getKnownMovement().lengthSqr();
        double movementThreshold = 0.005;

        if (lengthSqr < movementThreshold || entity.isCrouching()) {
            if (idleTicks < maxIdleTicks) {
                idleTicks++;
            } else {
                if (!entity.hasEffect(RelicsMobEffects.VANISHING)) {
                    this.getRelicData(entity, stack).getLevelingData().addExperience("invisibility", "going_invisible", 1D);
                }
                entity.addEffect(new MobEffectInstance(RelicsMobEffects.VANISHING, 24, 0, true, false));
            }
        } else {
            idleTicks = 0;
            entity.removeEffect(RelicsMobEffects.VANISHING);
        }

        stack.set(RTDataComponent.INVISIBILITY_CLOAK_TIME, idleTicks);
    }

    public int getMaxIdleTicks(SlotContext slotContext, ItemStack stack) {
        return (int) Math.round(this.getRelicData(null, stack).getAbilitiesData().getAbilityData("invisibility").getStatData("duration").getValue());
    }
}
