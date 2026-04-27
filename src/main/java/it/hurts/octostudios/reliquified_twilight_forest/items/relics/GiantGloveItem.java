package it.hurts.octostudios.reliquified_twilight_forest.items.relics;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.EntitiesButCool;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.Objects;

public class GiantGloveItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("oversized_grip")
                                .stat(AbilityStatTemplate.builder("multiplier")
                                        .initialValue(0.05, 0.15).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.035)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("entity_hit")
                                        .source("block_broken")
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(LootEntries.TROLL)
                        .build())
                .build();
    }

/*  Figure out new way to change Interaction range

    @Override
    public @Nullable RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        float multiplier = (float) this.getStatValue(stack, "oversized_grip", "multiplier");
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_INTERACTION_RANGE, 2.5F*(1+multiplier), AttributeModifier.Operation.ADD_VALUE))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ENTITY_INTERACTION_RANGE, 2.5F*(1+multiplier), AttributeModifier.Operation.ADD_VALUE))
                .build();
    }
*/
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        if (newStack.getItem() != stack.getItem()) {
            removeAttributes(slotContext);
        }
    }

    @EventBusSubscriber
    public static class CommonEvents {
        @SubscribeEvent
        public static void entityHit(LivingDamageEvent.Post event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getSource().getDirectEntity(), RTItems.GIANT_GLOVE.get());
            if (event.getEntity().level().isClientSide
                    || !(stack.getItem() instanceof GiantGloveItem relic)
                    || !(event.getSource().getDirectEntity() instanceof LivingEntity source)
                    || source.getMainHandItem().isEmpty()
            ) return;

            relic.getRelicData(null, stack).getLevelingData().addExperience("oversized_grip", "entity_hit", 1D);
        }

        @SubscribeEvent
        public static void blockBreak(BlockEvent.BreakEvent e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getPlayer(), RTItems.GIANT_GLOVE.get());
            if (e.getPlayer().level().isClientSide
                    || !(stack.getItem() instanceof GiantGloveItem relic)
                    || e.getState().getDestroySpeed(e.getLevel(), e.getPos()) <= 0
                    || e.getPlayer().getMainHandItem().isEmpty()
            ) return;

            relic.getRelicData(null, stack).getLevelingData().addExperience("oversized_grip", "block_broken", 1D);
        }

        @SubscribeEvent
        public static void playerTick(EntityTickEvent.Post e) {
            if (!(e.getEntity() instanceof Player living)
                    || living.level().isClientSide
            ) return;
            List<SlotResult> slots = EntitiesButCool.findEquippedSlots(living, RTItems.GIANT_GLOVE.get());

            slots.forEach(slotResult -> {
                ItemStack stack = slotResult.stack();
                if (!(stack.getItem() instanceof GiantGloveItem relic)) {
                    return;
                }

                ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "giant_glove_"+slotResult.slotContext().identifier());

                removeAttributes(slotResult.slotContext());

                float multiplier = (float) relic.getRelicData(null, stack).getAbilitiesData().getAbilityData("oversized_grip").getStatData("multiplier").getValue();
                living.getMainHandItem().getAttributeModifiers().forEach(EquipmentSlotGroup.MAINHAND, (attributeHolder, attributeModifier) -> {
                    if (!(living.getAttribute(attributeHolder) instanceof AttributeInstance instance)) {
                        return;
                    }

                    if (instance.getBaseValue() < 0) {
                        instance.addOrUpdateTransientModifier(new AttributeModifier(
                                rl,
                                Math.abs(instance.getValue() * multiplier),
                                AttributeModifier.Operation.ADD_VALUE
                        ));
                        return;
                    }

                    instance.addOrUpdateTransientModifier(new AttributeModifier(
                            rl,
                            multiplier,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ));
                });
            });
        }
    }

    private static void removeAttributes(SlotContext slotContext) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "giant_glove_"+slotContext.identifier());

        BuiltInRegistries.ATTRIBUTE.asHolderIdMap().iterator().forEachRemaining(holder -> {
            if (!slotContext.entity().getAttributes().hasAttribute(holder)) {
                return;
            }

            Objects.requireNonNull(slotContext.entity().getAttributes().getInstance(holder)).removeModifier(rl);
        });
    }
}
