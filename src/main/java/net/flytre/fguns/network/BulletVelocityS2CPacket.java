package net.flytre.fguns.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BulletVelocityS2CPacket implements Packet<ClientPlayPacketListener> {
    private final int id;
    private final double velocityX;
    private final double velocityY;
    private final double velocityZ;

    public BulletVelocityS2CPacket(Entity entity) {
        this(entity.getId(), entity.getVelocity());
    }

    public BulletVelocityS2CPacket(int id, Vec3d velocity) {
        this.id = id;
        this.velocityX = velocity.x;
        this.velocityY = velocity.y;
        this.velocityZ = velocity.z;
    }

    public BulletVelocityS2CPacket(PacketByteBuf buf) {
        this.id = buf.readVarInt();
        this.velocityX = buf.readDouble();
        this.velocityY = buf.readDouble();
        this.velocityZ = buf.readDouble();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeDouble(this.velocityX);
        buf.writeDouble(this.velocityY);
        buf.writeDouble(this.velocityZ);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void apply(ClientPlayPacketListener listener) {

        World world = ((ClientPlayNetworkHandler) listener).getWorld();
        MinecraftClient.getInstance().execute(() -> {
                    Entity entity = world.getEntityById(getId());
                    if (entity != null) {
                        entity.setVelocityClient(getVelocityX(), getVelocityY(), getVelocityZ());
                    }
                }
        );
    }

    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(EnvType.CLIENT)
    public double getVelocityX() {
        return this.velocityX;
    }

    @Environment(EnvType.CLIENT)
    public double getVelocityY() {
        return this.velocityY;
    }

    @Environment(EnvType.CLIENT)
    public double getVelocityZ() {
        return this.velocityZ;
    }
}
