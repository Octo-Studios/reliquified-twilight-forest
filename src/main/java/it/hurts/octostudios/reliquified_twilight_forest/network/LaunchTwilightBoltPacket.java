package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import twilightforest.entity.projectile.TwilightWandBolt;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFSounds;

public record LaunchTwilightBoltPacket() implements CustomPacketPayload {

    public static final Type<LaunchTwilightBoltPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "launch_twilight_bolt"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LaunchTwilightBoltPacket> STREAM_CODEC = CustomPacketPayload.codec(LaunchTwilightBoltPacket::write, LaunchTwilightBoltPacket::new);

    public LaunchTwilightBoltPacket(RegistryFriendlyByteBuf buf) {
        this();
    }

    public void write(RegistryFriendlyByteBuf buf) {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LaunchTwilightBoltPacket packet, IPayloadContext ctx) {
        Player entity = ctx.player();
        switch (ctx.flow()) {
            case SERVERBOUND -> ctx.enqueueWork(() -> {
                ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.LICH_CROWN.get());
                if (!(stack.getItem() instanceof LichCrownItem relic)) return;
                if (relic.getAbilityLevel(stack, "twilight") <= 0) return;
                if (entity.getAttackStrengthScale(0) < 1) return;

                TwilightWandBolt bolt = new TwilightWandBolt(entity.level(), entity) {
                    @Override
                    protected void onHitEntity(EntityHitResult result) {
                        if (!this.level().isClientSide()) {
                            result.getEntity().hurt(TFDamageTypes.getIndirectEntityDamageSource(this.level(), TFDamageTypes.TWILIGHT_SCEPTER, this, this.getOwner()), (float) relic.getStatValue(stack, "twilight", "damage"));
                            this.level().playSound(null, result.getEntity().blockPosition(), TFSounds.TWILIGHT_SCEPTER_HIT.get(), this.getOwner() != null ? this.getOwner().getSoundSource() : SoundSource.PLAYERS);
                            this.level().broadcastEntityEvent(this, (byte) 3);
                            this.discard();
                        }
                    }
                };
                bolt.setNoGravity(true);
                bolt.setDeltaMovement(entity.getViewVector(1f).scale(relic.getStatValue(stack, "twilight", "speed_scale")));
                entity.level().addFreshEntity(bolt);

                // Confirm this with the player
                PacketDistributor.sendToPlayer((ServerPlayer) entity, new LaunchTwilightBoltPacket());
            });
            case CLIENTBOUND -> ctx.enqueueWork(() -> {
                entity.playSound(TFSounds.TWILIGHT_SCEPTER_USE.get(), 1.0F, (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F);
            });
        }
    }
}