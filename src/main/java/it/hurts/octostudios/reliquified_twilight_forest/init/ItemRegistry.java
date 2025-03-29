package it.hurts.octostudios.reliquified_twilight_forest.init;

import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.item.BrokenCharmItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.GemItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.relic.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import twilightforest.init.TFItems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedTwilightForest.MOD_ID);
    public static final DeferredHolder<Item, GemItem> TWILIGHT_GEM   = ITEMS.register("twilight_gem",   () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> ABSORPTION_GEM = ITEMS.register("absorption_gem", () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> NECROMANCY_GEM = ITEMS.register("necromancy_gem", () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> SHIELDING_GEM  = ITEMS.register("shielding_gem",  () -> new GemItem());
    public static final DeferredHolder<Item, GemItem> FROST_GEM      = ITEMS.register("frost_gem",      () -> new GemItem());

    // 0.1
    public static final DeferredHolder<Item, LichCrownItem> LICH_CROWN = ITEMS.register("lich_crown", LichCrownItem::new);
    public static final DeferredHolder<Item, MinotaurHoofItem> MINOTAUR_HOOF = ITEMS.register("minotaur_hoof", MinotaurHoofItem::new);
    public static final DeferredHolder<Item, HydraHeartItem> HYDRA_HEART = ITEMS.register("hydra_heart", HydraHeartItem::new);

    // 0.2
    public static final DeferredHolder<Item, FireflyQueenItem> FIREFLY_QUEEN = ITEMS.register("firefly_queen", FireflyQueenItem::new);
    public static final DeferredHolder<Item, CicadaBottleItem> CICADA_BOTTLE = ITEMS.register("cicada_bottle", CicadaBottleItem::new);
    public static final DeferredHolder<Item, DeerAntlerItem> DEER_ANTLER = ITEMS.register("deer_antler", DeerAntlerItem::new);
    public static final DeferredHolder<Item, ScaledCloakItem> SCALED_CLOAK = ITEMS.register("scaled_cloak", ScaledCloakItem::new);

    // 0.3
    public static final DeferredHolder<Item, TwilightFeatherItem> TWILIGHT_FEATHER = ITEMS.register("twilight_feather", TwilightFeatherItem::new);
    public static final DeferredHolder<Item, ThornCrownItem> THORN_CROWN = ITEMS.register("thorn_crown", ThornCrownItem::new);
    public static final DeferredHolder<Item, CharmBackpackItem> CHARM_BACKPACK = ITEMS.register("charm_backpack", CharmBackpackItem::new);
    public static final DeferredHolder<Item, SteelCapeItem> STEEL_CAPE = ITEMS.register("steel_cape", SteelCapeItem::new);
    public static final DeferredHolder<Item, GoblinNoseItem> GOBLIN_NOSE = ITEMS.register("goblin_nose", GoblinNoseItem::new);

    // 0.4
    public static final DeferredHolder<Item, GiantGloveItem> GIANT_GLOVE = ITEMS.register("giant_glove", GiantGloveItem::new);
    public static final DeferredHolder<Item, ChromaticCloakItem> CHROMATIC_CLOAK = ITEMS.register("chromatic_cloak", ChromaticCloakItem::new);

    public static final DeferredHolder<Item, BrokenCharmItem> BROKEN_CHARM_OF_LIFE_1 =
            ITEMS.register("broken_charm_of_life_1", () -> new BrokenCharmItem(TFItems.CHARM_OF_LIFE_1.get(), 1));
    public static final DeferredHolder<Item, BrokenCharmItem> BROKEN_CHARM_OF_LIFE_2 =
            ITEMS.register("broken_charm_of_life_2", () -> new BrokenCharmItem(TFItems.CHARM_OF_LIFE_2.get(), 2));
    public static final DeferredHolder<Item, BrokenCharmItem> BROKEN_CHARM_OF_KEEPING_1 =
            ITEMS.register("broken_charm_of_keeping_1", () -> new BrokenCharmItem(TFItems.CHARM_OF_KEEPING_1.get(), 1));
    public static final DeferredHolder<Item, BrokenCharmItem> BROKEN_CHARM_OF_KEEPING_2 =
            ITEMS.register("broken_charm_of_keeping_2", () -> new BrokenCharmItem(TFItems.CHARM_OF_KEEPING_2.get(), 2));
    public static final DeferredHolder<Item, BrokenCharmItem> BROKEN_CHARM_OF_KEEPING_3 =
            ITEMS.register("broken_charm_of_keeping_3", () -> new BrokenCharmItem(TFItems.CHARM_OF_KEEPING_3.get(), 3));

    public static final Function<Item, Item> CHARMS = item -> {
        if (item == TFItems.CHARM_OF_LIFE_1.get()) return ItemRegistry.BROKEN_CHARM_OF_LIFE_1.get();
        if (item == TFItems.CHARM_OF_LIFE_2.get()) return ItemRegistry.BROKEN_CHARM_OF_LIFE_2.get();
        if (item == TFItems.CHARM_OF_KEEPING_1.get()) return ItemRegistry.BROKEN_CHARM_OF_KEEPING_1.get();
        if (item == TFItems.CHARM_OF_KEEPING_2.get()) return ItemRegistry.BROKEN_CHARM_OF_KEEPING_2.get();
        if (item == TFItems.CHARM_OF_KEEPING_3.get()) return ItemRegistry.BROKEN_CHARM_OF_KEEPING_3.get();
        return Items.AIR;
    };

    public static final Map<Item, Holder<MobEffect>> CHROMATIC_EFFECTS = new HashMap<>();
    static {
        CHROMATIC_EFFECTS.put(Items.RED_WOOL.asItem(), MobEffects.HEALTH_BOOST);
        CHROMATIC_EFFECTS.put(Items.ORANGE_WOOL.asItem(), MobEffects.FIRE_RESISTANCE);
        CHROMATIC_EFFECTS.put(Items.YELLOW_WOOL.asItem(), MobEffects.DAMAGE_BOOST);
        CHROMATIC_EFFECTS.put(Items.LIME_WOOL.asItem(), MobEffects.JUMP);
        CHROMATIC_EFFECTS.put(Items.CYAN_WOOL.asItem(), MobEffects.WATER_BREATHING);
        CHROMATIC_EFFECTS.put(Items.LIGHT_BLUE_WOOL.asItem(), MobEffects.MOVEMENT_SPEED);
        CHROMATIC_EFFECTS.put(Items.BLUE_WOOL.asItem(), MobEffects.NIGHT_VISION);
        CHROMATIC_EFFECTS.put(Items.GREEN_WOOL.asItem(), MobEffects.LUCK);
        CHROMATIC_EFFECTS.put(Items.PURPLE_WOOL.asItem(), MobEffects.DAMAGE_RESISTANCE);
        CHROMATIC_EFFECTS.put(Items.LIGHT_GRAY_WOOL.asItem(), MobEffects.INVISIBILITY);
        CHROMATIC_EFFECTS.put(Items.PINK_WOOL.asItem(), MobEffects.SLOW_FALLING);
        CHROMATIC_EFFECTS.put(Items.BROWN_WOOL.asItem(), MobEffects.DIG_SPEED);
        CHROMATIC_EFFECTS.put(Items.GRAY_WOOL.asItem(), MobEffects.LEVITATION);
        CHROMATIC_EFFECTS.put(Items.WHITE_WOOL.asItem(), MobEffects.GLOWING);
        CHROMATIC_EFFECTS.put(Items.BLACK_WOOL.asItem(), MobEffects.BAD_OMEN);
        CHROMATIC_EFFECTS.put(Items.MAGENTA_WOOL.asItem(), MobEffects.REGENERATION);
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
