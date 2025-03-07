package it.hurts.octostudios.reliquified_twilight_forest.mixin;

import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.CharmBackpackItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.compat.curios.CuriosCompat;
import twilightforest.events.CharmEvents;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(CuriosCompat.class)
public class CuriosCompatMixin {
    @Inject(method = "findAndConsumeCurio", at = @At("HEAD"), cancellable = true)
    private static void injected(Item item, Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack backpack = EntityUtils.findEquippedCurio(player, ItemRegistry.CHARM_BACKPACK.get());
        if (!(backpack.getItem() instanceof CharmBackpackItem relic)) {
            return;
        }

        AtomicBoolean isDone = new AtomicBoolean(false);

        List<ItemStack> contents = relic.getContents(backpack).stream().map(stack -> {
            if (isDone.get()
                    || stack.getItem() != item
            ) return stack;

            ItemStack brokenCharm = ItemRegistry.CHARMS.apply(item).getDefaultInstance();
            if (brokenCharm.getItem() == Items.AIR) return stack;

            isDone.set(true);
            brokenCharm.setDamageValue(brokenCharm.getMaxDamage() - 1);
            CharmEvents.getPlayerData(player).put(CharmEvents.CONSUMED_CHARM_TAG, stack.save(player.registryAccess()));
            return brokenCharm;
        }).toList();

        if (isDone.get()) {
            relic.setContents(player, backpack, contents);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
