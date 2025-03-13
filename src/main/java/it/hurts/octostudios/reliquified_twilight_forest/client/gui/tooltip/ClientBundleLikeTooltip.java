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

    @Override
    public int getHeight() {
        return 16 * ((contents.getSize() - 1) / getRowCount() + 1) + 4;
    }

    @Override
    public int getWidth(Font font) {
        return contents.getSize() >= getRowCount() ? 16 * getRowCount() : contents.getSize() * 16;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int i = 0; i < contents.getSize(); i++) {
            ItemStack stack = contents.get(i);
            int ix = x + i % getRowCount() * 16;
            int iy = y + i / getRowCount() * 16;

            if (stack.isEmpty()) {
                guiGraphics.blit(EMPTY_SLOT, ix, iy, 0, 0, 16, 16, 16, 16);
                continue;
            }
            guiGraphics.renderItem(stack, ix, iy);
            guiGraphics.renderItemDecorations(font, stack, ix, iy);
        }
    }
}
