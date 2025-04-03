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

public class DataComponentRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ReliquifiedTwilightForest.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStack>>> BUNDLE_LIKE_CONTENTS = DATA_COMPONENTS.register("gems",
            () -> DataComponentType.<List<ItemStack>>builder()
                    .persistent(ItemStack.CODEC.listOf())
                    .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ZOMBIE_TIME =        registerInt("zombie_time");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TWILIGHT_TIME =      registerInt("twilight_time");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LIFEDRAIN_TIME =     registerInt("absorption_time");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORTIFICATION_TIME = registerInt("fortification_time");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<UUID>>> ZOMBIES = DATA_COMPONENTS.register("zombies",
            () -> DataComponentType.<List<UUID>>builder()
                    .persistent(UUIDUtil.CODEC.listOf())
                    .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DONT_EAT = registerBoolean("dont_eat");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REGENERATION_TICKS = registerInt("regeneration_ticks");

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> registerInt(String id) {
        return DATA_COMPONENTS.register(id, () -> DataComponentType.<Integer>builder()
                .persistent(Codec.INT)
                .build());
    }

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> registerBoolean(String id) {
        return DATA_COMPONENTS.register(id, () -> DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .build());
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
