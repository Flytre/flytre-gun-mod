package net.flytre.fguns.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.fguns.BulletDamageSource;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.Packets;
import net.flytre.fguns.ParticleHelper;
import net.flytre.fguns.flare.FlareWorld;
import net.flytre.fguns.gun.BulletProperties;
import net.flytre.fguns.gun.Shocker;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Collection;

public class Bullet extends ThrownEntity {

    private static final TrackedData<String> GUN_TYPE;

    static {
        GUN_TYPE = DataTracker.registerData(Bullet.class, TrackedDataHandlerRegistry.STRING);
    }

    double lastX;
    double lastY;
    double lastZ;
    private double damage;
    private double armorPen;
    private double dropoff;
    private int range;
    private Vec3d initialPos;
    private double initialSpeed = -1;

    private Vec3d lastVelocity;


    public Bullet(EntityType<? extends Bullet> entityType, World world) {
        super(entityType, world);
    }


    public Bullet(double x, double y, double z, World world) {
        this(FlytreGuns.BULLET, world);
        this.updatePosition(x, y, z);
        this.initialPos = new Vec3d(x, y, z);
    }


    public Bullet(World world) {
        this(FlytreGuns.BULLET, world);
    }

    public Bullet(LivingEntity livingEntity, World world) {
        super(FlytreGuns.BULLET, livingEntity, world);
    }

    public BulletProperties getProperties() {
        return BulletProperties.valueOf(this.dataTracker.get(GUN_TYPE));
    }

    public void setProperties(BulletProperties properties) {
        this.getDataTracker().set(GUN_TYPE, properties.name());
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(GUN_TYPE, BulletProperties.NONE.name());
    }

    public void setInitialPos(Vec3d initialPos) {
        this.initialPos = initialPos;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        double dist = Math.sqrt(distSquared(initialPos.x, initialPos.z, getX(), getZ()));
        double modifiedDamage = damage * Math.pow(1 - dropoff, dist);

        if (getProperties() == BulletProperties.SHOCKER)
            Shocker.chain(this, entityHitResult, (float) modifiedDamage);

        if (getProperties() == BulletProperties.FLARE) {
            entity.setFireTicks(entity.getFireTicks() + 100);
        } else if (world.isClient) {
            WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
            Vec3d pos = entity.getPos();
            double x = pos.x;
            double y = pos.y + entity.getHeight() / 1.5;
            double z = pos.z;
            for (int i = 0; i < 15; i++) {
                worldRenderer.addParticle(ParticleHelper.getParticle(entity), false, x, y, z, 0, 0, 0);
            }

        }

        entity.timeUntilRegen = 0;
        entity.damage(new BulletDamageSource(this, this.getOwner()), (float) modifiedDamage);

        if (getProperties() == BulletProperties.SLIME && entity instanceof LivingEntity) {
            ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2));
        }
    }


    @Override
    public void tick() {

        //Knock bullet outside range
        if (!world.isClient && !this.hasNoGravity() && distSquared(initialPos.x, initialPos.z, getX(), getZ()) >= range * range) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - 0.12, vec3d2.z);
        }

        //Keep Speed High
        if (initialSpeed == -1) {
            initialSpeed = getVelocity().getX() * getVelocity().getX() + getVelocity().getZ() * getVelocity().getZ();
        } else {
            double currentSpeed = getVelocity().getX() * getVelocity().getX() + getVelocity().getZ() * getVelocity().getZ();
            if (currentSpeed / initialSpeed < 0.9) {
                setVelocity(getVelocity().multiply(1.1));
            }
        }

        if (!world.isClient) {

            if (lastVelocity != getVelocity()) {
                Collection<ServerPlayerEntity> players = PlayerLookup.tracking(this);
                for (ServerPlayerEntity playerEntity : players) {
                    BulletPacket packet = new BulletPacket(this);
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    packet.write(buf);
                    ServerPlayNetworking.send(playerEntity, Packets.BULLET_VELOCITY, buf);
                }
            }

            lastVelocity = getVelocity();
        }


        //Sniper Trails
        if (getProperties() == BulletProperties.SNIPER && (lastX != 0 && 0 != lastZ))
            bulletTrails();


        lastX = getX();
        lastY = getY();
        lastZ = getZ();

        if (getProperties() == BulletProperties.FLARE)
            if (this.world.isClient)
                this.world.addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY() + 0.15D, this.getZ(), this.random.nextGaussian() * 0.05D, -this.getVelocity().y * 0.5D, this.random.nextGaussian() * 0.05D);


        super.tick();
    }

    private void bulletTrails() {
        double dX = getX() - lastX;
        double dY = getY() - lastY;
        double dZ = getZ() - lastZ;
        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        for (double i = 0; i <= 1; i += 1 / distance) {
            double mX = prevX + (dX * i);
            double mY = prevY + (dY * i);
            double mZ = prevZ + (dZ * i);
            world.addParticle(ParticleTypes.SMOKE, mX, mY, mZ, 0, 0, 0);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {

        super.onCollision(hitResult);


        if (getProperties() == BulletProperties.SLIME) {
            //slime particles
            BlockStateParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState());
            BlockStateParticleEffect particle2 = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState());

            for (int i = 0; i < 10; i++) {
                world.addParticle(particle, getX(), getY(), getZ(), 0, 0, 0);
                world.addParticle(particle2, getX(), getY(), getZ(), 0, 0, 0);
            }

        }

        if (getProperties() == BulletProperties.ROCKET) {
            boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            this.world.createExplosion(getOwner(), this.getX(), this.getY(), this.getZ(), 3f, bl, bl ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
        }

        if (getProperties() == BulletProperties.FLARE && !world.isClient && hitResult instanceof BlockHitResult) {
            ((FlareWorld) world).setFlare(getBlockPos());
        }

        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte) 3);
            this.remove();
        }
    }


    protected float getGravity() {
        return getProperties() == BulletProperties.SNIPER ? 0.00F :
                (getProperties() == BulletProperties.ROCKET ? 0.08F : 0.03F);
    }


    @Override
    public CompoundTag toTag(CompoundTag tag) {

        CompoundTag initial = new CompoundTag();
        initial.putDouble("x", initialPos.x);
        initial.putDouble("y", initialPos.y);
        initial.putDouble("z", initialPos.z);
        tag.put("initial", initial);

        tag.putDouble("damage", damage);
        tag.putDouble("armorPen", armorPen);
        tag.putDouble("dropoff", dropoff);
        tag.putInt("range", range);

        return super.toTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag) {

        CompoundTag initial = tag.getCompound("initial");
        initialPos = new Vec3d(initial.getDouble("x"), initial.getDouble("y"), initial.getDouble("z"));
        damage = tag.getDouble("damage");
        armorPen = tag.getDouble("armorPen");
        dropoff = tag.getDouble("dropoff");
        range = tag.getInt("range");
        super.fromTag(tag);
    }

    public void setProperties(double damage, double armorPen, double dropoff, int range) {
        this.damage = damage;
        this.armorPen = armorPen;
        this.dropoff = dropoff;
        this.range = range;
    }

    public double distSquared(double x1, double y1, double x2, double y2) {
        return (y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1);
    }

    public double getDamage() {
        return damage;
    }

    public double getArmorPen() {
        return armorPen;
    }

    public double getDropoff() {
        return dropoff;
    }

    public int getRange() {
        return range;
    }

}
