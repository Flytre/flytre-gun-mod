package net.flytre.fguns.mixin;

import net.flytre.fguns.MixinHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)

public class GameRendererMixin {


    @Redirect(
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fov:D",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0),
            method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)D"})
    private double fguns$getFov(GameOptions options, Camera camera, float tickDelta, boolean changingFov) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || MixinHelper.shiftTime == 0 || options.getPerspective() != Perspective.FIRST_PERSON || MixinHelper.gun == null || !MixinHelper.gun.hasScope() || mc.player.isSpectator())
            return options.fov;


        //map zoom
        //map zoom
        double d = MixinHelper.shiftTime;

        double maxZoom = MixinHelper.gun.getScopeZoom();
        d = d > 0 ? d + tickDelta : d;

        double zoomFactor = d >= 20 ? maxZoom : 1 + (d - 1) * (maxZoom - 1) / 19;
        return options.fov * (1 / zoomFactor);
    }

}
