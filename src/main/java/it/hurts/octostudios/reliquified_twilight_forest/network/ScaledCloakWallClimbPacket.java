package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.ScaledCloakItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScaledCloakWallClimbPacket {
    private final boolean isColliding;

    public ScaledCloakWallClimbPacket(boolean isColliding) {
        this.isColliding = isColliding;
    }

    public static void encode(ScaledCloakWallClimbPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isColliding);
    }

    public static ScaledCloakWallClimbPacket decode(FriendlyByteBuf buf) {
        return new ScaledCloakWallClimbPacket(buf.readBoolean());
    }

    public static void handle(ScaledCloakWallClimbPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.get().enqueueWork(() -> {
                Player player = ctx.get().getSender();
                if (player == null) return;
                ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SCALED_CLOAK.get());
                int time = stack.hasTag() ? stack.getTag().getInt("time") : 0;

                if (!(stack.getItem() instanceof ScaledCloakItem relic)) {
                    return;
                }

                int maxTime = (int) Math.round(relic.getStatValue(stack, "wall_crawler", "max_time"));

                if (packet.isColliding) {
                    player.fallDistance = 0;
                    if (time > 0) {
                        time--;
                        stack.getOrCreateTag().putInt("time", time);
                    }
                    if (player.tickCount % 20 == 0) {
                        relic.spreadRelicExperience(player, stack, 1);
                    }
                }

                if (player.onGround() && time != maxTime) {
                    stack.getOrCreateTag().putInt("time", maxTime);
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
