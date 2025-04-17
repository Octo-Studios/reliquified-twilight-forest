package it.hurts.octostudios.reliquified_twilight_forest.item.ability;

import com.google.common.collect.Lists;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.Gem;
import it.hurts.octostudios.reliquified_twilight_forest.item.GemItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.octostudios.reliquified_twilight_forest.network.LaunchTwilightBoltPacket;
import it.hurts.octostudios.reliquified_twilight_forest.network.LifedrainParticlePacket;
import it.hurts.octostudios.reliquified_twilight_forest.util.EntitiesButCool;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.components.entity.FortificationShieldAttachment;
import twilightforest.entity.monster.LoyalZombie;
import twilightforest.entity.projectile.TwilightWandBolt;
import twilightforest.init.*;
import twilightforest.item.LifedrainScepterItem;

import java.util.*;

public class LichCrownAbilities {
    public static final Map<String, AbilityData> ABILITIES = new HashMap<>();
    public static final Map<String, DeferredHolder<Item, GemItem>> GEMS = new HashMap<>();

    public static final int MAX_LIFEDRAIN_TIME = 100;
    public static final int MAX_TWILIGHT_TIME = 50;
    public static final String VENDETTA_ID = ReliquifiedTwilightForest.MOD_ID+":vendetta";

    public static final AbilityData FORTIFICATION = register(AbilityData.builder("fortification")
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
            .build(), ItemRegistry.SHIELDING_GEM);

