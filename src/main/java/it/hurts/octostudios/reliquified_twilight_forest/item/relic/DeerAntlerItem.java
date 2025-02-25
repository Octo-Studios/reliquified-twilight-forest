package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.network.CastRideAlongAbilityPacket;
import it.hurts.octostudios.reliquified_twilight_forest.util.MathButCool;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

@EventBusSubscriber
public class DeerAntlerItem extends RelicItem implements IRenderableCurio {
    public static final String ON_ANTLERS = "reliquified_twilight_forest:on_antlers";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("ride_along")
                                .stat(StatData.builder("entity_volume")
                                        .initialValue(0.5d, 2d)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 1d)
                                        .formatValue(MathButCool::roundSingleDigit)
                                        .build())
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);

        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide
                || entity.tickCount % 20 != 0
                || entity.getPassengers().isEmpty()
                || !entity.getPassengers().getFirst().getPersistentData().getBoolean(ON_ANTLERS)
                || !(stack.getItem() instanceof DeerAntlerItem relic)
        ) return;

        relic.spreadRelicExperience(entity, stack, 1);
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        super.castActiveAbility(stack, player, ability, type, stage);
        if (!player.level().isClientSide
                || !ability.equals("ride_along")
                || stage != CastStage.END
        ) return;

        boolean isMounting = player.getPassengers().isEmpty();
        int passengerId = -1;

        if (getEntityLookingAt(player, player.entityInteractionRange()) instanceof EntityHitResult result
                && result.getEntity() instanceof LivingEntity passenger
                && getBoundingBoxVolume(passenger.getBoundingBox()) <= this.getStatValue(stack, ability, "entity_volume")
        ) passengerId = passenger.getId();

        PacketDistributor.sendToServer(new CastRideAlongAbilityPacket(passengerId, isMounting));
    }

    public static EntityHitResult getEntityLookingAt(Player player, double maxDistance) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getViewVector(1.0F);
        Vec3 reachEnd = eyePosition.add(lookVector.scale(maxDistance));

        return ProjectileUtil.getEntityHitResult(player, eyePosition, reachEnd,
                player.getBoundingBox().expandTowards(lookVector.scale(maxDistance)).inflate(1.0),
                entity -> entity instanceof LivingEntity
                        && entity.isAlive()
                        && player.hasLineOfSight(entity)
                        && !player.getPassengers().contains(entity)
                        && entity != player,
                maxDistance * maxDistance);
    }

    @SubscribeEvent
    public static void riding(EntityMountEvent e) {
        if (!e.getLevel().isClientSide && e.isDismounting()) {
            e.getEntityMounting().getPersistentData().remove(ON_ANTLERS);
        }
    }


    @SubscribeEvent
    public static void livingDamage(LivingIncomingDamageEvent e) {
        Entity entity = e.getSource().getEntity();
        if (entity != null
                && entity.isPassenger()
                && entity.getPersistentData().getBoolean(ON_ANTLERS)
                && entity.getVehicle() == e.getEntity()
        ) e.setCanceled(true);
    }

    public static double getBoundingBoxVolume(AABB box) {
        return box.getXsize() * box.getYsize() * box.getZsize();
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MODID;
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

        PartDefinition bone = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(8, 11).addBox(18.4642F, -20.6733F, -0.998F, 2.0F, 10.0F, 2.0F,
                        new CubeDeformation(0.005F))
                .texOffs(14, 23).addBox(18.4642F, -21.6733F, 0.002F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F))
                .texOffs(8, 11).mirror().addBox(-20.4642F, -20.6733F, -0.998F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.005F)).mirror(false)
                .texOffs(14, 23).mirror().addBox(-19.4642F, -21.6733F, 0.002F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)).mirror(false), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 23).mirror().addBox(5.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)).mirror(false), PartPose.offsetAndRotation(-21.9278F, -12.3603F, 0.005F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(10, 23).mirror().addBox(2.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)).mirror(false), PartPose.offsetAndRotation(-16.3937F, -10.068F, -6.995F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r3 = bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(8, 23).mirror().addBox(2.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)).mirror(false), PartPose.offsetAndRotation(-11.326F, -14.4632F, 2.005F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r4 = bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(22, 10).mirror().addBox(2.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)).mirror(false)
                .texOffs(16, 11).mirror().addBox(1.0F, -8.0F, 2.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-8.2346F, -8.8478F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r5 = bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 4).mirror().addBox(-4.0F, -14.0F, 4.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(8, 4).mirror().addBox(-4.0F, -1.0F, 1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 10).mirror().addBox(1.0F, -1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 19).mirror().addBox(-7.0F, -8.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(22, 4).mirror().addBox(-7.0F, -1.0F, -5.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(-12.0F, -1.0F, -1.0F, 18.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-9.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r6 = bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(16, 22).mirror().addBox(-12.0F, -8.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 15).mirror().addBox(-12.0F, -1.0F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.1522F, -6.2346F, 2.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r7 = bone.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(12, 23).addBox(-6.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(21.9278F, -12.3603F, 0.005F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r8 = bone.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(10, 23).addBox(-3.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(16.3937F, -10.068F, -6.995F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r9 = bone.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(8, 23).addBox(-3.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(11.326F, -14.4632F, 2.005F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r10 = bone.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(22, 10).addBox(-3.0F, -9.0F, 3.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.005F))
                .texOffs(16, 11).addBox(-3.0F, -8.0F, 2.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.2346F, -8.8478F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r11 = bone.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 4).addBox(2.0F, -14.0F, 4.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 4).addBox(2.0F, -1.0F, 1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(24, 10).addBox(-3.0F, -1.0F, 1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(5.0F, -8.0F, -5.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 4).addBox(5.0F, -1.0F, -5.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-6.0F, -1.0F, -1.0F, 18.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -7.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r12 = bone.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(16, 22).addBox(10.0F, -8.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 15).addBox(10.0F, -1.0F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.1522F, -6.2346F, 2.0F, 0.0F, 0.0F, -0.3927F));

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
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(this.getTexture(stack)), stack.hasFoil());
        model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}
