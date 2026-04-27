package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip.ClientBundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.client.gui.tooltip.ClientChromaticCloakTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.entity.projectile.HydraFireEntity;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.BundleLikeTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.ChromaticCloakTooltip;
import it.hurts.sskirillss.relics.client.renderer.entities.NullRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber //(modid = ReliquifiedTwilightForest.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RemoteRegistry {
    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        EntityTrailRegistry.registerProvider(RTEntities.HYDRA_FIRE.get(), HydraFireEntity.TrailProvider::new);
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RTEntities.HYDRA_FIRE.get(), NullRenderer::new);
        event.registerEntityRenderer(RTEntities.HYDRA_FIRE_PUDDLE.get(), NullRenderer::new);
    }

    @SubscribeEvent
    public static void onTooltipRegistry(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BundleLikeTooltip.class, ClientBundleLikeTooltip::new);
        event.register(ChromaticCloakTooltip.class, ClientChromaticCloakTooltip::new);
    }

}
