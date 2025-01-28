package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.items.relics.MinotaurHoofItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedTwilightForest.MODID);
    public static final DeferredHolder<Item, MinotaurHoofItem> MINOTAUR_HOOF = ITEMS.register("minotaur_hoof", MinotaurHoofItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
