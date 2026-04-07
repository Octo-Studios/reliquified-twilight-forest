package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer.RedVignetteLayer;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer.TwilightIndicatorLayer;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.layer.VignetteLayer;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip.ClientBundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip.ClientChromaticCloakTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.entity.projectile.HydraFireEntity;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.ChromaticCloakTooltip;
import it.hurts.sskirillss.relics.client.renderer.entities.NullRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ReliquifiedTwilightForest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RemoteRegistry {
    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        EntityTrailRegistry.registerProvider(EntityRegistry.HYDRA_FIRE.get(), HydraFireEntity.TrailProvider::new);
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.HYDRA_FIRE.get(), NullRenderer::new);
        event.registerEntityRenderer(EntityRegistry.HYDRA_FIRE_PUDDLE.get(), NullRenderer::new);
    }

    @SubscribeEvent
    public static void onTooltipRegistry(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BundleLikeTooltip.class, ClientBundleLikeTooltip::new);
        event.register(ChromaticCloakTooltip.class, ClientChromaticCloakTooltip::new);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll(new ResourceLocation(ReliquifiedTwilightForest.MOD_ID, "goblin_vignette"), new VignetteLayer());
        event.registerBelowAll(new ResourceLocation(ReliquifiedTwilightForest.MOD_ID, "rage_consumption_vignette"), new RedVignetteLayer());
        event.registerAbove(
                VanillaGuiOverlay.CROSSHAIR.id(),
                new ResourceLocation(ReliquifiedTwilightForest.MOD_ID, "twilight_indicator"), new TwilightIndicatorLayer()
        );
    }
}
