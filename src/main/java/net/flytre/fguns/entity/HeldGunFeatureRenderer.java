package net.flytre.fguns.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.guns.GunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class HeldGunFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public HeldGunFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }


    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        boolean bl = livingEntity.getMainArm() == Arm.RIGHT;
        ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
        ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
        if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
            matrixStack.push();
            if (this.getContextModel().child) {
                matrixStack.translate(0.0D, 0.75D, 0.0D);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
            }

            if (itemStack2.getItem() instanceof GunItem)
                this.renderItem(livingEntity, itemStack2, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i);

            if (itemStack.getItem() instanceof GunItem)
                this.renderItem(livingEntity, itemStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i);
            matrixStack.pop();
        }
    }

    private void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!stack.isEmpty()) {
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate(0, 0.125D, -0.625D);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }
}
