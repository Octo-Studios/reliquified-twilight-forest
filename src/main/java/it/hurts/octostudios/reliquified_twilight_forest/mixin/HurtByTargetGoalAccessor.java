package it.hurts.octostudios.reliquified_twilight_forest.mixin;

import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HurtByTargetGoal.class)
public interface HurtByTargetGoalAccessor {
    @Accessor
    int getTimestamp();

    @Accessor
    Class<?>[] getToIgnoreDamage();
}
