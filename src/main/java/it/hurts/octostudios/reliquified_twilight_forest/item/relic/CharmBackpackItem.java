package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.BrokenCharm;
import it.hurts.octostudios.reliquified_twilight_forest.item.BundleLike;
import it.hurts.octostudios.reliquified_twilight_forest.item.Gem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;
import twilightforest.init.TFItems;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CharmBackpackItem extends RelicItem implements BundleLike<BrokenCharm> {
    private static final List<Item> CHARMS = List.of(
            TFItems.CHARM_OF_LIFE_1.get(),
            TFItems.CHARM_OF_LIFE_2.get(),
            TFItems.CHARM_OF_KEEPING_1.get(),
            TFItems.CHARM_OF_KEEPING_2.get(),
            TFItems.CHARM_OF_KEEPING_3.get()
    );

    public CharmBackpackItem() {
        super((new Item.Properties()).rarity(Rarity.RARE).stacksTo(1).component(DataComponentRegistry.BUNDLE_LIKE_CONTENS, List.of()));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder().build();
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(DataComponentRegistry.BUNDLE_LIKE_CONTENS)).map(list -> new BundleLikeTooltip(list, this.getSize(stack)))
                : Optional.empty();
    }

    @Override
    public int getSize(ItemStack stack) {
        return 100;
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> stack.getItem() instanceof BrokenCharm || CHARMS.contains(stack.getItem());
    }
}
