package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;

public class Parasite116Item extends Parasite115Item {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(Parasite115Item.getInfectiousBloom(2, 10))
                        .ability(AbilityData.builder("rage_consumption")
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .maxLevel(15)
                        .build())
                .style(StyleData.builder()
                        .beams((player, stack) -> {
                            float ticks = player.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                            float lerp = (float) (Math.sin(ticks/5f)/2f+0.5f);

                            float r = 1f;
                            float g = Mth.lerp(lerp, 127, 233) / 255f;
                            float b = Mth.lerp(lerp, 39, 0) / 255f;

                            return BeamsData.builder()
                                    .startColor(new Color(r,g,b,1).getRGB())
                                    .endColor(new Color(r,g,b,0).getRGB())
                                    .build();
                        })
                        .build())
                .build();
    }

    @Override
    public void evolve(ItemStack stack, LivingEntity entity) {
        //no
    }
}
