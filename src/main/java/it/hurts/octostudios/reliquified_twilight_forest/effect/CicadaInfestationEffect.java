package it.hurts.octostudios.reliquified_twilight_forest.effect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import twilightforest.init.TFParticleType;

public class CicadaInfestationEffect extends MobEffect {
    public CicadaInfestationEffect() {
        super(MobEffectCategory.HARMFUL, 0xffffff);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance effect) {
        return TFParticleType.FIREFLY.get();
    }
}
