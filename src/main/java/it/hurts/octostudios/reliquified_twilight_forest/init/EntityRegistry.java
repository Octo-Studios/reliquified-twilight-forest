package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.entity.HydraFirePuddleEntity;
import it.hurts.octostudios.reliquified_twilight_forest.entity.projectile.HydraFireEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ReliquifiedTwilightForest.MOD_ID);

    public static final RegistryObject<EntityType<HydraFireEntity>> HYDRA_FIRE = ENTITIES.register("hydra_fire", () ->
            EntityType.Builder.of(HydraFireEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .build("hydra_fire")
    );

    public static final RegistryObject<EntityType<HydraFirePuddleEntity>> HYDRA_FIRE_PUDDLE = ENTITIES.register("hydra_fire_puddle", () ->
            EntityType.Builder.of(HydraFirePuddleEntity::new, MobCategory.MISC)
                    .build("hydra_fire_puddle")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
