package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import twilightforest.init.TFItems;

@EventBusSubscriber
public class MapleSyrupBottleItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("sugar_rush")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.1, 0.2)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.055)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .stat(StatData.builder("regen_multiplier")
                                        .initialValue(0.3, 0.5)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.15)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .stat(StatData.builder("regen_time")
                                        .initialValue(200, 300)
                                        .upgradeModifier(UpgradeOperation.ADD, 30)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("sugar_rush")
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
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
                        .entry(LootEntries.LABYRINTH)
                        .entry(LootEntries.STRONGHOLD)
                        .build())
                .build();
    }

    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Finish e) {
        e.getEntity().sendSystemMessage(Component.literal(Thread.currentThread().getName()));

        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.MAPLE_SYRUP_BOTTLE.get());
        if (e.getEntity().level().isClientSide
                || !(stack.getItem() instanceof MapleSyrupBottleItem relic)
                || e.getItem().getItem() != TFItems.MAZE_WAFER.get()
                || e.getEntity().getRandom().nextDouble() > relic.getStatValue(stack, "sugar_rush", "chance")
        ) return;

        e.setResultStack(e.getItem());
        e.setDuration(e.getItem().getUseDuration(e.getEntity()));
    }
}
