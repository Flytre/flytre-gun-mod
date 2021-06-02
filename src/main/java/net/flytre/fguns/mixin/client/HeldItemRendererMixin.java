package net.flytre.fguns.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.gun.AbstractGun;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = HeldItemRenderer.class, priority = 100)
public abstract class HeldItemRendererMixin {


    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ItemStack mainHand;

    @Shadow
    private ItemStack offHand;

    @Shadow
    private float equipProgressMainHand;

    @Shadow
    private float equipProgressOffHand;

    @Shadow
    protected abstract float getMapAngle(float tickDelta);

    @Shadow
    protected abstract void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm);


    @Inject(method = "updateHeldItems", at = @At("TAIL"))
    public void fguns$cancelAnimation(CallbackInfo ci) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        assert clientPlayerEntity != null;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();

        if (this.mainHand.getItem() instanceof AbstractGun && itemStack.getItem() instanceof AbstractGun && ItemStack.areItemsEqualIgnoreDamage(mainHand, itemStack)) {
            this.equipProgressMainHand = 1;
            this.mainHand = itemStack;
        }

        if (this.offHand.getItem() instanceof AbstractGun && itemStack2.getItem() instanceof AbstractGun && ItemStack.areItemsEqualIgnoreDamage(offHand, itemStack2)) {
            this.equipProgressOffHand = 1;
            this.offHand = itemStack2;
        }

    }

    @Inject(method = "renderFirstPersonItem", cancellable = true, at = @At("HEAD"))
    public void fguns$gunRenders(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        boolean bl = hand == Hand.MAIN_HAND;
        matrices.push();
        if (item.getItem() == FlytreGuns.MINIGUN) {
            if (bl && this.offHand.isEmpty()) {
                this.renderGun(matrices, vertexConsumers, light, equipProgress, swingProgress);
            }
        }
    }


    @Unique
    private void renderGun(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress) {
        float f = MathHelper.sqrt(swingProgress);
        float g = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
        float h = -0.4F * MathHelper.sin(f * 3.1415927F);
        matrices.translate(0.0D, -g / 2.0F, h);
        float i = getMapAngle(45);
        matrices.translate(0.0D, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.7200000286102295D);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i * -85.0F));
        assert this.client.player != null;
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
            this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
            matrices.pop();
        }

        float j = MathHelper.sin(f * 3.1415927F);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 20.0F));
        matrices.scale(2.0F, 2.0F, 2.0F);
        matrices.translate(-0.5F, -0.4F, 0.0);
    }
}