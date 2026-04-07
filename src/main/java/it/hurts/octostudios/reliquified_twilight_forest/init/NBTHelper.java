package it.hurts.octostudios.reliquified_twilight_forest.init;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * NBT-based helper replacing DataComponentRegistry for Forge 1.20.1 compatibility.
 */
public class NBTHelper {
    // NBT keys matching the original DataComponentRegistry field names
    public static final String BUNDLE_LIKE_CONTENTS = "gems";
    public static final String ZOMBIE_TIME = "zombie_time";
    public static final String TWILIGHT_TIME = "twilight_time";
    public static final String LIFEDRAIN_TIME = "absorption_time";
    public static final String FORTIFICATION_TIME = "fortification_time";
    public static final String ZOMBIES = "zombies";
    public static final String ENTITIES = "entities";
    public static final String MULTIPLIER = "multiplier";
    public static final String TIME = "time";
    public static final String DONT_EAT = "dont_eat";
    public static final String REGENERATION_TICKS = "regeneration_ticks";

    // --- Bundle-like contents ---

    public static List<ItemStack> getBundleLikeContents(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(BUNDLE_LIKE_CONTENTS)) return List.of();
        ListTag list = stack.getTag().getList(BUNDLE_LIKE_CONTENTS, Tag.TAG_COMPOUND);
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(ItemStack.of(list.getCompound(i)));
        }
        return result;
    }

    public static void setBundleLikeContents(ItemStack stack, List<ItemStack> contents) {
        ListTag list = new ListTag();
        for (ItemStack s : contents) {
            if (!s.isEmpty()) list.add(s.save(new CompoundTag()));
        }
        stack.getOrCreateTag().put(BUNDLE_LIKE_CONTENTS, list);
    }

    // --- Integer fields ---

    public static int getInt(ItemStack stack, String key) {
        if (!stack.hasTag()) return 0;
        return stack.getTag().getInt(key);
    }

    public static int getIntOrDefault(ItemStack stack, String key, int def) {
        if (!stack.hasTag() || !stack.getTag().contains(key)) return def;
        return stack.getTag().getInt(key);
    }

    public static void setInt(ItemStack stack, String key, int value) {
        stack.getOrCreateTag().putInt(key, value);
    }

    // --- Float fields ---

    public static float getFloat(ItemStack stack, String key) {
        if (!stack.hasTag()) return 0f;
        return stack.getTag().getFloat(key);
    }

    public static float getFloatOrDefault(ItemStack stack, String key, float def) {
        if (!stack.hasTag() || !stack.getTag().contains(key)) return def;
        return stack.getTag().getFloat(key);
    }

    public static void setFloat(ItemStack stack, String key, float value) {
        stack.getOrCreateTag().putFloat(key, value);
    }

    // --- Boolean fields ---

    public static boolean getBoolean(ItemStack stack, String key) {
        if (!stack.hasTag()) return false;
        return stack.getTag().getBoolean(key);
    }

    public static void setBoolean(ItemStack stack, String key, boolean value) {
        stack.getOrCreateTag().putBoolean(key, value);
    }

    public static boolean hasBoolean(ItemStack stack, String key) {
        return stack.hasTag() && stack.getTag().contains(key);
    }

    public static void removeKey(ItemStack stack, String key) {
        if (stack.hasTag()) {
            stack.getTag().remove(key);
        }
    }

    // --- UUID list fields ---

    public static List<UUID> getUUIDList(ItemStack stack, String key) {
        if (!stack.hasTag() || !stack.getTag().contains(key)) return List.of();
        ListTag list = stack.getTag().getList(key, Tag.TAG_INT_ARRAY);
        List<UUID> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(NbtUtils.loadUUID(list.get(i)));
        }
        return result;
    }

    public static void setUUIDList(ItemStack stack, String key, List<UUID> uuids) {
        ListTag list = new ListTag();
        for (UUID uuid : uuids) {
            list.add(NbtUtils.createUUID(uuid));
        }
        stack.getOrCreateTag().put(key, list);
    }

    // Convenience methods using the predefined keys

    public static int getZombieTime(ItemStack stack) { return getInt(stack, ZOMBIE_TIME); }
    public static void setZombieTime(ItemStack stack, int v) { setInt(stack, ZOMBIE_TIME, v); }

    public static int getTwilightTime(ItemStack stack) { return getInt(stack, TWILIGHT_TIME); }
    public static void setTwilightTime(ItemStack stack, int v) { setInt(stack, TWILIGHT_TIME, v); }

    public static int getLifedrainTime(ItemStack stack) { return getInt(stack, LIFEDRAIN_TIME); }
    public static void setLifedrainTime(ItemStack stack, int v) { setInt(stack, LIFEDRAIN_TIME, v); }

    public static int getFortificationTime(ItemStack stack) { return getInt(stack, FORTIFICATION_TIME); }
    public static void setFortificationTime(ItemStack stack, int v) { setInt(stack, FORTIFICATION_TIME, v); }

    public static List<UUID> getZombies(ItemStack stack) { return getUUIDList(stack, ZOMBIES); }
    public static void setZombies(ItemStack stack, List<UUID> v) { setUUIDList(stack, ZOMBIES, v); }

    public static List<UUID> getEntities(ItemStack stack) { return getUUIDList(stack, ENTITIES); }
    public static void setEntities(ItemStack stack, List<UUID> v) { setUUIDList(stack, ENTITIES, v); }

    public static float getMultiplier(ItemStack stack) { return getFloat(stack, MULTIPLIER); }
    public static void setMultiplier(ItemStack stack, float v) { setFloat(stack, MULTIPLIER, v); }

    public static int getTime(ItemStack stack) { return getInt(stack, TIME); }
    public static void setTime(ItemStack stack, int v) { setInt(stack, TIME, v); }

    public static boolean getDontEat(ItemStack stack) { return getBoolean(stack, DONT_EAT); }
    public static void setDontEat(ItemStack stack, boolean v) { setBoolean(stack, DONT_EAT, v); }
    public static boolean hasDontEat(ItemStack stack) { return hasBoolean(stack, DONT_EAT); }

    public static int getRegenerationTicks(ItemStack stack) { return getInt(stack, REGENERATION_TICKS); }
    public static void setRegenerationTicks(ItemStack stack, int v) { setInt(stack, REGENERATION_TICKS, v); }
}
