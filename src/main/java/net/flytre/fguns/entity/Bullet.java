package net.flytre.fguns.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.flare.FlareWorld;
import net.flytre.fguns.gun.BulletProperties;
import net.flytre.fguns.gun.Shocker;
import net.flytre.fguns.misc.ParticleHelper;
import net.flytre.fguns.network.BulletVelocityS2CPacket;
import net.flytre.flytre_lib.api.config.reference.block.ConfigBlock;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
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
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

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
    private Vec3d initialPos = new Vec3d(0, 0, 0);
    private double initialSpeed = -1;

    private Vec3d lastVelocity;


    public Bullet(EntityType<? extends Bullet> entityType, World world) {
        super(entityType, world);
    }


    public Bullet(double x, double y, double z, World world) {
        this(FlytreGuns.BULLET, world);
        this.setPosition(x, y, z);
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

        if (getFireTicks() > 0) {
            entity.setFireTicks(80);
        }


        double dist = world.isClient ? 0 : Math.sqrt(distSquared(initialPos.x, initialPos.z, getX(), getZ()));
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

//        //Keep Speed High
//        if (initialSpeed == -1) {
//            initialSpeed = getVelocity().getX() * getVelocity().getX() + getVelocity().getZ() * getVelocity().getZ();
//        } else {
//            double currentSpeed = getVelocity().getX() * getVelocity().getX() + getVelocity().getZ() * getVelocity().getZ();
//            if (currentSpeed / initialSpeed < 0.9) {
//                setVelocity(getVelocity().multiply(1.1));
//            }
//        }

        if (!world.isClient) {

            if (lastVelocity != getVelocity()) {
                Collection<ServerPlayerEntity> players = PlayerLookup.tracking(this);
                for (ServerPlayerEntity playerEntity : players) {
                    playerEntity.networkHandler.sendPacket(new BulletVelocityS2CPacket(this));
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
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        Set<ConfigBlock> blocks = FlytreGuns.CONFIG.getConfig().breakableBlocks;
        if (ConfigBlock.contains(blocks, world.getBlockState(blockHitResult.getBlockPos()).getBlock(), world)) {
            double dist = Math.sqrt(distSquared(initialPos.x, initialPos.z, getX(), getZ()));
            double modifiedDamage = damage * Math.pow(1 - dropoff, dist);
            if (modifiedDamage >= 10 || Math.random() * 10 < modifiedDamage)
                world.breakBlock(blockHitResult.getBlockPos(), false);
        }

        if (getFireTicks() > 0 && FlytreGuns.CONFIG.getConfig().flammableGriefing) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
                BlockPos blockPos2 = blockPos.offset(blockHitResult.getSide());
                if (AbstractFireBlock.canPlaceAt(world, blockPos2, Direction.NORTH)) {
                    world.playSound(null, blockPos2, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
                    world.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                }
            } else {
                world.playSound(null, blockPos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                world.setBlockState(blockPos, blockState.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            }

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
            float power = FlytreGuns.CONFIG.getConfig().rocketExplosionPower;
            this.world.createExplosion(getOwner(), this.getX(), this.getY(), this.getZ(), power, bl, bl ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
        }

        if (getProperties() == BulletProperties.FLARE && !world.isClient && hitResult instanceof BlockHitResult) {
            ((FlareWorld) world).setFlare(getBlockPos());
        }

        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte) 3);
            this.discard();
        }
    }


    protected float getGravity() {
        return getProperties() == BulletProperties.SNIPER ? 0.00F :
                (getProperties() == BulletProperties.ROCKET ? 0.08F : 0.03F);
    }


    @Override
    public NbtCompound writeNbt(NbtCompound tag) {

        NbtCompound initial = new NbtCompound();
        initial.putDouble("x", initialPos.x);
        initial.putDouble("y", initialPos.y);
        initial.putDouble("z", initialPos.z);
        tag.put("initial", initial);

        tag.putDouble("damage", damage);
        tag.putDouble("armorPen", armorPen);
        tag.putDouble("dropoff", dropoff);

        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {

        NbtCompound initial = tag.getCompound("initial");
        initialPos = new Vec3d(initial.getDouble("x"), initial.getDouble("y"), initial.getDouble("z"));
        damage = tag.getDouble("damage");
        armorPen = tag.getDouble("armorPen");
        dropoff = tag.getDouble("dropoff");
        super.readNbt(tag);
    }

    public void setProperties(double damage, double armorPen, double dropoff) {
        this.damage = damage;
        this.armorPen = armorPen;
        this.dropoff = dropoff;
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

}
