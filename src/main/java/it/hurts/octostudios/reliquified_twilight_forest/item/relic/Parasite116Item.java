package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
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
                        .ability(Parasite115Item.getInfectiousBloom(2, 10))
                        .ability(AbilityData.builder("rage_consumption")
                                .stat(StatData.builder("amount_restored")
                                        .initialValue(50, 100)
                                        .upgradeModifier(UpgradeOperation.ADD, 20)
                                        .formatValue(value -> value / 2f)
                                        .build())
                                .stat(StatData.builder("multiplier")
                                        .initialValue(0.4, 0.75)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.25)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(15)
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
