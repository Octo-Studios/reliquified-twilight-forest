package it.hurts.octostudios.reliquified_twilight_forest.item;

import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import net.minecraft.core.component.DataComponents;
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
                .component(DataComponentRegistry.BUNDLE_LIKE_CONTENTS, List.of())
                .rarity(Rarity.RARE)
                .stacksTo(1)
        );
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(DataComponentRegistry.BUNDLE_LIKE_CONTENTS)).map(list -> new BundleLikeTooltip(list, this.getSize(stack)))
                : Optional.empty();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
