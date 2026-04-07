package it.hurts.octostudios.reliquified_twilight_forest.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityStartRidingPacket {
    private final int entityID;
    private final int vehicleID;

    public EntityStartRidingPacket(int entityID, int vehicleID) {
        this.entityID = entityID;
        this.vehicleID = vehicleID;
    }

    public static void encode(EntityStartRidingPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityID);
        buf.writeInt(packet.vehicleID);
    }

    public static EntityStartRidingPacket decode(FriendlyByteBuf buf) {
        return new EntityStartRidingPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(EntityStartRidingPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;
                Entity entity = mc.level.getEntity(packet.entityID);
                Entity vehicle = mc.level.getEntity(packet.vehicleID);
                if (entity == null || vehicle == null) return;
                entity.startRiding(vehicle, true);
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
