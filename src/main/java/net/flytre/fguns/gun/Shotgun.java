package net.flytre.fguns.gun;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Shotgun extends AbstractGun {

    private final int pelletCount;

    protected Shotgun(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, BulletProperties bulletProperties, boolean scope, double scopeZoom, SoundEvent fireSound, Item ammoItem, int pelletCount, double recoilMultiplier) {
        super(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, recoilMultiplier);
        this.pelletCount = pelletCount;
    }

    public int getPelletCount() {
        return pelletCount;
    }

    @Override
    public void fireBullet(World world, LivingEntity user, Hand hand, LivingEntity target, boolean semi) {

        for (int i = 0; i < pelletCount; i++)
            super.fireBullet(world, user, hand, target, false);
    }


    @Override
    public Text getDamageLine() {
        return new TranslatableText("text.fguns.tooltip.damage.shotgun", String.format("%.1f", getDamage()));
    }

    public static class Builder extends AbstractGun.Builder<Shotgun> {

        protected int pelletCount = 5;

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
            this.ammoItem = FlytreGuns.SHOTGUN_SHELL;
            this.fireSound = Sounds.SHOTGUN_FIRE_EVENT;
            this.scope = false;
            this.recoilMultiplier = 1.4;
        }

        public void setPelletCount(int pelletCount) {
            this.pelletCount = pelletCount;
        }

        @Override
        public Shotgun build() {
            return new Shotgun(damage, armorPen, rps, dropoff, spray, range, clipSize, reloadTime, bulletProperties, scope, scopeZoom, fireSound, ammoItem, pelletCount, recoilMultiplier);
        }
    }
}
