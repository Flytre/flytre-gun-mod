package net.flytre.fguns.guns;

import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SlimeGun extends GunItem {
    public SlimeGun(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, GunType.SLIME);
    }

    @Override
    public void bulletSetup(World world, PlayerEntity user, Hand hand, Bullet bullet) {
        bullet.setProperties(GunType.SLIME);
        super.bulletSetup(world, user, hand, bullet);
    }
}
