package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.api.HurtByTargetGoalWithPredicate;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.mixin.NearestAttackableTargetGoalAccessor;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@EventBusSubscriber
public class LichCrownItem extends RelicItem {
    public LichCrownItem() {
        super((new Item.Properties()).rarity(Rarity.RARE).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, new BundleContents(List.of(Items.DIAMOND.getDefaultInstance(), Items.EMERALD.getDefaultInstance(), Items.BEACON.getDefaultInstance()))));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("bone_pact")
                                .maxLevel(0)
                                .build())
                        .ability(AbilityData.builder("soulbound_gems")
                                .requiredPoints(3)
                                .stat(StatData.builder("gem_amount")
                                        .initialValue(1, 3)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .build())
                                .maxLevel(10)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .step(125)
                        .maxLevel(30)
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
                ? Optional.ofNullable(stack.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new)
                : Optional.empty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;

        LivingEntity livingEntity = slotContext.entity();
        if (livingEntity.level().isClientSide) return;
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof AbstractSkeleton skeleton)) return;

        Predicate<LivingEntity> predicate = target -> {
            if (!(target instanceof Player player)) return false;
            return EntityUtils.findEquippedCurio(player, ItemRegistry.LICH_CROWN.get()).isEmpty();
        };

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
}
