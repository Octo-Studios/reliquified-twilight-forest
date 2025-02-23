package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.network.CastRideAlongAbilityPacket;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;

@EventBusSubscriber
public class DeerAntlerItem extends RelicItem {
    public static final String ON_ANTLERS = "reliquified_twilight_forest:on_antlers";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("ride_along")
                                .stat(StatData.builder("entity_volume")
                                        .initialValue(1d, 2d)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 3d)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);

        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide
                || entity.tickCount % 20 != 0
                || entity.getPassengers().isEmpty()
                || !entity.getPassengers().getFirst().getPersistentData().getBoolean(ON_ANTLERS)
                || !(stack.getItem() instanceof DeerAntlerItem relic)
        ) return;

        relic.spreadRelicExperience(entity, stack, 1);
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        super.castActiveAbility(stack, player, ability, type, stage);
        if (!player.level().isClientSide
                || !ability.equals("ride_along")
                || stage != CastStage.END
        ) return;

        boolean isMounting = player.getPassengers().isEmpty();
        int passengerId = -1;

        if (getEntityLookingAt(player, player.entityInteractionRange()) instanceof EntityHitResult result
                && result.getEntity() instanceof LivingEntity passenger
                && getBoundingBoxVolume(passenger.getBoundingBox()) <= this.getStatValue(stack, ability, "entity_volume")
        ) passengerId = passenger.getId();

        PacketDistributor.sendToServer(new CastRideAlongAbilityPacket(passengerId, isMounting));
    }

    public static EntityHitResult getEntityLookingAt(Player player, double maxDistance) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getViewVector(1.0F);
        Vec3 reachEnd = eyePosition.add(lookVector.scale(maxDistance));

        return ProjectileUtil.getEntityHitResult(player, eyePosition, reachEnd,
                player.getBoundingBox().expandTowards(lookVector.scale(maxDistance)).inflate(1.0),
                entity -> entity instanceof LivingEntity
                        && entity.isAlive()
                        && player.hasLineOfSight(entity)
                        && !player.getPassengers().contains(entity)
                        && entity != player,
                maxDistance * maxDistance);
    }

    @SubscribeEvent
    public static void riding(EntityMountEvent e) {
        if (!e.getLevel().isClientSide && e.isDismounting()) {
            e.getEntityMounting().getPersistentData().remove(ON_ANTLERS);
        }
    }


    @SubscribeEvent
    public static void livingDamage(LivingIncomingDamageEvent e) {
        Entity entity = e.getSource().getEntity();
        if (entity != null
                && entity.isPassenger()
                && entity.getPersistentData().getBoolean(ON_ANTLERS)
                && entity.getVehicle() == e.getEntity()
        ) e.setCanceled(true);
    }

    public static double getBoundingBoxVolume(AABB box) {
        return box.getXsize() * box.getYsize() * box.getZsize();
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MODID;
    }
}
