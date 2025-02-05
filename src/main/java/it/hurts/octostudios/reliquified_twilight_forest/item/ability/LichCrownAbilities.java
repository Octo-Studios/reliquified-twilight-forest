package it.hurts.octostudios.reliquified_twilight_forest.item.ability;

import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.EntitiesButCool;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import twilightforest.components.entity.FortificationShieldAttachment;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFDataAttachments;

import java.util.List;

public class LichCrownAbilities {
    public static final int MAX_LIFEDRAIN_TIME = 120;

    public static final AbilityData FORTIFICATION = AbilityData.builder("fortification")
            .stat(StatData.builder("max_shields")
                    .initialValue(3, 5)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(140, 180)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.125f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static final AbilityData ZOMBIE = AbilityData.builder("zombie")
            .stat(StatData.builder("max_zombies")
                    .initialValue(3, 5)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("damage")
                    .initialValue(1, 2)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(200, 260)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.125f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static final AbilityData TWILIGHT = AbilityData.builder("twilight")
            .stat(StatData.builder("damage")
                    .initialValue(3, 4)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(80, 120)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.1f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static final AbilityData LIFEDRAIN = AbilityData.builder("lifedrain")
            .stat(StatData.builder("heal_percentage")
                    .initialValue(0.01, 0.02)
                    .upgradeModifier(UpgradeOperation.ADD, 0.0075)
                    .formatValue(value -> MathButCool.percentage(value) * 2)
                    .build())
            .stat(StatData.builder("radius")
                    .initialValue(3, 6)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(140, 180)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.1f)
                    .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static void fortificationTick(LivingEntity entity, ItemStack stack) {
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        FortificationShieldAttachment attachment = entity.getData(TFDataAttachments.FORTIFICATION_SHIELDS);
        int maxTime = (int) Math.round(relic.getStatValue(stack, "fortification", "interval"));
        int time = stack.getOrDefault(DataComponentRegistry.FORTIFICATION_TIME, 0);

        if (attachment.permanentShieldsLeft() < relic.getStatValue(stack, "fortification", "max_shields")) {
            if (time >= maxTime) {
                attachment.addShields(entity, 1, false);
                time = 0;
            } else time += 1;
        }

        stack.set(DataComponentRegistry.FORTIFICATION_TIME, time);
    }

    public static void lifedrainTick(LivingEntity entity, ItemStack stack) {
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        int maxTime = (int) Math.round(relic.getStatValue(stack, "lifedrain", "interval")) + MAX_LIFEDRAIN_TIME;
        int time = stack.getOrDefault(DataComponentRegistry.LIFEDRAIN_TIME, 0);
        float healAmount = (float) (entity.getMaxHealth() * relic.getStatValue(stack, "lifedrain", "heal_percentage"));
        DamageSource dmg = TFDamageTypes.getEntityDamageSource(entity.level(), TFDamageTypes.LIFEDRAIN, entity);

        if (time > maxTime - MAX_LIFEDRAIN_TIME && entity.getHealth() < entity.getMaxHealth()) {
            List<LivingEntity> toAbsorb = EntitiesButCool.findEligibleEntities(entity, relic.getStatValue(stack, "lifedrain", "radius"), e ->
                    !EntityUtils.isAlliedTo(entity, e)
                            && e.tickCount % 10 == 0
                            && e.isAlive()
                            && entity.hasLineOfSight(e)
            );
            toAbsorb.forEach(e -> {
                e.invulnerableTime = 0;
                if (!e.hurt(dmg, healAmount)) return;

                entity.heal(healAmount);
                if (e.getMaxHealth() <= entity.getMaxHealth()) e.setDeltaMovement(0, 0.15D, 0);
                else e.setDeltaMovement(0, 0, 0);
            });
        }

        if (time <= 0) {
            if (entity.getHealth() / entity.getMaxHealth() <= 0.2f) time = maxTime;
        } else time--;
        stack.set(DataComponentRegistry.LIFEDRAIN_TIME, time);
    }
}
