package net.flytre.fguns.gun;

import net.flytre.fguns.entity.Bullet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FlareGun extends AbstractGun {


    private FlareGun(Builder builder) {
        super(builder);
    }


    public static class Builder extends AbstractGun.Builder<Builder> {

        public Builder() {
            super();
            bulletProperties = BulletProperties.FLARE;
            damage = 0;
            armorPen = 0;
            dropoff = 0;
            scopeZoom = 0;
            scope = false;
            ammoItem = Items.FIREWORK_ROCKET;
            velocity = 0.2f;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public FlareGun build() {
            return new FlareGun(this);
        }
    }
}
