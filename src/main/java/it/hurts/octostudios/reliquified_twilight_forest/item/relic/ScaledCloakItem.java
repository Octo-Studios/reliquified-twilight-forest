package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.network.ScaledCloakWallClimbPacket;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;

@EventBusSubscriber
public class ScaledCloakItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("wall_crawler")
                                .stat(StatData.builder("max_time")
                                        .initialValue(30, 50)
                                        .upgradeModifier(UpgradeOperation.ADD, 50)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
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
        int time = stack.getOrDefault(DataComponentRegistry.TIME, 0);
        boolean isColliding = false;

        if (!level.isClientSide
                || entity != Minecraft.getInstance().player
                || !(stack.getItem() instanceof IRelicItem relic)
        ) return;

        // I was today years old when I discovered that collision are only detected on client, too bad!
        //entity.sendSystemMessage(Component.literal(Thread.currentThread().getName()+", Collision: "+entity.horizontalCollision+", Minor: "+entity.minorHorizontalCollision));

        if (entity.horizontalCollision) {
            isColliding = true;
            Vec3 deltaMovement = entity.getDeltaMovement();
            float deltaY = time > 0 ? 0.1f : -0.07f;

            entity.setDeltaMovement(deltaMovement.x, deltaY, deltaMovement.z);
        }

        PacketDistributor.sendToServer(new ScaledCloakWallClimbPacket(isColliding));
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent e) {
        LivingEntity entity = e.getEntity();
        Entity attacker = e.getSource().getEntity();
        EntityHitResult result = getEntityLookingAt(entity, 100);
        ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.SCALED_CLOAK.get());

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
