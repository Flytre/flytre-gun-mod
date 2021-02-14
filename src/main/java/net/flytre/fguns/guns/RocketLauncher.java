package net.flytre.fguns.guns;

import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RocketLauncher extends GunItem {
    public RocketLauncher(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, GunType.ROCKET);
    }

    @Override
    public void bulletSetup(World world, LivingEntity user, Hand hand, Bullet bullet) {
        bullet.setProperties(GunType.ROCKET);
        bullet.setVelocity(bullet.getVelocity().multiply(0.4));
    }
}
