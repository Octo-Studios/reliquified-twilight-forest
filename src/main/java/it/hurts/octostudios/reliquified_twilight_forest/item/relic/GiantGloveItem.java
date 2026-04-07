package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.client.event.RenderItemInHandEvent;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.EntitiesButCool;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.UUID;

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
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("oversized_grip")
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.TROLL)
                        .build())
                .build();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        if (newStack.getItem() != stack.getItem()) {
            removeAttributes(slotContext);
        }
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }

    @Mod.EventBusSubscriber(modid = ReliquifiedTwilightForest.MOD_ID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void entityHit(LivingDamageEvent e) {
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
        public static void playerTick(TickEvent.PlayerTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            Player living = e.player;
            if (living.level().isClientSide) return;

            List<SlotResult> slots = EntitiesButCool.findEquippedSlots(living, ItemRegistry.GIANT_GLOVE.get());

            slots.forEach(slotResult -> {
                ItemStack stack = slotResult.stack();
                if (!(stack.getItem() instanceof GiantGloveItem relic)) {
                    return;
                }

                ResourceLocation rl = new ResourceLocation(ReliquifiedTwilightForest.MOD_ID, "giant_glove_"+slotResult.slotContext().identifier());

                removeAttributes(slotResult.slotContext());

                float multiplier = (float) relic.getStatValue(stack, "oversized_grip", "multiplier");
                living.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((attribute, attributeModifier) -> {
                    AttributeInstance instance = living.getAttribute(attribute);
                    if (instance == null) {
                        return;
                    }

                    UUID modId = UUID.nameUUIDFromBytes(rl.toString().getBytes());
                    if (instance.getBaseValue() < 0) {
                        instance.addOrUpdateTransientModifier(new AttributeModifier(
                                modId,
                                rl.toString(),
                                Math.abs(instance.getValue() * multiplier),
                                AttributeModifier.Operation.ADDITION
                        ));
                        return;
                    }

                    instance.addOrUpdateTransientModifier(new AttributeModifier(
                            modId,
                            rl.toString(),
                            multiplier,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                });
            });
        }
    }

    private static void removeAttributes(SlotContext slotContext) {
        ResourceLocation rl = new ResourceLocation(ReliquifiedTwilightForest.MOD_ID, "giant_glove_"+slotContext.identifier());
        UUID modId = UUID.nameUUIDFromBytes(rl.toString().getBytes());

        for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {
            if (!slotContext.entity().getAttributes().hasAttribute(attribute)) {
                continue;
            }
            slotContext.entity().getAttributes().getInstance(attribute).removeModifier(modId);
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ReliquifiedTwilightForest.MOD_ID)
    public static class ClientEvents {
        @SubscribeEvent
        public static void renderItem(RenderItemInHandEvent e) {
            ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.GIANT_GLOVE.get());
            if (!(stack.getItem() instanceof GiantGloveItem relic)) {
                return;
            }

            float scale;
            if (e.getDisplayContext().firstPerson()) {
                scale = (float) (1.25d + relic.getStatValue(stack, "oversized_grip", "multiplier") * 1.8d);

                e.getPoseStack().scale(scale,scale,scale);
                e.getPoseStack().translate(0,-0.1,0);

                if (e.getItemStack().is(Tags.Items.TOOLS_SHIELD)) {
                    e.getPoseStack().translate(0.1 * (e.getDisplayContext() == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1), -0.2, 0);
                }
                return;
            }

            scale = (float) (2d + relic.getStatValue(stack, "oversized_grip", "multiplier") * 6d);
            e.getPoseStack().scale(scale,scale,scale);
            e.getPoseStack().translate(0,0,-0.075f);
        }
    }
}
