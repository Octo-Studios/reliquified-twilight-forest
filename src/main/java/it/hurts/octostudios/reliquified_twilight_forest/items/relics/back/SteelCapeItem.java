package it.hurts.octostudios.reliquified_twilight_forest.items.relics.back;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import twilightforest.entity.projectile.ChainBlock;
import twilightforest.init.TFEntities;

@EventBusSubscriber
public class SteelCapeItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("iron_guard")
                                .stat(AbilityStatTemplate.builder("flat_armor")
                                        .initialValue(0.2, 0.4).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.16)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .stat(AbilityStatTemplate.builder("chance")
                                        .initialValue(0.1, 0.25).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.025)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .stat(AbilityStatTemplate.builder("damage")
                                        .initialValue(4, 6).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.6)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .stat(AbilityStatTemplate.builder("stun_duration")
                                        .initialValue(10, 20).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 8)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("steel_orbs_released")
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(LootEntries.STRONGHOLD)
                        .build())
                .build();
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        Entity entity = event.getSource().getEntity();
        LivingEntity victim = event.getEntity();
        ItemStack stack = EntityUtils.findEquippedCurio(victim, RTItems.STEEL_CAPE.get());

        if (victim.level().isClientSide
                || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)
                || !(stack.getItem() instanceof SteelCapeItem relic)
        ) return;

        var relicData = relic.getRelicData(victim, stack);
        var ability = relicData.getAbilitiesData().getAbilityData("iron_guard");

        float newDamage = event.getNewDamage() - (float) ability.getStatData("flat_armor").getValue();
        event.setNewDamage(Math.max(newDamage, 0.001f));

        if (newDamage <= 0
            || victim.getRandom().nextDouble() > ability.getStatData("chance").getValue()
        ) return;

        if (entity instanceof LivingEntity source) {
            ChainBlock chain = new ChainBlock(TFEntities.CHAIN_BLOCK.get(), victim.level(), victim, null, stack);
            chain.setPos(victim.position().add(0, victim.getBbHeight() / 2f, 0));
            Vec3 direction = source.position().add(0, source.getBbHeight()/2f, 0).subtract(chain.position()).normalize();
            chain.shoot(direction.x, direction.y, direction.z, 1.5f, 1f);
            victim.level().addFreshEntity(chain);
            relicData.getLevelingData().addExperience("iron_guard","steel_orbs_released", 1D);
        }
    }
}
