package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.api.OreCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateChunkPacket(ChunkPos pos) implements CustomPacketPayload {
    public static final Type<UpdateChunkPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "update_chunk"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateChunkPacket> STREAM_CODEC = CustomPacketPayload.codec(
            UpdateChunkPacket::write, UpdateChunkPacket::new
    );

    public UpdateChunkPacket(RegistryFriendlyByteBuf buf) {
        this(new ChunkPos(buf.readLong()));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateChunkPacket packet, IPayloadContext ctx) {
        if (!ctx.flow().isClientbound()) {
            return;
        }

        ctx.enqueueWork(() -> {
            if (OreCache.hasChunk(packet.pos)) {
                OreCache.scanChunkAsync(ctx.player().level(), ctx.player().level().getChunk(packet.pos.x, packet.pos.z));
            }
        });
    }
}
