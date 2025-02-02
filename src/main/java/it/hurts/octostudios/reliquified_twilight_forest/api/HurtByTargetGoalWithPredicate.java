package it.hurts.octostudios.reliquified_twilight_forest.api;

import it.hurts.octostudios.reliquified_twilight_forest.mixin.HurtByTargetGoalAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;

public class HurtByTargetGoalWithPredicate extends HurtByTargetGoal {
    public final TargetingConditions conditions;

    public HurtByTargetGoalWithPredicate(PathfinderMob mob, TargetingConditions predicate, Class<?>... toIgnoreDamage) {
        super(mob, toIgnoreDamage);
        this.conditions = predicate;
    }

    @Override
    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != ((HurtByTargetGoalAccessor) this).getTimestamp() && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                for (Class<?> oclass : ((HurtByTargetGoalAccessor) this).getToIgnoreDamage()) {
                    if (oclass.isAssignableFrom(livingentity.getClass())) {
                        return false;
                    }
                }

                return this.canAttack(livingentity, conditions);
            }
        } else {
            return false;
        }
    }
}
