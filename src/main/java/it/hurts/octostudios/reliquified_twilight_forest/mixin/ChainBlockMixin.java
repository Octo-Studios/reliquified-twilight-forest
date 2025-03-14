package it.hurts.octostudios.reliquified_twilight_forest.mixin;

import it.hurts.octostudios.reliquified_twilight_forest.item.relic.SteelCapeItem;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import twilightforest.entity.projectile.ChainBlock;
import twilightforest.init.TFSounds;

@Mixin(ChainBlock.class)
public abstract class ChainBlockMixin {
    @Shadow private @Nullable ItemStack stack;

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean redirected(Entity instance, DamageSource source, float amount) {
        if (stack == null || !(stack.getItem() instanceof SteelCapeItem relic)) {
            return instance.hurt(source, amount);
        }

        ChainBlock self = (ChainBlock) (Object) this;
        if (instance.hurt(source, (float) relic.getStatValue(stack, "iron_guard", "damage"))) {
            self.playSound(TFSounds.BLOCK_AND_CHAIN_HIT.get(), 1.0f, self.getRandom().nextFloat());
            if (instance instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(EffectRegistry.STUN, (int) relic.getStatValue(stack, "iron_guard", "stun_duration"), 0, false, true, true));
            }
            return false;
        }

        return instance.hurt(source, amount);
    }
}
