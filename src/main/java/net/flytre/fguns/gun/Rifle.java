package net.flytre.fguns.gun;

import net.flytre.fguns.misc.Sounds;

public class Rifle extends AbstractGun {

    private Rifle(Builder builder) {
        super(builder);
    }

    public static class Builder extends AbstractGun.Builder<Builder> {

        public Builder() {
            super();
            this.fireSound = Sounds.RIFLE_FIRE_EVENT;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Rifle build() {
            return new Rifle(this);
        }
    }
}
