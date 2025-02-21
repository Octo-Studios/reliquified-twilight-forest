package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import com.google.common.collect.Lists;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.api.HurtByTargetGoalWithPredicate;
import it.hurts.octostudios.reliquified_twilight_forest.gui.tooltip.GemTooltip;
import it.hurts.octostudios.reliquified_twilight_forest.init.DataComponentRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.item.Gem;
import it.hurts.octostudios.reliquified_twilight_forest.item.ability.LichCrownAbilities;
import it.hurts.octostudios.reliquified_twilight_forest.mixin.NearestAttackableTargetGoalAccessor;
import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.components.entity.FortificationShieldAttachment;
import twilightforest.data.tags.EntityTagGenerator;
import twilightforest.entity.monster.LoyalZombie;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFDataAttachments;
import twilightforest.loot.TFLootTables;
import twilightforest.util.entities.EntityUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static twilightforest.item.LifedrainScepterItem.animateTargetShatter;

@EventBusSubscriber
public class LichCrownItem extends RelicItem {
    public static final Predicate<LivingEntity> HAS_CROWN = target -> !EntityUtils.findEquippedCurio(target, ItemRegistry.LICH_CROWN.get()).isEmpty();

    public LichCrownItem() {
        super((new Item.Properties()).rarity(Rarity.RARE).stacksTo(1).component(DataComponentRegistry.GEMS, List.of()));
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("bone_pact").maxLevel(0).build())
                        .ability(AbilityData.builder("soulbound_gems")
                                .stat(StatData.builder("gem_amount")
                                        .initialValue(1, 3)
                                        .formatValue(Math::round)
                                        .upgradeModifier(UpgradeOperation.ADD, 1)
                                        .build())
                                .maxLevel(15)
                                .build())
                        .ability(LichCrownAbilities.ZOMBIE)
                        .ability(LichCrownAbilities.TWILIGHT)
                        .ability(LichCrownAbilities.LIFEDRAIN)
                        .ability(LichCrownAbilities.FORTIFICATION)
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(250)
                        .step(250)
                        .maxLevel(15)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("zombie")
                                        .gem(GemShape.SQUARE, GemColor.GREEN)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("twilight")
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("lifedrain")
                                        .gem(GemShape.SQUARE, GemColor.RED)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("fortification")
                                        .gem(GemShape.SQUARE, GemColor.YELLOW)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(DataComponentRegistry.GEMS)).map(list -> new GemTooltip(list, this.getSize(stack)))
                : Optional.empty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        LivingEntity livingEntity = slotContext.entity();
        if (livingEntity.level().isClientSide) return;

        if (relic.isAbilityUnlocked(stack, "zombie")) LichCrownAbilities.zombieTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "twilight")) LichCrownAbilities.twilightTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "lifedrain")) LichCrownAbilities.lifedrainTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "fortification")) LichCrownAbilities.fortificationTick(livingEntity, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        if ((newStack.getItem() instanceof LichCrownItem newRelic
                && newRelic.getAbilitiesComponent(newStack).equals(this.getAbilitiesComponent(stack)))
                || slotContext.entity().level().isClientSide
        ) return;

        if (slotContext.entity().level().isClientSide) return;
        LichCrownAbilities.fortificationUnequip(slotContext, stack);
        LichCrownAbilities.zombieUnequip(slotContext, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(stack.getItem() instanceof LichCrownItem relic)
                || !(entity instanceof Player player)
                || player.level().isClientSide
        ) return;

        relic.dropExcessive(player, stack);
    }


    @SubscribeEvent
    public static void onSlotClick(ContainerSlotClickEvent event) {
        if (event.getAction() != ClickAction.SECONDARY) return;

        ItemStack stack = event.getSlotStack();
        //if (!(stack.getItem() instanceof LichCrownItem relic)) return;

        ItemStack heldStack = event.getHeldStack();

        if (stack.getItem() instanceof LichCrownItem relic) {
            if (heldStack.getItem() instanceof Gem) {
                if (relic.tryInsert(event.getEntity(), stack, heldStack))
                    event.getEntity().playSound(SoundEvents.AMETHYST_BLOCK_STEP, 1f, 1.25f);

                event.setCanceled(true);
            } else if (heldStack.isEmpty()) {
                event.setCanceled(true);
                ItemStack gem = relic.pop(event.getEntity(), stack);
                if (gem.isEmpty()) return;

                event.getEntity().playSound(SoundEvents.ITEM_PICKUP, 0.75f, 1.25f);
                event.getEntity().containerMenu.setCarried(gem);
            }
        } else if (heldStack.getItem() instanceof LichCrownItem relic) {
            if (stack.getItem() instanceof Gem) {
                if (relic.tryInsert(event.getEntity(), heldStack, stack))
                    event.getEntity().playSound(SoundEvents.AMETHYST_BLOCK_STEP, 1f, 1.25f);

                event.setCanceled(true);
            } else if (stack.isEmpty()) {
                event.setCanceled(true);
                ItemStack gem = relic.pop(event.getEntity(), heldStack);
                if (gem.isEmpty()) return;

                event.getEntity().playSound(SoundEvents.ITEM_PICKUP, 0.75f, 1.25f);
                event.getSlot().set(gem);
            }
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof AbstractSkeleton skeleton)) return;

        skeleton.targetSelector.getAvailableGoals().removeIf(goal ->
                goal.getGoal() instanceof NearestAttackableTargetGoal<?> g
                        && ((NearestAttackableTargetGoalAccessor) g).getTargetType().isAssignableFrom(Player.class));
        skeleton.targetSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof HurtByTargetGoal);

        skeleton.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(skeleton, Player.class, true, HAS_CROWN.negate()));
        skeleton.targetSelector.addGoal(1, new HurtByTargetGoalWithPredicate(
                skeleton,
                TargetingConditions
                        .forCombat()
                        .ignoreLineOfSight()
                        .ignoreInvisibilityTesting()
                        .selector(HAS_CROWN.negate())
        ));
    }

    @SubscribeEvent
    public static void onLivingEntityTick(EntityTickEvent.Post e) {
        Entity entity = e.getEntity();
        if (entity.level().isClientSide
                || entity.tickCount % 10 != 0
                || !(entity instanceof AbstractSkeleton skeleton)
                || !(skeleton.getTarget() instanceof Player player)
                || HAS_CROWN.negate().test(player)
        ) return;

        skeleton.setTarget(null);
    }

    public int getSize(ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "soulbound_gems", "gem_amount"));
    }

    public boolean tryInsert(Player player, ItemStack stack, ItemStack toInsert) {
        List<ItemStack> notMutable = stack.get(DataComponentRegistry.GEMS);
        if (notMutable == null) return false;

        ArrayList<ItemStack> gems = Lists.newArrayList(notMutable);
        int size = this.getSize(stack);

        if (gems.size() >= size) return false;
        gems.add(toInsert.copyAndClear());
        gems.removeIf(ItemStack::isEmpty);
        this.setGemsContent(player, stack, gems);
        return true;
    }

    public ItemStack pop(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getGemContents(stack);
        if (notMutable.isEmpty()) return ItemStack.EMPTY;

        ArrayList<ItemStack> gems = Lists.newArrayList(notMutable);

        ItemStack toReturn = gems.removeLast();
        gems.removeIf(ItemStack::isEmpty);
        this.setGemsContent(player, stack, gems);
        return toReturn;
    }

    public void dropExcessive(Player player, ItemStack stack) {
        List<ItemStack> notMutable = getGemContents(stack);

        ArrayList<ItemStack> gems = Lists.newArrayList(notMutable);
        int size = this.getSize(stack);

        if (gems.size() <= size) return;
        List<ItemStack> toDrop = gems.subList(size - 1, gems.size() - 1);

        for (ItemStack drop : toDrop) {
            ItemEntity entity = player.drop(drop, false, true);
            if (entity != null) entity.setNoPickUpDelay();
        }

        toDrop.clear();
        gems.removeIf(ItemStack::isEmpty);
        this.setGemsContent(player, stack, gems);
    }

    public @NotNull List<ItemStack> getGemContents(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.GEMS, List.of());
    }

    public void setGemsContent(Player player, ItemStack stack, List<ItemStack> gems) {
        List<ItemStack> oldGems = getGemContents(stack);
        stack.set(DataComponentRegistry.GEMS, gems);
        this.onGemsChanged(player, stack, oldGems);
    }

    public int getGems(ItemStack stack, Gem gem) {
        return this.getGems(stack, gem, this.getGemContents(stack));
    }

    public int getGems(ItemStack stack, Gem gem, List<ItemStack> gems) {
        return (int) gems.stream().filter(itemStack -> itemStack.getItem() == gem).count();
    }

    public void onGemsChanged(Player player, ItemStack stack, List<ItemStack> oldGems) {
        if (player.level().isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) player.level();

        int oldShielding = this.getGems(stack, ItemRegistry.SHIELDING_GEM.get(), oldGems);
        int oldNecromancy = this.getGems(stack, ItemRegistry.NECROMANCY_GEM.get(), oldGems);
        int shielding = this.getGems(stack, ItemRegistry.SHIELDING_GEM.get());
        int necromancy = this.getGems(stack, ItemRegistry.NECROMANCY_GEM.get());
        int maxShields = shielding < 1 ? 0 : (int) Math.round(this.getStatValue(stack, "fortification", "max_shields"));
        int maxZombies = necromancy < 1 ? 0 : (int) Math.round(this.getStatValue(stack, "zombie", "max_zombies"));

        FortificationShieldAttachment attachment = player.getData(TFDataAttachments.FORTIFICATION_SHIELDS);
        ArrayList<UUID> zombies = Lists.newArrayList(stack.getOrDefault(DataComponentRegistry.ZOMBIES, List.of()));

        if (shielding < oldShielding && attachment.permanentShieldsLeft() > maxShields) {
            attachment.setShields(player, maxShields, false);
        }

        if (necromancy < oldNecromancy && zombies.size() > maxZombies) {
            List<UUID> toClear = maxZombies < 1 ? zombies : zombies.subList(
                    Math.max((int) Math.round(this.getStatValue(stack, "zombie", "max_zombies")), zombies.size()) - 1,
                    zombies.size()
            );
            toClear.forEach(uuid -> {
                if (level.getEntity(uuid) instanceof LoyalZombie zombie) {
                    zombie.discard();
                }
            });
            toClear.clear();
            stack.set(DataComponentRegistry.ZOMBIES, zombies);
        }
    }

    @Override
    public int getAbilityLevel(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification" -> this.getGems(stack, ItemRegistry.SHIELDING_GEM.get());
            case "zombie" -> this.getGems(stack, ItemRegistry.NECROMANCY_GEM.get());
            case "twilight" -> this.getGems(stack, ItemRegistry.TWILIGHT_GEM.get());
            case "lifedrain" -> this.getGems(stack, ItemRegistry.ABSORPTION_GEM.get());
            default -> super.getAbilityLevel(stack, ability);
        };
    }

    @Override
    public int getAbilityMaxLevel(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> (int) Math.round(this.getStatValue(stack, "soulbound_gems", "gem_amount"));
            default -> super.getAbilityMaxLevel(stack, ability);
        };
    }

    @Override
    public boolean mayUpgrade(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> false;
            default -> super.mayUpgrade(stack, ability);
        };
    }

    @Override
    public boolean mayReset(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> false;
            default -> super.mayReset(stack, ability);
        };
    }

    @Override
    public boolean isAbilityEnabled(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> this.getAbilityLevel(stack, ability) > 0;
            default -> super.isAbilityEnabled(stack, ability);
        };
    }

    @Override
    public boolean isAbilityUpgradeEnabled(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> false;
            default -> super.isAbilityUpgradeEnabled(stack, ability);
        };
    }

    @Override
    public boolean isAbilityResetEnabled(ItemStack stack, String ability) {
        return switch (ability) {
            case "fortification", "zombie", "twilight", "lifedrain" -> false;
            default -> super.isAbilityResetEnabled(stack, ability);
        };
    }

    @Override
    public boolean isRelicFlawless(ItemStack stack) {
        return (this.isAbilityFlawless(stack, "soulbound_gems")
                && this.isAbilityMaxLevel(stack, "soulbound_gems")
                && this.getGemContents(stack).size() >= this.getSize(stack)
        );
    }

    public static void makeRedMagicTrail(Level level, LivingEntity source, Vec3 target) {
        float r = 1.0F;
        float g = 0.5F;
        float b = 0.5F;
        Vec3 pos = source.position().add(0, source.getBbHeight() / 2f, 0);
        double distance = pos.distanceTo(target);

        for (double i = 0; i <= distance * 6; i++) {
            Vec3 particlePos = pos.subtract(target).scale(i / (distance * 6));
            particlePos = pos.subtract(particlePos);
            level.addParticle(
                    ParticleUtils.constructSimpleSpark(new Color(r, g, b, 0.25f), 0.35f, 20, 0.75f),
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    0,
                    0.05,
                    0
            );
        }
    }

    public static void explodeEntity(LivingEntity living, LivingEntity target, DamageSource damageSource) {
        Level level = target.level();
        if (!target.getType().is(EntityTagGenerator.LIFEDRAIN_DROPS_NO_FLESH) && level instanceof ServerLevel serverLevel && living instanceof Player player) {
            LootParams ctx = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, target)
                    .withParameter(LootContextParams.ORIGIN, target.getEyePosition())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                    .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                    .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player).create(LootContextParamSets.ENTITY);
            serverLevel.getServer().reloadableRegistries().getLootTable(TFLootTables.LIFEDRAIN_SCEPTER_KILL_BONUS).getRandomItems(ctx).forEach(target::spawnAtLocation);
            animateTargetShatter(serverLevel, target);
        }

        if (target instanceof Mob mob) {
            mob.spawnAnim();
        }
        SoundEvent deathSound = EntityUtil.getDeathSound(target);
        if (deathSound != null) {
            level.playSound(null, target.blockPosition(), deathSound, SoundSource.HOSTILE, 1.0F, target.getVoicePitch());
        }
        if (!target.isDeadOrDying()) {
            if (target instanceof Player) {
                target.hurt(TFDamageTypes.getEntityDamageSource(level, TFDamageTypes.LIFEDRAIN, living), Float.MAX_VALUE);
            } else {
                target.die(TFDamageTypes.getEntityDamageSource(level, TFDamageTypes.LIFEDRAIN, living));
                target.discard();
            }
        }
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MODID;
    }
}
