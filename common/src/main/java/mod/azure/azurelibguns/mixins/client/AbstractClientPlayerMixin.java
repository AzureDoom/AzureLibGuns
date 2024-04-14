package mod.azure.azurelibguns.mixins.client;

import com.mojang.authlib.GameProfile;
import mod.azure.azurelibguns.example.BaseGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Zooms in when aimming with the guns
 */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    public AbstractClientPlayerMixin(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile) {
        super(level, blockPos, yaw, gameProfile);
    }

    @Inject(method = "getFieldOfViewModifier", at = @At("TAIL"), cancellable = true)
    public void zoomLevel(CallbackInfoReturnable<Float> ci) {
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.getMainHandItem().getItem() instanceof BaseGunItem)
            ci.setReturnValue(this.getMainHandItem().getOrCreateTag().getBoolean(
                    "isAiming") && !this.getMainHandItem().getOrCreateTag().getBoolean("reloading") ? 0.75f : 1.0F);
    }
}
