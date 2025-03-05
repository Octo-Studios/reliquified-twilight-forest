package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Objects;

@EventBusSubscriber
public class ThornCrown extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        List<LivingEntity> toHurt = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(0.1), living ->
                !living.equals(entity)
                        && !EntityUtils.isAlliedTo(entity, living)
                        && living.isAlive()
        );

        toHurt.forEach(living -> {
            living.invulnerableTime = 0;
            living.hurt(entity.level().damageSources().thorns(entity), 2);
        });
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent e) {
        LivingEntity entity = e.getEntity();
        ItemStack stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.THORN_CROWN.get());
        ResourceKey<DamageType> type = e.getSource().typeHolder().getKey();
        if (!(stack.getItem() instanceof ThornCrown relic)
                || !(Objects.equals(type, DamageTypes.THORNS)
                || Objects.equals(type, DamageTypes.CACTUS)
                || Objects.equals(type, DamageTypes.SWEET_BERRY_BUSH))
        ) return;

        e.setCanceled(true);
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MODID;
    }
}
