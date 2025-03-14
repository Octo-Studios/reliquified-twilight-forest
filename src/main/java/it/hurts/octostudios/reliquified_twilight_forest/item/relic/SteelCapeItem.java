package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
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
public class SteelCapeItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("iron_guard")
                                .stat(StatData.builder("flat_armor")
                                        .initialValue(0.2, 0.4)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.16)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .stat(StatData.builder("chance")
                                        .initialValue(0.1, 0.25)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.025)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(4, 6)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.6)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .stat(StatData.builder("stun_duration")
                                        .initialValue(10, 20)
                                        .upgradeModifier(UpgradeOperation.ADD, 8)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xffD0D0D0)
                                .endColor(0)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("iron_guard")
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.STRONGHOLD)
                        .build())
                .build();
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre e) {
        Entity entity = e.getSource().getEntity();
        LivingEntity victim = e.getEntity();
        ItemStack stack = EntityUtils.findEquippedCurio(victim, ItemRegistry.STEEL_CAPE.get());

        if (victim.level().isClientSide
                || e.getSource().is(DamageTypeTags.BYPASSES_ARMOR)
                || !(stack.getItem() instanceof SteelCapeItem relic)
        ) return;

        float newDamage = e.getNewDamage() - (float) relic.getStatValue(stack, "iron_guard", "flat_armor");
        e.setNewDamage(Math.max(newDamage, 0.001f));

        if (newDamage <= 0
            || victim.getRandom().nextDouble() > relic.getStatValue(stack, "iron_guard", "chance")
        ) return;

        if (entity instanceof LivingEntity source) {
            ChainBlock chain = new ChainBlock(TFEntities.CHAIN_BLOCK.get(), victim.level(), victim, null, stack);
            chain.setPos(victim.position().add(0, victim.getBbHeight() / 2f, 0));
            Vec3 direction = source.position().add(0, source.getBbHeight()/2f, 0).subtract(chain.position()).normalize();
            chain.shoot(direction.x, direction.y, direction.z, 1.5f, 1f);
            victim.level().addFreshEntity(chain);
            relic.spreadRelicExperience(victim, stack, 1);
        }
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
