package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.DamageTypeRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.network.ExecutionEffectPacket;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
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
public class TwilightFeatherItem extends RelicItem {
    public static final Map<ResourceKey<TinyBirdVariant>, Color> VARIANTS = Map.of(
            TinyBirdVariants.RED, new Color(231, 70, 70, 255),
            TinyBirdVariants.BLUE, new Color(59, 52, 232, 255),
            TinyBirdVariants.GOLD, new Color(255, 202, 73, 255),
            TinyBirdVariants.BROWN, new Color(79, 50, 34, 255)
    );
    private static final Object[] keys = VARIANTS.keySet().toArray();

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("execution")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.005, 0.01)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .upgradeModifier(UpgradeOperation.ADD, 0.004)
                                        .build())
                                .maxLevel(10)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(10)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("execution")
                                        .gem(GemShape.SQUARE, GemColor.PURPLE)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xff44ff71)
                                .endColor(0x000f1c13)
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.TREE_CACHE)
                        .build())
                .build();
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre e) {
        LivingEntity victim = e.getEntity();
        Entity entity = e.getSource().getEntity();

        if (victim.level().isClientSide
                || Objects.equals(e.getSource().typeHolder().getKey(), DamageTypeRegistry.EXECUTION)
                || !(entity instanceof LivingEntity source)
                || source == victim
                || victim.getHealth() > source.getMaxHealth()
        ) return;

        for (ItemStack stack : EntityUtils.findEquippedCurios(source, ItemRegistry.TWILIGHT_FEATHER.get())) {
            if (!(stack.getItem() instanceof TwilightFeatherItem relic)
                    || !source.isAlive()
                    || source.getRandom().nextDouble() > relic.getStatValue(stack, "execution", "chance")
            ) continue;

            if (hasPerformedExecution(source, victim)) {
                e.setNewDamage(0);
                relic.spreadRelicExperience(source, stack, 1);
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
