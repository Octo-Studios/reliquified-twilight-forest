package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EntityStartRidingPacket(int entityID, int vehicleID) implements CustomPacketPayload {
    public static final Type<EntityStartRidingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "entity_start_riding"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityStartRidingPacket> STREAM_CODEC = CustomPacketPayload.codec(
            EntityStartRidingPacket::write, EntityStartRidingPacket::new
    );

    public EntityStartRidingPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(vehicleID);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EntityStartRidingPacket packet, IPayloadContext ctx) {
        if (ctx.flow().isServerbound()) {
            return;
        }
        ctx.enqueueWork(() -> {
            Entity entity = ctx.player().level().getEntity(packet.entityID);
            Entity vehicle = ctx.player().level().getEntity(packet.vehicleID);

            if (entity == null || vehicle == null) {
                return;
            }

            entity.startRiding(vehicle, true);
        });
    }
}