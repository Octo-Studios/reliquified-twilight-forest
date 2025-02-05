package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import com.google.common.collect.Lists;
import it.hurts.octostudios.reliquified_twilight_forest.api.HurtByTargetGoalWithPredicate;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.GemTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.IGem;
import it.hurts.octostudios.reliquified_twilight_forest.item.ability.LichCrownAbilities;
import it.hurts.octostudios.reliquified_twilight_forest.mixin.NearestAttackableTargetGoalAccessor;
import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@EventBusSubscriber
public class LichCrownItem extends RelicItem {
    public LichCrownItem() {
        super((new Item.Properties()).rarity(Rarity.RARE).stacksTo(1).component(DataComponentRegistry.GEMS, List.of()));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("soulbound_gems")
                                .stat(StatData.builder("gem_amount")
                                        .initialValue(1, 3)
                                        .formatValue(Math::round)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .build())
                                .maxLevel(15)
                                .build())
                        .ability(LichCrownAbilities.ZOMBIE)
                        .ability(LichCrownAbilities.TWILIGHT)
                        .ability(LichCrownAbilities.LIFEDRAIN)
                        .ability(LichCrownAbilities.FORTIFICATION)
                        .ability(AbilityData.builder("bone_pact").maxLevel(0).build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .step(125)
                        .maxLevel(15)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("soulbound_gems")
                                        .gem(GemShape.OVAL, GemColor.PURPLE)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(DataComponentRegistry.GEMS)).map(list -> new GemTooltip(list, this.getSize(stack)))
                : Optional.empty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        LivingEntity livingEntity = slotContext.entity();
        if (livingEntity.level().isClientSide) return;

        if (relic.isAbilityUnlocked(stack, "fortification")) LichCrownAbilities.fortificationTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "lifedrain")) LichCrownAbilities.lifedrainTick(livingEntity, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        if (!(entity instanceof Player player) || player.level().isClientSide) return;
        relic.dropExcessive(player, stack);
    }


    @SubscribeEvent
    public static void onSlotClick(ContainerSlotClickEvent event) {
        if (event.getAction() != ClickAction.SECONDARY) return;

        ItemStack stack = event.getSlotStack();
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;

        ItemStack heldStack = event.getHeldStack();

        if (heldStack.getItem() instanceof IGem) {
            if (relic.tryInsert(stack, heldStack))
                event.getEntity().playSound(SoundEvents.AMETHYST_BLOCK_STEP, 1f, 1.25f);

            event.setCanceled(true);
        } else if (heldStack.isEmpty()) {
            event.setCanceled(true);
            ItemStack gem = relic.pop(stack);
            if (gem.isEmpty()) return;

            event.getEntity().playSound(SoundEvents.ITEM_PICKUP, 0.75f, 1.25f);
            event.getEntity().containerMenu.setCarried(gem);
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof AbstractSkeleton skeleton)) return;

        Predicate<LivingEntity> predicate = target -> EntityUtils.findEquippedCurio(target, ItemRegistry.LICH_CROWN.get()).isEmpty();

        skeleton.targetSelector.getAvailableGoals().removeIf(goal ->
                goal.getGoal() instanceof NearestAttackableTargetGoal<?> g
                        && ((NearestAttackableTargetGoalAccessor) g).getTargetType().isAssignableFrom(Player.class));
        skeleton.targetSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof HurtByTargetGoal);

        skeleton.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(skeleton, Player.class, true, predicate));
        skeleton.targetSelector.addGoal(1, new HurtByTargetGoalWithPredicate(
                skeleton,
                TargetingConditions
                        .forCombat()
                        .ignoreLineOfSight()
                        .ignoreInvisibilityTesting()
                        .selector(predicate)
        ));
    }

    public int getSize(ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "soulbound_gems", "gem_amount"));
    }

    public boolean tryInsert(ItemStack stack, ItemStack toInsert) {
        List<ItemStack> notMutable = stack.get(DataComponentRegistry.GEMS);
        if (notMutable == null) return false;

        ArrayList<ItemStack> gems = Lists.newArrayList(notMutable);
        int size = this.getSize(stack);

        if (gems.size() >= size) return false;
        gems.add(toInsert.copyAndClear());
        gems.removeIf(ItemStack::isEmpty);
        stack.set(DataComponentRegistry.GEMS, gems);
        return true;
    }

    public ItemStack pop(ItemStack stack) {
        List<ItemStack> notMutable = getGemContents(stack);
        if (notMutable.isEmpty()) return ItemStack.EMPTY;

        ArrayList<ItemStack> gems = Lists.newArrayList(notMutable);

        ItemStack toReturn = gems.removeLast();
        gems.removeIf(ItemStack::isEmpty);
        stack.set(DataComponentRegistry.GEMS, gems);
        return toReturn;
    }

    public void dropExcessive(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getGemContents(stack);

        ArrayList<ItemStack> gems = Lists.newArrayList(notMutable);
        int size = this.getSize(stack);

        if (gems.size() <= size) return;
        List<ItemStack> toDrop = gems.subList(size - 1, gems.size() - 1);

        for (ItemStack drop : toDrop) {
            ItemEntity entity = player.drop(drop, false, true);
            if (entity != null) entity.setNoPickUpDelay();
        }

        toDrop.clear();
        gems.removeIf(ItemStack::isEmpty);
        stack.set(DataComponentRegistry.GEMS, gems);
    }

    public @NotNull List<ItemStack> getGemContents(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.GEMS, List.of());
    }

    public int getGems(ItemStack stack, IGem gem) {
        List<ItemStack> notMutable = getGemContents(stack);
        return (int) notMutable.stream().filter(itemStack -> itemStack.getItem() == gem).count();
    }

    @Override
    public int getAbilityLevel(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification" -> getGems(stack, ItemRegistry.SHIELDING_GEM.get());
            case "zombie" -> getGems(stack, ItemRegistry.NECROMANCY_GEM.get());
            case "twilight" -> getGems(stack, ItemRegistry.TWILIGHT_GEM.get());
            case "lifedrain" -> getGems(stack, ItemRegistry.ABSORPTION_GEM.get());
            default -> super.getAbilityLevel(stack, ability);
        };
    }

    @Override
    public boolean mayUpgrade(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> false;
            default -> super.mayUpgrade(stack, ability);
        };
    }

    @Override
    public boolean mayReset(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> false;
            default -> super.mayReset(stack, ability);
        };
    }

    @Override
    public boolean isAbilityUnlocked(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> getAbilityLevel(stack, ability) > 0;
            default -> super.isAbilityUnlocked(stack, ability);
        };
    }

    @Override
    public boolean isRelicFlawless(ItemStack stack) {
        return isAbilityFlawless(stack, "soulbound_gems") && isAbilityMaxLevel(stack, "soulbound_gems") && getGemContents(stack).size() >= getSize(stack);
    }
}