    public static final AbilityData ZOMBIE = register(AbilityData.builder("zombie")
            .stat(StatData.builder("max_zombies")
                    .initialValue(1, 2)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("damage")
                    .initialValue(2, 3)
                    .upgradeModifier(UpgradeOperation.ADD, 0.5)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(400, 320)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.075f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .build(), ItemRegistry.NECROMANCY_GEM);

    public static final AbilityData TWILIGHT = register(AbilityData.builder("twilight")
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
            .build(), ItemRegistry.TWILIGHT_GEM);

    public static final AbilityData LIFEDRAIN = register(AbilityData.builder("lifedrain")
            .stat(StatData.builder("heal_percentage")
                    .initialValue(0.0075, 0.015)
                    .upgradeModifier(UpgradeOperation.ADD, 0.0035)
                    .formatValue(value -> MathButCool.roundSingleDigit(value * 400))
                    .build())
            .stat(StatData.builder("radius")
                    .initialValue(2, 5)
                    .upgradeModifier(UpgradeOperation.ADD, 0.5)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(300, 260)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.1f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .build(), ItemRegistry.ABSORPTION_GEM);

    public static final AbilityData FROSTBITE = register(AbilityData.builder("frostbite")
            .stat(StatData.builder("duration")
                    .initialValue(80, 100)
                    .upgradeModifier(UpgradeOperation.ADD, 10)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .build(), ItemRegistry.FROST_GEM);

    public static final AbilityData BIOME_BURN = register(AbilityData.builder("biome_burn")
            .stat(StatData.builder("multiplier")
                    .initialValue(0.02, 0.05)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2)
                    .formatValue(MathButCool::percentageAndRoundSingleDigit)
                    .build())
            .build(), ItemRegistry.FIRE_GEM);

    public static final AbilityData VENDETTA = register(AbilityData.builder("vendetta")
            .stat(StatData.builder("lifetime")
                    .initialValue(10, 15)
                    .upgradeModifier(UpgradeOperation.ADD, 4)
                    .formatValue(MathButCool::percentageAndRoundSingleDigit)
                    .build())
            .stat(StatData.builder("multiplier")
                    .initialValue(0.5, 1)
                    .upgradeModifier(UpgradeOperation.ADD, 0.1)
                    .formatValue(MathButCool::percentage)
                    .build())
            .build(), ItemRegistry.VENGEFUL_GEM);

    public static final AbilityData ETHEREAL_GUARD = register(AbilityData.builder("ethereal_guard")
            .stat(StatData.builder("chance")
                    .initialValue(0.2, 0.3)
                    .upgradeModifier(UpgradeOperation.ADD, 0.2/18d)
                    .formatValue(MathButCool::percentageAndRoundSingleDigit)
                    .build())
            .build(), ItemRegistry.ETHER_GEM);

    public static final AbilityData MIRROR_LEECH = register(AbilityData.builder("mirror_leech")
            .stat(StatData.builder("chance")
                    .initialValue(0.05, 0.1)
                    .upgradeModifier(UpgradeOperation.ADD, 0.4/18d)
                    .formatValue(MathButCool::percentageAndRoundSingleDigit)
                    .build())
            .build(), ItemRegistry.CARMINITE_GEM);

    public static void fortificationUnequip(SlotContext slotContext, ItemStack stack) {
        slotContext.entity().getData(TFDataAttachments.FORTIFICATION_SHIELDS).setShields(slotContext.entity(), 0, false);
    }

    public static void zombieUnequip(SlotContext slotContext, ItemStack stack) {
        ArrayList<UUID> uuids = Lists.newArrayList(stack.getOrDefault(DataComponentRegistry.ZOMBIES, List.of()));
        uuids.forEach(uuid -> {
            Entity entity = ((ServerLevel) slotContext.entity().level()).getEntity(uuid);
            if (entity != null) entity.discard();
        });
        uuids.clear();
        stack.set(DataComponentRegistry.ZOMBIES, List.of());
    }

    public static void fortificationTick(LivingEntity entity, ItemStack stack) {
        if (entity.isSpectator()
                || !(stack.getItem() instanceof LichCrownItem relic)
        ) return;

        FortificationShieldAttachment attachment = entity.getData(TFDataAttachments.FORTIFICATION_SHIELDS);
        int maxTime = (int) Math.round(relic.getStatValue(stack, "fortification", "interval"));
        int time = stack.getOrDefault(DataComponentRegistry.FORTIFICATION_TIME, 0);

        if (attachment.permanentShieldsLeft() < relic.getStatValue(stack, "fortification", "max_shields")) {
            if (time <= 0) {
                attachment.addShields(entity, 1, false);
                time = maxTime;
                relic.spreadRelicExperience(entity, stack, 1);
            } else time--;
        }

        stack.set(DataComponentRegistry.FORTIFICATION_TIME, time);
    }

    public static void lifedrainTick(LivingEntity entity, ItemStack stack) {
        if (entity.isSpectator()
                || !(stack.getItem() instanceof LichCrownItem relic)
                || !entity.isAlive()
        ) return;

        int maxTime = (int) Math.round(relic.getStatValue(stack, "lifedrain", "interval")) + MAX_LIFEDRAIN_TIME;
        int time = stack.getOrDefault(DataComponentRegistry.LIFEDRAIN_TIME, 0);
        float healAmount = (float) (entity.getMaxHealth() * relic.getStatValue(stack, "lifedrain", "heal_percentage"));
        DamageSource dmg = TFDamageTypes.getEntityDamageSource(entity.level(), TFDamageTypes.LIFEDRAIN, entity);

        if (time > maxTime - MAX_LIFEDRAIN_TIME && entity.getHealth() < entity.getMaxHealth() && entity.tickCount % 5 == 0) {
            List<LivingEntity> toAbsorb = EntitiesButCool.findEligibleEntities(entity, relic.getStatValue(stack, "lifedrain", "radius"),
                    e -> !EntityUtils.isAlliedTo(entity, e)
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
            if (!toAbsorb.isEmpty()) {
                entity.level().playSound(null, entity.blockPosition(), TFSounds.LIFE_SCEPTER_DRAIN.get(), SoundSource.PLAYERS);
                if (entity.tickCount % 10 == 0) {
                    relic.spreadRelicExperience(entity, stack, 1);
                }
            }
        }

        if (time <= 0) {
            if (entity.getHealth() / entity.getMaxHealth() <= 0.2f) {
                time = maxTime;
            }
        } else time--;
        stack.set(DataComponentRegistry.LIFEDRAIN_TIME, time);
    }

    public static void twilightTick(LivingEntity entity, ItemStack stack) {
        if (entity.isSpectator()
                || !(stack.getItem() instanceof LichCrownItem relic)
        ) return;

        int time = stack.getOrDefault(DataComponentRegistry.TWILIGHT_TIME, 0);
        if (time > 0) {
            time--;
        }
        stack.set(DataComponentRegistry.TWILIGHT_TIME, time);
    }

    public static void zombieTick(LivingEntity entity, ItemStack stack) {
        if (entity.isSpectator()
                || !(stack.getItem() instanceof LichCrownItem relic)
        ) return;

        int time = stack.getOrDefault(DataComponentRegistry.ZOMBIE_TIME, 0);
        ArrayList<UUID> uuids = Lists.newArrayList(stack.getOrDefault(DataComponentRegistry.ZOMBIES, List.of()));
        int maxZombies = (int) Math.round(relic.getStatValue(stack, "zombie", "max_zombies"));

        if (time <= 0 && uuids.size() < maxZombies) {
            time = (int) Math.round(relic.getStatValue(stack, "zombie", "interval"));

            LoyalZombie zombie = spawnZombie(entity, (float) relic.getStatValue(stack, "zombie", "damage"), entity.blockPosition());
            if (zombie != null) {
                relic.spreadRelicExperience(entity, stack, 1);
                uuids.add(zombie.getUUID());
            }
        }
        if (time > 0) time--;
        stack.set(DataComponentRegistry.ZOMBIE_TIME, time);

        if (entity.tickCount % 15 == 0) uuids.removeIf(uuid -> {
            Entity e = ((ServerLevel) entity.level()).getEntity(uuid);
            if (e == null) return true;
            boolean flag = !e.isAlive() || !((ServerLevel) entity.level()).isPositionEntityTicking(e.blockPosition());
            if (flag) e.discard();
            return flag;
        });
        stack.set(DataComponentRegistry.ZOMBIES, uuids);
    }

    @EventBusSubscriber
    public static class CommonEvents {
        @SubscribeEvent
        public static void processMirrorLeech(LivingDamageEvent.Pre e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.LICH_CROWN.get());

            if (e.getSource().getEntity() == e.getEntity()
                    || e.getEntity().level().isClientSide
                    || !(stack.getItem() instanceof LichCrownItem relic)
                    || !(relic.isAbilityUnlocked(stack, "mirror_leech"))
                    || e.getEntity().getRandom().nextFloat() > relic.getStatValue(stack, "mirror_leech", "chance")
            ) return;

            if (e.getSource().getEntity() instanceof LivingEntity source) {
                source.hurt(e.getSource(), e.getOriginalDamage());
            }

            e.getEntity().heal(e.getNewDamage());
            relic.spreadRelicExperience(e.getEntity(), stack, 1);
            e.getEntity().level().playSound(null, e.getEntity(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1f, 2f);
            e.setNewDamage(0);
        }

        @SubscribeEvent
        public static void phaseProjectiles(ProjectileImpactEvent e) {
            if (e.getEntity().level().isClientSide || !(e.getRayTraceResult() instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof LivingEntity entity)) {
                return;
            }

            ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.LICH_CROWN.get());
            if (!(stack.getItem() instanceof LichCrownItem relic)
                    || !(relic.isAbilityUnlocked(stack, "ethereal_guard"))
                    || entity.getRandom().nextFloat() > relic.getStatValue(stack, "ethereal_guard", "chance")
            ) return;

            entity.level().playSound(null, entity, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1f, 2f);
            relic.spreadRelicExperience(entity, stack, 1);
            e.setCanceled(true);
        }

        @SubscribeEvent
        public static void storeRevengeData(LivingDamageEvent.Post e) {
            LivingEntity victim = e.getEntity();
            ItemStack stack = EntityUtils.findEquippedCurio(victim, ItemRegistry.LICH_CROWN.get());

            if (!(e.getSource().getEntity() instanceof LivingEntity source)
                    || source == e.getEntity()
                    || victim.level().isClientSide
                    || !(stack.getItem() instanceof LichCrownItem relic)
                    || !(relic.isAbilityUnlocked(stack, "vendetta"))
            ) return;

            CompoundTag tag = source.getPersistentData().getCompound(VENDETTA_ID);
            tag.putInt(victim.getStringUUID(), (int) Math.round(relic.getStatValue(stack, "vendetta", "lifetime")));
            source.getPersistentData().put(VENDETTA_ID, tag);
        }

        @SubscribeEvent
        public static void tickRevenge(EntityTickEvent.Post e) {
            if (!e.getEntity().getPersistentData().contains(VENDETTA_ID)) {
                return;
            }

            CompoundTag tag = e.getEntity().getPersistentData().getCompound(VENDETTA_ID);
            Set<String> keys = tag.getAllKeys();
            if (keys.isEmpty()) {
                e.getEntity().getPersistentData().remove(VENDETTA_ID);
                return;
            }

            CompoundTag newTag = new CompoundTag();
            tag.getAllKeys().forEach(key -> {
                int ticks = tag.getInt(key);
                ticks--;
                if (ticks > 0) {
                    newTag.putInt(key, ticks);
                }
            });
            e.getEntity().getPersistentData().put(VENDETTA_ID, newTag);
        }

        @SubscribeEvent
        public static void multiplyRevengeDamage(LivingDamageEvent.Pre e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getSource().getEntity(), ItemRegistry.LICH_CROWN.get());

            if (!(e.getSource().getEntity() instanceof LivingEntity source)
                    || source == e.getEntity()
                    || source.level().isClientSide
                    || !(stack.getItem() instanceof LichCrownItem relic)
                    || !(relic.isAbilityUnlocked(stack, "vendetta"))
                    || !e.getEntity().getPersistentData().getCompound(VENDETTA_ID).contains(source.getStringUUID())
            ) return;

            float multiplier = (float) relic.getStatValue(stack, "vendetta", "multiplier");
            e.setNewDamage(e.getNewDamage() * (multiplier + 1));
        }

        @SubscribeEvent
        public static void multiplyBiomeDamage(LivingDamageEvent.Pre e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getSource().getEntity(), ItemRegistry.LICH_CROWN.get());

            if (!(e.getSource().getEntity() instanceof Player player)
                    || player == e.getEntity()
                    || player.level().isClientSide
                    || !(stack.getItem() instanceof LichCrownItem relic)
                    || !(relic.isAbilityUnlocked(stack, "biome_burn"))
            ) return;

            float multiplier = (float) relic.getStatValue(stack, "biome_burn", "multiplier");
            float temperature = player.level().getBiome(player.blockPosition()).value().getBaseTemperature();
            player.displayClientMessage(Component.literal("Temperature: " + temperature), false);
            if (temperature <= 0.5f) {
                return;
            }

            e.setNewDamage(e.getNewDamage() * (1 + multiplier * (temperature * 10 - 5f)));
        }

        @SubscribeEvent
        public static void applyFrostbite(LivingDamageEvent.Post e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getSource().getEntity(), ItemRegistry.LICH_CROWN.get());

            if (!(e.getSource().getEntity() instanceof Player player)
                    || player == e.getEntity()
                    || player.level().isClientSide
                    || !(stack.getItem() instanceof LichCrownItem relic)
                    || !(relic.isAbilityUnlocked(stack, "frostbite"))
            ) return;

            e.getEntity().setTicksFrozen(e.getEntity().getTicksFrozen() + (int) Math.round(relic.getStatValue(stack, "frostbite", "duration")));
            relic.spreadRelicExperience(player, stack, 1);
        }

