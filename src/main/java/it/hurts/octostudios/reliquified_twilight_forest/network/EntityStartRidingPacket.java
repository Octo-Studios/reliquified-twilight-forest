package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.ability.LichCrownAbilities;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import twilightforest.entity.projectile.TwilightWandBolt;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFSounds;

public record EntityStartRidingPacket(int entityID, int vehicleID) implements CustomPacketPayload {
    public static final Type<EntityStartRidingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "entity_start_riding"));

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