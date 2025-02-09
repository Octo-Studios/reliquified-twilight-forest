package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
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

    public int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    public void setTime(ItemStack stack, int time) {
        stack.set(DataComponentRegistry.TIME, Math.clamp(time, 0, MAX_TIME));
    }

    public void addTime(ItemStack stack, int time) {
        setTime(stack, getTime(stack) + time);
    }

    public boolean isActive(ItemStack stack) {
        return getTime(stack) == MAX_TIME;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof MinotaurHoofItem relic)
                || !(slotContext.entity() instanceof Player player))
            return;

        Level level = player.getCommandSenderWorld();

        if (level.isClientSide())
            return;

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackResistance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);

        if (movementSpeed == null || knockbackResistance == null || stepHeight == null) return;
        double maxSpeedMultiplier = relic.getStatValue(stack, "momentum_rush", "max_speed_multiplier");

        int time = getTime(stack);
        addTime(stack, player.isSprinting() ? 1 : -1);
        if (time == 0) return;

        movementSpeed.addOrUpdateTransientModifier(new AttributeModifier(MOVEMENT_MODIFIER, maxSpeedMultiplier * time / (float) MAX_TIME, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        knockbackResistance.addOrUpdateTransientModifier(new AttributeModifier(MOVEMENT_MODIFIER, time / (float) MAX_TIME, AttributeModifier.Operation.ADD_VALUE));
        stepHeight.addOrUpdateTransientModifier(new AttributeModifier(MOVEMENT_MODIFIER, isActive(stack) ? 0.55 : 0, AttributeModifier.Operation.ADD_VALUE));

        if (!relic.isActive(stack)) return;
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(0.025), target -> !target.getStringUUID().equals(player.getStringUUID()));

        for (LivingEntity entity : entities) {
            entity.hurt(player.damageSources().playerAttack(player), (float) relic.getStatValue(stack, "momentum_rush", "damage"));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        if (newStack.getItem() == stack.getItem())
            return;

        setTime(stack, 0);
        resetAttributes(slotContext.entity());
    }

    private static void resetAttributes(LivingEntity livingEntity) {
        AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackResistance = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
        if (movementSpeed == null || knockbackResistance == null || stepHeight == null) return;
        if (movementSpeed.hasModifier(MOVEMENT_MODIFIER)) movementSpeed.removeModifier(MOVEMENT_MODIFIER);
        if (knockbackResistance.hasModifier(MOVEMENT_MODIFIER)) knockbackResistance.removeModifier(MOVEMENT_MODIFIER);
        if (stepHeight.hasModifier(MOVEMENT_MODIFIER)) stepHeight.removeModifier(MOVEMENT_MODIFIER);
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
}