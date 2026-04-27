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

import static it.hurts.sskirillss.relics.init.RelicsDataComponents.construct;

public class RTDataComponent {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ReliquifiedTwilightForest.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStack>>> BUNDLE_LIKE_CONTENTS = construct("gems", ItemStack.CODEC.listOf());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LICH_CROWN_ZOMBIE_TIME =        construct("lich_crown/zombie_time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LICH_CROWN_TWILIGHT_TIME =      construct("lich_crown/twilight_time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LICH_CROWN_LIFEDRAIN_TIME =     construct("lich_crown/absorption_time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LICH_CROWN_FORTIFICATION_TIME = construct("lich_crown/fortification_time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<UUID>>> LICH_CROWN_ZOMBIES =               construct("lich_crown/zombies", UUIDUtil.CODEC.listOf());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<UUID>>> LICH_CROWN_ENTITIES =              construct("lich_crown/entities", UUIDUtil.CODEC.listOf());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>>      LICH_CROWN_MULTIPLIER = construct("lich_crown/multiplier", Codec.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>    LICH_CROWN_TIME =                  construct("lich_crown/time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SCALED_CLOAK_TIME = construct("scaled_cloak/time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> INVISIBILITY_CLOAK_TIME = construct("invisibility_cloak/time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MAPLE_SYRUP_DONT_EAT =             construct("maple_syrup/dont_eat", Codec.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAPLE_SYRUP_REGENERATION_TICKS =   construct("maple_syrup/regeneration_ticks", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FIREFLY_QUEEN_TIME = construct("firefly_queen/time", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FIREFLY_QUEEN_CHARGE = construct("firefly_queen/charge", Codec.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINOTAUR_HOOF_TIME = construct("minotaur_hoof/time", Codec.INT);



    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
