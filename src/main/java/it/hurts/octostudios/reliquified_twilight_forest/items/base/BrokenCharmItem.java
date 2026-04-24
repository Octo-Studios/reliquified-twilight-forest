package it.hurts.octostudios.reliquified_twilight_forest.items.base;

import it.hurts.octostudios.reliquified_twilight_forest.items.relics.CharmBackpackItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BrokenCharmItem extends Item {
    public final Item original;
    public final int tier;

    public BrokenCharmItem(Item original, int tier) {
        super(new Properties()
                .durability(300 * tier)
        );

        this.original = original;
        this.tier = tier;
    }

    public void backpackTick(LivingEntity entity, ItemStack backpackStack, ItemStack stack) {
        if (!(backpackStack.getItem() instanceof CharmBackpackItem relic)
        ) return;

        var ability = relic.getRelicData(entity, backpackStack).getAbilitiesData().getAbilityData("charm_storage");

        if (entity.tickCount % Math.round(ability.getStatData("repair_time").getValue()) != 0
                || stack.getDamageValue() <= 0)
            return;

        stack.setDamageValue(stack.getDamageValue() - 1);
    }
}
