package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.entity.projectile.HydraFireEntity;
import it.hurts.octostudios.reliquified_twilight_forest.init.EntityRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.CuriosHelper;

@EventBusSubscriber
public class HydraHeartItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
//                        .ability(AbilityData.builder("regenerative_heads")
//                                .stat(StatData.builder("head_amount")
//                                        .initialValue(2, 4)
//                                        .formatValue(Math::round)
//                                        .upgradeModifier(UpgradeOperation.ADD, 1)
//                                        .build())
//                                .maxLevel(5)
//                                .build())
                        .ability(AbilityData.builder("hydra_fire")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.15, 0.2)
                                        .formatValue(MathButCool::percentage)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.12)
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(1, 2)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.8)
                                        .build())
                                .stat(StatData.builder("lifetime")
                                        .initialValue(80, 100)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .upgradeModifier(UpgradeOperation.ADD, 20)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("hydra_fire")
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
                                        .build())
                                .build())
                        .initialCost(100)
                        .step(125)
                        .maxLevel(5)
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xff38f3fa)
                                .endColor(0x00182566)
                                .build())
                        .tooltip(TooltipData.builder()
                                .textured(true)
                                .borderTop(0xff2b2934)
                                .borderBottom(0xff222125)
                                .build())
                        .build())
                .build();
    }

    @Override
    public @Nullable RelicSlotModifier getSlotModifiers(ItemStack stack) {
        if (!(stack.getItem() instanceof HydraHeartItem relic)) return super.getSlotModifiers(stack);
//        return RelicSlotModifier.builder()
//                .modifier("head", (int) Math.round(relic.getStatValue(stack, "regenerative_heads", "head_amount")))
//                .build();
        return super.getSlotModifiers(stack);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        //if (EntityUtils.getEquippedRelics(slotContext.entity()).stream().filter(item -> item.getItem() instanceof HydraHeartItem).toList().isEmpty())
        return super.canEquip(slotContext, stack);
//
//        return false;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        if (livingEntity.level().isClientSide) {
            return;
        }

        //CuriosApi.getCuriosInventory(livingEntity).ifPresent(handler -> handler.setSlotsActive("head", !slotContext.entity().isCrouching()));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Post e) {
        LivingEntity entity = e.getEntity();
        if (entity.level().isClientSide
                || !entity.isAlive()
        ) return;

        for (ItemStack stack : EntityUtils.findEquippedCurios(entity, ItemRegistry.HYDRA_HEART.get())) {
            if (!(stack.getItem() instanceof HydraHeartItem relic)
                    || entity.getRandom().nextFloat() > relic.getStatValue(stack, "hydra_fire", "chance")
            ) continue;

            HydraFireEntity fire = new HydraFireEntity(EntityRegistry.HYDRA_FIRE.get(), entity.level());
            fire.setAge((int) Math.round(relic.getStatValue(stack, "hydra_fire", "lifetime")));
            fire.setPos(entity.getEyePosition());
            fire.setRelicStack(stack);
            fire.setDeltaMovement((fire.getRandom().nextFloat()-0.5f)*0.1f, 0.4f, (fire.getRandom().nextFloat()-0.5f)*0.1f);
            fire.setOwner(e.getEntity());
            entity.level().addFreshEntity(fire);
        }
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
