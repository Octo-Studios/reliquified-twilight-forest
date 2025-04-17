package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeRegistry {
    public static final ResourceKey<DamageType> EXECUTION =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "execution"));
    public static final ResourceKey<DamageType> INFECTIOUS_BLOOM =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "infectious_bloom"));
}