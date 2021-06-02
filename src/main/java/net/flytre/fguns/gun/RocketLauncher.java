package net.flytre.fguns.gun;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.Sounds;
import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RocketLauncher extends AbstractGun {


    protected RocketLauncher(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, double horizontalRecoil, double verticalRecoil) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
    }

    @Override
    public Text getDamageLine() {
        return new TranslatableText("text.fguns.tooltip.damage.varies");
    }

    @Override
    public void bulletSetup(World world, LivingEntity user, Hand hand, Bullet bullet) {
        bullet.setVelocity(bullet.getVelocity().multiply(0.4));
        super.bulletSetup(world, user, hand, bullet);
    }

    public static class Builder extends AbstractGun.Builder<RocketLauncher> {

        public Builder() {
            bulletProperties = BulletProperties.ROCKET;
            fireSound = Sounds.ROCKET_FIRE_EVENT;
            ammoItem = FlytreGuns.ROCKET_AMMO;
            damage = 1;
            armorPen = 100;
            dropoff = 0;
            horizontalRecoil = 2.2;
            this.scope = false;
        }

        @Override
        public RocketLauncher build() {
            return new RocketLauncher(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
        }
    }
}
