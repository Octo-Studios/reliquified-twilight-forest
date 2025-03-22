package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.BrokenCharmItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.BundleLikeRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Predicate;

public class CharmBackpackItem extends BundleLikeRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("charm_storage")
                                .stat(StatData.builder("max_slots")
                                        .initialValue(1, 3)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(StatData.builder("repair_time")
                                        .initialValue(30, 20)
                                        .upgradeModifier(UpgradeOperation.ADD, -1)
                                        .formatValue(value -> MathButCool.roundSingleDigit(300 * value / 1200f))
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("charm_storage")
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.LABYRINTH)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player)
        ) return;

        List<ItemStack> charms = this.getContents(stack).stream().map(itemStack -> {
            if (!(itemStack.getItem() instanceof BrokenCharmItem charm)) {
                return itemStack;
            }

            charm.backpackTick(entity, stack, itemStack);

            if (itemStack.getDamageValue() <= 0) {
                return charm.original.getDefaultInstance();
            }

            return itemStack;
        }).toList();

        this.setContents(player, stack, charms);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(stack.getItem() instanceof CharmBackpackItem relic)
                || !(entity instanceof Player player)
                || player.level().isClientSide
        ) return;

        relic.dropExcessive(player, stack);
    }

    @Override
    public int getSize(ItemStack stack) {
        if (!(stack.getItem() instanceof CharmBackpackItem relic)) {
            return 0;
        }
        return (int) Math.round(relic.getStatValue(stack, "charm_storage", "max_slots"));
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> stack.getItem() instanceof BrokenCharmItem || ItemRegistry.CHARMS.apply(stack.getItem()) != Items.AIR;
    }
}
