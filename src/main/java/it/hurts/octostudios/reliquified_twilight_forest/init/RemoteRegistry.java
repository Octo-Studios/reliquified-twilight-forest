package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.sskirillss.relics.items.relics.InfiniteHamItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(modid = ReliquifiedTwilightForest.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RemoteRegistry {
    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {

    }

    @SubscribeEvent
    public static void onTooltipRegistry(RegisterClientTooltipComponentFactoriesEvent event) {

    }
}
