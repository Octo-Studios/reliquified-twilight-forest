package it.hurts.octostudios.reliquified_twilight_forest.entity;

import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class HydraFirePuddleEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(HydraFirePuddleEntity.class, EntityDataSerializers.FLOAT);
    public static final ParticleOptions BORDER_PARTICLES = ParticleUtils.constructSimpleSpark(
            new Color(255, 150, 0, 150),
            0.25f,
            10,
            0.87f
    );
    public static final ParticleOptions INSIDE_PARTICLES = ParticleUtils.constructSimpleSpark(
            new Color(175, 65, 0, 75),
            0.4f,
            30,
            0.925f
    );

    @Getter
    @Setter
    private int lifetime = 200;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;

    public void setDamage(float damage) {
        this.getEntityData().set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.getEntityData().get(DAMAGE);
    }

    public HydraFirePuddleEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    public void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) {
            this.cachedOwner = serverlevel.getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        this.refreshDimensions();

        if (level().isClientSide) {
            float time = tickCount % 40 + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);

            for (int i = 0; i < 360; i += 20) {
                Vec3 direction = new Vec3(1, 0, 0).yRot((float) Math.toRadians(i + time * 2));
                Vec3 pos = position().add(direction.scale(getBbWidth() / 2f));

                Vec3 randomPos = new Vec3(getRandomX(0.5), position().y, getRandomZ(0.5));

                level().addParticle(BORDER_PARTICLES, pos.x, pos.y + 0.05, pos.z, 0, 0, 0);
                if (i % 40 == 0 && randomPos.distanceTo(position()) < getBbWidth() / 2f) {
                    level().addParticle(INSIDE_PARTICLES, randomPos.x, randomPos.y + 0.05, randomPos.z, 0, 0, 0);
                }
            }

            if (random.nextFloat() < 0.08f) {
                Vec3 randomPos = new Vec3(getRandomX(0.5), position().y, getRandomZ(0.5));

                if (randomPos.distanceTo(position()) < getBbWidth() / 2f) {
                    level().addParticle(ParticleTypes.LAVA, randomPos.x, randomPos.y+0.05, randomPos.z, 0.05, 0.05, 0.05);
                }
            }

            return;
        }

        if (this.getLifetime() <= 0) {
            this.discard();
            return;
        }
        //Damage entities
        List<LivingEntity> toHurt = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0,0.1,0)).stream().filter(entity ->
                !entity.equals(this.getOwner())
                        && !EntityUtils.isAlliedTo(this.getOwner(), entity)
                        && entity.isAlive()
        ).toList();

        toHurt.forEach(entity -> {
            entity.hurt(level().damageSources().source(DamageTypes.IN_FIRE, this.getOwner(), this), this.getDamage());
            entity.setRemainingFireTicks(100);
        });

        if (this.isAlive()) {
            int i = this.getLifetime();
            if (i > 0) {
                this.setLifetime(--i);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DAMAGE, 1F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setLifetime(compound.getInt("lifetime"));
        this.setDamage(compound.getFloat("damage"));
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
            this.cachedOwner = null;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("lifetime", this.getLifetime());
        compound.putFloat("damage", this.getDamage());
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(4.0f + Mth.sin(this.tickCount/10f)/4f, 0.3f);
    }
}
