package it.hurts.octostudios.reliquified_twilight_forest.items.relics.charm;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTDataComponent;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.init.TFItems;

@EventBusSubscriber
public class MapleSyrupBottleItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("sugar_rush")
                                .stat(AbilityStatTemplate.builder("chance")
                                        .initialValue(0.1, 0.2).targetValue(RelicsScalingModels.ADDITIVE.get(), 0.055)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .stat(AbilityStatTemplate.builder("regen_multiplier")
                                        .initialValue(0.3, 0.5).targetValue(RelicsScalingModels.ADDITIVE.get(), 0.15)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .stat(AbilityStatTemplate.builder("regen_time")
                                        .initialValue(140, 200).targetValue(RelicsScalingModels.ADDITIVE.get(), 20)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("eaten")
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(LootEntries.LABYRINTH)
                        .entry(LootEntries.STRONGHOLD)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide) {
            return;
        }

        int regenerationTicks = stack.getOrDefault(RTDataComponent.MAPLE_SYRUP_REGENERATION_TICKS, 0);
        if (regenerationTicks > 0) {
            regenerationTicks--;
        }

        stack.set(RTDataComponent.MAPLE_SYRUP_REGENERATION_TICKS, regenerationTicks);
    }

    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Start e) {
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), RTItems.MAPLE_SYRUP_BOTTLE.get());
        if (e.getEntity().level().isClientSide
                || !(stack.getItem() instanceof MapleSyrupBottleItem relic)
                || !MapleSyrupBottleItem.isAcceptable(e.getItem())
        ) return;

        if (e.getEntity().getRandom().nextDouble() > relic.getRelicData(null, stack).getAbilitiesData().getAbilityData("sugar_rush").getStatData("chance").getValue()) {
            e.getItem().remove(RTDataComponent.MAPLE_SYRUP_DONT_EAT);
            return;
        }

        e.getItem().set(RTDataComponent.MAPLE_SYRUP_DONT_EAT, true);
    }

    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Finish event) {
        ItemStack original = event.getItem();
        ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), RTItems.MAPLE_SYRUP_BOTTLE.get());
        if (stack.isEmpty() || !(stack.getItem() instanceof MapleSyrupBottleItem relic)) {
            return;
        }

        if (!event.getEntity().level().isClientSide && MapleSyrupBottleItem.isAcceptable(event.getItem())) {
            int regenerationTicks = stack.getOrDefault(RTDataComponent.MAPLE_SYRUP_REGENERATION_TICKS, 0);
            int toAdd = (int) Math.round(relic.getRelicData(null, stack).getAbilitiesData().getAbilityData("sugar_rush").getStatData("regen_time").getValue());

            stack.set(RTDataComponent.MAPLE_SYRUP_REGENERATION_TICKS, regenerationTicks + toAdd);
            relic.getRelicData(event.getEntity(), stack).getLevelingData().addExperience("sugar_rush", "eaten", 1D);
        }

        if (original.has(RTDataComponent.MAPLE_SYRUP_DONT_EAT)) {
            event.setResultStack(original);
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent e) {
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), RTItems.MAPLE_SYRUP_BOTTLE.get());
        if (e.getEntity().level().isClientSide
                || !(stack.getItem() instanceof MapleSyrupBottleItem relic)
                || stack.getOrDefault(RTDataComponent.MAPLE_SYRUP_REGENERATION_TICKS, 0) <= 0
        ) return;

        e.setAmount(e.getAmount() * (float) (1f + relic.getRelicData(null, stack).getAbilitiesData().getAbilityData("sugar_rush").getStatData("regen_multiplier").getValue()));
    }

    public static boolean isAcceptable(ItemStack stack) {
        return stack.getItem() == TFItems.MAZE_WAFER.asItem();
    }
}
