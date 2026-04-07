package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.NBTHelper;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.init.TFItems;

@Mod.EventBusSubscriber(modid = ReliquifiedTwilightForest.MOD_ID)
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
                                        .initialValue(140, 200)
                                        .upgradeModifier(UpgradeOperation.ADD, 20)
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
                .loot(LootData.builder()
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

        int regenerationTicks = NBTHelper.getRegenerationTicks(stack);
        if (regenerationTicks > 0) {
            regenerationTicks--;
        }

        NBTHelper.setRegenerationTicks(stack, regenerationTicks);
    }

    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Start e) {
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.MAPLE_SYRUP_BOTTLE.get());
        if (e.getEntity().level().isClientSide
                || !(stack.getItem() instanceof MapleSyrupBottleItem relic)
                || !MapleSyrupBottleItem.isAcceptable(e.getItem())
        ) return;

        if (e.getEntity().getRandom().nextDouble() > relic.getStatValue(stack, "sugar_rush", "chance")) {
            NBTHelper.removeKey(e.getItem(), NBTHelper.DONT_EAT);
            return;
        }

        NBTHelper.setDontEat(e.getItem(), true);
    }

    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Finish e) {
        ItemStack original = e.getItem();
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.MAPLE_SYRUP_BOTTLE.get());
        if (stack.isEmpty() || !(stack.getItem() instanceof MapleSyrupBottleItem relic)) {
            return;
        }

        if (!e.getEntity().level().isClientSide && MapleSyrupBottleItem.isAcceptable(e.getItem())) {
            int regenerationTicks = NBTHelper.getRegenerationTicks(stack);
            int toAdd = (int) Math.round(relic.getStatValue(stack, "sugar_rush", "regen_time"));

            NBTHelper.setRegenerationTicks(stack, regenerationTicks + toAdd);
            relic.spreadRelicExperience(e.getEntity(), stack, 1);
        }

        if (NBTHelper.hasDontEat(original)) {
            e.setResultStack(original);
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent e) {
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.MAPLE_SYRUP_BOTTLE.get());
        if (e.getEntity().level().isClientSide
                || !(stack.getItem() instanceof MapleSyrupBottleItem relic)
                || NBTHelper.getRegenerationTicks(stack) <= 0
        ) return;

        e.setAmount(e.getAmount() * (float) (1f + relic.getStatValue(stack, "sugar_rush", "regen_multiplier")));
    }

    public static boolean isAcceptable(ItemStack stack) {
        return stack.getItem() == TFItems.MAZE_WAFER.asItem();
    }
}
