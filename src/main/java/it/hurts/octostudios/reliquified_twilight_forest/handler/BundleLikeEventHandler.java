package it.hurts.octostudios.reliquified_twilight_forest.handler;

import it.hurts.octostudios.reliquified_twilight_forest.item.BundleLike;
import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class BundleLikeEventHandler {
    @SubscribeEvent
    public static void onSlotClick(ContainerSlotClickEvent event) {
        if (event.getAction() != ClickAction.SECONDARY) return;

        ItemStack slotStack = event.getSlotStack();
        ItemStack heldStack = event.getHeldStack();
        Player player = event.getEntity();

        if (slotStack.getItem() instanceof BundleLike relicInSlot) {
            handleRelicInteraction(event, player, relicInSlot, slotStack, heldStack, true);
        } else if (heldStack.getItem() instanceof BundleLike relicInHeld) {
            handleRelicInteraction(event, player, relicInHeld, heldStack, slotStack, false);
        }
    }

    private static void handleRelicInteraction(ContainerSlotClickEvent event, Player player,
                                               BundleLike relic, ItemStack primaryStack,
                                               ItemStack secondaryStack, boolean primaryIsSlot) {

        if (relic.isAcceptable(secondaryStack)) {
            if (relic.tryInsert(player, primaryStack, secondaryStack)) {
                relic.playInsertSound(player);
            }
            event.setCanceled(true);
        } else if (secondaryStack.isEmpty()) {
            event.setCanceled(true);
            ItemStack poppedItem = relic.pop(player, primaryStack);
            if (!poppedItem.isEmpty()) {
                relic.playPopSound(player);
                if (primaryIsSlot) {
                    player.containerMenu.setCarried(poppedItem);
                } else {
                    event.getSlot().set(poppedItem);
                }
            }
        }
    }
}
