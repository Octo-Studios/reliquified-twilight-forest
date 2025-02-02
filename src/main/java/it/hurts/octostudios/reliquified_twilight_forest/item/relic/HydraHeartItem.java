package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public class HydraHeartItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("regenerative_heads")
                                .stat(StatData.builder("head_amount")
                                        .initialValue(2, 4)
                                        .formatValue(Math::round)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .step(125)
                        .maxLevel(5)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("regenerative_heads")
                                        .gem(GemShape.OVAL, GemColor.GREEN)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public @Nullable RelicSlotModifier getSlotModifiers(ItemStack stack) {
        if (!(stack.getItem() instanceof HydraHeartItem relic)) return super.getSlotModifiers(stack);
        return RelicSlotModifier.builder()
                .modifier("head", (int) Math.round(relic.getStatValue(stack, "regenerative_heads", "head_amount")))
                .build();
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
        if (!(stack.getItem() instanceof HydraHeartItem relic)) return;

        LivingEntity livingEntity = slotContext.entity();
        if (livingEntity.level().isClientSide) return;
    }
}
