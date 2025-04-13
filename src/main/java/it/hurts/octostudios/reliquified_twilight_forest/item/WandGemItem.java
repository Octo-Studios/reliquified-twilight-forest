package it.hurts.octostudios.reliquified_twilight_forest.item;

import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import it.hurts.sskirillss.relics.items.misc.CreativeContentConstructor;
import it.hurts.sskirillss.relics.items.misc.ICreativeTabContent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import twilightforest.init.TFCreativeTabs;

import java.util.List;

public class WandGemItem extends GemItem {
    @Override
    public Component getTipText(ItemStack stack, TooltipContext context) {
        return Component.translatable("item.reliquified_twilight_forest.gem.tip_wand").withStyle(ChatFormatting.GRAY);
    }
}
