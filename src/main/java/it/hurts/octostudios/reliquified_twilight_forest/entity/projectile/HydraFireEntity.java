package it.hurts.octostudios.reliquified_twilight_forest.entity.projectile;

import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailProvider;
import it.hurts.octostudios.reliquified_twilight_forest.entity.HydraFirePuddleEntity;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTEntities;
import it.hurts.octostudios.reliquified_twilight_forest.items.relics.HydraHeartItem;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;

public class HydraFireEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<ItemStack> RELIC_STACK = SynchedEntityData.defineId(HydraFireEntity.class, EntityDataSerializers.ITEM_STACK);
    @Getter
    @Setter
    private int age = 200;

    public void setRelicStack(ItemStack stack) {
        this.getEntityData().set(RELIC_STACK, stack);
    }

    public ItemStack getRelicStack() {
        return this.getEntityData().get(RELIC_STACK);
    }

    public HydraFireEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = getCommandSenderWorld();
        Vec3 particleCenter = this.position().add(this.getDeltaMovement().scale(-1F));

        if (level.isClientSide) {
            for (int i = 0; i < 3; i++) {
                level.addParticle(ParticleUtils.constructSimpleSpark(
                                new Color(
                                        155 + random.nextInt(100),
                                        50 + random.nextInt(100),
                                        0
                                ),
                                0.01F + random.nextFloat() * Math.min(tickCount * 0.01F, 0.1F),
                                5 + random.nextInt(3),
                                0.9F
                        ),
                        particleCenter.x() + MathUtils.randomFloat(random) * 0.05F,
                        particleCenter.y() + MathUtils.randomFloat(random) * 0.05F,
                        particleCenter.z() + MathUtils.randomFloat(random) * 0.05F,
                        0F, 0F, 0F
                );
            }

            return;
        }

        if (this.getAge() <= 0) {
            this.discard();
        }

        if (this.isAlive()) {
            int i = this.getAge();
            if (i < 0) {
                this.setAge(++i);
            } else if (i > 0) {
                this.setAge(--i);
            }
        }

    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();

        Level level = getCommandSenderWorld();
        Vec3 vec = this.position();

        level.playSound(null, this.blockPosition(), SoundEvents.PUFFER_FISH_BLOW_UP, SoundSource.MASTER, 0.5F, 1.5F + random.nextFloat() * 0.5F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) {
            return;
        }

        if (result instanceof BlockHitResult block) {
            this.discard();

            ItemStack stack = getRelicStack();
            int lifetime = 100;
            float damage = 1f;
            if (stack.getItem() instanceof HydraHeartItem relic && this.getOwner() instanceof Player player) {
                lifetime = (int) Math.round(relic.getRelicData(null, stack).getAbilitiesData().getAbilityData("hydra_fire").getStatData("lifetime").getValue());
                damage = (float) relic.getRelicData(null, stack).getAbilitiesData().getAbilityData("hydra_fire").getStatData("damage").getValue();
                relic.getRelicData(player, stack).getLevelingData().addExperience("hydra_fire", "blood_droplet", 1D);
            }

            HydraFirePuddleEntity puddle = new HydraFirePuddleEntity(RTEntities.HYDRA_FIRE_PUDDLE.get(), level());
            puddle.setPos(block.getLocation());
            puddle.setLifetime(lifetime);
            puddle.setDamage(damage);
            puddle.setOwner(this.getOwner());
            level().addFreshEntity(puddle);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RELIC_STACK, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.put("relic_stack", getRelicStack().save(this.registryAccess()));
        tag.putInt("Age", this.getAge());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        setRelicStack(ItemStack.parseOptional(this.registryAccess(), tag.getCompound("relic_stack")));
        setAge(tag.getInt("Age"));
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static class TrailProvider extends EntityTrailProvider<HydraFireEntity> {
        public TrailProvider(HydraFireEntity entity) {
            super(entity);
        }

        @Override
        public Vec3 getTrailPosition(float partialTicks) {
            return entity.getPosition(partialTicks);
        }

        @Override
        public int getTrailUpdateFrequency() {
            return 1;
        }

        @Override
        public boolean isTrailAlive() {
            return entity.isAlive();
        }

        @Override
        public boolean isTrailGrowing() {
            return entity.tickCount > 1;
        }

        @Override
        public int getTrailMaxLength() {
            return 3;
        }

        @Override
        public int getTrailFadeInColor() {
            return 0xFFFF7700;
        }

        @Override
        public int getTrailFadeOutColor() {
            return 0x00FF0000;
        }

        @Override
        public double getTrailScale() {
            return 0.075F;
        }
    }
}