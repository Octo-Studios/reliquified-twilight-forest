package it.hurts.octostudios.reliquified_twilight_forest.items.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public class WandGemItem extends GemItem {
    @Override
    public Component getTipText(ItemStack stack, TooltipContext context) {
        return Component.translatable("item.reliquified_twilight_forest.gem.tip_wand").withStyle(ChatFormatting.GRAY);
    }
}
