package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.ScaledCloakItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScaledCloakWallClimbPacket(boolean isColliding) implements CustomPacketPayload {
    public static final Type<ScaledCloakWallClimbPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "scaled_cloak_wall_climb"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ScaledCloakWallClimbPacket> STREAM_CODEC = CustomPacketPayload.codec(
            ScaledCloakWallClimbPacket::write, ScaledCloakWallClimbPacket::new
    );

    public ScaledCloakWallClimbPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(isColliding);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScaledCloakWallClimbPacket packet, IPayloadContext ctx) {
        if (ctx.flow().isClientbound()) {
            return;
        }

        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SCALED_CLOAK.get());
            int time = stack.getOrDefault(DataComponentRegistry.TIME, 0);

            if (!(stack.getItem() instanceof ScaledCloakItem relic)) {
                return;
            }

            int maxTime = (int) Math.round(relic.getStatValue(stack, "wall_crawler", "max_time"));

            if (packet.isColliding()) {
                player.fallDistance = 0;
                if (time > 0) {
                    stack.set(DataComponentRegistry.TIME, --time);
                }
                if (player.tickCount % 20 == 0) {
                    relic.spreadRelicExperience(player, stack, 1);
                }
            }

            if (player.onGround() && time != maxTime) {
                stack.set(DataComponentRegistry.TIME, maxTime);
            }
        });
    }
}
