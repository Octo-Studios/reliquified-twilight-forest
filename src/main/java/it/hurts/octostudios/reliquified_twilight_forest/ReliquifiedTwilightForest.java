package it.hurts.octostudios.reliquified_twilight_forest;

import it.hurts.octostudios.reliquified_twilight_forest.init.*;
import net.minecraft.client.particle.Particle;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ReliquifiedTwilightForest.MODID)
public class ReliquifiedTwilightForest {
    public static final String MODID = "reliquified_twilight_forest";

    public ReliquifiedTwilightForest(IEventBus bus) {
        bus.addListener(this::setupCommon);

        ItemRegistry.register(bus);
        EffectRegistry.register(bus);
        EntityRegistry.register(bus);
        ParticleRegistry.register(bus);
        DataComponentRegistry.register(bus);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {

    }
}