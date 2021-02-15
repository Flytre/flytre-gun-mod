package net.flytre.fguns.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BulletPacket {
    private int id;
    private int velocityX;
    private int velocityY;
    private int velocityZ;

    public BulletPacket() {
    }

    public BulletPacket(Entity entity) {
        this(entity.getEntityId(), entity.getVelocity());
    }

    public BulletPacket(int id, Vec3d velocity) {
        this.id = id;
        this.velocityX = (int) (velocity.x * 8000.0D);
        this.velocityY = (int) (velocity.y * 8000.0D);
        this.velocityZ = (int) (velocity.z * 8000.0D);
    }

    public void read(PacketByteBuf buf) {
        this.id = buf.readVarInt();
        this.velocityX = buf.readInt();
        this.velocityY = buf.readInt();
        this.velocityZ = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeInt(this.velocityX);
        buf.writeInt(this.velocityY);
        buf.writeInt(this.velocityZ);
    }

    public void apply(World world) {
        Entity entity = world.getEntityById(getId());
        if (entity != null) {
            entity.setVelocityClient((double) getVelocityX() / 8000.0D, (double) getVelocityY() / 8000.0D, (double) getVelocityZ() / 8000.0D);
        }
    }

    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(EnvType.CLIENT)
    public int getVelocityX() {
        return this.velocityX;
    }

    @Environment(EnvType.CLIENT)
    public int getVelocityY() {
        return this.velocityY;
    }

    @Environment(EnvType.CLIENT)
    public int getVelocityZ() {
        return this.velocityZ;
    }
}
