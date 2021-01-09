package net.flytre.fguns.mixin;

import net.flytre.fguns.MixinHelper;
import net.flytre.fguns.guns.GunItem;
import net.flytre.fguns.guns.GunType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null)
            return;


        Item item = mc.player.getMainHandStack().getItem();
        if(item instanceof GunItem && ((GunItem) item).getType() == GunType.SNIPER && mc.player.isSneaking()) {
          MixinHelper.shiftTime++;
        } else
            MixinHelper.shiftTime = 0;
    }
}
