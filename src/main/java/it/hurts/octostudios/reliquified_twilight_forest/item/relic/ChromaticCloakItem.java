package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.ChromaticCloakTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.BundleLikeRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ChromaticCloakItem extends BundleLikeRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("wool_storage")
                                .stat(StatData.builder("max_slots")
                                        .initialValue(3, 6)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(StatData.builder("max_stack_size")
                                        .initialValue(5, 8)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(200, 280)
                                        .upgradeModifier(UpgradeOperation.ADD, 30)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("effect_stacking")
                                .stat(StatData.builder("max_amplifier")
                                        .initialValue(2, 4)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(Math::round)
                                        .build())
                                .requiredPoints(2)
                                .requiredLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("wool_storage")
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams((player, stack) -> {
                            float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                            Color color = Color.getHSBColor((player.tickCount+partialTick)/60f, .75f, 1f);

                            return BeamsData.builder()
                                    .startColor(color.getRGB())
                                    .endColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0).getRGB())
                                    .build();
                        })
                        .build())
                .build();
    }


    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(DataComponentRegistry.BUNDLE_LIKE_CONTENTS)).map(list -> new ChromaticCloakTooltip(list, this.getMaxSlots(stack)))
                : Optional.empty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player)
                || entity.level().isClientSide
        ) return;

        int maxAmplifier = this.isAbilityUnlocked(stack, "effect_stacking") ? (int) Math.round(this.getStatValue(stack, "effect_stacking", "max_amplifier")) : 1;
        Map<Holder<MobEffect>, Integer> toApply = new HashMap<>();

        List<ItemStack> contents = this.getContents(stack).stream().map(itemStack -> {
            Holder<MobEffect> effect = ItemRegistry.CHROMATIC_EFFECTS.get(itemStack.getItem());
            int durationOffset = ChromaticCloakItem.getEffectDurationOffset(effect);

            if (effect == null
                    || (durationOffset == 0 ? player.hasEffect(effect) :
                    (player.getEffect(effect) != null && player.getEffect(effect).getDuration() > durationOffset))
                    || toApply.getOrDefault(effect, 0) >= maxAmplifier
            ) return itemStack;

            toApply.merge(effect, 1, Integer::sum);
            itemStack.shrink(1);

            return itemStack;
        }).filter(itemStack -> !itemStack.isEmpty() && itemStack.getCount() > 0).toList();

        toApply.forEach((effect, amplifier) -> {
            int duration = (int) Math.round(this.getStatValue(stack, "wool_storage", "duration")) + ChromaticCloakItem.getEffectDurationOffset(effect);
            player.addEffect(new MobEffectInstance(effect, duration, amplifier - 1));
            this.spreadRelicExperience(player, stack, amplifier);
        });

        this.setContents(player, stack, contents);
    }

    private static int getEffectDurationOffset(Holder<MobEffect> effect) {
        if (effect == MobEffects.NIGHT_VISION) {
            return 210;
        } else if (effect == MobEffects.HEALTH_BOOST || effect == MobEffects.ABSORPTION) {
            return 10;
        }

        return 1;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(stack.getItem() instanceof ChromaticCloakItem relic)
                || !(entity instanceof Player player)
                || player.level().isClientSide
        ) return;

        relic.dropExcessive(player, stack);
    }

    @Override
    public int getMaxSlots(ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "wool_storage", "max_slots"));
    }

    @Override
    public int getMaxSlotStackSize(ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "wool_storage", "max_stack_size"));
    }

    @Override
    public void playInsertSound(Player player, ItemStack toInsert) {
        player.playSound(SoundEvents.WOOL_PLACE, 0.8f, 1.25f);
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> ItemRegistry.CHROMATIC_EFFECTS.containsKey(stack.getItem());
    }
}
