package it.hurts.octostudios.reliquified_twilight_forest.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityStopRidingPacket {
    private final int entityID;

    public EntityStopRidingPacket(int entityID) {
        this.entityID = entityID;
    }

    public static void encode(EntityStopRidingPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityID);
    }

    public static EntityStopRidingPacket decode(FriendlyByteBuf buf) {
        return new EntityStopRidingPacket(buf.readInt());
    }

    public static void handle(EntityStopRidingPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;
                Entity entity = mc.level.getEntity(packet.entityID);
                if (entity == null) return;
                entity.stopRiding();
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
