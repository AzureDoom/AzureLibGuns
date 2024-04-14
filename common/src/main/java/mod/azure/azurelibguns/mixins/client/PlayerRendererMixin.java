package mod.azure.azurelibguns.mixins.client;

import mod.azure.azurelibguns.example.BaseGunItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sets the guns 3rd person holding animation on the player
 */
@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "getArmPose", at = @At(value = "TAIL"), cancellable = true)
    private static void tryItemPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> ci) {
        final ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() instanceof BaseGunItem) {
            ci.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
