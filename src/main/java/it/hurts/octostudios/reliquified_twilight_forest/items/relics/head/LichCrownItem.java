package it.hurts.octostudios.reliquified_twilight_forest.items.relics.head;

import it.hurts.octostudios.reliquified_twilight_forest.init.RTItems;
import it.hurts.octostudios.reliquified_twilight_forest.items.base.RTBundleLikeRelicItem;
import it.hurts.octostudios.reliquified_twilight_forest.items.ability.LichCrownAbilities;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.function.Predicate;

public class LichCrownItem extends RTBundleLikeRelicItem  {
    public static final Predicate<LivingEntity> HAS_CROWN = target -> !EntityUtils.findEquippedCurio(target, RTItems.LICH_CROWN.get()).isEmpty();

    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("bone_pact").initialMaxLevel(0).build())
                        .ability(AbilityTemplate.builder("soulbound_gems")
                                .stat(AbilityStatTemplate.builder("gem_amount")
                                        .initialValue(1, 3)
                                        .formatValue(Math::round).targetValue(RelicsScalingModels.ADDITIVE.get(), 1)
                                        .build())
                                .initialMaxLevel(15)
                                .build())
                        .ability(LichCrownAbilities.FORTIFICATION)
                        .ability(LichCrownAbilities.LIFEDRAIN)
                        .ability(LichCrownAbilities.TWILIGHT)
                        .ability(LichCrownAbilities.ZOMBIE)
                        .ability(LichCrownAbilities.FROSTBITE)
                        .ability(LichCrownAbilities.BIOME_BURN)
                        .ability(LichCrownAbilities.ETHEREAL_GUARD)
                        .ability(LichCrownAbilities.VENDETTA)
                        .ability(LichCrownAbilities.MIRROR_LEECH)
                        .ability(LichCrownAbilities.FRENZY)
                        .ability(AbilityTemplate.builder("twilight_sovereign")
                                .initialMaxLevel(0)
                                .build())
                        .build())
                .leveling(LevelingTemplate.builder()
                        .initialCost(250)
                        .step(250)
                        .build())
                .build();
    }
