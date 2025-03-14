package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import com.mojang.blaze3d.shaders.FogShape;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.BundleLikeRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.item.OreCache;
import it.hurts.octostudios.reliquified_twilight_forest.network.UpdateChunkPacket;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.client.particles.BasicColoredParticle;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class GoblinNoseItem extends BundleLikeRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("vein_seeker")
                                .stat(StatData.builder("radius")
                                        .initialValue(4, 8)
                                        .upgradeModifier(UpgradeOperation.ADD, 1.6)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .stat(StatData.builder("filter_slots")
                                        .initialValue(0, 0)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .formatValue(Math::round)
                                        .build())
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("vein_seeker")
                                        .gem(GemShape.SQUARE, GemColor.GREEN)
                                        .build())
                                .build())
                        .maxLevel(5)
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.TROLL)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        if (!(stack.getItem() instanceof GoblinNoseItem relic)
                || !relic.isAbilityTicking(stack, "vein_seeker")
        ) return;

        if (!livingEntity.level().isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 30, 0, true, false, false));
        }

        if (!livingEntity.level().isClientSide || livingEntity != Minecraft.getInstance().player) {
            return;
        }

        // Cached list of found ore positions.
        List<BlockPos> ORES = OreCache.getNearbyOres(livingEntity.level(), livingEntity.blockPosition(), (float) relic.getStatValue(stack, "vein_seeker", "radius"));
        ORES.forEach(bp -> {
            BlockState state = livingEntity.level().getBlockState(bp);
            if (livingEntity.getRandom().nextFloat() < 0.66f
                    || !state.is(Tags.Blocks.ORES)
                    || (!relic.getContents(stack).isEmpty() && relic.getItemCount(stack, state.getBlock().asItem()) < 1)
            ) return;

            GoblinNoseItem.spawnOreParticles(livingEntity, bp);
        });
    }

    @Override
    public int getSize(ItemStack stack) {
        if (!(stack.getItem() instanceof GoblinNoseItem relic)) {
            return 0;
        }

        return (int) Math.round(relic.getStatValue(stack, "vein_seeker", "filter_slots"));
    }

    @EventBusSubscriber
    public static class CommonEvents {
        @SubscribeEvent
        public static void onChunkLoad(ChunkEvent.Load event) {
            if (!event.getLevel().isClientSide()) return;

            ChunkAccess chunk = event.getChunk();
            OreCache.scanChunkAsync(event.getLevel(), chunk);
        }

        @SubscribeEvent
        public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (event.getLevel().isClientSide()) return;

            ChunkAccess chunk = event.getLevel().getChunk(event.getPos());
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) event.getLevel(), chunk.getPos(), new UpdateChunkPacket(chunk.getPos()));
        }

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getPlayer(), ItemRegistry.GOBLIN_NOSE.get());
            if (event.getLevel().isClientSide()) return;

            ChunkAccess chunk = event.getLevel().getChunk(event.getPos());
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) event.getLevel(), chunk.getPos(), new UpdateChunkPacket(chunk.getPos()));

            if (stack.getItem() instanceof GoblinNoseItem relic
                    && relic.isAbilityTicking(stack, "vein_seeker")
                    && event.getState().is(Tags.Blocks.ORES)
            ) relic.spreadRelicExperience(event.getPlayer(), stack, 1);
        }

        @SubscribeEvent
        public static void onChunkUnload(ChunkEvent.Unload event) {
            if (!event.getLevel().isClientSide()) {
                return;
            }

            ChunkAccess chunk = event.getChunk();
            OreCache.removeChunk(chunk.getPos());
        }
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void changeFog(ViewportEvent.RenderFog event) {
            ItemStack stack = EntityUtils.findEquippedCurio(Minecraft.getInstance().player, ItemRegistry.GOBLIN_NOSE.get());
            if (!(stack.getItem() instanceof GoblinNoseItem relic)
                    || !relic.isAbilityTicking(stack, "vein_seeker")
                    || !(Minecraft.getInstance().player.getEffect(MobEffects.DARKNESS) instanceof MobEffectInstance inst && inst.getDuration() <= 30)
            ) return;

            float newDistance = (float) relic.getStatValue(stack, "vein_seeker", "radius");
            event.scaleNearPlaneDistance(newDistance / 15f);
            event.scaleFarPlaneDistance(newDistance / 15f);
            event.setFogShape(FogShape.SPHERE);
            event.setCanceled(true);
        }
    }

    public static void spawnOreParticles(LivingEntity livingEntity, BlockPos pos) {
        AABB area = new AABB(pos.getCenter(), pos.getCenter()).inflate(0.5);
        Random random = new Random();
        Level level = livingEntity.level();

        ParticleOptions ORE_PARTICLE = new BasicColoredParticle.Options(BasicColoredParticle.Constructor.builder()
                .color(new Color(random.nextInt(230, 255), random.nextInt(170, 255), 0, 255).getRGB())
                .diameter(random.nextFloat(0.2f, 0.35f))
                .lifetime(20)
                .scaleModifier(0.8f)
                .physical(false)
                .visibleThroughWalls(true)
                .roll(0.15F)
                .build());

        level.addParticle(
                ORE_PARTICLE, true,
                random.nextDouble(area.minX, area.maxX),
                random.nextDouble(area.minY, area.maxY),
                random.nextDouble(area.minZ, area.maxZ),
                0, 0, 0
        );

        if (random.nextFloat() < 0.1f && livingEntity.tickCount % 4 == 0) {
            ORE_PARTICLE = new BasicColoredParticle.Options(BasicColoredParticle.Constructor.builder()
                    .color(new Color(255, 255, 255, 255).getRGB())
                    .diameter(0.4f)
                    .lifetime(60)
                    .scaleModifier(0.95f)
                    .physical(false)
                    .visibleThroughWalls(true)
                    .roll(0.25F)
                    .build());
            level.addParticle(
                    ORE_PARTICLE, true,
                    random.nextDouble(area.minX, area.maxX),
                    random.nextDouble(area.minY, area.maxY),
                    random.nextDouble(area.minZ, area.maxZ),
                    0, 0, 0
            );
        }
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(Tags.Blocks.ORES);
    }
}
