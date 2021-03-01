package net.flytre.fguns.gun;

import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FlareGun extends AbstractGun {

    protected FlareGun(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, double recoilMultiplier) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, recoilMultiplier);
    }

    @Override
    public void bulletSetup(World world, LivingEntity user, Hand hand, Bullet bullet) {
        bullet.setVelocity(bullet.getVelocity().multiply(0.2));
        super.bulletSetup(world, user, hand, bullet);
    }

    public static class Builder extends AbstractGun.Builder<FlareGun> {

        public Builder() {
            super();
            bulletProperties = BulletProperties.FLARE;
            damage = 0;
            armorPen = 0;
            dropoff = 0;
            scopeZoom = 0;
            scope = false;
            ammoItem = Items.FIREWORK_ROCKET;
        }

        @Override
        public FlareGun build() {
            return new FlareGun(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, recoilMultiplier);
        }
    }
}
