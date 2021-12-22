package net.flytre.fguns.gun;

import net.flytre.fguns.misc.Sounds;

public class SlimeGun extends AbstractGun {

    private SlimeGun(Builder builder) {
        super(builder);
    }

    public static class Builder extends AbstractGun.Builder<Builder> {

        public Builder() {
            super();
            bulletProperties = BulletProperties.SLIME;
            fireSound = Sounds.SLIME_FIRE_EVENT;
            horizontalRecoil = 0.8;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public SlimeGun build() {
            return new SlimeGun(this);
        }
    }
}
