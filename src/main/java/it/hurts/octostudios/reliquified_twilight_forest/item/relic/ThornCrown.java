package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
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
public class ThornCrown extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("thorn_shield")
                                .maxLevel(0)
                                .build())
                        .ability(AbilityData.builder("poking")
                                .stat(StatData.builder("paralyze_chance")
                                        .initialValue(0.05, 0.1)
                                        .formatValue(MathButCool::percentage)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.02)
                                        .build())
                                .stat(StatData.builder("paralyze_duration")
                                        .initialValue(10, 20)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .upgradeModifier(UpgradeOperation.ADD, 10)
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(1, 2)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("poking")
                                        .gem(GemShape.SQUARE, GemColor.GREEN)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xff66da00)
                                .endColor(0x00014f2b)
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide
                || !(stack.getItem() instanceof ThornCrown relic)
        ) return;

        List<LivingEntity> toHurt = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(0.1), living ->
                !living.equals(entity)
                        && !EntityUtils.isAlliedTo(entity, living)
                        && living.isAlive()
        );

        toHurt.forEach(living -> {
            if (living.hurt(entity.level().damageSources().thorns(entity), (float) relic.getStatValue(stack, "poking", "damage"))
            && living.getRandom().nextDouble() < relic.getStatValue(stack, "poking", "paralyze_chance")) {
                living.addEffect(new MobEffectInstance(EffectRegistry.PARALYSIS, (int) Math.round(relic.getStatValue(stack, "poking", "paralyze_duration")),
                        0, false, false)
                );
                relic.spreadRelicExperience(entity, stack, 1);
            };
        });
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent e) {
        LivingEntity entity = e.getEntity();
        ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.THORN_CROWN.get());
        ResourceKey<DamageType> type = e.getSource().typeHolder().getKey();
        if (!(stack.getItem() instanceof ThornCrown relic)
                || !(Objects.equals(type, DamageTypes.CACTUS)
                || Objects.equals(type, DamageTypes.SWEET_BERRY_BUSH)
                || (type != null && type.location().getPath().contains("thorn")))
        ) return;

        e.setCanceled(true);
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
