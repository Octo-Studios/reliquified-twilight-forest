package it.hurts.octostudios.reliquified_twilight_forest.item.ability;

import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;

public class LichCrownAbilities {
    public static final AbilityData FORTIFICATION = AbilityData.builder("fortification")
            .stat(StatData.builder("max_shields")
                    .initialValue(3, 5)
                    .upgradeModifier(UpgradeOperation.ADD, 1)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(4, 6)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.66f)
                    .formatValue(MathButCool::roundSingleDigit)
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
                    .initialValue(4, 6)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.66f)
                    .formatValue(MathButCool::roundSingleDigit)
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
                    .initialValue(4, 6)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.66f)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();

    public static final AbilityData LIFEDRAIN = AbilityData.builder("lifedrain")
            .stat(StatData.builder("heal_percentage")
                    .initialValue(0.03, 0.04)
                    .upgradeModifier(UpgradeOperation.ADD, 0.02)
                    .formatValue(MathButCool::percentage)
                    .build())
            .stat(StatData.builder("radius")
                    .initialValue(3, 6)
                    .upgradeModifier(UpgradeOperation.ADD, 2)
                    .formatValue(Math::round)
                    .build())
            .stat(StatData.builder("interval")
                    .initialValue(4, 6)
                    .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, -0.66f)
                    .formatValue(MathButCool::roundSingleDigit)
                    .build())
            .maxLevel(5)
            .build();
}
