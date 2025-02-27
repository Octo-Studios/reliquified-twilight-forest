package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.ability.LichCrownAbilities;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.DeerAntlerItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CastRideAlongAbilityPacket(int entityID, boolean isMounting) implements CustomPacketPayload {
    public static final Type<CastRideAlongAbilityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "cast_ride_along_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastRideAlongAbilityPacket> STREAM_CODEC = CustomPacketPayload.codec(
            CastRideAlongAbilityPacket::write, CastRideAlongAbilityPacket::new
    );

    public CastRideAlongAbilityPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeBoolean(isMounting);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CastRideAlongAbilityPacket packet, IPayloadContext ctx) {
        if (ctx.flow().isClientbound()) {
            return;
        }

        Entity vehicle = ctx.player();
        Entity entity = vehicle.level().getEntity(packet.entityID);
        Entity passenger = vehicle.getFirstPassenger();

        if (!packet.isMounting()) {
            if (passenger != null) {
                passenger.stopRiding();
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(passenger, new EntityStopRidingPacket(passenger.getId()));
            }
        }

        if (entity != null) {
            if (passenger != null) {
                passenger.setPos(entity.position());
                passenger.setOldPosAndRot();
            }

            entity.getPersistentData().putBoolean(DeerAntlerItem.ON_ANTLERS, true);
            entity.startRiding(vehicle, true);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(vehicle, new EntityStartRidingPacket(packet.entityID, vehicle.getId()));
        }
    }
}
