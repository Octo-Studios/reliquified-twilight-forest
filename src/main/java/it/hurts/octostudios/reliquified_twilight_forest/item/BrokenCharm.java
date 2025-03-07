package it.hurts.octostudios.reliquified_twilight_forest.item;

import it.hurts.octostudios.reliquified_twilight_forest.item.relic.CharmBackpackItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BrokenCharm extends Item {
    public final Item original;
    public final int tier;

    public BrokenCharm(Item original, int tier) {
        super(new Properties()
                .durability(300 * tier)
        );

        this.original = original;
        this.tier = tier;
    }

    public void backpackTick(LivingEntity entity, ItemStack backpackStack, ItemStack stack) {
        if (!(backpackStack.getItem() instanceof CharmBackpackItem relic)
                || entity.tickCount % Math.round(relic.getStatValue(backpackStack, "charm_storage", "repair_time")) != 0
                || stack.getDamageValue() <= 0
        ) return;

        stack.setDamageValue(stack.getDamageValue() - 1);
    }
}
