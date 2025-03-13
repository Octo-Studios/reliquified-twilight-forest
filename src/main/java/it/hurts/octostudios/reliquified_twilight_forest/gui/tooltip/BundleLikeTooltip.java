package it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip;

import it.hurts.octostudios.reliquified_twilight_forest.item.component.BundleLikeContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BundleLikeTooltip implements TooltipComponent {
    public BundleLikeContents contents;

    public BundleLikeTooltip(List<ItemStack> contents, int size) {
        this.contents = new BundleLikeContents(contents, size);
    }
}
