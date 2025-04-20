package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.Color;

public class Parasite116Item extends Parasite115Item {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("infectious_bloom")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.1, 0.15)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.4)
                                        .formatValue(MathButCool::percentageAndRoundSingleDigit)
                                        .build())
                                .stat(StatData.builder("max_attacks")
                                        .initialValue(1, 3)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(0.75, 1.25)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.54)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .stat(StatData.builder("drops")
                                        .initialValue(2, 5)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(Math::round)
                                        .build())
                                .maxLevel(10)
                                .build())
                        .ability(AbilityData.builder("rage_consumption")
                                .stat(StatData.builder("amount_restored")
                                        .initialValue(50, 100)
                                        .upgradeModifier(UpgradeOperation.ADD, 20)
                                        .formatValue(value -> MathUtils.round(value / 2f, 1))
                                        .build())
                                .stat(StatData.builder("multiplier")
                                        .initialValue(0.4, 0.75)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.25)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .maxLevel(5)
                                .requiredLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("infectious_bloom")
                                        .gem(GemShape.SQUARE, GemColor.YELLOW)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("rage_consumption")
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
                                        .build())
                                .build())
                        .maxLevel(10)
                        .build())
                .style(StyleData.builder()
                        .beams((player, stack) -> {
                            float ticks = player.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                            float lerp = (float) (Math.sin(ticks/5f)/2f+0.5f);

                            float r = 1f;
                            float g = Mth.lerp(lerp, 127, 233) / 255f;
                            float b = Mth.lerp(lerp, 39, 0) / 255f;

                            return BeamsData.builder()
                                    .startColor(new Color(r,g,b,1).getRGB())
                                    .endColor(new Color(r,g,b,0).getRGB())
                                    .build();
                        })
                        .build())
                .build();
    }

    @Override
    public @Nullable RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        float percentage = stack.getOrDefault(DataComponentRegistry.TIME, 0) / 200f;
        float multiplier = (float) this.getStatValue(stack, "rage_consumption", "multiplier") * percentage;
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_DAMAGE, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_SPEED, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.MOVEMENT_SPEED, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.STEP_HEIGHT, percentage*1.5f, AttributeModifier.Operation.ADD_VALUE))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        int time = stack.getOrDefault(DataComponentRegistry.TIME, 0);
        if (slotContext.entity().level().isClientSide
                || time <= 0
        ) return;

        time--;
        stack.set(DataComponentRegistry.TIME, time);
    }

    @Override
    public void evolve(String identifier, int index, ItemStack stack, LivingEntity entity) {
        super.evolve(identifier, index, stack, entity);
    }

    @Override
    public boolean isEvolved() {
        return true;
    }
}
