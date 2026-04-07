package it.hurts.octostudios.reliquified_twilight_forest.item;

import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.NBTHelper;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public abstract class BundleLikeItem extends Item implements BundleLike {
    public BundleLikeItem() {
        super(new Properties()
                .rarity(Rarity.RARE)
                .stacksTo(1)
        );
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        List<ItemStack> contents = NBTHelper.getBundleLikeContents(stack);
        return Optional.of(new BundleLikeTooltip(contents, this.getMaxSlots(stack)));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
