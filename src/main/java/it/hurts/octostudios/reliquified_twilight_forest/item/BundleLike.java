package it.hurts.octostudios.reliquified_twilight_forest.item;

import com.google.common.collect.Lists;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface BundleLike<T> {
    default int getSize(ItemStack stack) {
        return 1;
    }

    default boolean tryInsert(Player player, ItemStack stack, ItemStack toInsert) {
        List<ItemStack> notMutable = stack.get(DataComponentRegistry.BUNDLE_LIKE_CONTENS);
        if (notMutable == null) return false;

        ArrayList<ItemStack> contents = Lists.newArrayList(notMutable);
        int size = this.getSize(stack);

        if (contents.size() >= size) return false;
        contents.add(toInsert.copyAndClear());
        contents.removeIf(ItemStack::isEmpty);
        this.setContents(player, stack, contents);
        return true;
    }

    default ItemStack pop(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getContents(stack);
        if (notMutable.isEmpty()) return ItemStack.EMPTY;

        ArrayList<ItemStack> contents = Lists.newArrayList(notMutable);

        ItemStack toReturn = contents.removeLast();
        contents.removeIf(ItemStack::isEmpty);
        this.setContents(player, stack, contents);
        return toReturn;
    }

    default void dropExcessive(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getContents(stack);

        ArrayList<ItemStack> contents = Lists.newArrayList(notMutable);
        int size = this.getSize(stack);

        if (contents.size() <= size) return;
        List<ItemStack> toDrop = contents.subList(size - 1, contents.size() - 1);

        for (ItemStack drop : toDrop) {
            ItemEntity entity = player.drop(drop, false, true);
            if (entity != null) entity.setNoPickUpDelay();
        }

        toDrop.clear();
        contents.removeIf(ItemStack::isEmpty);
        this.setContents(player, stack, contents);
    }

    default @NotNull List<ItemStack> getContents(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.BUNDLE_LIKE_CONTENS, List.of());
    }

    default void setContents(Player player, ItemStack stack, List<ItemStack> contents) {
        List<ItemStack> oldContents = getContents(stack);
        stack.set(DataComponentRegistry.BUNDLE_LIKE_CONTENS, contents);
        this.onContentsChanged(player, stack, oldContents);
    }

    default int getItemCount(ItemStack stack, T item) {
        return this.getItemCount(stack, item, this.getContents(stack));
    }

    default int getItemCount(ItemStack stack, T item, List<ItemStack> contents) {
        return (int) contents.stream().filter(itemStack -> itemStack.getItem() == item).count();
    }

    default void onContentsChanged(Player player, ItemStack stack, List<ItemStack> oldContents) {

    }

    default void playInsertSound(Player player) {
        player.playSound(SoundEvents.AMETHYST_BLOCK_STEP, 1f, 1.25f);
    }

    default void playPopSound(Player player) {
        player.playSound(SoundEvents.ITEM_PICKUP, 0.75f, 1.25f);
    }

    default Predicate<ItemStack> getPredicate() {
        return stack -> true;
    }

    default boolean isAcceptable(ItemStack stack) {
        return getPredicate().test(stack);
    }
}
