package net.flytre.fguns.gun;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.misc.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RocketLauncher extends AbstractGun {


    private RocketLauncher(Builder builder) {
        super(builder);
    }

    @Override
    public Text getDamageLine() {
        return new TranslatableText("text.fguns.tooltip.damage.varies");
    }


    public static class Builder extends AbstractGun.Builder<Builder> {

        public Builder() {
            velocity = 0.4f;
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
        protected Builder self() {
            return this;
        }

        @Override
        public RocketLauncher build() {
            return new RocketLauncher(this);
        }
    }
}
