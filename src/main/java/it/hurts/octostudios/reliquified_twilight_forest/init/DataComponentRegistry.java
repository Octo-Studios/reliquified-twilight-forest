package it.hurts.octostudios.reliquified_twilight_forest.init;

import com.mojang.serialization.Codec;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class DataComponentRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ReliquifiedTwilightForest.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStack>>> GEMS = DATA_COMPONENTS.register("gems", () ->
            DataComponentType.<List<ItemStack>>builder()
                    .persistent(ItemStack.CODEC.listOf())
                    .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORTIFICATION_TIME = DATA_COMPONENTS.register("fortification_time", DataComponentRegistry::integer);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LIFEDRAIN_TIME = DATA_COMPONENTS.register("absorption_time", DataComponentRegistry::integer);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ZOMBIE_TIME = DATA_COMPONENTS.register("zombie_time", DataComponentRegistry::integer);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<UUID>>> ZOMBIES = DATA_COMPONENTS.register("zombies", () ->
        DataComponentType.<List<UUID>>builder()
                .persistent(UUIDUtil.CODEC.listOf())
                .build()
    );

    public static DataComponentType<Integer> integer() {
        return DataComponentType.<Integer>builder()
                .persistent(Codec.INT)
                .build();
    }
    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
