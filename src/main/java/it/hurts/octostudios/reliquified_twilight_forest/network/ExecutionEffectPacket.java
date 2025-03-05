package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.awt.*;
import java.util.Random;

public record ExecutionEffectPacket(int entityId, Color color) implements CustomPacketPayload {
    public static final Type<ExecutionEffectPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "execution_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExecutionEffectPacket> STREAM_CODEC =
            CustomPacketPayload.codec(ExecutionEffectPacket::write, ExecutionEffectPacket::new);

    public ExecutionEffectPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), new Color(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(color.getRed());
        buf.writeInt(color.getGreen());
        buf.writeInt(color.getBlue());
        buf.writeInt(color.getAlpha());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ExecutionEffectPacket packet, IPayloadContext ctx) {
        if (ctx.flow().isServerbound()) {
            return;
        }

        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            Random random = new Random();
            if (!(level.getEntity(packet.entityId) instanceof LivingEntity entity)) {
                return;
            }
            AABB aabb = entity.getBoundingBox();
            double size = aabb.getSize();

            for (int i = 0; i < size * 32; i++) {
                level.addParticle(
                        ParticleUtils.constructSimpleSpark(
                                packet.color(),
                                0.5f,
                                entity.getRandom().nextInt(40, 60),
                                random.nextFloat(0.9f,0.97f)
                        ),
                        entity.getRandomX(0.5f),
                        entity.getRandomY(),
                        entity.getRandomZ(0.5f),
                        0d,
                        random.nextFloat(0.005f, 0.05f),
                        0d
                );
            }
        });
    }
}