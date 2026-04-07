package it.hurts.octostudios.reliquified_twilight_forest.network;

import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.NBTHelper;
import it.hurts.octostudios.reliquified_twilight_forest.init.PacketHandler;
import it.hurts.octostudios.reliquified_twilight_forest.item.ability.LichCrownAbilities;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import twilightforest.entity.projectile.TwilightWandBolt;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFSounds;

import java.util.function.Supplier;

public class LaunchTwilightBoltPacket {

    public LaunchTwilightBoltPacket() {
    }

    public static void encode(LaunchTwilightBoltPacket packet, FriendlyByteBuf buf) {
        // no data
    }

    public static LaunchTwilightBoltPacket decode(FriendlyByteBuf buf) {
        return new LaunchTwilightBoltPacket();
    }

    public static void handle(LaunchTwilightBoltPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.get().enqueueWork(() -> {
                Player entity = ctx.get().getSender();
                if (entity == null) return;
                ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.LICH_CROWN.get());
                if (!(stack.getItem() instanceof LichCrownItem relic)
                        || relic.getAbilityLevel(stack, "twilight") <= 0
                        || NBTHelper.getTwilightTime(stack) > 0
                ) return;

                TwilightWandBolt bolt = new TwilightWandBolt(entity.level(), entity) {
                    @Setter
                    @Getter
                    int age = 200;

                    @Override
                    public void tick() {
                        super.tick();
                        if (this.level().isClientSide()) {
                            return;
                        }

                        if (this.getAge() < 0) {
                            this.discard();
                        }

                        this.setAge(this.getAge() - 1);
                    }

                    @Override
                    public void addAdditionalSaveData(CompoundTag compound) {
                        super.addAdditionalSaveData(compound);
                        compound.putInt("Age", this.getAge());
                    }

                    @Override
                    public void readAdditionalSaveData(CompoundTag compound) {
                        super.readAdditionalSaveData(compound);
                        this.setAge(compound.getInt("Age"));
                    }

                    @Override
                    protected void onHitEntity(EntityHitResult result) {
                        if (this.level().isClientSide()) {
                            return;
                        }

                        result.getEntity().hurt(
                                TFDamageTypes.getIndirectEntityDamageSource(this.level(), TFDamageTypes.TWILIGHT_SCEPTER, this, this.getOwner()),
                                (float) relic.getStatValue(stack, "twilight", "damage")
                        );

                        this.level().playSound(
                                null,
                                result.getEntity().blockPosition(),
                                TFSounds.TWILIGHT_SCEPTER_HIT.get(),
                                this.getOwner() != null ? this.getOwner().getSoundSource() : SoundSource.PLAYERS
                        );

                        this.level().broadcastEntityEvent(this, (byte) 3);
                        this.discard();
                    }
                };
                bolt.getPersistentData().putBoolean("reliquified_twilight_forest:isCustom", true);
                bolt.setNoGravity(true);
                bolt.setDeltaMovement(entity.getViewVector(1f).scale(relic.getStatValue(stack, "twilight", "speed_scale")));
                entity.level().addFreshEntity(bolt);

                NBTHelper.setTwilightTime(stack, LichCrownAbilities.MAX_TWILIGHT_TIME);

                // Confirm this with the player
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), new LaunchTwilightBoltPacket());
            });
        } else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                Player entity = net.minecraft.client.Minecraft.getInstance().player;
                if (entity == null) return;
                entity.playSound(
                        TFSounds.TWILIGHT_SCEPTER_USE.get(),
                        1.0F,
                        (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F
                );
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
