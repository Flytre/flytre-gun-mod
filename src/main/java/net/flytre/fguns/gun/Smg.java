package net.flytre.fguns.gun;

import net.flytre.fguns.Sounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class Smg extends AbstractGun {
    protected Smg(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, double recoilMultiplier) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, recoilMultiplier);
    }

    public static class Builder extends AbstractGun.Builder<Smg> {

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
            this.scope = false;
            this.recoilMultiplier = 0.8;
        }

        @Override
        public Smg build() {
            return new Smg(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, recoilMultiplier);
        }
    }
}
