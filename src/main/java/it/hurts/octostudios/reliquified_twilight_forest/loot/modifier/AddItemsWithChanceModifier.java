package it.hurts.octostudios.reliquified_twilight_forest.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.Map;

public class AddItemsWithChanceModifier extends LootModifier {
    public static final Codec<AddItemsWithChanceModifier> CODEC = RecordCodecBuilder.create(inst ->
            codecStart(inst).and(
                    Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.FLOAT)
                            .fieldOf("item_modifiers").forGetter(e -> e.itemModifiers)
            ).apply(inst, AddItemsWithChanceModifier::new)
    );

    private final Map<Item, Float> itemModifiers;

    public AddItemsWithChanceModifier(LootItemCondition[] conditions, Map<Item, Float> itemModifiers) {
        super(conditions);
        this.itemModifiers = itemModifiers;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (Item item : itemModifiers.keySet()) {
            if (context.getRandom().nextFloat() < itemModifiers.get(item)) {
                generatedLoot.add(item.getDefaultInstance());
            }
        }

        return generatedLoot;
    }
}
