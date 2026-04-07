package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.awt.Color;
import java.util.function.Supplier;

public class ParasiteEvolveParticlePacket {
    private final int entityID;

    public ParasiteEvolveParticlePacket(int entityID) {
        this.entityID = entityID;
    }

    public static void encode(ParasiteEvolveParticlePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityID);
    }

    public static ParasiteEvolveParticlePacket decode(FriendlyByteBuf buf) {
        return new ParasiteEvolveParticlePacket(buf.readInt());
    }

    public static void handle(ParasiteEvolveParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;
                Entity entity = mc.level.getEntity(packet.entityID);
                if (!(entity instanceof LivingEntity living)) {
                    return;
                }

                Vec3 center = living.position().add(0, living.getBbHeight() / 2f, 0);
                for (int i = 0; i < 150; i++) {
                    ParticleOptions options = ParticleUtils.constructSimpleSpark(
                            new Color(living.getRandom().nextInt(230, 255), living.getRandom().nextInt(230, 255), 0),
                            living.getRandom().nextFloat() / 2f,
                            living.getRandom().nextInt(120, 200),
                            0.9f
                    );

                    Vec3 pos = new Vec3(living.getRandomX(0.5), living.getRandomY(), living.getRandomZ(0.5));
                    living.level().addParticle(options, pos.x, pos.y, pos.z, (center.x - pos.x) / 8f, (center.y - pos.y) / 8f, (center.z - pos.z) / 8f);
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
