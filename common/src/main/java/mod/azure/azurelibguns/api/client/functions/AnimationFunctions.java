package mod.azure.azurelibguns.api.client.functions;

import mod.azure.azurelib.common.internal.common.core.animation.AnimationController;
import mod.azure.azurelibguns.api.common.GunData;
import mod.azure.azurelibguns.example.BaseGunItem;
import mod.azure.azurelibguns.example.Constants;
import org.jetbrains.annotations.NotNull;

public record AnimationFunctions() {
    public static boolean isAnimationPlaying(AnimationController<?> animationController) {
        if (animationController.isPlayingTriggeredAnimation() && animationController.getCurrentAnimation() != null) {
            return animationController.getCurrentAnimation().animation().name().equals(Constants.RELOAD_STRING);
        }
        return false;
    }

    @NotNull
    public static String getFiringAnimation(boolean isAiming, int fireMode, int burstTicks, GunData gunData) {
        String animationToTrigger;
        var burstTickCheck = burstTicks <= (gunData.magSize() / 3);
        var isSemiOrSingle = fireMode == Constants.semiAutomatic.getName().hashCode() || fireMode == Constants.single.getName().hashCode();

        if (isAiming) {
            animationToTrigger = burstTickCheck && isSemiOrSingle ? Constants.AIMFIRING_STRING : Constants.AIMFIRINGAUTO_STRING;
        } else {
            animationToTrigger = burstTickCheck && isSemiOrSingle ? Constants.FIRING_STRING : Constants.FIRINGAUTO_STRING;
        }

        return animationToTrigger;
    }
}
