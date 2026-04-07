package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.init.PacketHandler;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.DeerAntlerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class CastRideAlongAbilityPacket {
    private final int entityID;
    private final boolean isMounting;

    public CastRideAlongAbilityPacket(int entityID, boolean isMounting) {
        this.entityID = entityID;
        this.isMounting = isMounting;
    }

    public static void encode(CastRideAlongAbilityPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityID);
        buf.writeBoolean(packet.isMounting);
    }

    public static CastRideAlongAbilityPacket decode(FriendlyByteBuf buf) {
        return new CastRideAlongAbilityPacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(CastRideAlongAbilityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.get().enqueueWork(() -> {
                Entity vehicle = ctx.get().getSender();
                if (vehicle == null) return;
                Entity entity = vehicle.level().getEntity(packet.entityID);
                Entity passenger = vehicle.getFirstPassenger();

                if (!packet.isMounting) {
                    if (passenger != null) {
                        passenger.stopRiding();
                        PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> passenger), new EntityStopRidingPacket(passenger.getId()));
                    }
                }

                if (entity != null) {
                    if (passenger != null) {
                        passenger.setPos(entity.position());
                        passenger.setOldPosAndRot();
                    }

                    entity.getPersistentData().putBoolean(DeerAntlerItem.ON_ANTLERS, true);
                    entity.startRiding(vehicle, true);
                    PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> vehicle), new EntityStartRidingPacket(packet.entityID, vehicle.getId()));
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
