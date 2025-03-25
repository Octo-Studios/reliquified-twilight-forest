package it.hurts.octostudios.reliquified_twilight_forest.item;

import com.google.common.collect.Lists;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface BundleLike {
    default int getMaxSlots(ItemStack stack) {
        return 1;
    }

    default int getMaxSlotStackSize(ItemStack stack) {
        return 1;
    }

    default boolean tryInsert(Player player, ItemStack bundleStack, ItemStack toInsert) {
        List<ItemStack> notMutable = bundleStack.get(DataComponentRegistry.BUNDLE_LIKE_CONTENTS);
        if (notMutable == null) return false;

        ArrayList<ItemStack> contents = Lists.newArrayList(notMutable);
        int maxSlots = this.getMaxSlots(bundleStack);
        int maxStackSize = this.getMaxSlotStackSize(bundleStack);

        // Try merging with an existing stack if the items match.
        for (ItemStack contained : contents) {
            if (!contained.isEmpty() && contained.getItem() == toInsert.getItem()) {
                int currentCount = contained.getCount();
                if (currentCount < maxStackSize) {
                    int freeSpace = maxStackSize - currentCount;
                    int inserting = Math.min(freeSpace, toInsert.getCount());

                    contained.grow(inserting);
                    toInsert.shrink(inserting);

                    this.setContents(player, bundleStack, contents);
                    return true;
                }
                // If matching stack is full, nothing can be inserted.
            }
        }

        // If no mergeable stack exists, try creating a new one (if there is an empty slot).
        if (contents.size() < maxSlots) {
            int inserting = Math.min(maxStackSize, toInsert.getCount());
            ItemStack newStack = toInsert.copy();
            newStack.setCount(inserting);
            toInsert.shrink(inserting);

            contents.add(newStack);
            this.setContents(player, bundleStack, contents);
            return true;
        }

        return false;
    }

    default ItemStack pop(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getContents(stack);
        if (notMutable.isEmpty()) return ItemStack.EMPTY;

        ArrayList<ItemStack> contents = Lists.newArrayList(notMutable);
        ItemStack toReturn = contents.remove(contents.size() - 1);
        contents.removeIf(ItemStack::isEmpty);
        this.setContents(player, stack, contents);
        return toReturn;
    }

    default void dropExcessive(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getContents(stack);
        ArrayList<ItemStack> contents = Lists.newArrayList(notMutable);
        int maxStacks = this.getMaxSlots(stack);
        int maxStackSize = this.getMaxSlotStackSize(stack);
        if (contents.size() > maxStacks) {
            List<ItemStack> toDrop = contents.subList(maxStacks - 1, contents.size() - 1);
            for (ItemStack drop : toDrop) {
                ItemEntity entity = player.drop(drop, false, true);
                if (entity != null) entity.setNoPickUpDelay();
            }
            toDrop.clear();
        }

        contents.removeIf(ItemStack::isEmpty);

        int maxStackSizeFound = contents.stream().mapToInt(ItemStack::getCount).max().orElse(0);
        if (maxStackSizeFound > maxStackSize) {
            contents = Lists.newArrayList(contents.stream().map(itemStack -> {
                if (itemStack.getCount() < maxStackSize) return itemStack;

                int excess = itemStack.getCount() - maxStackSize;
                ItemEntity entity = player.drop(itemStack.copyWithCount(excess), false, true);
                if (entity != null) entity.setNoPickUpDelay();
                itemStack.shrink(excess);

                return itemStack;
            }).toList());
        }

        contents.removeIf(ItemStack::isEmpty);
        this.setContents(player, stack, contents);
    }

    default void dropAll(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getContents(stack);
        ArrayList<ItemStack> contents = Lists.newArrayList(notMutable);

        for (ItemStack drop : contents) {
            ItemEntity entity = player.drop(drop, false, true);
            if (entity != null) entity.setNoPickUpDelay();
        }
        contents.clear();
        this.setContents(player, stack, contents);
    }

    default @NotNull List<ItemStack> getContents(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.BUNDLE_LIKE_CONTENTS, List.of());
    }

    default void setContents(Player player, ItemStack stack, List<ItemStack> contents) {
        List<ItemStack> oldContents = getContents(stack);
        if (oldContents.equals(contents)) {
            return;
        }

        stack.set(DataComponentRegistry.BUNDLE_LIKE_CONTENTS, contents.stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        this.onContentsChanged(player, stack, oldContents);
    }

    default int getItemCount(ItemStack stack, Item item) {
        return this.getItemCount(stack, item, this.getContents(stack));
    }

    default int getItemCount(ItemStack stack, Item item, List<ItemStack> contents) {
        return (int) contents.stream().filter(itemStack -> itemStack.getItem() == item).count();
    }

    default int getTotalItemCount(ItemStack stack, Item item) {
        return this.getTotalItemCount(stack, item, this.getContents(stack));
    }

    default int getTotalItemCount(ItemStack stack, Item item, List<ItemStack> contents) {
        return contents.stream().filter(itemStack -> itemStack.getItem() == item).mapToInt(ItemStack::getCount).sum();
    }

    default void onContentsChanged(Player player, ItemStack stack, List<ItemStack> oldContents) {
    }

    default void playInsertSound(Player player, ItemStack toInsert) {
        player.playSound(SoundEvents.AMETHYST_BLOCK_STEP, 1f, 1.25f);
    }

    default void playPopSound(Player player, ItemStack toPop) {
        player.playSound(SoundEvents.ITEM_PICKUP, 0.75f, 1.25f);
    }

    default Predicate<ItemStack> getPredicate() {
        return stack -> true;
    }

    default boolean isAcceptable(ItemStack stack) {
        return getPredicate().test(stack);
    }
}

