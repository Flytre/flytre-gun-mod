package net.flytre.fguns.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.guns.GunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {


    @Shadow @Final private MinecraftClient client;

    @Shadow private ItemStack mainHand;

    @Shadow private ItemStack offHand;

    @Shadow private float equipProgressMainHand;

    @Shadow private float equipProgressOffHand;

    @Inject(method = "updateHeldItems", at = @At("TAIL"))
    public void cancelAnimation(CallbackInfo ci) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();

        if (this.mainHand.getItem() instanceof GunItem && itemStack.getItem() instanceof GunItem && ItemStack.areItemsEqualIgnoreDamage(mainHand,itemStack)) {
            this.equipProgressMainHand = 1;
        }

        if (this.offHand.getItem() instanceof GunItem && itemStack2.getItem() instanceof GunItem  && ItemStack.areItemsEqualIgnoreDamage(offHand,itemStack2)) {
            this.equipProgressOffHand = 1;
        }

    }
}