/*
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof LichCrownItem relic)) return;
        LivingEntity livingEntity = slotContext.entity();

        if (livingEntity.level().isClientSide) return;

        if (relic.isAbilityUnlocked(stack, "zombie")) LichCrownAbilities.zombieTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "twilight")) LichCrownAbilities.twilightTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "lifedrain")) LichCrownAbilities.lifedrainTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "fortification")) LichCrownAbilities.fortificationTick(livingEntity, stack);
        if (relic.isAbilityUnlocked(stack, "frenzy")) LichCrownAbilities.frenzyTick(livingEntity, stack, this);

        if (relic.isAbilityUnlocked(stack, "twilight_sovereign") && livingEntity.level().dimension().equals(TFDimension.DIMENSION_KEY)) {
            livingEntity.addEffect(new MobEffectInstance(EffectRegistry.IMMORTALITY, 45, 0, true, false, false));
        }
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

    @Override
    public int getMaxSlots(ItemStack stack) {
        return (int) Math.round(this.getStatValue(stack, "soulbound_gems", "gem_amount"));
    }

    @Override
    public void onContentsChanged(Player player, ItemStack stack, List<ItemStack> oldContents) {
        if (player.level().isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) player.level();

        int oldShielding = this.getItemCount(stack, RTItems.SHIELDING_GEM.get(), oldContents);
        int oldNecromancy = this.getItemCount(stack, RTItems.NECROMANCY_GEM.get(), oldContents);
        int shielding = this.getItemCount(stack, RTItems.SHIELDING_GEM.get());
        int necromancy = this.getItemCount(stack, RTItems.NECROMANCY_GEM.get());
        int maxShields = shielding < 1 ? 0 : (int) Math.round(this.getStatValue(stack, "fortification", "max_shields"));
        int maxZombies = necromancy < 1 ? 0 : (int) Math.round(this.getStatValue(stack, "zombie", "max_zombies"));

        FortificationShieldAttachment attachment = player.getData(TFDataAttachments.FORTIFICATION_SHIELDS);
        ArrayList<UUID> zombies = Lists.newArrayList(stack.getOrDefault(RTDataComponent.LICH_CROWN_ZOMBIES, List.of()));

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
            stack.set(RTDataComponent.LICH_CROWN_ZOMBIES, zombies);
        }
    }

    @Override
    public Predicate<ItemStack> getPredicate() {
        return stack -> stack.getItem() instanceof Gem;
    }

    @Override
    public int getAbilityLevel(ItemStack stack, String ability) {
        if (LichCrownAbilities.GEMS.containsKey(ability)) {
            return this.getItemCount(stack, LichCrownAbilities.GEMS.get(ability).get());
        }

        return super.getAbilityLevel(stack, ability);
    }

    @Override
    public int getAbilityMaxLevel(ItemStack stack, String ability) {
        if (LichCrownAbilities.ABILITIES.containsKey(ability)) {
            return (int) Math.round(this.getStatValue(stack, "soulbound_gems", "gem_amount"));
        }

        return super.getAbilityMaxLevel(stack, ability);
    }

    @Override
    public boolean mayUpgrade(ItemStack stack, String ability) {
        if (LichCrownAbilities.ABILITIES.containsKey(ability)) {
            return false;
        }

        return super.mayUpgrade(stack, ability);
    }

    @Override
    public boolean mayReset(ItemStack stack, String ability) {
        if (LichCrownAbilities.ABILITIES.containsKey(ability)) {
            return false;
        }

        return super.mayReset(stack, ability);
    }

    @Override
    public boolean isAbilityEnabled(ItemStack stack, String ability) {
        if (LichCrownAbilities.ABILITIES.containsKey(ability)) {
            return this.getAbilityLevel(stack, ability) > 0;
        }

        if (!ability.equals("twilight_sovereign")) {
            return super.isAbilityEnabled(stack, ability);
        }

        for (String key : LichCrownAbilities.ABILITIES.keySet()) {
            if (this.getAbilityLevel(stack, key) <= 0) {
                return false;
            }
        }

        return super.isAbilityEnabled(stack, ability);
    }

    @Override
    public boolean isAbilityUpgradeEnabled(ItemStack stack, String ability) {
        if (LichCrownAbilities.ABILITIES.containsKey(ability)) {
            return false;
        }

        return super.isAbilityUpgradeEnabled(stack, ability);
    }

    @Override
    public boolean isAbilityResetEnabled(ItemStack stack, String ability) {
        if (LichCrownAbilities.ABILITIES.containsKey(ability)) {
            return false;
        }

        return super.isAbilityResetEnabled(stack, ability);
    }

    @Override
    public boolean isRelicFlawless(ItemStack stack) {
        for (String key : LichCrownAbilities.ABILITIES.keySet()) {
            if (!this.isAbilityFlawless(stack, key)) {
                return false;
            }
        }

        if (!this.isAbilityUnlocked(stack, "twilight_sovereign") || !this.isAbilityFlawless(stack, "twilight_sovereign")) {
            return false;
        }

        return this.isAbilityFlawless(stack, "soulbound_gems") && this.isAbilityMaxLevel(stack, "soulbound_gems");
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
    @OnlyIn(Dist.CLIENT)
    public List<String> headParts() {
        return List.of("head");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4f), 0);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-2.5F, -9.0F, -4.5F, 5.0F, 2.0F, 2.0F,
                        new CubeDeformation(0.005F))
                .texOffs(0, 33).addBox(-2.5F, -9.0F, 2.5F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 29).addBox(-1.0F, -7.0F, -4.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 26).addBox(-0.5F, -16.0F, -3.5F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(-4.5F, -7.0F, -4.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(30, 6).addBox(2.5F, -7.0F, -4.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(-4.5F, -7.0F, 4.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(-4.499F, -7.0F, 2.501F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(-4.499F, -7.0F, -4.499F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(4.501F, -7.0F, -4.499F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(4.501F, -7.0F, 2.501F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(-4.5F, -7.0F, 4.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 29).addBox(-1.0F, -7.0F, 4.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(30, 6).addBox(2.5F, -7.0F, 4.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(14, 22).addBox(2.5F, -14.0F, -4.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 20).addBox(-4.5F, -14.0F, -4.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 4).addBox(-1.0F, -15.0F, -4.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(2.5F, -11.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 4).addBox(-4.5F, -11.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(14, 22).addBox(2.5F, -14.0F, 2.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 4).addBox(-1.0F, -15.0F, 2.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 20).addBox(-4.5F, -14.0F, 2.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 26).addBox(-0.5F, -16.0F, 3.5F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-4.5F, -9.0F, -4.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 11).addBox(2.5F, -9.0F, -4.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CurioModel model = this.getModel(stack);
        matrixStack.pushPose();
        LivingEntity entity = slotContext.entity();
        ICurioRenderer.followBodyRotations(entity, model);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.entityCutout(this.getTexture(stack)), stack.hasFoil());
        matrixStack.scale(1.0047f, 1.0047f, 1.0047f);
        model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    public static LevelingSourceData getSource(AbilityTemplate data, GemColor color) {
        return LevelingSourceData.abilityBuilder(data.getId()).gem(GemShape.SQUARE, color).build();
    }
    */
}
