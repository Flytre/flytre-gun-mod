package net.flytre.fguns.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.entity.Bullet;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    @Inject(method = "onEntitySpawn", at = @At(value = "RETURN"))
    private void fguns$spawnBullet(EntitySpawnS2CPacket packet, CallbackInfo info) {
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        EntityType<?> entityType = packet.getEntityTypeId();
        Entity entity = null;
        if (entityType == FlytreGuns.BULLET) {
            entity = new Bullet(d, e, f, world);
        }

        if (entity != null) {
            int i = packet.getId();
            entity.updateTrackedPosition(d, e, f);
            entity.refreshPositionAfterTeleport(d, e, f);
            entity.pitch = (float)(packet.getPitch() * 360) / 256.0F;
            entity.yaw = (float)(packet.getYaw() * 360) / 256.0F;
            entity.setEntityId(i);
            entity.setUuid(packet.getUuid());
            this.world.addEntity(i, entity);
        }

    }
}
