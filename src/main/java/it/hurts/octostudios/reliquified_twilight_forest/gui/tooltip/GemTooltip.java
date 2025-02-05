package it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip;

import it.hurts.octostudios.reliquified_twilight_forest.item.component.GemContents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GemTooltip implements TooltipComponent {
    public GemContents contents;

    public GemTooltip(List<ItemStack> contents, int size) {
        this.contents = new GemContents(contents, size);
    }
}
