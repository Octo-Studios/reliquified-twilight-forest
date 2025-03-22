package it.hurts.octostudios.reliquified_twilight_forest.api;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OreCache {
    // Cache mapping each chunk position to a list of ore block positions.
    private static final Map<ChunkPos, List<BlockPos>> oreCache = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(2); // Background worker thread

    public static void scanChunkAsync(LevelAccessor level, ChunkAccess chunk) {
        executor.submit(() -> {
            List<BlockPos> ores = scanChunk(level, chunk);
            ChunkPos chunkPos = chunk.getPos();
            Minecraft.getInstance().execute(() -> oreCache.put(chunkPos, ores)); // Safe cache update
        });
    }
    // Called when a chunk loads to scan it once for ores.
    private static List<BlockPos> scanChunk(LevelAccessor level, ChunkAccess chunk) {
        List<BlockPos> orePositions = new ArrayList<>();
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int maxX = chunk.getPos().getMaxBlockX();
        int maxZ = chunk.getPos().getMaxBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.getBlockState(pos).is(Tags.Blocks.ORES)) {
                        orePositions.add(pos);
                    }
                }
            }
        }
        return orePositions;
    }

    // Returns ore positions cached in chunks near the center within the given radius.
    public static List<BlockPos> getNearbyOres(Level level, BlockPos center, float radius) {
        List<BlockPos> result = new ArrayList<>();
        int chunkRadius = Mth.ceil(radius) / 16 + 1;
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                ChunkPos cp = new ChunkPos(centerChunkX + dx, centerChunkZ + dz);
                List<BlockPos> chunkOres = oreCache.get(cp);
                if (chunkOres != null) {
                    for (BlockPos orePos : chunkOres) {
                        if (center.distSqr(orePos) <= (radius * radius)) {
                            result.add(orePos);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean hasChunk(ChunkPos pos) {
        return oreCache.containsKey(pos);
    }

    // Removes ore data for a chunk when it unloads.
    public static void removeChunk(ChunkPos pos) {
        oreCache.remove(pos);
    }
}