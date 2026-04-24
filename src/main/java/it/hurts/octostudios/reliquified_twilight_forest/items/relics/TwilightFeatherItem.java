package it.hurts.octostudios.reliquified_twilight_forest.items.relics;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.DamageTypeRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.network.ExecutionEffectPacket;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourceTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import twilightforest.entity.passive.TinyBird;
import twilightforest.entity.passive.TinyBirdVariant;
import twilightforest.init.TFEntities;
import twilightforest.init.custom.TinyBirdVariants;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

@EventBusSubscriber
public class TwilightFeatherItem extends RTWearableRelicItem {
    public static final Map<ResourceKey<TinyBirdVariant>, Color> VARIANTS = Map.of(
            TinyBirdVariants.RED, new Color(231, 70, 70, 255),
            TinyBirdVariants.BLUE, new Color(59, 52, 232, 255),
            TinyBirdVariants.GOLD, new Color(255, 202, 73, 255),
            TinyBirdVariants.BROWN, new Color(79, 50, 34, 255)
    );
    private static final Object[] keys = VARIANTS.keySet().toArray();

    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("execution")
                                .stat(AbilityStatTemplate.builder("chance")
                                        .initialValue(0.005, 0.01)
                                        .formatValue(value -> MathUtils.round(value * 100, 1)).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.004)
                                        .build())
                                .initialMaxLevel(10)
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("entities_executed").build())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        Entity entity = event.getSource().getEntity();

        if (victim.level().isClientSide
                || Objects.equals(event.getSource().typeHolder().getKey(), DamageTypeRegistry.EXECUTION)
                || !(entity instanceof LivingEntity source)
                || source == victim
                || victim.getHealth() > source.getMaxHealth()
        ) return;

        for (ItemStack stack : EntityUtils.findEquippedCurios(source, RTItems.TWILIGHT_FEATHER.get())) {
            if (!(stack.getItem() instanceof TwilightFeatherItem relic)
                    || !source.isAlive()
            ) continue;

            var relicData = relic.getRelicData(source, stack);
            var ability = relicData.getAbilitiesData().getAbilityData("execution");

            if (source.getRandom().nextDouble() > ability.getStatData("chance").getValue())
                continue;

            if (hasPerformedExecution(source, victim)) {
                event.setNewDamage(0);
                relicData.getLevelingData().addExperience("execution", "entities_executed", 1D);
                break;
            }
        }
    }

    public static boolean hasPerformedExecution(LivingEntity source, LivingEntity victim) {
        if (!victim.hurt(new DamageSource(victim.level().damageSources().damageTypes.getHolderOrThrow(DamageTypeRegistry.EXECUTION), source), 99999)) {
            return false;
        };
        victim.deathTime = 19;
        if (victim instanceof TinyBird) {
            return false;
        }

        TinyBird birb = new TinyBird(TFEntities.TINY_BIRD.get(), victim.level());
        ResourceKey<TinyBirdVariant> variant = (ResourceKey<TinyBirdVariant>) keys[birb.getRandom().nextInt(keys.length)];
        Color color = VARIANTS.get(variant);
        birb.setVariant(victim.level().registryAccess().holderOrThrow(variant));
        birb.setPos(victim.getEyePosition());
        birb.setDeltaMovement(victim.getDeltaMovement());
        victim.level().addFreshEntity(birb);
        victim.level().playSound(null, birb, SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 1f, 0.8f);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(victim, new ExecutionEffectPacket(victim.getId(), color));
        return true;
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
