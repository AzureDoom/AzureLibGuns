package mod.azure.azurelibguns.api.client;

import mod.azure.azurelib.common.api.client.model.DefaultedItemGeoModel;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.common.constant.DataTickets;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class GunModel<T extends Item & GeoItem> extends DefaultedItemGeoModel<T> {
    public GunModel(ResourceLocation assetSubpath, String name) {
        super(assetSubpath);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        var isAiming = animationState.getController().isPlayingTriggeredAnimation() && animationState.getController().getCurrentAnimation() != null && animationState.getController().getCurrentAnimation().animation().name().equals(
                "aim");
        var isAimFiring = animationState.getController().isPlayingTriggeredAnimation() && animationState.getController().getCurrentAnimation() != null && animationState.getController().getCurrentAnimation().animation().name().equals(
                "aimfiring");
        var isReloading = animationState.getController().isPlayingTriggeredAnimation() && animationState.getController().getCurrentAnimation() != null && animationState.getController().getCurrentAnimation().animation().name().equals(
                "reload");
        switch (animationState.getData(DataTickets.ITEM_RENDER_PERSPECTIVE)) {
            case GUI, GROUND, HEAD, NONE, FIXED -> animationState.getController().stop();
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                if (isAimFiring) animationState.getController().tryTriggerAnimation("firing");
                if (isAiming) animationState.getController().tryTriggerAnimation("idle");
                if (isReloading) animationState.getController().tryTriggerAnimation("reload3rd");
            }
        }
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
