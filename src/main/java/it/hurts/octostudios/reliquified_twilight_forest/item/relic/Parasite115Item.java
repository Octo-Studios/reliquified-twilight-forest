package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.DamageTypeRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.EffectRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.EntitiesButCool;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import twilightforest.entity.boss.UrGhast;
import twilightforest.init.TFItems;

import java.awt.Color;
import java.util.Optional;

@EventBusSubscriber
public class Parasite115Item extends RelicItem {
    public static final String LAST_INFECTED_BY = ReliquifiedTwilightForest.MOD_ID+":last_infected_by";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(Parasite115Item.getInfectiousBloom(1, 5))
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(5)
                        .build())
                .style(StyleData.builder()
                        .beams((player, stack) -> {
                            float ticks = player.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                            float lerp = (float) (Math.sin(ticks/10f)/2f+0.5f);

                            float r = Mth.lerp(lerp, 237, 255) / 255f;
                            float g = Mth.lerp(lerp, 28, 127) / 255f;
                            float b = Mth.lerp(lerp, 36, 39) / 255f;

                            return BeamsData.builder()
                                    .startColor(new Color(r,g,b,1).getRGB())
                                    .endColor(new Color(r,g,b,0).getRGB())
                                    .build();
                        })
                        .build())
                .build();
    }

    @SubscribeEvent
    public static void attackEntity(LivingDamageEvent.Post e) {
        if (!(e.getSource().getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }

        ItemStack stack = EntitiesButCool.findEquippedStack(Parasite115Item.class, livingEntity);
        if (e.getEntity().level().isClientSide
                || !(stack.getItem() instanceof Parasite115Item relic)
                || e.getSource().is(DamageTypeRegistry.INFECTIOUS_BLOOM)
                || e.getEntity() == livingEntity
        ) return;

        if (e.getEntity().hasEffect(EffectRegistry.INFECTIOUS_BLOOM) || e.getEntity().getRandom().nextFloat() < relic.getStatValue(stack, "infectious_bloom", "chance")) {
            if (!e.getEntity().hasEffect(EffectRegistry.INFECTIOUS_BLOOM)) {
                relic.spreadRelicExperience(livingEntity, stack, 1);
            }

            e.getEntity().addEffect(new MobEffectInstance(EffectRegistry.INFECTIOUS_BLOOM, 100, (int) Math.round(relic.getStatValue(stack, "infectious_bloom", "amplifier"))));
            e.getEntity().getPersistentData().putUUID(LAST_INFECTED_BY, livingEntity.getUUID());

        }
    }

    @SubscribeEvent
    public static void generateDrops(LivingDeathEvent e) {
        if (!(e.getEntity().level() instanceof ServerLevel serverLevel)
                || !e.getEntity().getPersistentData().contains(LAST_INFECTED_BY)
        ) return;

        Entity infectedBy = serverLevel.getEntity(e.getEntity().getPersistentData().getUUID(LAST_INFECTED_BY));
        if (!(infectedBy instanceof LivingEntity livingEntity)) {
            return;
        }

        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = EntitiesButCool.findEquippedCurio(Parasite115Item.class, livingEntity);

        if (optional.isEmpty()
                || e.getEntity().level().isClientSide
                || !e.getSource().is(DamageTypeRegistry.INFECTIOUS_BLOOM)
                || !(optional.get().getRight().getItem() instanceof Parasite115Item relic)
        ) return;

        ImmutableTriple<String, Integer, ItemStack> triple = optional.get();
        ItemStack stack = triple.getRight();

        Vec3 pos = e.getEntity().position();
        for (int i = 0; i < e.getEntity().getRandom().nextIntBetweenInclusive(1, (int) Math.round(relic.getStatValue(stack, "infectious_bloom", "drops"))); i++) {
            Vec3 dropVec = new Vec3((e.getEntity().getRandom().nextFloat() - 0.5f)/3f, 0.5f, (e.getEntity().getRandom().nextFloat() - 0.5f)/3f);
            ItemEntity toDrop = new ItemEntity(e.getEntity().level(), pos.x, pos.y, pos.z, TFItems.EXPERIMENT_115.toStack(), dropVec.x, dropVec.y, dropVec.z);
            toDrop.setNoPickUpDelay();
            e.getEntity().level().addFreshEntity(toDrop);
        }

        if (e.getEntity() instanceof UrGhast && !relic.isEvolved()) {
            relic.evolve(triple.getLeft(), triple.getMiddle(), stack, livingEntity);
        }
    }

    public void evolve(String identifier, int index, ItemStack stack, LivingEntity entity) {
        ItemStack evolvedParasite = ItemRegistry.PARASITE_116.get().getDefaultInstance();
        Parasite116Item relic = (Parasite116Item) evolvedParasite.getItem();

        relic.setDataComponent(evolvedParasite, this.getDataComponent(stack));

        for (var ability : this.getAbilitiesData().getAbilities().values()) {
            var abilityID = ability.getId();

            for (var stat : ability.getStats().values()) {
                var statID = stat.getId();

                var quality = this.getStatQuality(stack, abilityID, statID);
                var value = relic.getStatValueByQuality(abilityID, statID, quality);

                relic.setStatInitialValue(evolvedParasite, abilityID, statID, value);
            }
        }

        CuriosApi.getCuriosInventory(entity).get().setEquippedCurio(identifier, index, evolvedParasite);
    }

    public boolean isEvolved() {
        return false;
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }

    public static AbilityData getInfectiousBloom(float baseStatMultiplier, int maxLevel) {
        return AbilityData.builder("infectious_bloom")
                .stat(StatData.builder("chance")
                        .initialValue(0.05*baseStatMultiplier, 0.1*baseStatMultiplier)
                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.3)
                        .formatValue(MathButCool::percentageAndRoundSingleDigit)
                        .build())
                .stat(StatData.builder("amplifier")
                        .initialValue(baseStatMultiplier, 2+baseStatMultiplier)
                        .upgradeModifier(UpgradeOperation.ADD, baseStatMultiplier/3d)
                        .formatValue(Math::round)
                        .build())
                .stat(StatData.builder("drops")
                        .initialValue(baseStatMultiplier, 2+baseStatMultiplier)
                        .upgradeModifier(UpgradeOperation.ADD, 1)
                        .build())
                .maxLevel(maxLevel)
                .build();
    }

    @SubscribeEvent
    public static void consumeExperiment115(LivingEntityUseItemEvent.Finish e) {
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.PARASITE_116.get());
        if (e.getEntity().level().isClientSide
                || e.getItem().getItem() != TFItems.EXPERIMENT_115.get()
                || !(stack.getItem() instanceof Parasite116Item relic)
        ) return;

        int time = stack.getOrDefault(DataComponentRegistry.TIME, 0);
        time = (int) Mth.clamp(time + relic.getStatValue(stack, "rage_consumption", "amount_restored"), 0, 200);
        stack.set(DataComponentRegistry.TIME, time);
        relic.spreadRelicExperience(e.getEntity(), stack, 1);
    }
}