        @SubscribeEvent
        public static void onTwilightBoltHit(LivingDamageEvent.Post e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getSource().getEntity(), ItemRegistry.LICH_CROWN.get());

            if (!(e.getSource().getDirectEntity() instanceof TwilightWandBolt bolt)
                    || !(e.getSource().getEntity() instanceof Player player)
                    || !(stack.getItem() instanceof LichCrownItem relic)
                    || !(bolt.getPersistentData().contains("reliquified_twilight_forest:isCustom"))
            ) return;

            relic.spreadRelicExperience(player, stack, 1);
        }
    }
    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void renderRevengeData(RenderLivingEvent.Post e) {

        }

        @SubscribeEvent
        public static void swingEvent(PlayerInteractEvent.LeftClickEmpty e) {
            Player player = e.getEntity();
            if (player.isSpectator()) {
                return;
            }

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
                    entity -> entity instanceof LivingEntity
                            && entity.isAlive()
                            && player.hasLineOfSight(entity)
                            && entity != player
                            && !EntityUtils.isAlliedTo(player, entity),
                    maxDistance * maxDistance);
        }

    }

    public static LoyalZombie spawnZombie(LivingEntity entity, float damage, BlockPos position) {
        Level level = entity.level();
        LoyalZombie zombie = new LoyalZombie(TFEntities.LOYAL_ZOMBIE.get(), level) {
            @Override
            public boolean doHurtTarget(Entity entity) {
                if (entity.hurt(this.damageSources().mobAttack(this), damage)) {
                    entity.push(0.0D, 0.2D, 0.0D);
                    return true;
                }
                return false;
            }

            @Override
            public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand hand) {
                return InteractionResult.PASS;
            }

            @Override
            public void aiStep() {
                if (!this.hasEffect(MobEffects.DAMAGE_BOOST)) {
                    if (!level.isClientSide) {
                        LifedrainScepterItem.animateTargetShatter((ServerLevel) level, this);
                    }

                    this.hurt(TFDamageTypes.getDamageSource(this.level(), TFDamageTypes.EXPIRED), Float.MAX_VALUE);
                    this.discard();
                }
                super.aiStep();
            }
        };

        zombie.moveTo(findSafeSpawn(position, (ServerLevel) level, 3).getBottomCenter());
        if (!level.noCollision(zombie, zombie.getBoundingBox())) {
            return null;
        }

        zombie.spawnAnim();
        zombie.setTame(true, false);
        zombie.setOwnerUUID(entity.getUUID());
        zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 0, true, false, false));
        zombie.setBaby(true);
        level.addFreshEntity(zombie);
        level.gameEvent(entity, GameEvent.ENTITY_PLACE, position);

        zombie.playSound(TFSounds.ZOMBIE_SCEPTER_USE.get(), 1.0F, 1.0F);
        zombie.setSilent(true);
        return zombie;
    }

    public static BlockPos findSafeSpawn(BlockPos position, ServerLevel world, int radius) {
        Random random = new Random();
        for (int attempts = 0; attempts < 10; attempts++) { // Try 10 times to find a valid location
            int xOffset = random.nextInt(radius * 2) - radius;
            int zOffset = random.nextInt(radius * 2) - radius;
            BlockPos potentialPos = position.offset(xOffset, 0, zOffset);

            // Find the highest solid ground
            BlockPos spawnPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, potentialPos);

            if (isSafe(spawnPos, world)) {
                return spawnPos;
            }
        }
        return position; // Fallback: spawn at the player's position
    }

    public static boolean isSafe(BlockPos pos, ServerLevel world) {
        return world.getBlockState(pos).isAir() && // Ensure the spawn spot is air
                world.getBlockState(pos.below()).isSolid() && // Ensure there's solid ground
                world.getBlockState(pos.below()).getBlock() != Blocks.LAVA; // Avoid lava pools
    }

    private static AbilityData register(AbilityData data, DeferredHolder<Item, GemItem> gem) {
        ABILITIES.put(data.getId(), data);
        GEMS.put(data.getId(), gem);
        return data;
    }
}
