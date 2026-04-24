package it.hurts.octostudios.reliquified_twilight_forest.items.relics;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.entity.projectile.HydraFireEntity;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTEntities;
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
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

@EventBusSubscriber
public class HydraHeartItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
//                        .ability(AbilityTemplate.builder("regenerative_heads")
//                                .stat(AbilityStatTemplate.builder("head_amount")
//                                        .initialValue(2, 4)
//                                        .formatValue(Math::round)
//                                        .upgradeModifier(RelicsScalingModels.ADD, 1)
//                                        .build())
//                                .maxLevel(5)
//                                .build())
                        .ability(AbilityTemplate.builder("hydra_fire")
                                .stat(AbilityStatTemplate.builder("chance")
                                        .initialValue(0.15, 0.2)
                                        .formatValue(MathButCool::percentage).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.12)
                                        .build())
                                .stat(AbilityStatTemplate.builder("damage")
                                        .initialValue(1, 2)
                                        .formatValue(MathButCool::roundSingleDigit).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.8)
                                        .build())
                                .stat(AbilityStatTemplate.builder("lifetime")
                                        .initialValue(80, 100)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 20)
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("blood_droplet").build())
                                        .build())
                                .initialMaxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingTemplate.builder()
                        .initialCost(100)
                        .step(125)
                        .build())
                .build();
    }

//    @Override
//    public @Nullable RelicSlotModifier getSlotModifiers(LivingEntity entity, ItemStack stack) {
//    if (!(stack.getItem() instanceof HydraHeartItem relic)) return super.getSlotModifiers(stack);
//        return RelicSlotModifier.builder()
//                .modifier("head", (int) Math.round(relic.getStatValue(stack, "regenerative_heads", "head_amount")))
//                .build();
//        return super.getSlotModifiers(stack);
//    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        //if (EntityUtils.getEquippedRelics(slotContext.entity()).stream().filter(item -> item.getItem() instanceof HydraHeartItem).toList().isEmpty())
        return super.canEquip(slotContext, stack);
//
//        return false;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        if (livingEntity.level().isClientSide) {
            return;
        }

        //CuriosApi.getCuriosInventory(livingEntity).ifPresent(handler -> handler.setSlotsActive("head", !slotContext.entity().isCrouching()));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide
                || !entity.isAlive()
        ) return;

        for (ItemStack stack : EntityUtils.findEquippedCurios(entity, RTItems.HYDRA_HEART.get())) {
            if (!(stack.getItem() instanceof HydraHeartItem relic)
            ) continue;

            var relicData = relic.getRelicData(entity, stack);
            var ability = relicData.getAbilitiesData().getAbilityData("hydra_fire");

            if (entity.getRandom().nextFloat() > ability.getStatData("chance").getValue() )
                continue;

            HydraFireEntity fire = new HydraFireEntity(RTEntities.HYDRA_FIRE.get(), entity.level());
            fire.setAge((int) Math.round(ability.getStatData("lifetime").getValue()));
            fire.setPos(entity.getEyePosition());
            fire.setRelicStack(stack);
            fire.setDeltaMovement((fire.getRandom().nextFloat()-0.5f)*0.1f, 0.4f, (fire.getRandom().nextFloat()-0.5f)*0.1f);
            fire.setOwner(event.getEntity());
            entity.level().addFreshEntity(fire);
        }
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
