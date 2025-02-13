package it.hurts.octostudios.reliquified_twilight_forest.init;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip.ClientGemTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.GemTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.item.ability.LichCrownAbilities;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import twilightforest.TwilightForestMod;

@EventBusSubscriber(modid = ReliquifiedTwilightForest.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RemoteRegistry {
    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {

    }

    @SubscribeEvent
    public static void onTooltipRegistry(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(GemTooltip.class, ClientGemTooltip::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "twilight_indicator"), ((guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            ItemStack stack = EntityUtils.findEquippedCurio(mc.player, ItemRegistry.LICH_CROWN.get());
            if (!(stack.getItem() instanceof LichCrownItem relic)) return;
            if (relic.getAbilityLevel(stack, "twilight") <= 0) return;
            if (LichCrownAbilities.ClientEvents.getEntityLookingAt(mc.player, 64) == null) return;
            boolean flag = mc.getCameraEntity() instanceof LivingEntity living && living.isSleeping();
            if (!mc.options.getCameraType().isFirstPerson()
                    || flag
                    || mc.options.hideGui
                    || mc.player.isSpectator()
            || mc.crosshairPickEntity != null) return;

            float partialTick = mc.player.tickCount+deltaTracker.getGameTimeDeltaPartialTick(true);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(guiGraphics.guiWidth()/2f+4, guiGraphics.guiHeight()/2f+4, 0);
//            guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(2*(mc.player.tickCount+deltaTracker.getGameTimeDeltaPartialTick(true))));
//            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(1.7f*(mc.player.tickCount+deltaTracker.getGameTimeDeltaPartialTick(true))));
//            guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(2.13f*(mc.player.tickCount+deltaTracker.getGameTimeDeltaPartialTick(true))));
            guiGraphics.pose().scale(0.5f,0.5f,0.5f);

            float sin = (float) (Math.sin(partialTick/2f)/4f+0.75f);
            guiGraphics.pose().translate(0,0, -150);
            RenderSystem.setShaderColor(sin/2f+1.125f,sin/2f+1.125f,sin+1.25f, sin);
            guiGraphics.renderItem(ItemRegistry.TWILIGHT_GEM.get().getDefaultInstance(), -8, -8);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            guiGraphics.pose().popPose();
        }));
    }
}
