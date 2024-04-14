package mod.azure.azurelibguns.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.azure.azurelibguns.example.BaseGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables the guns from swinging, which currently breaks the arm animations
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public HitResult hitResult;
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreaking(boolean b, CallbackInfo ci) {
        if (player.getUseItem().getItem() instanceof BaseGunItem) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;shouldSwing()Z"))
    private boolean dontSwingGun(boolean original) {
        return original && !(this.player.getUseItem().getItem() instanceof BaseGunItem);
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), cancellable = true)
    private void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
        if (hitResult == null || player == null) {
            return;
        }
        var mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof BaseGunItem) {
            cir.setReturnValue(false);
        }
    }
}
