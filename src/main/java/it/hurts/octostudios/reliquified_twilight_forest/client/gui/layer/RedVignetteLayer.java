package it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.GoblinNoseItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.Parasite116Item;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
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
import org.lwjgl.opengl.GL11;

public class RedVignetteLayer implements LayeredDraw.Layer {
    private static final ResourceLocation VIGNETTE = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft MC = Minecraft.getInstance();
        Player player = MC.player;
        ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.PARASITE_116.get());

        if (player == null || !(stack.getItem() instanceof Parasite116Item relic) || !relic.isAbilityUnlocked(stack, "rage_consumption")) {
            return;
        }

        float percentage = stack.getOrDefault(DataComponentRegistry.TIME, 0) / 200f;

        PoseStack poseStack = guiGraphics.pose();
        Window window = MC.getWindow();
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);

        poseStack.pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GUIRenderer.begin(VIGNETTE, poseStack)
                .anchor(SpriteAnchor.TOP_LEFT)
                .patternSize(width, height)
                .texSize(width, height)
                .color(0xff0000)
                .alpha(percentage*0.75f)
                .end();

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        poseStack.popPose();
    }
}