package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.data.loot.LootEntries;
import it.hurts.octostudios.reliquified_twilight_forest.init.EffectRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.entity.CicadaModel;
import twilightforest.network.CreateMovingCicadaSoundPacket;

@EventBusSubscriber
public class CicadaBottleItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("cicada_infestation")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.1, 0.2)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.1)
                                        .formatValue(MathButCool::percentage)
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(60, 140)
                                        .upgradeModifier(UpgradeOperation.ADD, 20)
                                        .formatValue(MathButCool::ticksToSecondsAndRoundSingleDigit)
                                        .build())
                                .maxLevel(5)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("cicada_infestation")
                                        .gem(GemShape.SQUARE, GemColor.GREEN)
                                        .build())
                                .build())
                        .maxLevel(5)
                        .build())
                .loot(LootData.builder()
                        .entry(LootEntries.TWILIGHT)
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xffa7e000)
                                .endColor(0x00014f2b)
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
    }

    @SubscribeEvent
    public static void onEntityHit(LivingDamageEvent.Post e) {
        Entity user = e.getSource().getEntity();
        LivingEntity target = e.getEntity();
        if (target.level().isClientSide
                || !(user instanceof LivingEntity source)
        ) return;

        for (ItemStack stack : EntityUtils.findEquippedCurios(user, ItemRegistry.CICADA_BOTTLE.get())) {
            float random = source.getRandom().nextFloat();
            if (!(stack.getItem() instanceof CicadaBottleItem relic)
                    || target.hasEffect(EffectRegistry.CICADA_INFESTATION)
                    || random > relic.getStatValue(stack, "cicada_infestation", "chance")
            ) continue;

            target.addEffect(new MobEffectInstance(EffectRegistry.CICADA_INFESTATION, (int) relic.getStatValue(stack, "cicada_infestation", "duration"), 0));
            relic.spreadRelicExperience(source, stack, 1);
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added e) {
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity.level().isClientSide
                || (e.getOldEffectInstance() != null
                && e.getEffectInstance().getEffect() == e.getOldEffectInstance().getEffect())
        ) return;

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(e.getEntity(), new CreateMovingCicadaSoundPacket(e.getEntity().getId()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide
                || !(event.getEntity() instanceof Mob mob)
        ) return;

        //mob.goalSelector.addGoal(-100, new MeleeAttackGoal(mob, 1.3d, true)); // crashes if the mob doesn't have attack damage attribute
        mob.targetSelector.addGoal(-100, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, false,
                toAttack -> toAttack.hasEffect(EffectRegistry.CICADA_INFESTATION))
        );
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        private static final ResourceLocation TEXTURE = TwilightForestMod.getModelTexture("cicada-model.png");

        @SubscribeEvent
        public static void renderLivingEntity(RenderLivingEvent.Post<?, ?> e) {
            if (!e.getEntity().hasEffect(EffectRegistry.CICADA_INFESTATION)) {
                return;
            }

            float bbWidth = e.getEntity().getBbWidth();
            float bbHeight = e.getEntity().getBbHeight();
            float bbMin = Math.min(bbWidth, bbHeight);
            float scale = Math.clamp(bbMin/1.5f, 0.25f, 1.5f);

            int max = (int) ((Mth.ceil(bbWidth*1.25f) + 1)/scale);

            for (int i = 0; i < max; i++) {
                CicadaModel model = new CicadaModel(CicadaModel.create().bakeRoot());
                float time = (e.getEntity().tickCount + e.getPartialTick());
                float sine = Mth.sin(time / 4f + i);
                float cosine = Mth.cos(time / 4f + i);

                PoseStack poseStack = e.getPoseStack();

                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(time * 2 + 360f / (max) * i));
                poseStack.translate(0, bbHeight / 2 + sine * (bbHeight / 20f) + 0.25, bbWidth / 2f + scale / 3f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90 + cosine * -10f));
                //poseStack.mulPose(Axis.XP.rotationDegrees(time * 8 + i * 10));
                poseStack.translate(0, 0.5, 0);
                poseStack.scale(scale, -scale, scale);
                model.renderToBuffer(
                        poseStack,
                        e.getMultiBufferSource().getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY
                );
                poseStack.popPose();
            }
        }
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MOD_ID;
    }
}
