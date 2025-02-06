package it.hurts.octostudios.reliquified_twilight_forest.item.ability;

import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.octostudios.reliquified_twilight_forest.network.LaunchTwilightBoltPacket;
import it.hurts.octostudios.reliquified_twilight_forest.network.LifedrainParticlePacket;
import it.hurts.octostudios.reliquified_twilight_forest.util.EntitiesButCool;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import twilightforest.components.entity.FortificationShieldAttachment;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFDataAttachments;
import twilightforest.init.TFSounds;

import javax.annotation.Nullable;
import java.util.List;

public class LichCrownAbilities {
    public static final int MAX_LIFEDRAIN_TIME = 120;

    public static final AbilityData FORTIFICATION = AbilityData.builder("fortification")
            .stat(StatData.builder("max_shields")
                    .initialValue(3, 5)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(200, 160)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.125f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static final AbilityData ZOMBIE = AbilityData.builder("zombie")
            .stat(StatData.builder("max_zombies")
                    .initialValue(3, 5)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("damage")
                    .initialValue(1, 2)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(260, 200)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.1f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static final AbilityData TWILIGHT = AbilityData.builder("twilight")
            .stat(StatData.builder("damage")
                    .initialValue(3, 4)
                    .upgradeModifier(UpgradeOperation.ADD, 0.5)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .stat(StatData.builder("speed_scale")
                    .initialValue(0.9, 1.2)
                    .upgradeModifier(UpgradeOperation.ADD, 0.1)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
//            .stat(StatData.builder("interval")
//                    .initialValue(200, 180)
//                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.075f)
//                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
//                    .build())
            .maxLevel(5)
            .build();

    public static final AbilityData LIFEDRAIN = AbilityData.builder("lifedrain")
            .stat(StatData.builder("heal_percentage")
                    .initialValue(0.01, 0.02)
                    .upgradeModifier(UpgradeOperation.ADD, 0.005)
                    //.upgradeModifier((current, level) -> 1 - 1/(current*Math.sqrt(level)+1))
                    .formatValue(value -> MathButCool.roundSingleDigit(value * 400))
                    .build())
            .stat(StatData.builder("radius")
                    .initialValue(3, 6)
                    .upgradeModifier(UpgradeOperation.ADD, 0.5)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(240, 200)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.1f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static void fortificationTick(LivingEntity entity, ItemStack stack) {
        if (entity.isSpectator()) return;

        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        FortificationShieldAttachment attachment = entity.getData(TFDataAttachments.FORTIFICATION_SHIELDS);
        int maxTime = (int) Math.round(relic.getStatValue(stack, "fortification", "interval"));
        int time = stack.getOrDefault(DataComponentRegistry.FORTIFICATION_TIME, 0);

        if (attachment.permanentShieldsLeft() < relic.getStatValue(stack, "fortification", "max_shields")) {
            if (time >= maxTime) {
                attachment.addShields(entity, 1, false);
                time = 0;
            } else time += 1;
        }

        stack.set(DataComponentRegistry.FORTIFICATION_TIME, time);
    }

    public static void lifedrainTick(LivingEntity entity, ItemStack stack) {
        if (entity.isSpectator()) return;

        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        int maxTime = (int) Math.round(relic.getStatValue(stack, "lifedrain", "interval")) + MAX_LIFEDRAIN_TIME;
        int time = stack.getOrDefault(DataComponentRegistry.LIFEDRAIN_TIME, 0);
        float healAmount = (float) (entity.getMaxHealth() * relic.getStatValue(stack, "lifedrain", "heal_percentage"));
        DamageSource dmg = TFDamageTypes.getEntityDamageSource(entity.level(), TFDamageTypes.LIFEDRAIN, entity);

        if (time > maxTime - MAX_LIFEDRAIN_TIME && entity.getHealth() < entity.getMaxHealth() && entity.tickCount % 5 == 0) {
            List<LivingEntity> toAbsorb = EntitiesButCool.findEligibleEntities(entity, relic.getStatValue(stack, "lifedrain", "radius"), e ->
                    !EntityUtils.isAlliedTo(entity, e)
                            && e.isAlive()
                            && entity.hasLineOfSight(e)
            );
            toAbsorb.forEach(toHurt -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new LifedrainParticlePacket(entity.getId(), toHurt.getEyePosition()));
                if (toHurt.getHealth() <= healAmount / toAbsorb.size() && !toHurt.getType().is(Tags.EntityTypes.BOSSES)) {
                    LichCrownItem.explodeEntity(entity, toHurt, dmg);
                    return;
                }

                toHurt.invulnerableTime = 0;
                if (!toHurt.hurt(dmg, healAmount / toAbsorb.size())) return;
                if (toHurt.getMaxHealth() <= entity.getMaxHealth()) toHurt.setDeltaMovement(0, 0.15D, 0);
                else toHurt.setDeltaMovement(0, 0, 0);
            });
            entity.heal(healAmount);
            if (!toAbsorb.isEmpty())
                entity.level().playSound(null, entity.blockPosition(), TFSounds.LIFE_SCEPTER_DRAIN.get(), SoundSource.PLAYERS);
        }

        if (time <= 0) {
            if (entity.getHealth() / entity.getMaxHealth() <= 0.2f) time = maxTime;
        } else time--;
        stack.set(DataComponentRegistry.LIFEDRAIN_TIME, time);
    }

    public static void twilightTick(LivingEntity entity, ItemStack stack) {
        if (entity.isSpectator()) return;
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
//        int time = stack.getOrDefault(DataComponentRegistry.TWILIGHT_TIME, 0);
//        if (time > 0) time--;
//        stack.set(DataComponentRegistry.TWILIGHT_TIME, time);
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void swingEvent(PlayerInteractEvent.LeftClickEmpty e) {
            Player player = e.getEntity();
            if (player.isSpectator()) return;

            double maxDistance = 64.0;

            EntityHitResult target = getEntityLookingAt(player, maxDistance);

            if (target != null && target.getEntity() instanceof LivingEntity) {
                PacketDistributor.sendToServer(new LaunchTwilightBoltPacket());
            }
        }

        public static EntityHitResult getEntityLookingAt(Player player, double maxDistance) {
            Vec3 eyePosition = player.getEyePosition(1.0F);
            Vec3 lookVector = player.getViewVector(1.0F);
            Vec3 reachEnd = eyePosition.add(lookVector.scale(maxDistance));

            // Perform entity ray tracing
            return ProjectileUtil.getEntityHitResult(player, eyePosition, reachEnd,
                    player.getBoundingBox().expandTowards(lookVector.scale(maxDistance)).inflate(1.0),
                    entity -> entity instanceof LivingEntity && entity.isAlive() && entity != player && !EntityUtils.isAlliedTo(player, entity),
                    maxDistance * maxDistance);
        }
    }
}
