package net.flytre.fguns.mixin.client;

import net.flytre.fguns.client.Key;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sneak when scoped
 */
@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At("TAIL"))
    public void fguns$tick(boolean slowDown, CallbackInfo ci) {
        this.sneaking |= Key.SCOPED;
    }
}
