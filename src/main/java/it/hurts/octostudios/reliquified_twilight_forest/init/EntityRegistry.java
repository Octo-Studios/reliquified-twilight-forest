package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.entity.HydraFirePuddleEntity;
import it.hurts.octostudios.reliquified_twilight_forest.entity.projectile.HydraFireEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ReliquifiedTwilightForest.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<HydraFireEntity>> HYDRA_FIRE = ENTITIES.register("hydra_fire", () ->
            EntityType.Builder.of(HydraFireEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .build("hydra_fire")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<HydraFirePuddleEntity>> HYDRA_FIRE_PUDDLE = ENTITIES.register("hydra_fire_puddle", () ->
            EntityType.Builder.of(HydraFirePuddleEntity::new, MobCategory.MISC)
                    .build("hydra_fire_puddle")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
