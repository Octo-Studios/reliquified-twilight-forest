package it.hurts.octostudios.reliquified_twilight_forest.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.reliquified_twilight_forest.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.CuriosHelper;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void cancelRender(
            PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity,
            float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci
    ) {
        CuriosApi.getCuriosHelper().findFirstCurio(livingEntity, ItemRegistry.INVISIBILITY_CLOAK.get());
        if (!EntityUtils.findEquippedCurio(livingEntity, ItemRegistry.INVISIBILITY_CLOAK.get()).isEmpty()) {
            ci.cancel();
        }
    }
}
