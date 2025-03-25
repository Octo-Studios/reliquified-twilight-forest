package it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip;

import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientChromaticCloakTooltip extends ClientBundleLikeTooltip {
    public ClientChromaticCloakTooltip(BundleLikeTooltip tooltip) {
        super(tooltip);
    }

    @Override
    public int getItemGapX() {
        return 3;
    }

    @Override
    public int getItemGapY() {
        return 3;
    }

    @Override
    public int getRowCount() {
        return 10;
    }

    @Override
    public void renderItem(ItemStack stack, int x, int y, GuiGraphics guiGraphics, Font font) {
        super.renderItem(stack, x+1, y+1, guiGraphics, font);
        Holder<MobEffect> effect = ItemRegistry.CHROMATIC_EFFECTS.get(stack.getItem());
        if (effect == null) {
            return;
        }

        MobEffectTextureManager manager = Minecraft.getInstance().getMobEffectTextures();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,200);
        guiGraphics.blit(x-1, y-1, 0, 10, 10, manager.get(effect));
        guiGraphics.pose().popPose();
    }

    @Override
    public void renderSlot(ItemStack stack, int x, int y, GuiGraphics guiGraphics, Font font) {
        super.renderSlot(stack, x, y, guiGraphics, font);
    }
}
