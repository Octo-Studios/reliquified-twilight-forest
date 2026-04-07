package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LifedrainParticlePacket {
    private final int entityID;
    private final Vec3 victimPos;

    public LifedrainParticlePacket(int entityID, Vec3 victimPos) {
        this.entityID = entityID;
        this.victimPos = victimPos;
    }

    public static void encode(LifedrainParticlePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityID);
        buf.writeDouble(packet.victimPos.x());
        buf.writeDouble(packet.victimPos.y());
        buf.writeDouble(packet.victimPos.z());
    }

    public static LifedrainParticlePacket decode(FriendlyByteBuf buf) {
        return new LifedrainParticlePacket(buf.readInt(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    public static void handle(LifedrainParticlePacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;
                Entity entity = mc.level.getEntity(packet.entityID);
                if (entity instanceof LivingEntity living) {
                    LichCrownItem.makeRedMagicTrail(living.level(), living, packet.victimPos);
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
