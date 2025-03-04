package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EntityStopRidingPacket(int entityID) implements CustomPacketPayload {
    public static final Type<EntityStopRidingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "entity_stop_riding"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityStopRidingPacket> STREAM_CODEC = CustomPacketPayload.codec(
            EntityStopRidingPacket::write, EntityStopRidingPacket::new
    );

    public EntityStopRidingPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityID);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EntityStopRidingPacket packet, IPayloadContext ctx) {
        if (ctx.flow().isServerbound()) {
            return;
        }
        ctx.enqueueWork(() -> {

            Entity entity = ctx.player().level().getEntity(packet.entityID);

            if (entity == null) {
                return;
            }

            entity.stopRiding();
        });
    }
}