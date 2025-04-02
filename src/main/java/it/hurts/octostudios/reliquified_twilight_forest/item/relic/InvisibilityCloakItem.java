package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.init.TFItems;

public class InvisibilityCloakItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("cosmetic_armor")
                                .maxLevel(0)
                                .build())
                        .ability(AbilityData.builder("invisibility")
                                .stat(StatData.builder("duration")
                                        .initialValue(160, 110)
                                        .upgradeModifier(UpgradeOperation.ADD, -10)
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
                        .beams(BeamsData.builder()
                                .startColor(0xff44ff71)
                                .endColor(0x000f1c13)
                                .build())
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
