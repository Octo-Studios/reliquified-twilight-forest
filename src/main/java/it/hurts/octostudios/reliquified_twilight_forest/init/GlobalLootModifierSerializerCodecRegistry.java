package it.hurts.octostudios.reliquified_twilight_forest.init;

import com.mojang.serialization.Codec;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.loot.modifier.AddItemsWithChanceModifier;
import it.hurts.octostudios.reliquified_twilight_forest.loot.modifier.ModifyItemsModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GlobalLootModifierSerializerCodecRegistry {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ReliquifiedTwilightForest.MOD_ID);

    public static final RegistryObject<Codec<ModifyItemsModifier>> MODIFY_ITEMS =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("modify_items", () -> ModifyItemsModifier.CODEC);

    public static final RegistryObject<Codec<AddItemsWithChanceModifier>> ADD_ITEMS =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("add_items", () -> AddItemsWithChanceModifier.CODEC);

    public static void register(IEventBus bus) {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}
