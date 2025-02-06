package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LifedrainParticlePacket(int entityID, Vec3 victimPos) implements CustomPacketPayload {

    public static final Type<LifedrainParticlePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "lifedrain_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LifedrainParticlePacket> STREAM_CODEC = CustomPacketPayload.codec(LifedrainParticlePacket::write, LifedrainParticlePacket::new);

    public LifedrainParticlePacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeDouble(this.victimPos.x());
        buf.writeDouble(this.victimPos.y());
        buf.writeDouble(this.victimPos.z());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LifedrainParticlePacket packet, IPayloadContext ctx) {
        if (ctx.flow().isClientbound()) {
            ctx.enqueueWork(() -> {
                Entity entity = ctx.player().level().getEntity(packet.entityID());
                if (entity instanceof LivingEntity living) {
                    LichCrownItem.makeRedMagicTrail(living.level(), living, packet.victimPos());
                }
            });
        }
    }
}