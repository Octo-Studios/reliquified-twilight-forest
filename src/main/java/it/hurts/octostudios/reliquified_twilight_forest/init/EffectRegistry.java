package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.effect.CicadaInfestationEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ReliquifiedTwilightForest.MOD_ID);

    public static final RegistryObject<MobEffect> CICADA_INFESTATION = EFFECTS.register("cicada_infestation", CicadaInfestationEffect::new);
    //public static final RegistryObject<MobEffect> INFECTIOUS_BLOOM = EFFECTS.register("infectious_bloom", InfectiousBloomEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
