package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.sskirillss.relics.client.particles.BasicColoredParticle;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;

public class GoblinNoseItem extends RelicItem {
    public static final ParticleOptions ORE_PARTICLE = new BasicColoredParticle.Options(BasicColoredParticle.Constructor.builder()
            .color(new Color(255, 255, 0).getRGB())
            .diameter(1)
            .lifetime(20)
            .scaleModifier(0.85f)
            .physical(false)
            .visibleThroughWalls(true)
            .roll(0.5F)
            .build());

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("vein_seeker")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        if (!(stack.getItem() instanceof GoblinNoseItem relic)
                || !relic.isAbilityTicking(stack, "vein_seeker")
        ) return;

        if (livingEntity.level().isClientSide && livingEntity == Minecraft.getInstance().player) {
            livingEntity.level().addParticle(
                    ORE_PARTICLE,
                    livingEntity.position().x,
                    livingEntity.position().y + 1,
                    livingEntity.position().z + 20,
                    0, 0, 0
            );
        }
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
