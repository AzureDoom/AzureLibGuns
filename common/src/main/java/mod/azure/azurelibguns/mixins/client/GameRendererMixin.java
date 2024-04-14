package mod.azure.azurelibguns.mixins.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.azure.azurelibguns.example.BaseGunItem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables the bobView when holding the gun
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", shift = At.Shift.BEFORE))
    private void bobViewGun(PoseStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (this.minecraft.player.getUseItem().getItem() instanceof BaseGunItem && !this.minecraft.player.getUseItem().getOrCreateTag().getBoolean(
                "isAiming") && !this.minecraft.player.isSprinting()) {
            gunBobView(matrices, tickDelta);
        }
    }

    @SuppressWarnings("unused")
    @WrapWithCondition(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    private boolean vanillaBobView(GameRenderer instance, PoseStack matrices, float tickDelta) {
        return !(this.minecraft.player.getUseItem().getItem() instanceof BaseGunItem);
    }

    private void gunBobView(PoseStack matrices, float tickDelta) {
        if (!(this.minecraft.gameRenderer.getMainCamera().getEntity() instanceof Player playerEntity)) {
            return;
        }
        float f = (playerEntity.walkDist - playerEntity.walkDistO);
        float g = -(playerEntity.walkDist + f * tickDelta);
        float h = Mth.lerp(tickDelta, playerEntity.oBob, playerEntity.bob) * 0.25f;

        matrices.translate(Mth.sin(g * (float) Math.PI) * h * 0.5f, -Math.abs(Mth.cos(g * (float) Math.PI) * h), 0.0);

        matrices.mulPose(Axis.ZP.rotationDegrees(Mth.sin(g * (float) Math.PI) * h * 3.0f));
        matrices.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(g * (float) Math.PI - 0.2f) * h) * 5.0f));
    }

}
