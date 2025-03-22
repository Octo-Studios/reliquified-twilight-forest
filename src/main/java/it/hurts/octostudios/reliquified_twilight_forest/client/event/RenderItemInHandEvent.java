package it.hurts.octostudios.reliquified_twilight_forest.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Fired before an item stack is rendered in hand.
 * This can be used to prevent normal rendering or add custom rendering.
 *
 * <p>This event is {@linkplain ICancellableEvent cancellable}.
 * If the event is cancelled, then the item stack will not be rendered</p>
 *
 * <p>This event is fired on the {@linkplain NeoForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ItemInHandRenderer
 */
public class RenderItemInHandEvent extends Event implements ICancellableEvent {
    private final ItemRenderer renderer;
    private final LivingEntity entity;
    private ItemStack itemStack;
    private ItemDisplayContext displayContext;
    private boolean leftHand;
    private PoseStack poseStack;
    private MultiBufferSource multiBufferSource;
    private int packedLight;
    private int packedOverlay;

    public RenderItemInHandEvent(
            ItemRenderer renderer,
            LivingEntity entity,
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            int packedOverlay
    ) {
        this.renderer = renderer;
        this.entity = entity;
        this.itemStack = itemStack;
        this.displayContext = displayContext;
        this.leftHand = leftHand;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    public ItemRenderer getItemRenderer() {
        return renderer;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemDisplayContext getDisplayContext() {
        return displayContext;
    }

    public boolean isLeftHand() {
        return leftHand;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public int getPackedOverlay() {
        return packedOverlay;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setDisplayContext(ItemDisplayContext displayContext) {
        this.displayContext = displayContext;
    }

    public void setLeftHand(boolean leftHand) {
        this.leftHand = leftHand;
    }

    public void setPoseStack(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    public void setMultiBufferSource(MultiBufferSource multiBufferSource) {
        this.multiBufferSource = multiBufferSource;
    }

    public void setPackedLight(int packedLight) {
        this.packedLight = packedLight;
    }

    public void setPackedOverlay(int packedOverlay) {
        this.packedOverlay = packedOverlay;
    }
}
