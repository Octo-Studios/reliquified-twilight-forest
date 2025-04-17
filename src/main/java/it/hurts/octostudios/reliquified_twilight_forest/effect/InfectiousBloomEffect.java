package it.hurts.octostudios.reliquified_twilight_forest.effect;

import it.hurts.octostudios.reliquified_twilight_forest.init.DamageTypeRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.EffectRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.Parasite115Item;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import twilightforest.init.TFParticleType;

@EventBusSubscriber
public class InfectiousBloomEffect extends MobEffect {
    public InfectiousBloomEffect() {
        super(MobEffectCategory.HARMFUL, 0xffffff);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance effect) {
        return TFParticleType.FIREFLY.get();
    }

    @SubscribeEvent
    public static void effectTick(EntityTickEvent.Post e) {
        if (!(e.getEntity().level() instanceof ServerLevel serverLevel) || !(e.getEntity() instanceof LivingEntity livingEntity) || !livingEntity.hasEffect(EffectRegistry.INFECTIOUS_BLOOM)) {
            return;
        }

        int amplifier = livingEntity.getEffect(EffectRegistry.INFECTIOUS_BLOOM).getAmplifier() + 1;
        int duration = livingEntity.getEffect(EffectRegistry.INFECTIOUS_BLOOM).getDuration();

        float damage = (float) Math.sqrt(amplifier) + 1;
        if (duration < amplifier * 2 && livingEntity.tickCount % 2 == 0) {
            Entity source = livingEntity.getPersistentData().contains(Parasite115Item.LAST_INFECTED_BY)
                    ? serverLevel.getEntity(livingEntity.getPersistentData().getUUID(Parasite115Item.LAST_INFECTED_BY))
                    : livingEntity.getLastAttacker();

            livingEntity.invulnerableTime = 1;
            livingEntity.hurt(new DamageSource(livingEntity.level().registryAccess().holderOrThrow(DamageTypeRegistry.INFECTIOUS_BLOOM), source, source),
                    damage);
        }
    }
}
