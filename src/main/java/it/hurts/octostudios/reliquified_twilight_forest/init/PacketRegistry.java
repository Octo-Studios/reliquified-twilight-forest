package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.network.LaunchTwilightBoltPacket;
import it.hurts.octostudios.reliquified_twilight_forest.network.LifedrainParticlePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PacketRegistry {
    @SubscribeEvent
    public static void setupPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ReliquifiedTwilightForest.MODID).versioned("1.0.0").optional();
        registrar.playToClient(LifedrainParticlePacket.TYPE, LifedrainParticlePacket.STREAM_CODEC, LifedrainParticlePacket::handle);
        registrar.playBidirectional(LaunchTwilightBoltPacket.TYPE, LaunchTwilightBoltPacket.STREAM_CODEC, LaunchTwilightBoltPacket::handle);
    }
}
