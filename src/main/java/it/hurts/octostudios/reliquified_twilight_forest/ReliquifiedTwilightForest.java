package it.hurts.octostudios.reliquified_twilight_forest;

import it.hurts.octostudios.reliquified_twilight_forest.init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ReliquifiedTwilightForest.MOD_ID)
public class ReliquifiedTwilightForest {
    public static final String MOD_ID = "reliquified_twilight_forest";

    public ReliquifiedTwilightForest() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setupCommon);

        ConfigRegistry.register();
        ItemRegistry.register(bus);
        EffectRegistry.register(bus);
        EntityRegistry.register(bus);
        ParticleRegistry.register(bus);
        GlobalLootModifierSerializerCodecRegistry.register(bus);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }
}
