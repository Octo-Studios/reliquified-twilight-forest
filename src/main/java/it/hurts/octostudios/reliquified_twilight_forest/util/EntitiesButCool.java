package it.hurts.octostudios.reliquified_twilight_forest.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EntitiesButCool {
    public static List<LivingEntity> findEligibleEntities(LivingEntity entity, double radius, Predicate<LivingEntity> predicate) {
        return entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(entity.position(), entity.position()).inflate(radius), e ->
                entity.position().distanceTo(e.position()) <= radius && predicate.test(e)
        );
    }

    public static List<SlotResult> findEquippedSlots(LivingEntity entity, Item item) {
        Optional<ICuriosItemHandler> handler = CuriosApi.getCuriosInventory(entity);
        return handler.map(iCuriosItemHandler -> iCuriosItemHandler.findCurios(item).stream().toList()).orElseGet(List::of);
    }

    public static Optional<ImmutableTriple<String, Integer, ItemStack>> findEquippedCurio(Class<? extends Item> itemClass, LivingEntity livingEntity) {
        return CuriosApi.getCuriosHelper().findEquippedCurio(stack -> stack.getItem().getClass().isAssignableFrom(itemClass), livingEntity);
    }

    public static ItemStack findEquippedStack(Class<? extends Item> itemClass, LivingEntity livingEntity) {
        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = findEquippedCurio(itemClass, livingEntity);
        return optional.isEmpty() ? ItemStack.EMPTY : optional.get().getRight();
    }
}
