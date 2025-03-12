package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.item.BrokenCharm;
import it.hurts.octostudios.reliquified_twilight_forest.item.GemItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import twilightforest.init.TFItems;

import java.util.function.Function;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedTwilightForest.MOD_ID);
    public static final DeferredHolder<Item, GemItem> TWILIGHT_GEM   = ITEMS.register("twilight_gem",   () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> ABSORPTION_GEM = ITEMS.register("absorption_gem", () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> NECROMANCY_GEM = ITEMS.register("necromancy_gem", () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> SHIELDING_GEM  = ITEMS.register("shielding_gem",  () -> new GemItem());

    public static final DeferredHolder<Item, LichCrownItem> LICH_CROWN = ITEMS.register("lich_crown", LichCrownItem::new);
    public static final DeferredHolder<Item, MinotaurHoofItem> MINOTAUR_HOOF = ITEMS.register("minotaur_hoof", MinotaurHoofItem::new);
    public static final DeferredHolder<Item, HydraHeartItem> HYDRA_HEART = ITEMS.register("hydra_heart", HydraHeartItem::new);
    public static final DeferredHolder<Item, FireflyQueenItem> FIREFLY_QUEEN = ITEMS.register("firefly_queen", FireflyQueenItem::new);
    public static final DeferredHolder<Item, CicadaBottleItem> CICADA_BOTTLE = ITEMS.register("cicada_bottle", CicadaBottleItem::new);
    public static final DeferredHolder<Item, DeerAntlerItem> DEER_ANTLER = ITEMS.register("deer_antler", DeerAntlerItem::new);
    public static final DeferredHolder<Item, ScaledCloakItem> SCALED_CLOAK = ITEMS.register("scaled_cloak", ScaledCloakItem::new);
    public static final DeferredHolder<Item, TwilightFeatherItem> TWILIGHT_FEATHER = ITEMS.register("twilight_feather", TwilightFeatherItem::new);
    public static final DeferredHolder<Item, ThornCrown> THORN_CROWN = ITEMS.register("thorn_crown", ThornCrown::new);
    public static final DeferredHolder<Item, CharmBackpackItem> CHARM_BACKPACK = ITEMS.register("charm_backpack", CharmBackpackItem::new);
    public static final DeferredHolder<Item, SteelCapeItem> STEEL_CAPE = ITEMS.register("steel_cape", SteelCapeItem::new);


    public static final DeferredHolder<Item, BrokenCharm> BROKEN_CHARM_OF_LIFE_1 =
            ITEMS.register("broken_charm_of_life_1", () -> new BrokenCharm(TFItems.CHARM_OF_LIFE_1.get(), 1));
    public static final DeferredHolder<Item, BrokenCharm> BROKEN_CHARM_OF_LIFE_2 =
            ITEMS.register("broken_charm_of_life_2", () -> new BrokenCharm(TFItems.CHARM_OF_LIFE_2.get(), 2));
    public static final DeferredHolder<Item, BrokenCharm> BROKEN_CHARM_OF_KEEPING_1 =
            ITEMS.register("broken_charm_of_keeping_1", () -> new BrokenCharm(TFItems.CHARM_OF_KEEPING_1.get(), 1));
    public static final DeferredHolder<Item, BrokenCharm> BROKEN_CHARM_OF_KEEPING_2 =
            ITEMS.register("broken_charm_of_keeping_2", () -> new BrokenCharm(TFItems.CHARM_OF_KEEPING_2.get(), 2));
    public static final DeferredHolder<Item, BrokenCharm> BROKEN_CHARM_OF_KEEPING_3 =
            ITEMS.register("broken_charm_of_keeping_3", () -> new BrokenCharm(TFItems.CHARM_OF_KEEPING_3.get(), 3));

    public static final Function<Item, Item> CHARMS = item -> {
        if (item == TFItems.CHARM_OF_LIFE_1.get()) return ItemRegistry.BROKEN_CHARM_OF_LIFE_1.get();
        if (item == TFItems.CHARM_OF_LIFE_2.get()) return ItemRegistry.BROKEN_CHARM_OF_LIFE_2.get();
        if (item == TFItems.CHARM_OF_KEEPING_1.get()) return ItemRegistry.BROKEN_CHARM_OF_KEEPING_1.get();
        if (item == TFItems.CHARM_OF_KEEPING_2.get()) return ItemRegistry.BROKEN_CHARM_OF_KEEPING_2.get();
        if (item == TFItems.CHARM_OF_KEEPING_3.get()) return ItemRegistry.BROKEN_CHARM_OF_KEEPING_3.get();
        return Items.AIR;
    };

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
