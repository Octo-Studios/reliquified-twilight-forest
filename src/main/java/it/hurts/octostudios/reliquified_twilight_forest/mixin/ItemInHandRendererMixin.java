package it.hurts.octostudios.reliquified_twilight_forest.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.reliquified_twilight_forest.client.event.RenderItemInHandEvent;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Redirect(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"))
    private void redirected(ItemRenderer instance, LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack,
                            MultiBufferSource bufferSource, Level level, int packedLight, int packedOverlay, int seed
    ) {
        RenderItemInHandEvent event = NeoForge.EVENT_BUS.post(new RenderItemInHandEvent(
                instance, entity, itemStack, displayContext, leftHand, poseStack, bufferSource,
                packedLight, packedOverlay)
        );

        if (event.isCanceled()) {
            return;
        }

        event.getItemRenderer().renderStatic(
                event.getEntity(),
                event.getItemStack(),
                event.getDisplayContext(),
                event.isLeftHand(),
                event.getPoseStack(),
                event.getMultiBufferSource(),
                event.getEntity().level(),
                event.getPackedLight(),
                event.getPackedOverlay(),
                seed
        );
    }
}
