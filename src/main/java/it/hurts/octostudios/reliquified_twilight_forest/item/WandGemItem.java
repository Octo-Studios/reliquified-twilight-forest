package it.hurts.octostudios.reliquified_twilight_forest.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class WandGemItem extends GemItem {
    @Override
    public Component getTipText(ItemStack stack) {
        return Component.translatable("item.reliquified_twilight_forest.gem.tip_wand").withStyle(ChatFormatting.GRAY);
    }
}
