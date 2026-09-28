package it.hurts.octostudios.reliquified_twilight_forest.handler;

import it.hurts.octostudios.reliquified_twilight_forest.item.relic.DeerAntlerItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.LichCrownItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.MinotaurHoofItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.ScaledCloakItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class RenderableRelicEventHandler {
    @SubscribeEvent
    public static void deerAntlerRiding(EntityMountEvent event) {
        DeerAntlerItem.riding(event);
    }

    @SubscribeEvent
    public static void deerAntlerLivingDamage(LivingIncomingDamageEvent event) {
        DeerAntlerItem.livingDamage(event);
    }

    @SubscribeEvent
    public static void lichCrownEntitySpawn(EntityJoinLevelEvent event) {
        LichCrownItem.onEntitySpawn(event);
    }

    @SubscribeEvent
    public static void lichCrownLivingTick(EntityTickEvent.Post event) {
        LichCrownItem.onLivingEntityTick(event);
    }

    @SubscribeEvent
    public static void minotaurHoofDamageTaken(LivingIncomingDamageEvent event) {
        MinotaurHoofItem.onDamageTaken(event);
    }

    @SubscribeEvent
    public static void scaledCloakDamage(LivingIncomingDamageEvent event) {
        ScaledCloakItem.onDamage(event);
    }
}
