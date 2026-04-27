package it.hurts.octostudios.reliquified_twilight_forest.items.relics.back;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTDataComponent;
import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTWearableRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.api.relics.IRelicItem;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;

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
public class ScaledCloakItem extends RTWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("wall_crawler")
                                .stat(AbilityStatTemplate.builder("max_time")
                                        .initialValue(60, 100).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 40)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .initialMaxLevel(5)
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("time_crawled")
                                        .build())
                                .build())
                        .ability(AbilityTemplate.builder("elusive_stare")
                                .requiredLevel(5)
                                .requiredPoints(2)
                                .stat(AbilityStatTemplate.builder("chance")
                                        .initialValue(0.1, 0.25).upgradeModifier(RelicsScalingModels.ADDITIVE.get(), 0.1)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .initialMaxLevel(5)
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source("enemies_stared_at")
                                        .build())
                                .build())
                        .build())
                .build();
    }

/*    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        Level level = entity.level();
        int time = stack.getOrDefault(RTDataComponent.SCALED_CLOAK_TIME, 0);
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

 */

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent e) {
        LivingEntity entity = e.getEntity();
        Entity attacker = e.getSource().getEntity();
        EntityHitResult result = getEntityLookingAt(entity, 100);
        ItemStack stack = EntityUtils.findEquippedCurio(entity, RTItems.SCALED_CLOAK.get());

        if (entity.level().isClientSide
                || result == null
                || result.getEntity() != attacker
                || !(stack.getItem() instanceof ScaledCloakItem relic)
                || entity.getRandom().nextFloat() > relic.getRelicData(entity, stack).getAbilitiesData().getAbilityData("elusive_stare").getStatData("chance").getValue()
        ) return;

        relic.getRelicData(entity, stack).getLevelingData().addExperience("elusive_stare", "enemies_stared_at", 1D);
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
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
