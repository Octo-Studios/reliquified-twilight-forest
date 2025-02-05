package it.hurts.octostudios.reliquified_twilight_forest.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class EntitiesButCool {
    public static List<LivingEntity> findEligibleEntities(LivingEntity entity, double radius, Predicate<LivingEntity> predicate) {
        return entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(entity.position(), entity.position()).inflate(radius), e ->
                entity.position().distanceTo(e.position()) <= radius && predicate.test(e)
        );
    }
}
