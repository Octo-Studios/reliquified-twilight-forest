package it.hurts.octostudios.reliquified_twilight_forest.items.relics.back;

import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.ChromaticCloakTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTDataComponent;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTBundleLikeRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ChromaticCloakItem extends RTBundleLikeRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("wool_storage")
                                .stat(AbilityStatTemplate.builder("max_slots")
                                        .initialValue(3, 6).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 1)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_stack_size")
                                        .initialValue(5, 8)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.5)
                                        .formatValue(Math::round)
                                        .build())
                                .stat(AbilityStatTemplate.builder("duration")
                                        .initialValue(200, 280).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 30)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("effect_stacked")
                                        .build())
                                .build())
                        .ability(AbilityTemplate.builder("effect_stacking")
                                .stat(AbilityStatTemplate.builder("max_amplifier")
                                        .initialValue(2, 4).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 1)
                                        .formatValue(Math::round)
                                        .build())
                                .requiredPoints(2)
                                .requiredLevel(5)
                                .build())
                        .build())
                .build();
    }


    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(RTDataComponent.BUNDLE_LIKE_CONTENTS)).map(list -> new ChromaticCloakTooltip(list, this.getMaxSlots(stack)))
                : Optional.empty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player)
                || entity.level().isClientSide
        ) return;

        int maxAmplifier = this.getRelicData(entity, stack).getAbilitiesData().getAbilityData("effect_stacking").getMode().equals("enabled")
                ? (int) Math.round(this.getRelicData(entity, stack).getAbilitiesData().getAbilityData("effect_stacking").getStatData("max_amplifier").getValue()) : 1;
        Map<Holder<MobEffect>, Integer> toApply = new HashMap<>();

        List<ItemStack> contents = this.getContents(stack).stream().map(itemStack -> {
            Holder<MobEffect> effect = RTItems.CHROMATIC_EFFECTS.get(itemStack.getItem());
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
            int duration = (int) Math.round(this.getRelicData(player, stack).getAbilitiesData().getAbilityData("wool_storage").getStatData("duration").getValue() + ChromaticCloakItem.getEffectDurationOffset(effect));
            player.addEffect(new MobEffectInstance(effect, duration, amplifier - 1));
            this.getRelicData(player, stack).getLevelingData().addExperience("wool_storage", "effect_stacked", amplifier);
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
        return (int) Math.round(this.getRelicData(null, stack).getAbilitiesData().getAbilityData("wool_storage").getStatData("max_slots").getValue());
    }

    @Override
    public int getMaxSlotStackSize(ItemStack stack) {
        return (int) Math.round(this.getRelicData(null, stack).getAbilitiesData().getAbilityData("wool_storage").getStatData("max_stack_size").getValue());
    }

    @Override
    public void playInsertSound(Player player, ItemStack toInsert) {
        player.playSound(SoundEvents.WOOL_PLACE, 0.8f, 1.25f);
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> RTItems.CHROMATIC_EFFECTS.containsKey(stack.getItem());
    }
}
