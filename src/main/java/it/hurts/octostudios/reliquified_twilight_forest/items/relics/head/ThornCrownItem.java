package it.hurts.octostudios.reliquified_twilight_forest.items.relics.head;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.EffectRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourceTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Objects;

@EventBusSubscriber
public class ThornCrownItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("thorn_shield")
                                .initialMaxLevel(2)
                                .build())
                        .ability(AbilityTemplate.builder("poking")
                                .stat(AbilityStatTemplate.builder("paralyze_chance")
                                        .initialValue(0.05, 0.1)
                                        .formatValue(MathButCool::percentage).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.02)
                                        .build())
                                .stat(AbilityStatTemplate.builder("paralyze_duration")
                                        .initialValue(10, 20)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 10)
                                        .build())
                                .stat(AbilityStatTemplate.builder("damage")
                                        .initialValue(1, 2)
                                        .formatValue(MathButCool::roundSingleDigit).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.5)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("entities_paralyzed").build())
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(LootEntries.HEDGE)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide
                || !(stack.getItem() instanceof ThornCrownItem relic)
        ) return;

        List<LivingEntity> toHurt = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(0.1), living ->
                !living.equals(entity)
                        && !EntityUtils.isAlliedTo(entity, living)
                        && living.isAlive()
        );

        var relicData = relic.getRelicData(entity, stack);
        var ability = relicData.getAbilitiesData().getAbilityData("poking");

        toHurt.forEach(living -> {
            if (living.hurt(entity.level().damageSources().thorns(entity), (float) ability.getStatData("damage").getValue())
            && living.getRandom().nextDouble() < ability.getStatData("paralyze_chance").getValue()) {
                living.addEffect(new MobEffectInstance(EffectRegistry.PARALYSIS, (int) Math.round(ability.getStatData("paralyze_duration").getValue()),
                        0, false, false)
                );
                relicData.getLevelingData().addExperience("poking", "entities_paralyzed", 1D);
            };
        });
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = EntityUtils.findEquippedCurio(entity, RTItems.THORN_CROWN.get());
        ResourceKey<DamageType> type = event.getSource().typeHolder().getKey();
        if (!(stack.getItem() instanceof ThornCrownItem relic)
                || !(Objects.equals(type, DamageTypes.CACTUS)
                || Objects.equals(type, DamageTypes.SWEET_BERRY_BUSH)
                || (type != null && type.location().getPath().contains("thorn")))
        ) return;

        event.setCanceled(true);
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
