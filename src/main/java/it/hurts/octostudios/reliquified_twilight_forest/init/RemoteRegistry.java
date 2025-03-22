package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer.TwilightIndicatorLayer;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer.VignetteLayer;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip.ClientBundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.sskirillss.relics.client.renderer.entities.NullRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = ReliquifiedTwilightForest.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RemoteRegistry {
    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.HYDRA_FIRE.get(), NullRenderer::new);
        event.registerEntityRenderer(EntityRegistry.HYDRA_FIRE_PUDDLE.get(), NullRenderer::new);
    }

    @SubscribeEvent
    public static void onTooltipRegistry(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BundleLikeTooltip.class, ClientBundleLikeTooltip::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerBelowAll(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "goblin_vignette"), new VignetteLayer());
        event.registerAbove(
                VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "twilight_indicator"), new TwilightIndicatorLayer()
        );
    }
}
