package net.flytre.fguns.gun;

import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class Pistol extends AbstractGun {

    protected Pistol(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, double horizontalRecoil, double verticalRecoil) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
    }

    public static class Builder extends AbstractGun.Builder<Pistol> {

        public Builder() {
            super();
            scope = false;
        }

        @Override
        public Pistol build() {
            return new Pistol(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, horizontalRecoil, verticalRecoil);
        }
    }
}
