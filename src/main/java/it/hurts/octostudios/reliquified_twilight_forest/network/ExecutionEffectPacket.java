package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.awt.*;
import java.util.Random;
import java.util.function.Supplier;

public class ExecutionEffectPacket {
    private final int entityId;
    private final Color color;

    public ExecutionEffectPacket(int entityId, Color color) {
        this.entityId = entityId;
        this.color = color;
    }

    public static void encode(ExecutionEffectPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeInt(packet.color.getRed());
        buf.writeInt(packet.color.getGreen());
        buf.writeInt(packet.color.getBlue());
        buf.writeInt(packet.color.getAlpha());
    }

    public static ExecutionEffectPacket decode(FriendlyByteBuf buf) {
        return new ExecutionEffectPacket(buf.readInt(), new Color(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));
    }

    public static void handle(ExecutionEffectPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null || mc.player == null) return;
                Level level = mc.player.level();
                Random random = new Random();
                if (!(level.getEntity(packet.entityId) instanceof LivingEntity entity)) {
                    return;
                }
                AABB aabb = entity.getBoundingBox();
                double size = aabb.getSize();

                for (int i = 0; i < size * 32; i++) {
                    level.addParticle(
                            ParticleUtils.constructSimpleSpark(
                                    packet.color,
                                    0.5f,
                                    entity.getRandom().nextInt(40, 60),
                                    random.nextFloat(0.9f, 0.97f)
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
        ctx.get().setPacketHandled(true);
    }
}
