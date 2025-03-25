package it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.item.component.BundleLikeContents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBundleLikeTooltip implements ClientTooltipComponent {
    public static final ResourceLocation EMPTY_SLOT = ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "textures/gui/tooltip/bundle_like/empty_slot.png");
    public BundleLikeContents contents;

    public ClientBundleLikeTooltip(BundleLikeTooltip tooltip) {
        this.contents = tooltip.contents;
    }

    public int getRowCount() {
        return 9;
    }

    public int getItemGapX() {
        return 0;
    }

    public int getItemGapY() {
        return 0;
    }

    @Override
    public int getHeight() {
        return contents.getSize() == 0 ? 0 : (16 + getItemGapY()) * ((contents.getSize() - 1) / getRowCount() + 1) + 4;
    }

    @Override
    public int getWidth(Font font) {
        return contents.getSize() >= getRowCount() ? (16 + getItemGapX()) * getRowCount() : contents.getSize() * (16 + getItemGapX());
    }

    public void renderItem(ItemStack stack, int x, int y, GuiGraphics guiGraphics, Font font) {
        guiGraphics.renderItem(stack, x, y);
        guiGraphics.renderItemDecorations(font, stack, x, y);
    }

    public void renderSlot(ItemStack stack, int x, int y, GuiGraphics guiGraphics, Font font) {
        if (stack.isEmpty()) {
            guiGraphics.blit(EMPTY_SLOT, x, y, 0, 0, 16, 16, 16, 16);
            return;
        }

        renderItem(stack, x, y, guiGraphics, font);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int i = 0; i < contents.getSize(); i++) {
            ItemStack stack = contents.get(i);
            int ix = x + i % getRowCount() * (16 + getItemGapX());
            int iy = y + i / getRowCount() * (16 + getItemGapY());

            this.renderSlot(stack, ix, iy, guiGraphics, font);
        }
    }
}
