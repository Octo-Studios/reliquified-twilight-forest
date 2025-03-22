package it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.ability.LichCrownAbilities;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TwilightIndicatorLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = EntityUtils.findEquippedCurio(mc.player, ItemRegistry.LICH_CROWN.get());
        if (mc.player == null
                || !(stack.getItem() instanceof LichCrownItem relic)
                || relic.getAbilityLevel(stack, "twilight") <= 0
                || LichCrownAbilities.ClientEvents.getEntityLookingAt(mc.player, 64) == null
                || !mc.options.getCameraType().isFirstPerson()
                || (mc.getCameraEntity() instanceof LivingEntity living && living.isSleeping())
                || mc.options.hideGui
                || mc.player.isSpectator()
                || mc.crosshairPickEntity != null
        ) return;

        float partialTick = mc.player.tickCount + deltaTracker.getGameTimeDeltaPartialTick(true);
        float scale = 0.5f;
        float guiScale = (float) (Minecraft.getInstance().getWindow().getGuiScale() * scale);
        float sin = (float) (Math.sin(partialTick / 2f) / 4f + 0.75f);

        int itemX = guiGraphics.guiWidth();
        int itemY = guiGraphics.guiHeight();

        int scissorOffset = Math.round(Mth.map(
                stack.getOrDefault(DataComponentRegistry.TWILIGHT_TIME, 0),
                0,
                LichCrownAbilities.MAX_TWILIGHT_TIME,
                0,
                12 * guiScale
        ));
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1f);
        guiGraphics.pose().translate(0, 0, -150);

        if (scissorOffset == 0) {
            RenderSystem.setShaderColor(sin / 2f + 1.125f, sin / 2f + 1.125f, sin + 1.25f, sin);
        }

        RenderSystem.enableScissor(
                (int) (itemX * guiScale),
                (int) (Minecraft.getInstance().getWindow().getScreenHeight() - (14 + itemY) * guiScale),
                (int) (16 * guiScale),
                (int) (12 * guiScale - scissorOffset)
        );

        guiGraphics.renderItem(ItemRegistry.TWILIGHT_GEM.get().getDefaultInstance(), itemX, itemY);
        guiGraphics.pose().popPose();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableScissor();
    }
}
