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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.compat.top.QuestRamWoolElement;
import twilightforest.entity.passive.QuestRam;

import java.util.List;
import java.util.function.Predicate;

public class ChromaticCloakItem extends BundleLikeRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("wool_storage")
                                .stat(StatData.builder("max_slots")
                                        .initialValue(1, 3)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(StatData.builder("max_stack_size")
                                        .initialValue(3, 6)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(Math::round)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("wool_storage")
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
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(stack.getItem() instanceof ChromaticCloakItem relic)
                || !(entity instanceof Player player)
                || player.level().isClientSide
        ) return;

        relic.dropExcessive(player, stack);
    }

    @Override
    public int getMaxSlots(ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "wool_storage", "max_slots"));
    }

    @Override
    public int getMaxSlotStackSize(ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "wool_storage", "max_stack_size"));
    }

    @Override
    public void playInsertSound(Player player, ItemStack toInsert) {
        player.playSound(SoundEvents.WOOL_PLACE, 0.8f, 1.25f);
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> ItemRegistry.CHROMATIC_EFFECTS.containsKey(stack.getItem());
    }
}
