package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.effect.CicadaInfestationEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ReliquifiedTwilightForest.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> CICADA_INFESTATION = EFFECTS.register("cicada_infestation", CicadaInfestationEffect::new);
    //public static final DeferredHolder<MobEffect, MobEffect> INFECTIOUS_BLOOM = EFFECTS.register("infectious_bloom", InfectiousBloomEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
