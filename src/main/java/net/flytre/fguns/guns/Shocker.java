package net.flytre.fguns.guns;

import net.flytre.fguns.BulletDamageSource;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.entity.Bullet;
import net.flytre.flytre_lib.common.util.ParticleUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Shocker extends GunItem {
    public Shocker(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, GunType.SHOCKER);
    }

    public static void chain(Bullet bullet, EntityHitResult entityHitResult, float damage) {

        if (bullet.world.isClient)
            return;


        List<Entity> hit = new ArrayList<>();
        hit.add(entityHitResult.getEntity());

        if (!(entityHitResult.getEntity() instanceof LivingEntity))
            return;
        LivingEntity currentEntity = (LivingEntity) entityHitResult.getEntity();

        BlockStateParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.YELLOW_CONCRETE.getDefaultState());
        BlockStateParticleEffect particle2 = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.BLACK_CONCRETE.getDefaultState());
        Vec3d lastEntity = currentEntity.getPos();

        while ((currentEntity = getNextEntity(bullet, hit, currentEntity)) != null) {

            hit.add(currentEntity);
            ParticleUtils.drawLineTo(lastEntity, currentEntity.getPos(), particle, (ServerWorld) currentEntity.world);
            ParticleUtils.drawLineTo(lastEntity, currentEntity.getPos(), particle2, (ServerWorld) currentEntity.world);
            damage *= 0.8f;
            lastEntity = currentEntity.getPos();

            currentEntity.timeUntilRegen = 0;
            currentEntity.damage(new BulletDamageSource(bullet, bullet.getOwner()), damage);

            if (hit.size() > 5)
                break;
        }
    }

    private static LivingEntity getNextEntity(Bullet bullet, List<Entity> hit, LivingEntity currentEntity) {
        TargetPredicate predicate = new TargetPredicate();
        Entity owner = bullet.getOwner();

        if (!(owner instanceof HostileEntity))
            predicate.setPredicate(i -> !(hit.contains(i)) && !(i == bullet.getOwner()) && !(i instanceof PassiveEntity) && !(i instanceof GolemEntity));
        else
            predicate.setPredicate(i -> !(hit.contains(i)) && !(i == bullet.getOwner()) && !(i instanceof HostileEntity));

        return currentEntity.world.getClosestEntity(LivingEntity.class, predicate, currentEntity, currentEntity.getX(), currentEntity.getY(), currentEntity.getZ(), currentEntity.getBoundingBox().expand(10));
    }


    public Item getAmmoItem() {
        return FlytreGuns.ENERGY_CELL;
    }

    @Override
    public void bulletSetup(World world, LivingEntity user, Hand hand, Bullet bullet) {
        bullet.setProperties(GunType.SHOCKER);
//        bullet.setVelocity(bullet.getVelocity().multiply(4));
    }
}
