package it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.GemTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.item.component.GemContents;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientGemTooltip implements ClientTooltipComponent {
    public GemContents contents;

    public ClientGemTooltip(GemTooltip tooltip) {
        this.contents = tooltip.contents;
    }

    @Override
    public int getHeight() {
        return contents.getSize() > 0 ? 20 : 0;
    }

    @Override
    public int getWidth(Font font) {
        return contents.getSize() * 16;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int i = 0; i < contents.getSize(); i++) {
            ItemStack stack = contents.get(i);
            if (stack.isEmpty()) {
                guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "tooltip/lich_crown/empty_slot"), x + i * 16, y, 16, 16);
                continue;
            }
            guiGraphics.renderItem(stack, x + i * 16, y);
            guiGraphics.renderItemDecorations(font, stack, x+i*16, y);
        }
    }
}
