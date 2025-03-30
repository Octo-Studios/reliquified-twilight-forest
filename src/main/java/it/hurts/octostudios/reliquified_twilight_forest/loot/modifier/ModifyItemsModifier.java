package it.hurts.octostudios.reliquified_twilight_forest.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.Map;

public class ModifyItemsModifier extends LootModifier {
    public static final MapCodec<ModifyItemsModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).and(
                    Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), BuiltInRegistries.ITEM.byNameCodec())
                            .fieldOf("item_modifiers").forGetter(e -> e.itemModifiers)
            ).apply(inst, ModifyItemsModifier::new)
    );

    private final Map<Item, Item> itemModifiers;

    public ModifyItemsModifier(LootItemCondition[] conditions, Map<Item, Item> itemModifiers) {
        super(conditions);
        this.itemModifiers = itemModifiers;
    }
    
    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot.stream().map(itemStack -> {
            if (itemModifiers.containsKey(itemStack.getItem())) {
                return itemModifiers.get(itemStack.getItem()).getDefaultInstance();
            }
            return itemStack;
        }).collect(ObjectArrayList.toList());
    }
}