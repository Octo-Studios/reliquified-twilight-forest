package it.hurts.octostudios.reliquified_twilight_forest.item.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Data
@AllArgsConstructor
public class BundleLikeContents implements TooltipComponent {
    private List<ItemStack> stacks;
    private int size;

    public ItemStack get(int i) {
        if (i >= stacks.size()) return ItemStack.EMPTY;
        return stacks.get(i);
    }
}
