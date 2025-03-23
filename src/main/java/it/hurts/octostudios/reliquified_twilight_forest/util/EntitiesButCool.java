package it.hurts.octostudios.reliquified_twilight_forest.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.function.Predicate;

public class EntitiesButCool {
    public static List<LivingEntity> findEligibleEntities(LivingEntity entity, double radius, Predicate<LivingEntity> predicate) {
        return entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(entity.position(), entity.position()).inflate(radius), e ->
                entity.position().distanceTo(e.position()) <= radius && predicate.test(e)
        );
    }

    public static List<SlotResult> findEquippedSlots(Entity entity, Item item) {
        if (entity instanceof LivingEntity living) {
            return CuriosApi.getCuriosInventory(living).map((inventory) -> inventory.findCurios(item).stream().toList()).orElse(List.of());
        } else {
            return List.of();
        }
    }
}
