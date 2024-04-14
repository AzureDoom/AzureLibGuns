package mod.azure.azurelibguns.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;
import mod.azure.azurelibguns.CommonMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


/**
 * Credit: <a href="https://github.com/elidhan/Simple-Animated-Guns/blob/1.20.1/src/main/java/net/elidhan/anim_guns/client/render/GunRenderer.java">Simple Animated Guns</a>
 */
public class GunRender<T extends Item & GeoItem> extends GeoItemRenderer<T> {
    private ItemDisplayContext transformType;
    private MultiBufferSource bufferSource;

    public GunRender(String id) {
        super(new GunModel<>(CommonMod.modResource(id + "/" + id), id));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.bufferSource = bufferSource;
        this.transformType = transformType;
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        var client = Minecraft.getInstance();
        var renderArms = false;

        String name = bone.getName();
        if (name.equals("leftArm") || name.equals("rightArm")) {
            bone.setHidden(true);
            bone.setChildrenHidden(false);
            renderArms = true;
        }

        if (renderArms && this.transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || this.transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            assert client.player != null;
            var playerEntityRenderer = (PlayerRenderer) client.getEntityRenderDispatcher().getRenderer(client.player);
            var playerEntityModel = playerEntityRenderer.getModel();
            poseStack.pushPose();
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
            assert (client.player != null);
            var playerSkin = ((LocalPlayer) ClientUtils.getClientPlayer()).getSkin().texture();
            var arm = this.bufferSource.getBuffer(RenderType.entitySolid(playerSkin));
            var sleeve = this.bufferSource.getBuffer(RenderType.entityTranslucent(playerSkin));

            if (bone.getName().equals("leftArm")) {
                poseStack.scale(0.67f, 1.33f, 0.67f);
                poseStack.translate(-0.25, -0.43625, 0.1625);
                playerEntityModel.leftArm.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.leftArm.setRotation(0, 0, 0);
                playerEntityModel.leftArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                playerEntityModel.leftSleeve.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.leftSleeve.setRotation(0, 0, 0);
                playerEntityModel.leftSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);
            } else if (bone.getName().equals("rightArm")) {
                poseStack.scale(0.67f, 1.33f, 0.67f);
                poseStack.translate(0.25, -0.43625, 0.1625);
                playerEntityModel.rightArm.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.rightArm.setRotation(0, 0, 0);
                playerEntityModel.rightArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                playerEntityModel.rightSleeve.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                playerEntityModel.rightSleeve.setRotation(0, 0, 0);
                playerEntityModel.rightSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);
            }
            poseStack.popPose();
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                this.bufferSource.getBuffer(renderType), isReRender, partialTick, packedLight, packedOverlay, red,
                green, blue, alpha);
    }
}
