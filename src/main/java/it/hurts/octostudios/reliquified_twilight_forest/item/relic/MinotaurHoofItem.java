package it.hurts.octostudios.reliquified_twilight_forest.item.relic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.octostudios.reliquified_twilight_forest.ReliquifiedTwilightForest;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.sskirillss.relics.client.models.items.CurioModel;
import it.hurts.sskirillss.relics.client.models.items.SidedCurioModel;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.style.BeamsData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

@EventBusSubscriber
public class MinotaurHoofItem extends RelicItem implements IRenderableCurio {
    private static final ResourceLocation MOVEMENT_MODIFIER = ResourceLocation.fromNamespaceAndPath(ReliquifiedTwilightForest.MODID, "momentum_rush");

    private static final int MAX_TIME = 60;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("momentum_rush")
                                .stat(StatData.builder("max_speed_multiplier")
                                        .initialValue(0.25f, 0.3f)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .upgradeModifier(UpgradeOperation.ADD, 0.1f)
                                        .build())
                                .stat(StatData.builder("damage_reduction")
                                        .initialValue(0.25f, 0.3f)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.125f)
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(1, 2)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .upgradeModifier(UpgradeOperation.ADD, 1f)
                                        .build())
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .step(125)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("momentum_rush")
                                        .gem(GemShape.SQUARE, GemColor.PURPLE)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .beams(BeamsData.builder()
                                .startColor(0xffe0400c)
                                .endColor(0x0088410c)
                                .build())
                        .build())
                .build();
    }

    public int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    public void setTime(ItemStack stack, int time) {
        stack.set(DataComponentRegistry.TIME, Math.clamp(time, 0, MAX_TIME));
    }

    public void addTime(ItemStack stack, int time) {
        setTime(stack, getTime(stack) + time);
    }

    public boolean isActive(ItemStack stack) {
        return getTime(stack) == MAX_TIME;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(stack.getItem() instanceof MinotaurHoofItem relic)
                || !(slotContext.entity() instanceof Player player))
            return;

        Level level = player.getCommandSenderWorld();

        if (level.isClientSide())
            return;

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackResistance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);

        if (movementSpeed == null || knockbackResistance == null || stepHeight == null) return;
        double maxSpeedMultiplier = relic.getStatValue(stack, "momentum_rush", "max_speed_multiplier");

        int time = getTime(stack);
        addTime(stack, player.isSprinting() ? 1 : -1);
        if (time == 0) return;

        movementSpeed.addOrUpdateTransientModifier(new AttributeModifier(MOVEMENT_MODIFIER, maxSpeedMultiplier * time / (float) MAX_TIME, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        knockbackResistance.addOrUpdateTransientModifier(new AttributeModifier(MOVEMENT_MODIFIER, time / (float) MAX_TIME, AttributeModifier.Operation.ADD_VALUE));
        stepHeight.addOrUpdateTransientModifier(new AttributeModifier(MOVEMENT_MODIFIER, isActive(stack) ? 0.55 : 0, AttributeModifier.Operation.ADD_VALUE));

        if (!relic.isActive(stack)) return;
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(0.025), target -> !target.getStringUUID().equals(player.getStringUUID()));

        for (LivingEntity entity : entities) {
            if (entity.hurt(player.damageSources().playerAttack(player), (float) relic.getStatValue(stack, "momentum_rush", "damage"))) {
                relic.spreadRelicExperience(player, stack, 1);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        if (newStack.getItem() == stack.getItem())
            return;

        setTime(stack, 0);
        resetAttributes(slotContext.entity());
    }

    private static void resetAttributes(LivingEntity livingEntity) {
        AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackResistance = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance stepHeight = livingEntity.getAttribute(Attributes.STEP_HEIGHT);
        if (movementSpeed == null || knockbackResistance == null || stepHeight == null) return;
        if (movementSpeed.hasModifier(MOVEMENT_MODIFIER)) movementSpeed.removeModifier(MOVEMENT_MODIFIER);
        if (knockbackResistance.hasModifier(MOVEMENT_MODIFIER)) knockbackResistance.removeModifier(MOVEMENT_MODIFIER);
        if (stepHeight.hasModifier(MOVEMENT_MODIFIER)) stepHeight.removeModifier(MOVEMENT_MODIFIER);
    }

    @SubscribeEvent
    public static void onDamageTaken(LivingIncomingDamageEvent e) {
        ItemStack stack = EntityUtils.findEquippedCurio(e.getEntity(), ItemRegistry.MINOTAUR_HOOF.get());
        if (!(stack.getItem() instanceof MinotaurHoofItem relic)) return;
        double reducedDamage = e.getAmount() * Mth.clamp(1 - relic.getStatValue(stack, "momentum_rush", "damage_reduction"), 0, 1);
        //e.getEntity().sendSystemMessage(Component.literal(e.getAmount()+" : "+reducedDamage));

        if (!relic.isActive(stack)) return;
        e.setAmount((float) reducedDamage);

    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedTwilightForest.MODID;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public CurioModel getModel(ItemStack stack) {
        return new SidedCurioModel(stack.getItem());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<String> bodyParts() {
        return List.of("right_leg", "left_leg");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4f), 0);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CurioModel model = getModel(stack);

        if (!(model instanceof SidedCurioModel sidedModel))
            return;

        sidedModel.setSlot(slotContext.index());

        matrixStack.pushPose();

        LivingEntity entity = slotContext.entity();

        sidedModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        sidedModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ICurioRenderer.followBodyRotations(entity, sidedModel);

        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(getTexture(stack)), stack.hasFoil());

        matrixStack.translate(0, 0, 0);
        matrixStack.scale(1.0047f, 1.0047f, 1.0047f);

        sidedModel.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }
}