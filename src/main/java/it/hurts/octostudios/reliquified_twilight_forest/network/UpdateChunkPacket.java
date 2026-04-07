package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.api.OreCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateChunkPacket {
    private final ChunkPos pos;

    public UpdateChunkPacket(ChunkPos pos) {
        this.pos = pos;
    }

    public static void encode(UpdateChunkPacket packet, FriendlyByteBuf buf) {
        buf.writeLong(packet.pos.toLong());
    }

    public static UpdateChunkPacket decode(FriendlyByteBuf buf) {
        return new UpdateChunkPacket(new ChunkPos(buf.readLong()));
    }

    public static void handle(UpdateChunkPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (OreCache.hasChunk(packet.pos)) {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    OreCache.scanChunkAsync(mc.level, mc.level.getChunk(packet.pos.x, packet.pos.z));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
