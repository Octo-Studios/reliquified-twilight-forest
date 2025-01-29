package it.hurts.octostudios.reliquified_twilight_forest.items.relics;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

@EventBusSubscriber
public class MinotaurHoofItem extends RelicItem {
    private static final ResourceLocation MOVEMENT_MODIFIER = ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "momentum_rush");

    private static final int MAX_TIME = 60;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("momentum_rush")
                                .stat(StatData.builder("max_speed_multiplier")
                                        .initialValue(0.25f, 0.3f)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .upgradeModifier(UpgradeOperation.ADD, 0.1f)
                                        .build())
                                .stat(StatData.builder("damage_reduction")
                                        .initialValue(0.25f, 0.3f)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.125f)
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(1, 2)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5f)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .step(125)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("momentum_rush")
                                        .gem(GemShape.SQUARE, GemColor.PURPLE)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof MinotaurHoofItem relic)) return;

        LivingEntity livingEntity = slotContext.entity();
        if (livingEntity.level().isClientSide) return;

        AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackResistance = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);

        if (movementSpeed == null || knockbackResistance == null || stepHeight == null) return;
        double maxSpeedMultiplier = relic.getStatValue(stack, "momentum_rush", "max_speed_multiplier");

        int time = relic.getTime(stack);
        stack.set(DataComponentRegistry.TIME, Mth.clamp(time + (livingEntity.isSprinting() ? 1 : -1), 0, 60));
        if (time == 0) return;

        movementSpeed.addOrUpdateTransientModifier(
                new AttributeModifier(
                        MOVEMENT_MODIFIER,
                        (float) maxSpeedMultiplier * (float) time / (float) MAX_TIME,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );
        knockbackResistance.addOrUpdateTransientModifier(
                new AttributeModifier(
                        MOVEMENT_MODIFIER,
                        (float) time / (float) MAX_TIME,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
        stepHeight.addOrUpdateTransientModifier(
                new AttributeModifier(
                        MOVEMENT_MODIFIER,
                        isActive(stack) ? 0.55 : 0,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
    }

    @SubscribeEvent
    public static void onDamageTaken(LivingIncomingDamageEvent e) {
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.MINOTAUR_HOOF.get());
        if (!(stack.getItem() instanceof MinotaurHoofItem relic)) return;
        double reducedDamage = e.getAmount() * Mth.clamp(1 - relic.getStatValue(stack, "momentum_rush", "damage_reduction"), 0, 1);
        //e.getEntity().sendSystemMessage(Component.literal(e.getAmount()+" : "+reducedDamage));

        if (!relic.isActive(stack)) return;
        e.setAmount((float) reducedDamage);

    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post e) {
        if (e.getEntity().level().isClientSide) return;
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.MINOTAUR_HOOF.get());
        if (MinotaurHoofItem.getTime(stack) == 0) MinotaurHoofItem.resetAttributes(e.getEntity());
        if (!(stack.getItem() instanceof MinotaurHoofItem relic)) return;
        if (!relic.isActive(stack)) return;
        List<LivingEntity> entities = e.getEntity().level().getEntitiesOfClass(LivingEntity.class, e.getEntity().getBoundingBox().inflate(0.025), livingEntity -> e.getEntity() != livingEntity);

        for (LivingEntity entity : entities) {
            entity.hurt(e.getEntity().damageSources().playerAttack(e.getEntity()), (float) relic.getStatValue(stack, "momentum_rush", "damage"));
        }
    }

    public static int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    public boolean isActive(ItemStack stack) {
        return getTime(stack) == MAX_TIME;
    }

    public static void resetAttributes(LivingEntity livingEntity) {
        AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackResistance = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
        if (movementSpeed == null || knockbackResistance == null || stepHeight == null) return;
        movementSpeed.removeModifier(MOVEMENT_MODIFIER);
        knockbackResistance.removeModifier(MOVEMENT_MODIFIER);
        stepHeight.removeModifier(MOVEMENT_MODIFIER);
    }
}
