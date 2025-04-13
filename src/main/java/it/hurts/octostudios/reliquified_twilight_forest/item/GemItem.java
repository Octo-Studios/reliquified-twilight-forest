package it.hurts.octostudios.reliquified_twilight_forest.item;

import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import it.hurts.sskirillss.relics.items.misc.CreativeContentConstructor;
import it.hurts.sskirillss.relics.items.misc.ICreativeTabContent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import twilightforest.init.TFCreativeTabs;

import java.util.List;

public class GemItem extends Item implements Gem, ICreativeTabContent {
    public GemItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public GemItem(Properties properties) {
        super(properties);
    }

    public Component getTipText(ItemStack stack, TooltipContext context) {
        return Component.translatable("item.reliquified_twilight_forest.gem.tip").withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(this.getTipText(stack, context));
    }

    @Override
    public void gatherCreativeTabContent(CreativeContentConstructor creativeContentConstructor) {
        creativeContentConstructor.entry(TFCreativeTabs.ITEMS.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, this);
        creativeContentConstructor.entry(CreativeTabRegistry.RELICS_TAB.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, this);
    }
}
