package it.hurts.octostudios.reliquified_twilight_forest.item;

import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import it.hurts.sskirillss.relics.items.misc.CreativeContentConstructor;
import it.hurts.sskirillss.relics.items.misc.ICreativeTabContent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import twilightforest.TwilightForestMod;
import twilightforest.init.TFCreativeTabs;

import java.util.List;

public class GemItem extends Item implements IGem, ICreativeTabContent {
    public GemItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public GemItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.reliquified_twilight_forest.gem.tip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void gatherCreativeTabContent(CreativeContentConstructor creativeContentConstructor) {
        creativeContentConstructor.entry(TFCreativeTabs.ITEMS.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, this);
        creativeContentConstructor.entry(CreativeTabRegistry.RELICS_TAB.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, this);
    }
}
