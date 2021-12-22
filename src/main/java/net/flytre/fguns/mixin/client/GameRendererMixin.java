package net.flytre.fguns.mixin.client;

import net.flytre.fguns.client.TempClientData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Adjust FOV to simulate scoping effect
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    private boolean renderingPanorama;

    @Shadow
    private float lastMovementFovMultiplier;

    @Shadow
    private float movementFovMultiplier;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    public void fguns$scope_fov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        var options = mc.options;
        if (renderingPanorama || mc.player == null || TempClientData.shiftTime == 0 || options.getPerspective() != Perspective.FIRST_PERSON || TempClientData.gun == null || !TempClientData.gun.hasScope() || mc.player.isSpectator())
            return;


        double t = TempClientData.shiftTime;
        double maxZoom = TempClientData.gun.getScopeZoom();
        t = t > 0 ? t + tickDelta : t;
        double zoomFactor = t >= 20 ? maxZoom : 1 + (t - 1) * (maxZoom - 1) / 19;
        double d = options.fov * (1 / zoomFactor);


        if (changingFov) {
            d *= MathHelper.lerp(tickDelta, this.lastMovementFovMultiplier, this.movementFovMultiplier);
        }

        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity) camera.getFocusedEntity()).isDead()) {
            float f = Math.min((float) ((LivingEntity) camera.getFocusedEntity()).deathTime + tickDelta, 20.0F);
            d /= (1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F;
        }

        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
            d *= MathHelper.lerp(this.client.options.fovEffectScale, 1.0F, 0.85714287F);
        }

        cir.setReturnValue(d);
    }

}
