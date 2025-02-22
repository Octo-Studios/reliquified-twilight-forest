package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.item.GemItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.*;
import it.hurts.sskirillss.relics.items.ItemBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedTwilightForest.MODID);
    public static final DeferredHolder<Item, GemItem> TWILIGHT_GEM   = ITEMS.register("twilight_gem",   () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> ABSORPTION_GEM = ITEMS.register("absorption_gem", () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> NECROMANCY_GEM = ITEMS.register("necromancy_gem", () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> SHIELDING_GEM  = ITEMS.register("shielding_gem",  () -> new GemItem());

    public static final DeferredHolder<Item, LichCrownItem> LICH_CROWN = ITEMS.register("lich_crown", LichCrownItem::new);
    public static final DeferredHolder<Item, MinotaurHoofItem> MINOTAUR_HOOF = ITEMS.register("minotaur_hoof", MinotaurHoofItem::new);
    public static final DeferredHolder<Item, HydraHeartItem> HYDRA_HEART = ITEMS.register("hydra_heart", HydraHeartItem::new);
    public static final DeferredHolder<Item, FireflyQueenItem> FIREFLY_QUEEN = ITEMS.register("firefly_queen", FireflyQueenItem::new);
    public static final DeferredHolder<Item, CicadaBottleItem> CICADA_BOTTLE = ITEMS.register("cicada_bottle", CicadaBottleItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
