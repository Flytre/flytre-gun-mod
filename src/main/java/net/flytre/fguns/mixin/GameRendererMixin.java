package net.flytre.fguns.mixin;

import net.flytre.fguns.MixinHelper;
import net.flytre.fguns.guns.GunItem;
import net.flytre.fguns.guns.GunType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.Item;
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
    private double getFov(GameOptions options, Camera camera, float tickDelta, boolean changingFov) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || MixinHelper.shiftTime == 0 || options.getPerspective() != Perspective.FIRST_PERSON)
            return options.fov;

        //map zoom
        double d = MixinHelper.shiftTime;

        d = d > 0 ? d + tickDelta : d;

        double zoomFactor = d >= 20 ? 16 : 1 + (d - 1) * 15.0/19;
        return options.fov * (1 / zoomFactor);
    }

}
