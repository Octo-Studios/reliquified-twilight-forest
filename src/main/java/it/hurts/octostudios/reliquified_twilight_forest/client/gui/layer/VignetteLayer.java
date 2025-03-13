package it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.GoblinNoseItem;
import it.hurts.sskirillss.relics.items.relics.feet.PhantomBootItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteAnchor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VignetteLayer implements LayeredDraw.Layer {
    private static final ResourceLocation VIGNETTE = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft MC = Minecraft.getInstance();
        Player player = MC.player;
        ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.GOBLIN_NOSE.get());

        if (player == null || !(stack.getItem() instanceof GoblinNoseItem relic) || !relic.isAbilityTicking(stack, "vein_seeker")) {
            return;
        }

        PoseStack poseStack = guiGraphics.pose();
        Window window = MC.getWindow();
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        float alpha = (float) Mth.map(Math.sin((player.tickCount+partialTick)/20f), -1, 1, 0.2f, 0.4f);

        poseStack.pushPose();
        RenderSystem.enableBlend();
//        GUIRenderer.begin(VIGNETTE, poseStack)
//                .anchor(SpriteAnchor.TOP_LEFT)
//                .patternSize(width, height)
//                .texSize(width, height)
//                .color(0)
//                .alpha(0.6f)
//                .end();
        GUIRenderer.begin(VIGNETTE, poseStack)
                .anchor(SpriteAnchor.TOP_LEFT)
                .patternSize(width, height)
                .texSize(width, height)
                .color(0xbce63b)
                .alpha(alpha)
                .end();

        RenderSystem.disableBlend();
        poseStack.popPose();
    }
}