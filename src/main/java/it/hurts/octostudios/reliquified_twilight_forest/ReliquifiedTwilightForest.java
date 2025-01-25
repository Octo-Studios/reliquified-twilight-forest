package it.hurts.octostudios.reliquified_twilight_forest;

import it.hurts.octostudios.reliquified_twilight_forest.init.EntityRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ReliquifiedTwilightForest.MODID)
public class ReliquifiedTwilightForest {
    public static final String MODID = "reliquified_twilight_forest";

    public ReliquifiedTwilightForest(IEventBus bus) {
        bus.addListener(this::setupCommon);

        ItemRegistry.register(bus);
        EntityRegistry.register(bus);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {

    }
}