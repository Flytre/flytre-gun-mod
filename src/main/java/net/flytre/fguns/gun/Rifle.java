package net.flytre.fguns.gun;

import net.flytre.fguns.Sounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class Rifle extends AbstractGun {

    protected Rifle(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, double horizontalRecoil, double verticalRecoil) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
    }

    public static class Builder extends AbstractGun.Builder<Rifle> {

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
        }

        @Override
        public Rifle build() {
            return new Rifle(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
        }
    }
}
