package net.flytre.fguns.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.gun.AbstractGun;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Uncap gun fire rates
 */
@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow private int itemUseCooldown;

    @Inject(method = "doItemUse", at = @At("RETURN"))
    public void fguns$gunFireRate(CallbackInfo ci) {

        if (this.player == null)
            return;

        for (Hand hand : Hand.values()) {
            ItemStack itemStack = this.player.getStackInHand(hand);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof AbstractGun)
                    itemUseCooldown = 0;
                return;
            }
        }

    }

}
