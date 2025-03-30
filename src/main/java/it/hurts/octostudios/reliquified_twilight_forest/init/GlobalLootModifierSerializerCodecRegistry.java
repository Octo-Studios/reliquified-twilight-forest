package it.hurts.octostudios.reliquified_twilight_forest.init;

import com.mojang.serialization.MapCodec;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.loot.modifier.AddItemsWithChanceModifier;
import it.hurts.octostudios.reliquified_twilight_forest.loot.modifier.ModifyItemsModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class GlobalLootModifierSerializerCodecRegistry {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ReliquifiedTwilightForest.MOD_ID);

    public static final Supplier<MapCodec<ModifyItemsModifier>> MODIFY_ITEMS =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("modify_items", () -> ModifyItemsModifier.CODEC);

    public static final Supplier<MapCodec<AddItemsWithChanceModifier>> ADD_ITEMS =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("add_items", () -> AddItemsWithChanceModifier.CODEC);

    public static void register(IEventBus bus) {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}
