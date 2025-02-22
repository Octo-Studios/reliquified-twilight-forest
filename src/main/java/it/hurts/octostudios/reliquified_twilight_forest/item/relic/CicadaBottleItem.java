package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.api.HurtByTargetGoalWithPredicate;
import it.hurts.octostudios.reliquified_twilight_forest.init.EffectRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.mixin.NearestAttackableTargetGoalAccessor;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.A;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.network.CreateMovingCicadaSoundPacket;

import java.util.List;

@EventBusSubscriber
public class CicadaBottleItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder().build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added e) {
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity.level().isClientSide
                || (e.getOldEffectInstance() != null
                && e.getEffectInstance().getEffect() == e.getOldEffectInstance().getEffect())
        ) return;

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(e.getEntity(), new CreateMovingCicadaSoundPacket(e.getEntity().getId()));
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide
                || !(event.getEntity() instanceof PathfinderMob mob)
        ) return;

        //mob.goalSelector.addGoal(-100, new MeleeAttackGoal(mob, 1.3d, true)); // crashes if the mob doesn't have attack damage attribute
        mob.targetSelector.addGoal(-100, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, false,
                toAttack -> toAttack.hasEffect(EffectRegistry.CICADA_INFESTATION))
        );
    }
}
