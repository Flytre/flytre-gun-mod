package net.flytre.fguns.mixin.client;

import com.mojang.authlib.GameProfile;
import net.flytre.fguns.gun.AbstractGun;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Do not slow down when clickable a gun and moving
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow
    public Input input;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract boolean isUsingItem();

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public void fguns$cancelSlow(CallbackInfo ci) {
        if (this.isUsingItem() && !this.hasVehicle() && InventoryUtils.getHoldingStack(this, i -> i.getItem() instanceof AbstractGun) != null) {
            Input input = this.input;
            input.movementSideways *= 1f/0.3f;
            input = this.input;
            input.movementForward *= 1f/0.3f;
        }
    }

}
