package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.client.event.RenderItemInHandEvent;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.EntitiesButCool;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

public class GiantGloveItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("oversized_grip")
                                .stat(StatData.builder("multiplier")
                                        .initialValue(0.05, 0.15)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.035)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.TROLL)
                        .build())
                .build();
    }

    @Override
    public @Nullable RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        float multiplier = (float) this.getStatValue(stack, "oversized_grip", "multiplier");
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_INTERACTION_RANGE, 2.5F*(1+multiplier), AttributeModifier.Operation.ADD_VALUE))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ENTITY_INTERACTION_RANGE, 2.5F*(1+multiplier), AttributeModifier.Operation.ADD_VALUE))
                .build();
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }

    @EventBusSubscriber
    public static class CommonEvents {
        @SubscribeEvent
        public static void entityHit(LivingDamageEvent.Post e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getSource().getDirectEntity(), ItemRegistry.GIANT_GLOVE.get());
            if (e.getEntity().level().isClientSide
                    || !(stack.getItem() instanceof GiantGloveItem relic)
                    || !(e.getSource().getDirectEntity() instanceof LivingEntity source)
                    || source.getMainHandItem().isEmpty()
            ) return;

            relic.spreadRelicExperience(source, stack, 1);
        }

        @SubscribeEvent
        public static void blockBreak(BlockEvent.BreakEvent e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getPlayer(), ItemRegistry.GIANT_GLOVE.get());
            if (e.getPlayer().level().isClientSide
                    || !(stack.getItem() instanceof GiantGloveItem relic)
                    || e.getState().getDestroySpeed(e.getLevel(), e.getPos()) <= 0
                    || e.getPlayer().getMainHandItem().isEmpty()
            ) return;

            relic.spreadRelicExperience(e.getPlayer(), stack, 1);
        }

        @SubscribeEvent
        public static void playerTick(EntityTickEvent.Post e) {
            List<SlotResult> slots = EntitiesButCool.findEquippedSlots(e.getEntity(), ItemRegistry.GIANT_GLOVE.get());
            if (!(e.getEntity() instanceof LivingEntity living)
                    || living.level().isClientSide
            ) return;

            slots.forEach(slotResult -> {
                ItemStack stack = slotResult.stack();
                if (!(stack.getItem() instanceof GiantGloveItem relic)) {
                    return;
                }

                ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MOD_ID, "giant_glove_"+slotResult.slotContext().identifier());

                BuiltInRegistries.ATTRIBUTE.asHolderIdMap().iterator().forEachRemaining(holder -> {
                    if (!living.getAttributes().hasAttribute(holder)) {
                        return;
                    }

                    living.getAttributes().getInstance(holder).removeModifier(rl);
                });

                float multiplier = (float) relic.getStatValue(stack, "oversized_grip", "multiplier");
                living.getMainHandItem().getAttributeModifiers().forEach(EquipmentSlotGroup.MAINHAND, (attributeHolder, attributeModifier) -> {
                    if (!(living.getAttribute(attributeHolder) instanceof AttributeInstance instance)) {
                        return;
                    }

                    if (instance.getBaseValue() < 0) {
                        instance.addOrUpdateTransientModifier(new AttributeModifier(
                                rl,
                                Math.abs(instance.getValue() * multiplier),
                                AttributeModifier.Operation.ADD_VALUE
                        ));;
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

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void renderItem(RenderItemInHandEvent e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.GIANT_GLOVE.get());
            if (!(stack.getItem() instanceof GiantGloveItem relic)) {
                return;
            }

            float scale;
            if (e.getDisplayContext().firstPerson()) {
                scale = (float) (1.25d + relic.getStatValue(stack, "oversized_grip", "multiplier") * 2d);

                e.getPoseStack().scale(scale,scale,scale);
                return;
            }

            scale = (float) (2d + relic.getStatValue(stack, "oversized_grip", "multiplier") * 8d);
            e.getPoseStack().scale(scale,scale,scale);
            e.getPoseStack().translate(0,0,-0.075f);
        }
    }
}
