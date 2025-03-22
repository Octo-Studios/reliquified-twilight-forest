package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.client.event.RenderItemInHandEvent;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

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
                .build();
    }

    @Override
    public @Nullable RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        float multiplier = (float) this.getStatValue(stack, "oversized_grip", "multiplier");
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_INTERACTION_RANGE, 2.5F, AttributeModifier.Operation.ADD_VALUE))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ENTITY_INTERACTION_RANGE, 2.5F, AttributeModifier.Operation.ADD_VALUE))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_INTERACTION_RANGE, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ENTITY_INTERACTION_RANGE, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_BREAK_SPEED, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.SWEEPING_DAMAGE_RATIO, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.MINING_EFFICIENCY, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_DAMAGE, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_KNOCKBACK, multiplier))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ATTACK_SPEED, -multiplier))
                .build();
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
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
