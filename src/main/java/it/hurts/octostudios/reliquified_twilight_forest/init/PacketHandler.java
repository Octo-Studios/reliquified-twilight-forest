package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static int id = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ReliquifiedTwilightForest.MOD_ID, "main"),
            () -> "1.0.0",
            "1.0.0"::equals,
            "1.0.0"::equals
    );

    public static void register() {
        CHANNEL.registerMessage(id++, UpdateChunkPacket.class, UpdateChunkPacket::encode, UpdateChunkPacket::decode, UpdateChunkPacket::handle);
        CHANNEL.registerMessage(id++, ExecutionEffectPacket.class, ExecutionEffectPacket::encode, ExecutionEffectPacket::decode, ExecutionEffectPacket::handle);
        CHANNEL.registerMessage(id++, EntityStopRidingPacket.class, EntityStopRidingPacket::encode, EntityStopRidingPacket::decode, EntityStopRidingPacket::handle);
        CHANNEL.registerMessage(id++, LifedrainParticlePacket.class, LifedrainParticlePacket::encode, LifedrainParticlePacket::decode, LifedrainParticlePacket::handle);
        CHANNEL.registerMessage(id++, EntityStartRidingPacket.class, EntityStartRidingPacket::encode, EntityStartRidingPacket::decode, EntityStartRidingPacket::handle);
        CHANNEL.registerMessage(id++, ParasiteEvolveParticlePacket.class, ParasiteEvolveParticlePacket::encode, ParasiteEvolveParticlePacket::decode, ParasiteEvolveParticlePacket::handle);
        CHANNEL.registerMessage(id++, CastRideAlongAbilityPacket.class, CastRideAlongAbilityPacket::encode, CastRideAlongAbilityPacket::decode, CastRideAlongAbilityPacket::handle);
        CHANNEL.registerMessage(id++, ScaledCloakWallClimbPacket.class, ScaledCloakWallClimbPacket::encode, ScaledCloakWallClimbPacket::decode, ScaledCloakWallClimbPacket::handle);
        CHANNEL.registerMessage(id++, LaunchTwilightBoltPacket.class, LaunchTwilightBoltPacket::encode, LaunchTwilightBoltPacket::decode, LaunchTwilightBoltPacket::handle);
    }
}
