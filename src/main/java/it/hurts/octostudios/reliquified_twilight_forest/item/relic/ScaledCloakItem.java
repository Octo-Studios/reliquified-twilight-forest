package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFParticleType;

@EventBusSubscriber
public class ScaledCloakItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("wall_crawler")
                                .stat(StatData.builder("max_height")
                                        .initialValue(2, 4)
                                        .upgradeModifier(UpgradeOperation.ADD, 2)
                                        .formatValue(Math::round)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .ability(AbilityData.builder("elusive_stare")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.1, 0.25)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.1)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("wall_crawler")
                                        .gem(GemShape.SQUARE, GemColor.GREEN)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("elusive_stare")
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .build())
                        .maxLevel(10)
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.TWILIGHT)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        Level level = entity.level();
        if ((level.isClientSide && entity != Minecraft.getInstance().player)
                || !(stack.getItem() instanceof IRelicItem relic)
        ) return;

        if (entity.horizontalCollision) {
            Vec3 dt = entity.getDeltaMovement();
            entity.setDeltaMovement(dt.x, 0.1f, dt.z);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent e) {
        LivingEntity entity = e.getEntity();
        Entity attacker = e.getSource().getEntity();
        EntityHitResult result = getEntityLookingAt(entity, 100);
        ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.SCALED_CLOAD.get());

        if (entity.level().isClientSide
                || result == null
                || result.getEntity() != attacker
                || !(stack.getItem() instanceof ScaledCloakItem relic)
                || entity.getRandom().nextFloat() > relic.getStatValue(stack, "elusive_stare", "chance")
        ) return;

        relic.spreadRelicExperience(entity, stack, 1);
        entity.level().playSound(null, entity, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 0.6f, 1.35f);
        e.setCanceled(true);
    }

    public static EntityHitResult getEntityLookingAt(LivingEntity livingEntity, double maxDistance) {
        Vec3 eyePosition = livingEntity.getEyePosition(1.0F);
        Vec3 lookVector = livingEntity.getViewVector(1.0F);
        Vec3 reachEnd = eyePosition.add(lookVector.scale(maxDistance));

        return ProjectileUtil.getEntityHitResult(livingEntity, eyePosition, reachEnd,
                livingEntity.getBoundingBox().expandTowards(lookVector.scale(maxDistance)).inflate(1.0),
                entity -> entity instanceof LivingEntity
                        && entity.isAlive()
                        && livingEntity.hasLineOfSight(entity)
                        && entity != livingEntity,
                maxDistance * maxDistance);
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MODID;
    }
}
