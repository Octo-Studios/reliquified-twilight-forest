package it.hurts.octostudios.reliquified_twilight_forest.mixin;

import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.CharmBackpackItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.events.CharmEvents;
import twilightforest.util.TFItemStackUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(TFItemStackUtils.class)
public class TFItemStackUtilsMixin {
    @Inject(method = "consumeInventoryItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/Item;Lnet/minecraft/nbt/CompoundTag;Z)Z", at = @At("HEAD"), cancellable = true)
    private static void injected(Player player, Item item, CompoundTag persistentTag, boolean saveItemToTag, CallbackInfoReturnable<Boolean> cir) {
        ItemStack backpack = EntityUtils.findEquippedCurio(player, ItemRegistry.CHARM_BACKPACK.get());
        Item brokenCharm = ItemRegistry.CHARMS.apply(item);
        if (!(backpack.getItem() instanceof CharmBackpackItem relic)
                || brokenCharm == Items.AIR
        ) return;

        AtomicBoolean isDone = new AtomicBoolean(false);

        List<ItemStack> contents = relic.getContents(backpack).stream().map(stack -> {
            if (isDone.get()
                    || stack.getItem() != item
            ) return stack;

            isDone.set(true);
            ItemStack brokenStack = brokenCharm.getDefaultInstance();
            brokenStack.setDamageValue(brokenStack.getMaxDamage() - 1);
            CharmEvents.getPlayerData(player).put(CharmEvents.CONSUMED_CHARM_TAG, stack.save(player.registryAccess()));
            return brokenStack;
        }).toList();

        if (isDone.get()) {
            relic.setContents(player, backpack, contents);
            relic.spreadRelicExperience(player, backpack, 1);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
