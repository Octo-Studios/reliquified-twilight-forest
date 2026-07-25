package it.hurts.octostudios.reliquified_twilight_forest.items.relics.back;

import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.BrokenCharmItem;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTBundleLikeRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Predicate;

public class CharmBackpackItem extends RTBundleLikeRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("charm_storage")
                                .stat(AbilityStatTemplate.builder("max_slots")
                                        .initialValue(1, 3).targetValue(RelicsScalingModels.ADDITIVE.get(), 1)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(AbilityStatTemplate.builder("repair_time")
                                        .initialValue(30, 20).targetValue(RelicsScalingModels.ADDITIVE.get(), -1)
                                        .formatValue(value -> MathButCool.roundSingleDigit(300 * value / 1200f))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()

                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
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
    public int getMaxSlots(ItemStack stack) {
        if (!(stack.getItem() instanceof CharmBackpackItem relic)) {
            return 0;
        }
        return (int) Math.round(relic.getRelicData(null, stack).getAbilitiesData().getAbilityData("charm_storage").getStatData("max_slots").getValue());
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> stack.getItem() instanceof BrokenCharmItem || RTItems.CHARMS.apply(stack.getItem()) != Items.AIR;
    }
}
