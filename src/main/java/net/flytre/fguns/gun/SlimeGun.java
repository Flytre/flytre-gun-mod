package net.flytre.fguns.gun;

import net.flytre.fguns.Sounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class SlimeGun extends AbstractGun {

    protected SlimeGun(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, double horizontalRecoil, double verticalRecoil) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
    }

    public static class Builder extends AbstractGun.Builder<SlimeGun> {

        public Builder() {
            super();
            bulletProperties = BulletProperties.SLIME;
            fireSound = Sounds.SLIME_FIRE_EVENT;
            horizontalRecoil = 0.8;
        }

        @Override
        public SlimeGun build() {
            return new SlimeGun(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
        }
    }
}
